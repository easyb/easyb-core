import org.easyb.util.CategoryRegExHelper

scenario "using category helper", {
  given "a more complex string with a group", {
    lots = """category ["bazzy","bar"]"""
  }
  then "using category helper should return true or false", {
    helper = new CategoryRegExHelper()
    list1 = helper.getCategories(lots)
    list1[0].shouldBe "bazzy"
    list1[1].shouldBe "bar"
  }
}


