package org.disco.easyb.core.listener;

import org.disco.easyb.core.result.Result

class DefaultListener implements SpecificationListener{
//	 TODO encapsulate failures and successes so we can prevent mutable
	  def failures = []
	  def successes = []
	  def results = []

	  def specName;

	  private int methodsVerified = 0;

	  boolean hasBehaviorFailures() {
	      return !failures.isEmpty();
	  }

	  int getTotalBehaviorCount() {
	    return methodsVerified
	  }

	  int getSuccessfulBehaviorCount() {
	    return successes.size()
	  }

	  int getFailedBehaviorCount() {
	    return failures.size()
	  }

	  public void gotResult(Result result) {
		results << result
	    methodsVerified++
	      if (result.failed()) {
	    	//there is a better way to do this.
	    	result.source = this.specName
	        failures << result
	      }else{
	        successes << result
	      }
	  }
	  
	  public void setSpecificationName(String name){
		  this.specName = name
	  }
  
}