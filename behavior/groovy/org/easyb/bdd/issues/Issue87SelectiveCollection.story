


ignore  ["this is purposely a broken scenario", "this is also a purposely broken scenario"]//coll

scenario "this is purposely a broken scenario", {
    given "some variable with a value", {
        val = 12
    }
    then "to force an error, one should verify it is not 12", {
        val.shouldNotEqual 12
    }
}

scenario "this is also a purposely broken scenario", {
    given "some variable with a value", {
        val = 120
    }
    then "to force an error, one should verify it is not 12", {
        val.shouldNotEqual 120
    }
}

scenario "this one should work!", {
    given "something", {
        foo = "bar"
    }
    then "it should be itself", {
        foo.shouldEqual("bar")
    }
}