package org.disco.easyb

import org.disco.easyb.core.delegates.EnsuringDelegate
import org.disco.easyb.core.result.Result
import org.disco.easyb.core.delegates.PlugableDelegate
import org.disco.easyb.SpecificationCategory

class SpecificationBinding {

  
  // TODO change to constants when i break the binding into story and behavior bindings
  public static final String STORY = "story"
  public static final String STORY_SCENARIO = "scenario"
  public static final String STORY_GIVEN = "given"
  public static final String STORY_WHEN = "when"
  public static final String STORY_THEN = "then"
  public static final String BEHAVIOR_IT = "it"
  public static final String AND = "and"

  /**
	 * This method returns a fully initialized Binding object (or context) that 
	 * has definitions for methods such as "it" and "given", which are used
	 * in the context of behaviors (or stories). 
	 */
  static Binding getBinding(listener){
		 
		
		 
  	def binding = new Binding()

    def basicDelegate = basicDelegate()
    def givenDelegate = givenDelegate()

    def beforeIt
	
    binding.scenario = { scenarioDescription, scenarioClosure ->
      listener.gotResult(new Result(scenarioDescription, STORY_SCENARIO, Result.SUCCEEDED))
      scenarioClosure()
    }

    binding.before = { beforeDescription, closure ->
      beforeIt = closure
    }

    def itClosure = { spec, closure, storyPart ->
      closure.delegate = basicDelegate
      
      try{
        if(beforeIt != null){
          beforeIt()
        }
        use(SpecificationCategory){
          closure()
        }
        listener.gotResult(new Result(spec, storyPart, Result.SUCCEEDED))
      }catch(ex){
        listener.gotResult(new Result(spec, storyPart, ex))
      }
    }

    binding.it = { spec, closure ->
    	  itClosure(spec, closure, BEHAVIOR_IT)
    }

    binding.then = {spec, closure ->
    		itClosure(spec, closure, STORY_THEN)
    }
        		  
	binding.when = { whenDescription, closure ->
		closure.delegate = basicDelegate
		closure()
		listener.gotResult(new Result(whenDescription, STORY_WHEN, Result.SUCCEEDED))
	}
	
	binding.given = { givenDescription, closure ->
		closure.delegate = givenDelegate
		closure()
        listener.gotResult(new Result(givenDescription, STORY_GIVEN, Result.SUCCEEDED))
	}
		
	binding.and = {
      listener.gotResult(new Result("", AND, Result.SUCCEEDED))
	}
	  		  
	 return binding
		 
	}

	/**
	 * The easy delegate handles "it", "then", and "when"
	 * Currently, this delegate isn't plug and play.
	 */
	private static basicDelegate(){
		return new EnsuringDelegate()
	}
	/** 
	 * The "given" delegate supports plug-ins, consequently, 
	 * the flex guys are utlized. Currently, there is a DbUnit
	 * "given" plug-in and it is envisioned that there could be 
	 * others like Selenium. 
	 */
	private static givenDelegate(){
		return new PlugableDelegate()
	}
}