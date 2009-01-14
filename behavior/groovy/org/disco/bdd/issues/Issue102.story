import org.disco.easyb.exception.VerificationException

scenario "ensureThrows should be less noisy", {
    given "some error condition", {
        var = {
            throw new NullPointerException("ha!")
        }
    }
    then "the ensureThrows clause should accept a String value rather than a class", {
        ensureThrows(NullPointerException) {
            var.call()
        }
    }
}

scenario "ensure strict exception handling", {
    given "something useless", {
        blah = "blah"
    }
    then "the ensureThrows clause should be strict", {

        ensureStrictThrows(VerificationException){

            ensureStrictThrows(RuntimeException.class) {
                throw new IllegalArgumentException("Test")
            }

        }

        
    }
}