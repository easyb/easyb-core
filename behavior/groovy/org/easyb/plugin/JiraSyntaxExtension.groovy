
package org.easyb.plugin

import org.easyb.BehaviorStep
import org.easyb.result.ReportingTag
import groovy.xml.MarkupBuilder
import org.easyb.listener.ExecutionListener;

/*
Jira is of course a trademark of Atlassian
 */

public class JiraSyntaxExtension implements SyntaxExtension {
  def boolean autoLoad() {
    return false;
  }

  def Class[] getExtensionCategories() {
    return new Class[0]; 
  }

  class JiraReportingTag implements ReportingTag {
    def map

    public JiraReportingTag(map) {
      this.map = map
    }

    public void toXml(MarkupBuilder xml) {
      xml.jira(map)
    }
  }

  String getName() {
    return "jiraReportingPlugin"
  }

  Map<String, Closure> getSyntax() {
    return ['jira': { ExecutionListener listener, Binding binding, BehaviorStep stepParent, Object []params ->
      if ( params.length == 1 ) {
        listener.tag new JiraReportingTag(params[0])
      } else
        throw new RuntimeException("Incorrect number of parameters passed to jira syntax")
    }]
  }
}