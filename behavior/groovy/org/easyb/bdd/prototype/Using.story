scenario "the keyword using should be able to be found", {
  given "a string containing story text and a keyword using", {
    txt = "using com.blah.blah.Plugin"
  }

  then "a regular expression should be able to find it", {
    matcher = txt =~ ~/using (.*)/
    matcher.matches().shouldBe true
    matcher.group(1).shouldEqual "com.blah.blah.Plugin"
  }

}



scenario "the keyword using should be able to be found in large text", {
  given "a string containing story text and a keyword using", {
    fle = new File("./behavior/groovy/org/easyb/bdd/prototype/UsingExample.story")
  }

  when "this file is read", {

    lines = fle.readLines()
    clzz = null
    for (line in lines) {
      matcher = line =~ ~/using (.*)/
      if (matcher.matches()) {
        clzz = matcher.group(1)
        break;
      } else if (line.startsWith("scenario")) {
        break;
      }
    }


  }

  then "a regular expression should be able to find it", {
    clzz.shouldBe "'prototype'"
  }

}