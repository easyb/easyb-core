

before "it all!", {
  total = 0
}

// this is in top level context
shared_behavior "shared", {
  then "add it up", {
    total += amount
  }
}

// this will be in its own context because of the where clause
scenario "wibble #amount", {
  it_behaves_as "shared"

  where "give some amounts", [amount:[1,2,3]]
}


after "should be 6", {
  then "should be 6", {
    total.shouldBe 6
  }
}