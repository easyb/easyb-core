package org.easyb.util
/**
 *
 */

class BehaviorFileToPathsBuilder {

  String[] buildPaths(final String filepath, final String[] existingPaths) {
    def paths = []

    if (existingPaths) {
      existingPaths.each {
        paths << it
      }
    }
    try {
      new File(filepath.trim()).eachLine {line ->
        paths << line
      }
    } catch (FileNotFoundException e) {
      //safely ignore this exception and move on
      System.err.println "Apparently, the -f option was provided; however, the file ${filepath} can't " +
              "be found (note, the file failure-files.txt is an easyb default). " +
              "Nevertheless, any other behaviors provided will be run."
    }
    return paths as String[]
  }
}