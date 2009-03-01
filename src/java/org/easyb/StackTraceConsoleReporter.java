package org.easyb;


import org.easyb.result.Result;

public class StackTraceConsoleReporter extends ConsoleReporter {

    public void gotResult(Result result) {
        super.gotResult(result);

        if (result != null && result.failed()
                && result.cause() != null) {
            StackTraceElement[] stacktrace = result.cause().getStackTrace();
            System.out.println("\t An exception has occurred: ");
            for (int x = 0; x < stacktrace.length; x++) {
                System.out.println("\t\t" + stacktrace[x]);
            }
        }

    }
}
