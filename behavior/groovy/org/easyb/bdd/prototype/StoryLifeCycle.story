using 'prototype'


scenario "some simple scenario", {
  given "a small coll of numbers", {
    foo = [1,2]
  }
  when "another number is added", {
    foo << 3
  }
  then "the size should be 3", {
    foo.size().shouldBe 3
  }

  and "there should be a magic variable called _foo_", {
    _foo_.shouldBe "test"
  }

}