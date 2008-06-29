package org.disco.bdd.prototype


description "this story is fleshing out fixture logic for scenarios"


before_each "some description", {
    given "a value", {
        value = 12
    }
    and "and another value", {
        otherValue = "test"
    }
}

after_each "blah", {
    then "the value test should always be test", {
        otherValue.shouldBe "test"
    }
}


scenario "one time", {
    when "value is multiplied by 2", {
        value = value * 2
    }

    then "value should be 24", {
        value.shouldBe 24
    }
}

scenario "two times", {
    when "value is multiplied by 3", {
        value = value * 3
    }

    then "value should be 36", {
        value.shouldBe 36
    }

    and "the other value should still be test", {
        otherValue.shouldBe "test"
    }
}

