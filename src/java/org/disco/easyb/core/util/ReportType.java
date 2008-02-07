package org.disco.easyb.core.util;

public enum ReportType {

	STORY("story"), 
	BEHAVIOR("behavior");

	private final String type;

	ReportType(String type) {
	    this.type = type;
	}

	public String type() { 
		return type;
	}

}
