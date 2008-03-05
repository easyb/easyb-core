package org.disco.easyb.core.report


class Report {

  public static final String XML_BEHAVIOR = "xmlbehavior"
  public static final String TXT_BEHAVIOR = "txtbehavior"
  public static final String XML_STORY = "xmlstory"
  public static final String TXT_STORY = "txtstory"
  public static final String XML_EASYB = "xmleasyb"

  public static final String XML_FORMAT = "xml"
  public static final String TXT_FORMAT = "txt"

  public static final String STORY_TYPE = "story"
  public static final String BEHAVIOR_TYPE = "behavior"


  String location

  // xml or txt
  String format
  // story or behavior
  String type

}