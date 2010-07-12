package org.easyb.plugin;

public class ExampleWrapperDataParser implements ExampleDataParser {
  
  def boolean processData(Object data, Object closure, Object binding) {

    if ( data instanceof ExampleWrapper ) {
      def map = [a:1, b:2, c:3]
      closure(map)
      map = [a:2, b:4, c:6]
      closure(map)

      return true
    }

    return false
  }
}