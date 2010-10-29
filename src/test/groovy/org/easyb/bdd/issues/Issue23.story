package org.easyb.bdd.issues

scenario "shouldBe should accept negative numbers", {

    given "a negative number", {
        negint = -3
    }

    then "the value should be an Integer", {
        negint.shouldBeAn(Integer)
    }

    and

    then "it should be -3", {
        negint.shouldBe(-3)
        //this doesn't work-- results in a property call
        //negint.shouldBe -3
    }

}