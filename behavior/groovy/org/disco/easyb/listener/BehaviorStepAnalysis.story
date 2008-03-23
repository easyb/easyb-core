package org.disco.easyb.listner

import org.disco.easyb.listener.DefaultListener
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

description "this is a story for working on the behavior step class along with the listener stuff"

scenario "listner should expose previous step", {
	
	given "a listener instance", {
		listener = new DefaultListener()
	}
	
	when "an event is recorded and stop has been called", {
		listener.startStep(BehaviorStepType.THEN, "success then step")
	    listener.gotResult(new Result(Result.SUCCEEDED))
	    listener.stopStep()
	}
	
	then "the listener should be able to indicate the last step was a THEN", {
		listener.getCurrentStep().name.shouldBe "easyb-genesis"
		and
		listener.getPreviousStep().stepType.type().shouldBe "then"
	}
	
}