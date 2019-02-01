package net.digital15.goliath;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import net.digital15.goliath.resource.ResourceRegister;
import net.digital15.goliath.resource.ResourceWrapper;
import net.digital15.goliath.resource.SubResource;

public class InboundHandler extends SimpleChannelInboundHandler<HttpRequest> {

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
    public void channelRead0(ChannelHandlerContext context, HttpRequest request) {
        final String uri = request.uri();
        if(uri.equals("/favicon.ico")) return;
        final String rootUri = uri.equals("/") ? "/" : "/" + uri.split("/")[1];
        for(ResourceWrapper wrapper : resourceRegister.getResources()) {
            if(wrapper.getRootPath().equals(rootUri)) {
                for(SubResource subResource : wrapper.getSubResourcesList()) {
                    final String fullUri = fullUri(rootUri, subResource.getUri());
                    if(uri.equals(fullUri)) {
                        System.out.println("200");
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        response.headers().set("Content-Type", "application/json");
                        response.headers().set("Server", "Goliath");
                        response.headers().set("Content-Length", response.content().readableBytes());
                        context.write(response);
                        context.flush();
                        return;
                    }
                }
            }
        }
        System.out.println("404");
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        response.headers().set("Content-Type", "application/json");
        response.headers().set("Server", "Goliath");
        response.headers().set("Content-Length", response.content().readableBytes());
        context.write(response);
        context.flush();
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
