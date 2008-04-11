package org.disco.easyb.listener;

import org.disco.easyb.result.Result;

public class FailureDetector extends ExecutionListenerAdaptor {
    private boolean failures = false;

    public boolean failuresDetected() {
        return failures;
    }

    public void gotResult(Result result) {
        if (result.failed()) {
            failures = true;
        }
    }
}
