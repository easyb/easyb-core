scenario "files with spaces in path should still pass", {
  given "this file is in a path with spaces", {
    inconsequentialValue = 12 + 5
  }

  then "easyb should excute is as normal", {
    inconsequentialValue.shouldBe 17
  }
}