package net.digital15.goliath.resource;

import net.digital15.goliath.annotations.Method;
import net.digital15.goliath.annotations.Path;

import java.util.ArrayList;
import java.util.List;

public class ResourceRegister {

    private final List<ResourceWrapper> resources = new ArrayList<>();

    public void register(Object object) {
        Class<?> c = object.getClass();
        if(c.isAnnotationPresent(Path.class)) {
            final String rootPath = c.getAnnotation(Path.class).value();
            List<SubResource> subResources = new ArrayList<>();
            for(java.lang.reflect.Method method : c.getMethods()) {
                if(method.isAnnotationPresent(Method.class)) {
                    subResources.add(new SubResource(method.getAnnotation(Path.class).value(), method.getAnnotation(Method.class).value(), method));
                }
            }
            resources.add(new ResourceWrapper(rootPath, subResources));
        }
    }

    public List<ResourceWrapper> getResources() {
        return resources;
    }
}
