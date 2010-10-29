
package org.easyb.plugin;


public class ExampleAsMapDataParser implements ExampleDataParser {

  protected void process(data, closure) {
    def maxCount = -1
    def fields

    data.values().each { v ->
      if ( v.size() > maxCount )
        maxCount = v.size()
    }

    fields = data.keySet().collect { field -> field.toString() }


    for( int counter = 0; counter < maxCount; counter ++ ) {

      def map = [:]

      fields.each { field ->
        def result
        def values = data[field]
        // more values left
        if ( values.size() > counter ) {
          result = values[counter]
        } else if ( counter == 0 ) { // no data and counter is zero
          result = null
        } else { // counter has gone past end of data
          result = values[counter-1]
        }

        map[field] = result
      }
      
      closure(map)
    }
  }

  boolean processData(data, closure, binding) {
    if ( data instanceof Map ) {
      process(data, closure)
      return true
    }
    
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
}