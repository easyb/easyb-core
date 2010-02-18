import org.easyb.util.TagRegExHelper

tag "A"

scenario "simple one name categories", {
  given "a string", {
    foo = """tag "A" """
  }
  then "we should be able to match on it", {
    m = foo =~ /^tag "(.*)" /
    m[0][1].shouldBe "A"
  }
}

scenario "using brackets", {
  given "a more complex string with a group", {
    bar = """tag ["A", "B"] """
  }
  then "using []'s doesnt matter either", {
    m2 = bar =~ /^tag \[(.*)\] /
    vals = (m2[0][1]).split(",")
    //println vals
    vals[0].shouldBe "\"A\""
    vals[1].trim().shouldBe "\"B\""
  }
}

scenario "using brackets", {
  given "a more complex string with a group", {
    bar = """tag ["A", "B"] """
    baz = """//tag ["C", "D"] """
  }
  then "using []'s doesnt matter either with a regex that uses OR", {
    m2 = bar =~ /^tag "(.*)"|\[(.*)\] /
    m2.size().shouldBe 1
    //println baz

    m3 = baz =~ /^tag/
    m3.size().shouldBe 0

    m4 = bar =~ /^tag/
    m4.size().shouldBe 1
  }
}

scenario "using tag helper", {
  given "a more complex string with a group", {
    bar = """tag ["A", "B"] """
    baz = """//tag ["C", "D"] """
  }
  then "using tag helper should return true or false", {
    helper = new TagRegExHelper()

    helper.lineStartsWithTag(bar).shouldBe true
    helper.lineStartsWithTag(baz).shouldBe false

  }
}

scenario "using brackets", {
  given "a more complex string with a group", {
    bar = """tag ["A", "B"] """
    baz = """tag "C" """
  }
  then "using []'s doesnt matter either with a regex that uses OR", {
    m21 = bar =~ /^tag "(.*)"|\[(.*)\] /
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

    m22 = baz =~ /^tag "(.*)"|\[(.*)\] /
    m22.size().shouldBe 1
//    println m22[0]

    if (m22[0][0].startsWith("tag")) {
      //assume single syntax, thus, only one tag and it's the second position
      m22[0][1].trim().shouldBe "C"
    }

  }
}


scenario "using tag helper", {
  given "a more complex string with a group", {
    bar = """tag ["A", "B"] """
    baz = """tag "C" """
  }
  then "using tag helper should return true or false", {
    helper = new TagRegExHelper()

    list1 = helper.getTags(bar)
    list2 = helper.getTags(baz)

    list1[0].shouldBe "A"
    list1[1].shouldBe "B"
    list2[0].shouldBe "C"

  }
}


scenario "using tag helper", {
  given "a more complex string with a group", {
    bar = """tag ["Andy", "Brian"] """
    baz = """tag "Cat" """
  }
  then "using tag helper should return true or false", {
    helper = new TagRegExHelper()

    list1 = helper.getTags(bar)
    list2 = helper.getTags(baz)

    list1[0].shouldBe "Andy"
    list1[1].shouldBe "Brian"
    list2[0].shouldBe "Cat"

  }
}


