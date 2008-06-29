package org.disco.easyb.delegates;

import org.disco.easyb.BehaviorStepStack;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.util.BehaviorStepType;

public class NarrativeDelegate {

    private ExecutionListener listener;
    private BehaviorStepStack stepstack;

    public NarrativeDelegate(ExecutionListener listener, BehaviorStepStack stepstack) {
        this.listener = listener;
        this.stepstack = stepstack;
    }

    public void as_a(String role) {
        this.processType(BehaviorStepType.NARRATIVE_ROLE, role);
    }

    public void i_want(String feature) {
        this.processType(BehaviorStepType.NARRATIVE_FEATURE, feature);
    }

    public void so_that(String benefit) {
        this.processType(BehaviorStepType.NARRATIVE_BENEFIT, benefit);
    }

    public void asA(String role) {
        this.as_a(role);
    }

    public void iWant(String feature) {
        this.i_want(feature);
    }

    public void soThat(String benefit) {
        this.so_that(benefit);
    }

    private void processType(BehaviorStepType type, String text) {
        this.stepstack.startStep(this.listener, type, text);
        this.listener.describeStep(text);
        this.stepstack.stopStep(this.listener);
    }
}
