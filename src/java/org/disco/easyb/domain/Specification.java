package org.disco.easyb.domain;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyShell;
import org.disco.easyb.BehaviorStep;
import org.disco.easyb.SpecificationBinding;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.util.BehaviorStepType;

public class Specification extends BehaviorBase {
    public Specification(String phrase, File file) {
        super(phrase, file);
    }

    public String getTypeDescriptor() {
        return "specification";
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.SPECIFICATION, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);
        new GroovyShell(SpecificationBinding.getBinding(listener)).evaluate(getFile());
        listener.stopStep();
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }
}
