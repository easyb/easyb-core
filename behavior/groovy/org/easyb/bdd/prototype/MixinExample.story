
//import org.easyb.BehaviorCategory
//import org.easyb.bdd.prototype.ExtendedCategories
//
//BehaviorCategory.mixin ExtendedCategories

using "BetterBe"

scenario "mixins should work normally", {
  given "a definition of a new method" , {
    var = "blah"
  }
  then "mixing it into easyb should work", {
    var.betterBe "blah"
  }
}