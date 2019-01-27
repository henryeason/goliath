package net.digital15.goliath;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import net.digital15.goliath.annotations.Method;
import net.digital15.goliath.annotations.Path;
import net.digital15.goliath.resource.ResourceRegister;
import net.digital15.goliath.resource.ResourceWrapper;
import net.digital15.goliath.resource.SubResource;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InboundHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private ResourceRegister resourceRegister;

    public InboundHandler() {
        List<ResourceWrapper> wrappers = new ArrayList<>();
        wrappers.add(getResourceWrapper());
        this.resourceRegister = new ResourceRegister(wrappers);
    }

    public ResourceWrapper getResourceWrapper() {
        Class<TestResource> obj = TestResource.class;
        if(obj.isAnnotationPresent(Path.class)) {
            final String rootPath = obj.getAnnotation(Path.class).value();
            List<SubResource> subResources = new ArrayList<>();
            for(java.lang.reflect.Method method : obj.getMethods()) {
                if(method.isAnnotationPresent(Method.class)) {
                    subResources.add(new SubResource(method.getAnnotation(Path.class).value(), method.getAnnotation(Method.class).value(), method));
                }
            }
            return new ResourceWrapper(rootPath, subResources);
        }
        return null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, HttpRequest request) {
        final String uri = request.uri();
        if(uri.equals("/favicon.ico")) return;
        String[] uriParts = uri.split("/");
        final String rootUri = "/" + uriParts[1];
        for(ResourceWrapper wrapper : resourceRegister.getResources()) {
            if(wrapper.getRootPath().equals(rootUri)) {
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
        System.out.println("404");
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        response.headers().set("Content-Type", "application/json");
        response.headers().set("Server", "Goliath");
        response.headers().set("Content-Length", response.content().readableBytes());
        context.write(response);
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
