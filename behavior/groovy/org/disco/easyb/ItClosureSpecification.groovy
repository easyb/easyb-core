package org.disco.easyb

import org.disco.easyb.listener.BehaviorListener
import org.disco.easyb.listener.DefaultListener


before "obtain a reference to the binding object", {
  BehaviorListener behaviorListener = new DefaultListener()
  binding = BehaviorBinding.getBinding(behaviorListener)
}

it "should have an it method which isn't null", {
  itmethod = binding.it
  itmethod.shouldNotBe(null)
}

it "should accept 2 parameters", {
  itmethod = binding.it
  itmethod.getMaximumNumberOfParameters().shouldBe(2)
}

it "should accept a String and a closure and not throw any exceptions", {
  itmethod = binding.it
  try {
    itmethod("test") {
      1.shouldBe(1)
    }
  } catch (Throwable thr) {
    fail("apparently the it closure isn't working")
  }
}

it "should create a delegate for the passed in closure", {
  itmethod = binding.it
  xclos = {
    1.shouldBe(1)
  }
  try {
    itmethod("blah", xclos)
    xclos.delegate.class.name.shouldBe("org.disco.easyb.core.delegates.EnsuringDelegate")
  } catch (Throwable thr) {
    fail("apparently the it closure isn't working")
  }
}