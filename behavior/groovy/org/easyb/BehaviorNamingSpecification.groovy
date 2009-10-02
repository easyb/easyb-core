package org.easyb

import org.easyb.domain.Specification
import org.easyb.domain.Story
import org.easyb.domain.BehaviorFactory

//needed for eclipse

File prefix = new File("behavior/groovy/org/easyb")

it "should identify files ending with *Story.groovy as stories", {
    behavior = BehaviorFactory.createBehavior(new File(prefix, 'EnsuringHasStory.groovy'))
    (behavior instanceof Story).shouldBe(true)
    behavior.phrase.shouldBe('ensuring has')
}

it "should identify files ending with *.story as stories", {
    behavior = BehaviorFactory.createBehavior(new File(prefix, 'SharedBehaviors.story'))
    (behavior instanceof Story).shouldBe(true)
    behavior.phrase.shouldBe('shared behaviors')
}

it "should identify files ending with *Specification.groovy as specifications", {
    behavior = BehaviorFactory.createBehavior(new File(prefix, 'ItClosureSpecification.groovy'))
    (behavior instanceof Specification).shouldBe(true)
    behavior.phrase.shouldBe('it closure')
}

it "should identify files ending with *.specification as specifications", {
    behavior = BehaviorFactory.createBehavior(new File(prefix, 'WhenStartingBehaviorStep.specification'))
    (behavior instanceof Specification).shouldBe(true)
    behavior.phrase.shouldBe('when starting behavior step')
}

it "should throw an IllegalArgumentException for files not ending in a known format", {
    ensureThrows(IllegalArgumentException.class) {
        BehaviorFactory.createBehavior(new File(prefix, 'Noname.groovy'))
    }
}
