package net.digital15.goliath;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import net.digital15.goliath.resource.ResourceRegister;
import net.digital15.goliath.resource.ResourceWrapper;
import net.digital15.goliath.resource.SubResource;

public class InboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private ResourceRegister resourceRegister = new ResourceRegister();

    public InboundHandler() {
        resourceRegister.register(new TestResource());
        resourceRegister.register(new AnotherTestResource());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, FullHttpRequest request) {
        if(request.headers().contains("token")) {
            final String uri = request.uri();
            if(uri.equals("/favicon.ico")) return;
            final long started = System.currentTimeMillis();
            final String rootUri = uri.equals("/") ? "/" : "/" + uri.split("/")[1];
            for(ResourceWrapper wrapper : resourceRegister.getResources()) {
                if(wrapper.getRootPath().equals(rootUri)) {
                    for(SubResource subResource : wrapper.getSubResourcesList()) {
                        final String fullUri = fullUri(rootUri, subResource.getUri());
                        if(uri.equals(fullUri) && request.method().name().equals(subResource.getHttpMethod().name())) {
                            writeResponse(context, true);
                            logRequest(uri, request.method(), System.currentTimeMillis() - started);
                            return;
                        }
                    }
                    break;
                }
            }
            writeResponse(context, false);
            logRequest(uri, request.method(), System.currentTimeMillis() - started);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Not authenticated");
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED,
                    Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
            response.headers().set("Content-Type", "application/json");
            response.headers().set("Server", "Goliath");
            response.headers().set("Content-Length", response.content().readableBytes());
            context.writeAndFlush(response);
        }
    }

    private void logRequest(String uri, HttpMethod method, long latency) {
        System.out.println(method.name() + " - " + uri + " (" + latency + ")");
    }

    private void writeResponse(ChannelHandlerContext context, boolean found) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, found ? HttpResponseStatus.OK : HttpResponseStatus.NOT_FOUND);
        response.headers().set("Content-Type", "application/json");
        response.headers().set("Server", "Goliath");
        response.headers().set("Content-Length", response.content().readableBytes());
        context.writeAndFlush(response);
    }

    private String fullUri(String rootUri, String subResourceUri) {
        if(rootUri.equals("/")) return subResourceUri;
        return rootUri + subResourceUri;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
