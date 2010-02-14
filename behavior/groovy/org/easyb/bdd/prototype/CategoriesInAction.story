import java.util.regex.Pattern
import org.easyb.util.CategoryRegExHelper

category "A"

scenario "simple one name categories", {
  given "a string", {
    foo = """category "A" """
  }
  then "we should be able to match on it", {
    m = foo =~ /^category "(.*)" /
    m[0][1].shouldBe "A"
  }
}

scenario "using brackets", {
  given "a more complex string with a group", {
    bar = """category ["A", "B"] """
  }
  then "using []'s doesnt matter either", {
    m2 = bar =~ /^category \[(.*)\] /
    vals = (m2[0][1]).split(",")
    //println vals
    vals[0].shouldBe "\"A\""
    vals[1].trim().shouldBe "\"B\""
  }
}

scenario "using brackets", {
  given "a more complex string with a group", {
    bar = """category ["A", "B"] """
    baz = """//category ["C", "D"] """
  }
  then "using []'s doesnt matter either with a regex that uses OR", {
    m2 = bar =~ /^category "(.*)"|\[(.*)\] /
    m2.size().shouldBe 1
    //println baz

    m3 = baz =~ /^category/
    m3.size().shouldBe 0

    m4 = bar =~ /^category/
    m4.size().shouldBe 1
  }
}

scenario "using category helper", {
  given "a more complex string with a group", {
    bar = """category ["A", "B"] """
    baz = """//category ["C", "D"] """
  }
  then "using category helper should return true or false", {
    helper = new CategoryRegExHelper()

    helper.lineStartsWithCategory(bar).shouldBe true
    helper.lineStartsWithCategory(baz).shouldBe false

  }
}

scenario "using brackets", {
  given "a more complex string with a group", {
    bar = """category ["A", "B"] """
    baz = """category "C" """
  }
  then "using []'s doesnt matter either with a regex that uses OR", {
    m21 = bar =~ /^category "(.*)"|\[(.*)\] /
    m21.size().shouldBe 1

    //println m21[0][0]

    if (m21[0][0].startsWith("[")) {
      tmp = m21[0][0]
      catvals = tmp[1..(tmp.indexOf("]") - 1)].split(",")
      catvals[0].trim().shouldBe "\"A\""
      catvals[1].trim().shouldBe "\"B\""

    } else {
      fail("match didn't occur?")
    }

    m22 = baz =~ /^category "(.*)"|\[(.*)\] /
    m22.size().shouldBe 1
//    println m22[0]

    if (m22[0][0].startsWith("category")) {
      //assume single syntax, thus, only one category and it's the second position
      m22[0][1].trim().shouldBe "C"
    }

  }
}


scenario "using category helper", {
  given "a more complex string with a group", {
    bar = """category ["A", "B"] """
    baz = """category "C" """
  }
  then "using category helper should return true or false", {
    helper = new CategoryRegExHelper()

    list1 = helper.getCategories(bar)
    list2 = helper.getCategories(baz)

    list1[0].shouldBe "A"
    list1[1].shouldBe "B"
    list2[0].shouldBe "C"

  }
}


scenario "using category helper", {
  given "a more complex string with a group", {
    bar = """category ["Andy", "Brian"] """
    baz = """category "Cat" """
  }
  then "using category helper should return true or false", {
    helper = new CategoryRegExHelper()

    list1 = helper.getCategories(bar)
    list2 = helper.getCategories(baz)

    list1[0].shouldBe "Andy"
    list1[1].shouldBe "Brian"
    list2[0].shouldBe "Cat"

  }
}
