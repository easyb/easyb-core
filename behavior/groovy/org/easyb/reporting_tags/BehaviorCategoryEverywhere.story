

extension "behaviorCategory"

before_each "setup vars", {
  given "shoulds here", {
    1.shouldBe 1
  }
  when "shoulds should be here to", {
    2.shouldBe 2
  }
}

after_each "after_each", {
  given "shoulds here", {
    2.shouldBeGreaterThan 1
  }
  when "shoulds should be here to", {
    1.shouldBeLessThan 2
  }
}

scenario "testing shoulds in givens and whens", {
  given "shoulds here", {
    1.shouldBeEqualTo 1
  }
  when "shoulds should be here to", {
    2.shouldEqual 2
  }  
}

before "before", {
  given "shoulds here", {
    1.shouldntEqual 2
  }
  when "shoulds should be here to", {
    2.shouldNotBe 3
  }

}

after "before", {
  given "shoulds here", {
    1.shouldntEqual 2
  }
  when "shoulds should be here to", {
    2.shouldNotBe 3
  }

}