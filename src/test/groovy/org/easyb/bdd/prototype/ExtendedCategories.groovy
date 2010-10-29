package org.easyb.bdd.prototype

import org.easyb.BehaviorCategory

public class ExtendedCategories {  
  static void betterBe(Object self, value) {
    BehaviorCategory.shouldBe(self, value)
  }
}