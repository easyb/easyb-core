import org.easyb.exception.VerificationException

scenario "easyb should support customized error messages", {
    given "some value", {
        var = "blah"
    }
    then "you should be able to specify an error message in your should call", {
        try{
            var.shouldBe "bah", "I was expecting bah"
        }catch(VerificationException e){
            e.getMessage().shouldBe """"I was expecting bah", expected bah but was blah"""
        }
    }
    and "normal shoulds should still work", {
        try{
            var.shouldBe "bah"
        }catch(VerificationException e){
            e.getMessage().shouldBe "expected bah but was blah"
        }
    }
}