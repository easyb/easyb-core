package org.disco.easyb.ext


abstract class StoryLifeCyleAdaptor {

  def beforeScenario(binding) {}
  def afterScenario(binding) {}

  def beforeGiven(binding){}
  def afterGiven(binding){}

  def beforeWhen(binding){}
  def afterWhen(binding){}

  def beforeThen(binding){}
  def afterThen(binding){}


  def beforeStory(binding){}
  def afterStory(binding){}

}
