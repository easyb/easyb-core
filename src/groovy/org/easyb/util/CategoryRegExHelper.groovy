package org.easyb.util

import java.util.regex.Matcher

/**
 *
 */
class CategoryRegExHelper {
  def pattern_1 = /^category "(.*)"|\[(.*)\]/
  def pattern_3 = /^category/

  /**
   * does the line start with "category"
   * Note, "//category" won't match
   */
  boolean lineStartsWithCategory(line) {
    return ((line =~ pattern_3).size() > 0)
  }

  String[] getCategories(line) {
    def match = line =~ pattern_1
    if (match.size() > 0) {
      if (match[0][0].startsWith("[")) {
        return handleCategories(match)
      } else if (match[0][0].startsWith("category")) {
        def singlecat = match[0][1].trim()
        return [this.trimQuotes(singlecat)]
      }
    } else {
      return []
    }
  }

  private def handleCategories(match) {
    def tmp = match[0][0]
    def tcats = tmp[1..(tmp.indexOf("]") - 1)].split(",")
    def categories = []
    tcats.each {
      categories << this.trimQuotes(it)
    }
    return categories
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
