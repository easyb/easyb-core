package org.easyb.plugin

import org.easyb.bdd.prototype.ExtendedCategories
import org.easyb.plugin.BasePlugin

class BetterBePlugin extends BasePlugin {

  public String getName() {
    return "BetterBe";
  }

  public Object beforeStory(Binding binding) {
    Object.mixin ExtendedCategories
  }

}