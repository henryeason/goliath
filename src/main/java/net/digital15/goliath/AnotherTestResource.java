package net.digital15.goliath;

import net.digital15.goliath.annotations.Method;
import net.digital15.goliath.annotations.Path;
@Path
public class AnotherTestResource {

    @Path
    @Method(HttpMethod.GET)
    public void test() {

    }
}
