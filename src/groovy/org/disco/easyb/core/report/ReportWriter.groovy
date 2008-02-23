package org.disco.easyb.core.report

/**
 *  This is arguably needed as the reportin
 *  writing is done in a Java class and not 
 *  inside Groovy where we'd leverage duck typing
 */
interface ReportWriter {
	void writeReport()
}