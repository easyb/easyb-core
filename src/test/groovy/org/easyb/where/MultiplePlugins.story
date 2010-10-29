

using 'test', 'blah'
using 'test2', 'blah2'

before "setup plugins", {
  given "reset plugins", {
    blah.resetAllCounts()
    blah2.resetAllCounts()
  }
}

scenario "do this", {
  given "some thing", {

  }
  when "some other thing", {

  }
  then "yet some other thing", {

  }
}


after "our counts should be done", {
  then "checking...", {
    blah.beforeGivenCount == 1
    blah.beforeWhenCount == 1
    blah.beforeThenCount.shouldBe 2
    blah.afterThenCount.shouldBe 1
    
    blah2.beforeGivenCount == 1
    blah2.beforeWhenCount == 1
    blah2.beforeThenCount.shouldBe 2
    blah2.afterThenCount.shouldBe 1
  }
}

