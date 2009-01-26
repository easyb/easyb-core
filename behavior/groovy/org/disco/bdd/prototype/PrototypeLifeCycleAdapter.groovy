package org.disco.bdd.prototype

import org.disco.easyb.ext.StoryLifeCyleAdaptor

/**
 *
 */

class PrototypeLifeCycleAdapter extends StoryLifeCyleAdaptor{

  def beforeScenario(binding) {
     binding._foo_ = "test"
  }

  def afterScenario(binding) {
     
  }


}