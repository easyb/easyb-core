package org.easyb.plugin

public interface SyntaxExtension {
  boolean autoLoad()
  String getName()
  Map<String, Closure> getSyntax()
  Class[] getExtensionCategories()
}