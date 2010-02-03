import java.util.regex.Pattern

//category "A"

scenario "simple one name categories", {
  given "a string", {
    foo = """category "A" """
  }
  then "we should be able to match on it", {
    m = foo =~ /category "(.*)" /
    m[0][1].shouldBe "A"
  }  
}

scenario "using brackets", {
 given "a more complex string with a group", {
    bar = """category ["A", "B"] """
  }
  then "using []'s doesnt matter either", {
    m2 = bar =~ /category \[(.*)\] /
    vals = (m2[0][1]).split(",")
    //println vals
    vals[0].shouldBe "\"A\""
    vals[1].trim().shouldBe "\"B\""
  }
}