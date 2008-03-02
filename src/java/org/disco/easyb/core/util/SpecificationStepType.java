package org.disco.easyb.core.util;

public enum SpecificationStepType {

    GENESIS("genesis"),
    BEHAVIOR("behavior"),
    STORY("story"),
    SCENARIO("scenario"),
    GIVEN("given"),
    WHEN("when"),
    THEN("then"),
    AND("and"),
    BEFORE("before"),
    IT("it");

    
    private final String type;

    SpecificationStepType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

}
