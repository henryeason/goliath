package net.digital15.goliath.resource;

import net.digital15.goliath.HttpMethod;

import java.lang.reflect.Method;

public class SubResource {

    private final String uri;
    private final HttpMethod httpMethod;
    private final Method method;

    public SubResource(String uri, HttpMethod httpMethod, Method method) {
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Method getMethod() {
        return method;
    }
}
