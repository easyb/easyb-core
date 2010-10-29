import org.easyb.exception.VerificationException

description """
    this story will prototype less than and greater than
    for easyb users
"""

before_each "initalize values", {
    given "two defined integers", {
        foo = 23
        bar = 24
    }
}

scenario "easyb should support the phrase shouldBeGreaterThan", {
    then "easyb should faciliate comparing foo to bar effectively", {
        bar.shouldBeGreaterThan foo
    }
}

scenario "easyb should handle equal-- what do we do?", {
    given "two equal values", {
        ibar = bar
    }

    then "easyb shoud throw an exception as they are equal-- not greater than", {
        try {
            ibar.shouldBeGreaterThan foo
        } catch (e) {
            e.class.shouldBe VerificationException.class
        }
    }

    and "easyb shoud throw another exception as they are equal-- not less than", {
        try {
            ibar.shouldBeLessThan foo
        } catch (e) {
            e.class.shouldBe VerificationException.class
        }
    }
}

scenario "easby should handle less than too!", {
    then "easyb should faciliate comparing foo to bar effectively", {
        foo.shouldBeLessThan bar
    }
}

scenario "easyb should handle null comparison with shouldBeGreaterThan", {
    given "a null value", {
        ifoo = null
    }
    then "easyb should report can't do a null comparison", {
        try {
            ifoo.shouldBeLessThan foo
        } catch (e) {
            e.class.shouldBe VerificationException.class
        }
    }
    and "easyb should report can't do a null comparison for either side", {
        try {
            foo.shouldBeLessThan ifoo
        } catch (e) {
            e.class.shouldBe VerificationException.class
        }
    }
}