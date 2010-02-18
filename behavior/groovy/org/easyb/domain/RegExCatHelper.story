import org.easyb.util.TagRegExHelper

scenario "using tag helper", {
  given "a more complex string with a group", {
    lots = """tag ["bazzy","bar"]"""
  }
  then "using tag helper should return true or false", {
    helper = new TagRegExHelper()
    list1 = helper.getTags(lots)
    list1[0].shouldBe "bazzy"
    list1[1].shouldBe "bar"
  }
}


