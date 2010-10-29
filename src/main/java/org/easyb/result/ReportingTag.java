package org.easyb.result;

import groovy.xml.MarkupBuilder;

/**
 * a plugin can define syntax that it can add to the binding that inserts tags into the current behavior step. They only need to implement this
 * interface as we need to be able to convert it into XML.
 */

public interface ReportingTag {
  void toXml(MarkupBuilder xml);
}
