package org.easyb.bdd.story.stack

import org.easyb.bdd.Stack

scenario "pop returns last item", {
    given "a stack with two values", {
        stack = new Stack()
        push1 = "foo"
        push2 = "bar"
        stack.push(push1)
        stack.push(push2)
    }

    when "pop is called", {
        popVal = stack.pop()
    }

    then "the last item pushed should be returned", {
        popVal.shouldBe push2
    }

    and

    then "the stack should not be empty", {
        stack.empty.shouldBe false
    }
}

