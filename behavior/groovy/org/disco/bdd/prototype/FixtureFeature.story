package org.disco.bdd.prototype


description "this story is fleshing out fixture logic for scenarios"


each "some description" , {
    given "blah", {
        value = 12
    }
    and "given another value", {
        avalue = "test"
    }
}

scenario "one time" , {
   when "value is multiplied by 2", {
       value = value * 2
   }

   then "value should be 24" , {
       value.shouldBe 24
   }
}

scenario "two times", {
    when "value is multiplied by 3", {
       value = value * 3
   }

   then "value should be 24" , {
       value.shouldBe 36
   }

   and "then othe value should still be test", {
       avalue.shouldBe "test"
   }
}