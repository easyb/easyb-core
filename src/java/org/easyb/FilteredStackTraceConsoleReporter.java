package org.easyb;

import org.easyb.listener.ConsoleReporterListener;
import org.easyb.result.Result;

public class FilteredStackTraceConsoleReporter extends ConsoleReporterListener {
    public void gotResult(Result result) {
        super.gotResult(result);
        if (result != null && result.failed()
                && result.cause() != null) {
            StackTraceElement[] stacktrace = result.cause().getStackTrace();
            System.out.println("\t An exception has occurred: ");
            for (int x = 0; x < stacktrace.length; x++) {
                if (lineOkToPrint(stacktrace[x].toString())) {
                    System.out.println("\t\t" + stacktrace[x]);
                }
            }
        }
    }

    private boolean lineOkToPrint(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("org.codehaus.groovy") ||
                trimmed.startsWith("groovy") ||
                trimmed.startsWith("sun.reflect") ||
                trimmed.startsWith("java.lang.reflect") ||
                trimmed.startsWith("org.apache.tools.ant") ||
                trimmed.startsWith("org.codehaus.gant") ||
                trimmed.startsWith("gant.Gant") ||
                trimmed.startsWith("build_gant$")) {
            return false;
        }
        return true;
    }
}
