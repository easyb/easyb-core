package org.disco.easyb.report


class Report {

  public static final String TXT_SPECIFICATION = "txtspecification"
  public static final String TXT_STORY = "txtstory"
  public static final String XML_EASYB = "xml"

  public static final String STORY_TYPE = "story"
  public static final String SPECIFICATION_TYPE = "specification"
  public static final String EASYB_TYPE = "easyb"

  String location
  String format
  String type

  /**
   * builder functionality for reports
   */
  static Report build(type, loc) {
    if (type.equals(TXT_STORY)) {
      return new Report(type: "story",
              location: (loc == null ? "easyb-story-report.txt" : loc))
    } else if (type.equals(TXT_SPECIFICATION)) {
      return new Report(type: "specification",
              location: (loc == null ? "easyb-specification-report.txt" : loc))
    } else {
      return new Report(type: "easyb",
              location: (loc == null ? "easyb-report.xml" : loc))
    }
  }
}
  
  
  
	
				  
				  




