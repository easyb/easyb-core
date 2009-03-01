package org.easyb.bdd.story.stack

import org.easyb.bdd.Stack

scenario "pop is called on stack with one value", {

    given "an empty stack with one pushed value", {
        stack = new Stack()
        pushVal = "foo"
        stack.push(pushVal)
    }

    when "pop is called", {
        popVal = stack.pop()
    }

    then "that object should be returned", {
        popVal.shouldBe pushVal
    }

    and

    then "the stack should be empty", {
        stack.empty.shouldBe true
    }
}


scenario "stack with one value is not empty", {

    given "an empty stack with one pushed value", {
        stack = new Stack()
        stack.push("bar")
    }

    then "the stack should not be empty", {
        stack.empty.shouldBe false
    }
}

scenario "peek is called", {

    given "a stack containing an item", {
        stack = new Stack()
        push1 = "foo"
        stack.push(push1)
    }

    when "peek is called", {
        peeked = stack.peek()
    }

    then "it should provide the value of the most recent pushed value", {
        peeked.shouldBe("foo")
        peeked.shouldBe "foo"
        peeked.shouldEqual "foo"
        peeked.is "foo"
        peeked.shouldBeEqualTo "foo"
    }

    and

    then "the stack should not be empty", {
        stack.empty.shouldBe false
    }

    and

    then "calling pop should also return the peeked value which is the same as the original pushed value", {
        //ensure(stack.pop()) {
        //  isEqualTo(push1)
        // and
        //isEqualTo(peeked)
        //}
        def popped = stack.pop()

        popped.shouldBe(push1)
        and
        popped.shouldBe(peeked)
    }

    and

    then "the stack should  be empty", {
        stack.empty.shouldBe true
    }
}
