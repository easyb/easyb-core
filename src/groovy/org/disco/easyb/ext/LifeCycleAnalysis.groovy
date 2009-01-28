package org.disco.easyb.ext
/**
 *
 */

class LifeCycleAnalysis {

  def lifeCycleClass
  def story

  public LifeCycleAnalysis(File story) {
    this.story = story
  }

  /**
   *
   */
  def getLifeCycleClass() {
    if (lifeCycleClass != null) {
      return (StoryLifeCyleAdaptor) Class.forName(lifeCycleClass).newInstance();
    } else {
      return null
    }
  }


  /**
  * 
   */
  boolean isUsingLifeCycle() {
    lifeCycleClass = null

    if (this.story.exists() && this.story.isFile()) {
      def lines = story.readLines()
      for (line in lines) {
        def matcher = line =~ ~/using (.*)/
        if (matcher.matches()) {
          lifeCycleClass = matcher.group(1)
          break;
        } else if (line.startsWith("scenario")) {
          break;
        }
      }
    }
    return (lifeCycleClass != null ? true : false)
  }
}