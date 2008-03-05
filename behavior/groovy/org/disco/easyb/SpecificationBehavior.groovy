package org.disco.easyb //needed for eclipse

import org.disco.easyb.Specification

it "should identify files ending with *Story.groovy as stories", {
  specification = new Specification(new File('EmptyStackStory.groovy'))
  specification.isStory().shouldBe(true)
  specification.phrase.shouldBe('empty stack')
}

it "should identify files ending with *.story as stories", {
  specification = new Specification(new File('EmptyStack.story'))
  specification.isStory().shouldBe(true)
  specification.phrase.shouldBe('empty stack')
}

it "should identify other files as behaviors", {
  specification = new Specification(new File('EmptyStack.groovy'))
  specification.isStory().shouldBe(false)
  specification.phrase.shouldBe('empty stack')
}
