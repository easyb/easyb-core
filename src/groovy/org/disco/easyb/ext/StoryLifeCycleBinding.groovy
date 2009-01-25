package org.disco.easyb.ext

import org.disco.easyb.StoryBinding

class StoryLifeCycleBinding {
  /**
   *  TODO: add in other lifecycle events -- given, when, then, etc
   */
  static Binding getBinding(listener, adaptor) {
    def binding = new StoryBinding(listener)

    binding.scenario = { description, closure = binding.story.pendingClosure ->
      adaptor.beforeScenario(binding)
      binding.story.scenario(description, closure)
      adaptor.afterScenario(binding)
    }
    return binding
  }

}
