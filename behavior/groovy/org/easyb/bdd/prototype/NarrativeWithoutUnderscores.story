description "this is a prototype story for using narratives"

narrative "this feature is for easyb users", {
    as a "analyst or domain expert or developer"
    i want "to document the persona, etc of a user acting in the story"
    so that "the story/scenario is documented more fully"
}

scenario "this is a simple scenario" , {
    given "something", {
        foo = "bar"
    }
    when "something else happends", {
        val = foo[0]
    }
    then "something should be validated" , {
        val.shouldBe "b"
    }
}

scenario "one pending scenario for no reason at all"