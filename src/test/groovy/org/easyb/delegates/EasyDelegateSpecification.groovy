package org.easyb.delegates

import org.easyb.SpecificationBinding
import org.easyb.listener.ExecutionListener
import org.easyb.listener.ResultsCollector

before "obtain a reference to the binding object", {
    ExecutionListener behaviorListener = new ResultsCollector()
    itbehave = SpecificationBinding.getBinding(behaviorListener).it
}

it "should support a simple ensure call", {
    itbehave("ensure equality") {
        1.shouldBe(1)
    }
}

it "should support a catching an exception via ensureThrows call", {
    itbehave("ensureThrows exception") {
        ensureThrows(RuntimeException.class) {
            String tst = null
            tst.toUpperCase()
        }
    }
}

it "should support chaining in the ensure call", {

    itbehave("chained ensure calls") {

        String value = "test"
        //value.isA(String)
        ensure(value) {isAString}
        ensure(value) {
            isAString
            and
            isNotAnInteger
        }

        ensure(value) {
            isAString
            and
            isNotNull
            and
            isEqualTo("testing")
        }
    }
}

it "should support String variations in the ensure call", {
    itbehave("ensure with Strings") {
        String value = "Test"
        ensure(value) {startsWith("Te")}
        ensure(value) {endsWith("st")}
        ensure(value) {contains("e")}
    }
}

it "should support collection searching in the ensure call", {
    itbehave("ensure with collection") {
        values = ["1", "3", "2", "test"]
        ensure("2") {isContainedIn(values)}
    }
}


