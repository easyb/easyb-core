package org.easyb.domain;

public class NarrativePreProcessor {
    public String process(String specification) {
        return specification
                .replaceAll("(\\{*\\s)as a([^\\}]*\\})", "$1as_a$2")
                .replaceAll("(\\{*\\s)i want([^\\}]*\\})", "$1i_want$2")
                .replaceAll("(\\{*\\s)so that([^\\}]*\\})", "$1so_that$2");
    }
}
