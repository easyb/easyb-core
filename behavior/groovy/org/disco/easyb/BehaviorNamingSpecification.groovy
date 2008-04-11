package org.disco.easyb

import org.disco.easyb.Specification

//needed for eclipse

it "should identify files ending with *Story.groovy as stories", {
  behavior = BehaviorFactory.createBehavior(new File('EmptyStackStory.groovy'))
  (behavior instanceof Story).shouldBe(true)
  behavior.phrase.shouldBe('empty stack')
}

it "should identify files ending with *.story as stories", {
  behavior = BehaviorFactory.createBehavior(new File('EmptyStack.story'))
  (behavior instanceof Story).shouldBe(true)
  behavior.phrase.shouldBe('empty stack')
}

it "should identify files ending with *Specification.groovy as specifications", {
  behavior = BehaviorFactory.createBehavior(new File('EmptyStackSpecification.groovy'))
  (behavior instanceof Specification).shouldBe(true)
  behavior.phrase.shouldBe('empty stack')
}

it "should identify files ending with *.specification as specifications", {
  behavior = BehaviorFactory.createBehavior(new File('EmptyStack.specification'))
  (behavior instanceof Specification).shouldBe(true)
  behavior.phrase.shouldBe('empty stack')
}

it "should throw an IllegalArgumentException for files not ending in a known format", {
  ensureThrows(IllegalArgumentException.class) {
    BehaviorFactory.createBehavior(new File('NonConforming.groovy'))
  }
}
