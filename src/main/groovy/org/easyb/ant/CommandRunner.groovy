package org.easyb.ant

import org.apache.tools.ant.Task

public interface CommandRunner {
    int fork(Task task, String[] commandline)
}
