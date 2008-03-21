package org.disco.easyb.util;

public enum ReportType {

    EASYB("easyb"),
    STORY("story"),
    SPECIFICATION("specification");

    private final String type;

    ReportType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
