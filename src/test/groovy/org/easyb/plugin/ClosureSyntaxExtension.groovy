package org.easyb.plugin

import org.easyb.BehaviorStep
import org.easyb.listener.ExecutionListener
import org.easyb.result.ReportingTag
import groovy.xml.MarkupBuilder;

public class ClosureSyntaxExtension implements SyntaxExtension {

  def boolean autoLoad() {
    return true
  }

  def String getName() {
    return "closure"
  }

  class ClosureReporting implements ReportingTag {
    def result

    ClosureReporting(result) {
      this.result = result
    }

    void toXml(MarkupBuilder xml) {
      xml.exec(result:result.toString())
    }
  }

  def Map<String, Closure> getSyntax() {
    return ['exec': { ExecutionListener listener, Binding binding, BehaviorStep stepParent, Object[] params ->
      if (params.length != 1 || !(params[0] instanceof Closure))
        throw new RuntimeException("exec failure, must be executable closure")

      def r = (params[0])()

      listener.tag new ClosureReporting(r)
    }]
  }

  // sample from Groovy book
  static class StringCalculationCategory {
    static def plus(String self, String operand) {
      try {
        return self.toInteger() + operand.toInteger()
      } catch (NumberFormatException fallback) {
        return (self << operand).toString()
      }
    }
  }

  def Class[] getExtensionCategories() {
    return [StringCalculationCategory.class]
  }
}