/*
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Jun 12, 2010
 * Time: 2:03:12 AM
 */
package org.easyb.plugin;

public class ExampleAsClosureDataParser extends ExampleAsMapDataParser {

  private def processClosure(exampleData, binding) {
    def expando = new Expando( ['story':binding] )

    Closure c = exampleData
    c.resolveStrategy = Closure.DELEGATE_FIRST
    c.delegate = expando

    def retVal = c.call()

    def map = expando.getProperties()
    map.remove('story')

    // if there is only 1 item and its a map, then use that instead
    if ( map.size() == 1 ) {
      def item = map.values().asList()
      if ( item[0] instanceof Map )
        map = item[0]
    } else if ( map.size() == 0 && retVal instanceof Map )
      map = retVal

    return map
  }

  boolean processData(data, closure, binding) {
    if ( data instanceof Closure ) {
      def map = processClosure(data, binding)

      process( map, closure )
      return true
    }

    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
}