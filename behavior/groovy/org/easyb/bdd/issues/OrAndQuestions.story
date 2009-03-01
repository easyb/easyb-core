scenario "easyb can and should support expressions", {

    given "a value of 3", {
        value = 3
    }
    then "an expression should evaluate to true", {
        (value >=0 && value <=6).shouldBe true
    }
}