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

