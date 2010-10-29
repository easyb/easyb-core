import org.easyb.exception.VerificationException

scenario "ensureThrows accepts a list of exceptions", {
    given "a npe exception", {
        npe = { throw new NullPointerException("null") } 
    }
    
    then "ensure throws a npe", {
      ensureThrows(NullPointerException) {
        npe.call()
      }
    }
    
    and "then ensure doesn't throw an IllegalArgumentException", {
      ensureFails {
        ensureThrows(IllegalArgumentException) {
          npe.call()
        }
      }
    }
    
    and "then ensure one of the list is thrown", {
        ensureThrows( [IllegalArgumentException, NullPointerException] ) {
            npe.call()
        }
    }
    
    and "then ensure a VerificationException is thrown when the exception is not in the list", {
        ensureFails {
            ensureThrows( [IllegalArgumentException, NoSuchElementException] ) {
                npe.call()
            }
        }
    }
    
    then "ensure strict throws one of the exceptions", {
        ensureStrictThrows( [IllegalArgumentException, NullPointerException] ) {
            npe.call()
        }
    }
    
    and "then ensure a VerificationException is thrown when the ensureStrictThrows is called", {
        ensureFails {
            ensureStrictThrows( [IllegalArgumentException, NoSuchElementException] ) {
                npe.call()
            }
        }
    }
}