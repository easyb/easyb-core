package org.easyb.plugin

where "we are using example wrapper", new ExampleWrapper()

before "how many times", {
  times = 0
  total = 0
}

scenario "example wrapper test #a #b #c", {
  when "totals", {
    total += (a+b+c)
  }
  then "a + b should be c", {
    (a+b).shouldBe c
  }
}

after "checking results", {
  then "totals", {
    total.shouldBe 18
  }
}

