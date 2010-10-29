package org.easyb.plugin

import sun.misc.Service;

class PluginLocator {
  EasybPlugin findPluginWithName(String pluginName) {
    for (EasybPlugin plugin: Service.providers(EasybPlugin)) {
      if (plugin.name.equals(pluginName)) {
        return plugin
      }
    }
    throw new RuntimeException("Plugin <${pluginName}> not found")
  }

  public static def findAllExampleDataParsers() {
    def dps = []

    Service.providers(ExampleDataParser).each { parser ->
      dps.add(parser)
    }

//    dps.addAll( Service.providers(ExampleDataParser) )
    dps.add(new ExampleAsMapDataParser())
    dps.add(new ExampleAsClosureDataParser())

    return dps
  }

  public static def findAllAutoloadingSyntaxExtensions() {
    def sas = []

    Service.providers(SyntaxExtension).each { SyntaxExtension parser ->
      if ( parser.autoLoad() )
        sas.add(parser)
    }

    return sas
  }

  public static def findSyntaxExtensionByName(String name) {

    for( SyntaxExtension parser : Service.providers(SyntaxExtension) ) {
      if ( parser.getName().equalsIgnoreCase(name) )
        return parser
    }

    throw new RuntimeException( "SyntaxExtension ${name} not found")
  }
}
