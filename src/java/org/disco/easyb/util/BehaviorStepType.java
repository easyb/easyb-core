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
    AFTER("after"),
    IT("it"),
    DESCRIPTION("description"),
    NARRATIVE("narrative"),
    NARRATIVE_ROLE("role"),
    NARRATIVE_FEATURE("feature"),
    NARRATIVE_BENEFIT("benefit");

    private final String type;

    BehaviorStepType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
