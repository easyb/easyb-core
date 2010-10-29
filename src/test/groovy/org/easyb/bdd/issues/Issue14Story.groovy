package org.easyb.bdd.issues

import org.easyb.bdd.issues.Issue14Stack

scenario "Groovy wraps exceptions", {

    given "an empty stack", {
        stack = new Issue14Stack()
    }

    when "null is pushed", {
        pushnull = {
            stack.push(null)
        }
    }

    then "an IllegalArgumentException should be thrown", {
        ensureThrows(IllegalArgumentException.class) {
            pushnull()
        }
    }

    and

    then "the stack should still be empty", {
        stack.empty.shouldBe true
    }
}

scenario "the cause of an exception is null", {

    given "an IllegalArgumentException is created", {
        exceptn = new IllegalArgumentException();
    }

    then "the exception should have no cause", {
        exceptn.getCause().shouldBe null
    }

    and

    then "the excepiton is thrown and properly caught", {
        ensureThrows(IllegalArgumentException.class) {
            throw new IllegalArgumentException();
        }
    }
}