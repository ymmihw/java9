package com.ymmihw.core;

public class DefaultVersion implements Version {

    @Override
    public String version() {
        return Runtime.version().toString() + " from java9 version";
    }
}
