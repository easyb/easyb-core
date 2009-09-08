import org.easyb.exception.VerificationException

scenario "Using ensure until timeout", {
  then "Condition passes", {
    ensureUntil(2) {
      "hello".shouldBe "hello"
    }
  }

}

scenario "Another passing verification", {
  then "Condition passes", {

    var = 20
    delayedClosure = {i ->
      Thread.sleep(2000)
      return (i += i)
    }

    var = delayedClosure(20)
    ensureUntil(4) {
      var.shouldBe 40
    }
  }
}

scenario "a failing verification", {
  then "Condition should fail", {

    var = 20
    delayedClosure = {i ->
      Thread.sleep(2000)
      return (i += i)
    }
    try {
      var = delayedClosure(20)
      ensureUntil(4) {
        var.shouldBe 20
      }
    } catch (Throwable tr) {
      tr.shouldBeA VerificationException
    }
  }
}