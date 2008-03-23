package org.disco.easyb.util;

public enum BehaviorStepType {

    GENESIS("genesis"),
    SPECIFICATION("specification"),
    STORY("story"),
    SCENARIO("scenario"),
    GIVEN("given"),
    WHEN("when"),
    THEN("then"),
    AND("and"),
    BEFORE("before"),
    IT("it"),
    DESCRIPTION("description");

    private final String type;

    BehaviorStepType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
