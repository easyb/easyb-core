package org.disco.easyb.ant

import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.ArrayList
import java.util.LinkedList
import java.util.List
import java.util.StringTokenizer

import org.apache.tools.ant.DirectoryScanner
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

    public void addTarget(String targetArgument) {
        targetArgumentList.add(targetArgument)
    }

    public void addTarget(int index, String dir) {
        targetArgumentList.add(index, dir)
    }

    public String getFailureProperty() {
      return failureProperty;
    }

    public void setFailureProperty(String failureProperty) {
      this.failureProperty = failureProperty;
    }


    public void execute() {
        appendAntTaskJar()
        addArgumentsAndRun()
    }

    private void appendAntTaskJar() {
        createClasspath().append(new Path(getProject(), locate()))
    }

    private void addArgumentsAndRun() {

        for (iter in filesets) {
          FileSet fileset = (FileSet)iter

          DirectoryScanner ds = fileset.getDirectoryScanner(getProject())
          String[] includedFiles = ds.getIncludedFiles()

          for(i in 0..<includedFiles.length) {
            addTarget(ds.getBasedir().toString() + "/" + includedFiles[i])
          }
          log("easyb is preparing to process ${includedFiles.length} file(s)")
        }

        commandLine.setClassname(commandLineClass.getName())

        for (iter in targetArgumentList) {
          String className = iter.toString()
          commandLine.createArgument().setLine(className)
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
