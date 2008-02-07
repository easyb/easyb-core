package org.disco.easyb.core.listener

import org.disco.easyb.core.result.Result

interface SpecificationListener {
  // TODO on all the listeners implement the timer functionality again, probably expose a method here to get the executionLength
  void gotResult(Result result)
  boolean hasBehaviorFailures()
  int getTotalBehaviorCount()
  int getSuccessfulBehaviorCount()
  int getFailedBehaviorCount()
  void setSpecificationName(String name)
}