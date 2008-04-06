package org.disco.bdd.issues

import org.disco.easyb.exception.VerificationException



scenario "should not should work with null values", {

    given "a null value", {
        val = null
    }
    
    then "verifying it isn't null should work", {
        try{
            val.shouldNotBe null
        }catch (VerificationException e){
           e.message.shouldBe "expected values to differ but both were null"
        }
    }

}