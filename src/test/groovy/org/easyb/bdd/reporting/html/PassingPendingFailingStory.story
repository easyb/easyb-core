scenario "pending scenario"

scenario "failing scenario", {
  then "1 does not equal 0 should fail", {
    1.shouldEqual 0
  }
}

scenario "passing scenario", {
  then "1 should equal 1", {
    1.shouldEqual 1
  }
}