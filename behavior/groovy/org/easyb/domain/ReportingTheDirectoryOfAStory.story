package org.easyb.domain

import org.easyb.domain.Story

scenario "the story directory should be stored in the Groovy binding",  {
  given "a story to be read from a directory", {
	storyFilePath = "behavior/groovy/org/easyb/bdd/story/stack/EmptyStackStory.groovy"
    story = new Story(new GroovyShellConfiguration(), "a phrase", new File("behavior/groovy/org/easyb/bdd/story/stack/EmptyStackStory.groovy"))
  }		
  when "we execute this story", {
	  story.execute()
  }
  then "the story path is stored in the binding", {
	  story.binding.storyFile.shouldBe storyFilePath
  }
  then "the story name is also stored in the binding", {
	  story.binding.storyFile.name.shouldBe "EmptyStackStory.groovy"
  }
}
