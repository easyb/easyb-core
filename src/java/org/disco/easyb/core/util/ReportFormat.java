package org.disco.easyb.core.util;

public enum ReportFormat {

    XML("xml"),
    TXT("txt"),
    TERSE("");

    private final String format;

    ReportFormat(String format) {
        this.format = format;
    }

    public String format() {
        return format;
    }
}
