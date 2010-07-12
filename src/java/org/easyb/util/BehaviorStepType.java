package org.easyb.util;

import java.util.Arrays;
import java.util.List;

public enum BehaviorStepType {

  GENESIS("genesis"),
  SPECIFICATION("specification"),
  STORY("story"),
  EXECUTE("execute"),
  SCENARIO("scenario"),
  GIVEN("given"),
  WHEN("when"),
  THEN("then"),
  AND("and"),
  BEFORE("before"),
  AFTER("after"),
  IT("it"),
  BEFORE_EACH("before_each"),
  AFTER_EACH("after_each"),
  SHARED_BEHAVIOR("shared_behavior"),
  BEHAVES_AS("behaves_as"),
  DESCRIPTION("description"),
  NARRATIVE("narrative"),
  NARRATIVE_ROLE("role"),
  NARRATIVE_FEATURE("feature"),
  NARRATIVE_BENEFIT("benefit"),
  EXTENSION_POINT("extension_point"),
  WHERE("where");

  private final String type;

  BehaviorStepType(String type) {
    this.type = type;
  }

  public String type() {
    return type;
  }

  public int getSoftTabs(BehaviorStepType genesis) {
    int softTabs = 0;
    switch (this) {
      case STORY:
      case SPECIFICATION:
        softTabs = 2;
        break;
      case DESCRIPTION:
      case NARRATIVE:
        softTabs = 3;
        break;
      case SCENARIO:
      case BEFORE:
      case AFTER:
      case IT:
        softTabs = 4;
        break;
      case AFTER_EACH:
      case BEFORE_EACH:
      case SHARED_BEHAVIOR:
      case BEHAVES_AS:
      case GIVEN:
      case WHEN:
      case THEN:
      case NARRATIVE_ROLE:
      case NARRATIVE_FEATURE:
      case NARRATIVE_BENEFIT:
        softTabs = 6;
        break;
      case AND:
        softTabs = genesis.equals(BehaviorStepType.STORY) ? 6 : 4;
        break;

    }
    return softTabs;
  }

  public static List<BehaviorStepType> grossCountableTypes = Arrays.asList(
    BehaviorStepType.SPECIFICATION, BehaviorStepType.SCENARIO, BehaviorStepType.BEFORE, BehaviorStepType.AFTER, BehaviorStepType.AFTER_EACH,
    BehaviorStepType.BEFORE_EACH, BehaviorStepType.WHERE);

  public String format(BehaviorStepType genesis) {
    String format = type;
    switch (this) {
      case STORY:
      case SPECIFICATION:
      case NARRATIVE:
        format = type.substring(0, 1).toUpperCase() + type.substring(1) + ":";
        break;
      case NARRATIVE_ROLE:
        format = "As a";
        break;
      case NARRATIVE_FEATURE:
        format = "I want";
        break;
      case NARRATIVE_BENEFIT:
        format = "So that";
        break;
      case DESCRIPTION:
        format = genesis.equals(BehaviorStepType.STORY) ? type.substring(0, 1).toUpperCase() + type.substring(1) + ":" : "";
        break;
    }
    return format;
  }
}
