/*
 * Using this extension allows those who wish to, to add BehaviorCategory behavior to when and given
 */
package org.easyb.plugin

import org.easyb.BehaviorCategory;
public class BehaviorCategorySyntaxExtension implements SyntaxExtension {
  def boolean autoLoad() {
    return false 
  }

  def String getName() {
    return "behaviorCategory"
  }

  def Map<String, Closure> getSyntax() {
    return [:]
  }

  def Class[] getExtensionCategories() {
    return [BehaviorCategory.class]
  }
}