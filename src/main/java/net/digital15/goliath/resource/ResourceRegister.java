package net.digital15.goliath.resource;

import java.util.List;

public class ResourceRegister {

    private final List<ResourceWrapper> resources;

    public ResourceRegister(List<ResourceWrapper> resources) {
        this.resources = resources;
    }

    public List<ResourceWrapper> getResources() {
        return resources;
    }
}
