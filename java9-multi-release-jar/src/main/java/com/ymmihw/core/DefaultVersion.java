package com.ymmihw.core;

public class DefaultVersion implements Version {
    @Override
    public String version() {
        return System.getProperty("java.version") + " from default version";
    }
}
