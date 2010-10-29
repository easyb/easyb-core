package org.easyb

var = "string"

it "should have a length of 6", {
    var.length().shouldBe(6)
    var.length().shouldBe 6
}

it "should not be null", {
    var.shouldNotBe null
}

it "should handle null objects", {
    value = null

    value.shouldBe null
}
