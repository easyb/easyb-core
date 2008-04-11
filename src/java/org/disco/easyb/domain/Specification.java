package org.disco.easyb.domain;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyShell;
import org.disco.easyb.BehaviorStep;
import org.disco.easyb.SpecificationBinding;
import org.disco.easyb.listener.StepListener;
import org.disco.easyb.util.BehaviorStepType;

public class Specification extends BehaviorBase {
    public Specification(String phrase, File file) {
        super(phrase, file);
    }

    public String getTypeDescriptor() {
        return "specification";
    }

    public BehaviorStep execute(StepListener listener) throws IOException {
        BehaviorStep currentStep = listener.startStep(BehaviorStepType.SPECIFICATION, getPhrase());
        new GroovyShell(SpecificationBinding.getBinding(listener)).evaluate(getFile());
        listener.stopStep();

        return currentStep;
    }
}
