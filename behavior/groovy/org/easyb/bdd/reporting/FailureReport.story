package org.easyb.bdd.reporting

scenario "this scenario will fail!", {

    given "a false variable", {
        val = false
    }

    then "easyb will report a failure if should is true", {
        val.shouldBe true
    }

}