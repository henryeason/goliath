package net.digital15.goliath.resource;

import java.util.List;

public class ResourceWrapper {

    private final String rootPath;
    private final List<SubResource> subResourcesList;

    public ResourceWrapper(String rootPath, List<SubResource> subResourceList) {
        this.rootPath = rootPath;
        this.subResourcesList = subResourceList;
    }

    public String getRootPath() {
        return rootPath;
    }

    public List<SubResource> getSubResourcesList() {
        return subResourcesList;
    }
}
