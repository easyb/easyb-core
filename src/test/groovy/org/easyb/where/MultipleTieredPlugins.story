
using 'test', 'blah'

where "some data", [values:[1,2,3]]

before "setup plugins", {
  given "reset plugins", {
    blah.resetAllCounts()
    blah2.resetAllCounts()
    total = 0

  }
}

scenario "outside data should only affect blah not blah2", {
  given "outside given", {

  }
  when "outside when", {

  }
  then "outside then", {

  }
}

where "some more data", [num:[2]], {
  using 'test2', 'blah2'

  scenario "some scenario #values which should affect blah and blah2", {
    given "#values", {
      total = total + (num * values)
    }
    when "some other thing", {

    }
    then "yet some other thing", {

    }
  }
}

after "our counts should be done", {
  then "checking...", {
    // blah should get one more set of everything
    blah.beforeGivenCount == 6
    blah.beforeWhenCount == 6
    blah.beforeThenCount.shouldBe 7
    blah.afterThenCount.shouldBe 6

    blah2.beforeGivenCount == 3
    blah2.beforeWhenCount == 3
    blah2.beforeThenCount.shouldBe 3
    blah2.afterThenCount.shouldBe 3

    total.shouldBe 12

  }
}



