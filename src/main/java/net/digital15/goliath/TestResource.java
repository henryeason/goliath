package net.digital15.goliath;

import net.digital15.goliath.annotations.Method;
import net.digital15.goliath.annotations.Path;

@Path("/hello-world")
public class TestResource {

    @Path("/hello")
    @Method(HttpMethod.POST)
    public void test() {
        System.out.println("TEST!");
    }
}
