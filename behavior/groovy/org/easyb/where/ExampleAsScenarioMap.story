package org.easyb.where

/*
Example tests a map at the story level
 */

numberArray = [12, 8, 20, 199]



before "Before we start running the examples", {
  given "an initial value for counters", {
    println "initial"
    whenCount = 0
    thenCount = 0
    numberTotal = 0
    lengthTotal = 0 
  }
}

scenario "Text '#text' should equal #number", {
  when "we parse '#text' in #stepName", {
    parsedValue = Integer.parseInt(text)
    whenCount ++
  }
  then "it should equal #number", {
    parsedValue.shouldBe number
    thenCount ++
    numberTotal += parsedValue
  }
  where "Number examples with '#text' and #number", [text:["12", "8", "20", "199"], number:numberArray]
}

scenario "Text #text should be size #number", {
  when "we get the length of the #text", {
    length = text.length()
    whenCount ++
  }
  then "it should equal #number", {
    length.shouldBe number
    thenCount ++
    lengthTotal += length
  }
  where "Number examples with #text and #number", [text:["12", "8", "199"], number:[2, 1, 3]]
}


after "should be true after running example data", {
  then "we should have set totals", {
    whenCount.shouldBe 7
    thenCount.shouldBe 7 // change these and errors should print out for this after/then combo
    num = 0
    numberArray.each { num += it }
    numberTotal.shouldBe num
    lengthTotal.shouldBe 6
  }
}

