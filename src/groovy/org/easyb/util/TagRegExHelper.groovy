package org.easyb.util

/**
 *
 */
class TagRegExHelper {
  def pattern_1 = /^tag "(.*)"|\[(.*)\]/
  def pattern_3 = /^tag/

  /**
   * does the line start with "category"
   * Note, "//category" won't match
   */
  boolean lineStartsWithTag(line) {
    return ((line =~ pattern_3).size() > 0)
  }

  String[] getTags(line) {
    def match = line =~ pattern_1
    if (match.size() > 0) {
      if (match[0][0].startsWith("[")) {
        return handleTags(match)
      } else if (match[0][0].startsWith("tag")) {
        def singletag = match[0][1].trim()
        return [this.trimQuotes(singletag)]
      }
    } else {
      return []
    }
  }

  private def handleTags(match) {
    def tmp = match[0][0]
    def itags = tmp[1..(tmp.indexOf("]") - 1)].split(",")
    def tags = []
    itags.each {
      tags << this.trimQuotes(it)
    }
    return tags
  }

  private def trimQuotes(value) {
    if (!value) {
      return value
    } else {
      value = value.trim()
      if (value.startsWith("\"") && value.endsWith("\"")) {
        return value.substring(1, value.length() - 1)
      } else {
        return value
      }
    }
  }

}
