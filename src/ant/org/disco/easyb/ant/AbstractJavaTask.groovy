package org.disco.easyb.ant

import org.apache.tools.ant.Task
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.types.CommandlineJava
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.types.Reference
import org.apache.tools.ant.types.Commandline

abstract public class AbstractJavaTask extends Task {

  protected CommandlineJava commandLine = new CommandlineJava()
  private CommandRunner runner

  public AbstractJavaTask(CommandRunner commandRunner) {
    this.runner = commandRunner
  }

  protected int run() throws BuildException {
    return runner.fork(this, commandLine.getCommandline())
  }

  public Path createClasspath() {
    return commandLine.createClasspath(getProject()).createPath()
  }

  public void setClassPath(Path path) {
    createClasspath().append(path)
  }

  public void setClasspathRef(Reference r) {
    createClasspath().setRefid(r)
  }

  public Commandline.Argument createJvmarg() {
    return commandLine.createVmArgument()
  }

  public void setMaxmemory(int megabyte) {
    createJvmarg().setLine("-Xmx" + megabyte + "m")
  }
}
