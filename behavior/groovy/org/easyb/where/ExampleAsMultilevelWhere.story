package org.easyb.where
/*
Example tests a map at the story level
*/

numberArray = [12, 8, 20, 199]

where "we are using sample data at a global level", [number: numberArray], {
  before "Before we start running the examples", {
    given "an initial value for counters", {
      println "initial"
      whenCount = 0
      thenCount = 0
      numberTotal = 0
    }
  }

  where "Multipliers should be", {
    multiplier = [1, 2, 3]
  }, {
    scenario "Number is #number and multiplier is #multiplier and total is #{number * multiplier}", {
      when "we multiply #number by #multiplier", {
        whenCount++
        num = number * multiplier
      }
      then "our calculation (#num) should equal #{number * multiplier}", {
        num.shouldBeGreaterThan 0
        numberTotal += num
        thenCount++
      }
    }
  }


  after "should be true after running example data", {
    then "we should have set totals", {
      whenCount.shouldBe 12
      thenCount.shouldBe 12
      num = 0
      numberArray.each { n ->
        num = num + (n + (2 * n) + (3 * n))
      }

      num.shouldBe numberTotal
    }
  }

}