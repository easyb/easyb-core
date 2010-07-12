package org.easyb.where

/*
Example tests a map at the story level
 */

numberArray = [12, 8, 20, 199]

examples "Number examples with '#{text}' and #number", {
  text = ["12", "8", "20", "199"]
  number = story.numberArray
}

before "Before we start running the examples", {
  given "an initial value for counters", {
    println "initial"
    whenCount = 0
    thenCount = 0
    numberTotal = 0
  }
}

scenario "Text '#{text}' should equal #number", {
  when "we parse #text", {
    parsedValue = Integer.parseInt(text)
    whenCount ++
  }
  then "it should equal #number", {
    parsedValue.shouldBe number
    thenCount ++
    numberTotal += parsedValue
  }
}

after "should be true after running example data", {
  then "we should have set totals", {
    println "thenCount is ${thenCount}"
    whenCount.shouldBe 4
    thenCount.shouldBe 4 // change these and errors should print out for this after/then combo
    num = 0
    numberArray.each { num += it }
    numberTotal.shouldBe num
  }
}

