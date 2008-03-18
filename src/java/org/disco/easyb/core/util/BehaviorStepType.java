package org.disco.easyb.core.util;

public enum BehaviorStepType {

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

    BehaviorStepType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
