package org.disco.bdd.issues

import org.disco.bdd.issues.Issue14Stack
scenario "Groovy wraps exceptions", {
	
	given "an empty stack",{
		stack = new Issue14Stack()
	}

	when "null is pushed", {
		pushnull = {
			stack.push(null)
		}
	}

	then "an exception should be thrown", {
		ensureThrows(IllegalArgumentException.class){
			pushnull()
		}
	}

	and

	then "the stack should still be empty", {
		stack.empty.shouldBe true
	}
	
}