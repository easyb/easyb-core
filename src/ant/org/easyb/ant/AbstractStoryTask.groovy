package org.easyb.ant

import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.Path

public class AbstractStoryTask extends AbstractJavaTask {

  private final Class commandLineClass
  private List targetArgumentList = new LinkedList()
  private List filesets = new ArrayList()
  private String failureProperty


  public AbstractStoryTask(Class commandLineClass, CommandRunner runner) {
    super(runner)
    this.commandLineClass = commandLineClass
  }

  void addTarget(String targetArgument) {
    targetArgumentList.add(targetArgument)
  }

  void addTarget(int index, String dir) {
    targetArgumentList.add(index, dir)
  }

  void setFailureFile(String filename) {
    addTarget("-outfail ${filename}")
  }

  void setInFile(String filename) {
    addTarget("-f ${filename}")
  }

  void setNoExecute(boolean exe) {
    if (exe) {
      addTarget("-ne")
    }
  }

  void setTags(String tags) {
    addTarget("-tags ${tags}")
  }

  String getFailureProperty() {
    return failureProperty
  }

  void setFailureProperty(String failureProperty) {
    this.failureProperty = failureProperty
  }

  void execute() {
    appendAntTaskJar()
    addArgumentsAndRun()
  }

  private void appendAntTaskJar() {
    createClasspath().append(new Path(getProject(), locate()))
  }

  private void addArgumentsAndRun() {

    for (iter in filesets) {

      def ds = iter.getDirectoryScanner(getProject())
      def includedFiles = ds.getIncludedFiles()

      if (includedFiles.length == 0) {
        log("Nothing was found to process. Are your <include> elements defined correctly?")
        return
      }

      for (i in 0..<includedFiles.length) {
        addTarget("\"${ds.getBasedir().toString()}/${includedFiles[i]}\"")
      }
      log("easyb is preparing to process ${includedFiles.length} file(s)")
    }

    commandLine.setClassname(commandLineClass.getName())

    for (iter in targetArgumentList) {
      commandLine.createArgument().setLine(iter)
    }

    if (run() != 0) {
      log("easyb execution FAILED")
      if (getFailureProperty() != null) {
        getProject().setNewProperty(getFailureProperty(), "true");
      }
    } else {
      log("easyb execution passed")
    }
  }


  public void addFilesetTarget(FileSet fileset) {
    filesets.add(fileset)
  }

  private String locate() {
    URL url = getClass().getResource(resourcePathToClassFile())
    if ("jar".equalsIgnoreCase(url.getProtocol())) {
      return getJarFileOnClassPath(url)
    } else {
      return goToClassPathRootDirectory(url)
    }
  }

  private String goToClassPathRootDirectory(URL url) {
    int level = new StringTokenizer(getClass().getName(), ".").countTokens()
    File directory = getFileOnClassPath(url).getParentFile()
    for (i in 0..<(level - 1)) {
      directory = directory.getParentFile()
    }
    return directory.getAbsolutePath()
  }

  private String getJarFileOnClassPath(URL url) {
    String file = url.getFile()
    int index = file.indexOf("!")
    if (index == -1) {
      throw new IllegalArgumentException(url.toExternalForm() + " does not have '!' for a Jar URL")
    }
    File fileObject = fromUri(file.substring(0, index))
    return fileObject.getAbsolutePath()
  }

  private File getFileOnClassPath(URL url) {
    return fromUri(url.getFile())
  }

  private File fromUri(String fileUri) {
    try {
      return new File(new URI(fileUri))
    } catch (URISyntaxException e) {
      throw new RuntimeException("Couldn't locate file:" + fileUri, e)
    }
  }

  private String resourcePathToClassFile() {
    return "/" + getClass().getName().replace('.', '/') + ".class"
  }
}
