package org.easyb.plugin


public interface ExampleDataParser {
  /**
   * processData if the data is of a type that this plugin should parse, then
   * it needs to decode it, calling the closure for each iteration with a map
   * containing fields and values to add to the binding.  The parser can add new elements
   * to the binding for the duration of its operation, but must remove them (for consistency)
   * on completion. The StoryProcessing will take care of removing the excess properties
   * in the binding that occur because of these plugins.
   *
   * @param data - the data object passed in the Story
   * @param closure - a closure that we can iterate over for each record. It expects one parameter { map -> }
   * @param binding - the current binding
   *
   * @return true if we processed it, false if not
   */
  boolean processData(data, closure, binding);
}