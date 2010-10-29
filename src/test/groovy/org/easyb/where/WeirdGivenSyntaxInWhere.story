
before "make total", {
  total = 0
}

where "weird syntax data", [amount:[1,2,3]], {
  then "amount #amount", {
    total += amount
  }
}

after "should be 6", {
  then "should be 6", {
    total.shouldBe 6
  }
}