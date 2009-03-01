package org.easyb.domain;

import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.SpecificationBinding;
import org.easyb.util.BehaviorStepType;
import org.easyb.listener.ExecutionListener;

import java.io.File;
import java.io.IOException;

public class Specification extends BehaviorBase {
    public Specification(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        super(gShellConfig, phrase, file);
    }

    public String getTypeDescriptor() {
        return "specification";
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.SPECIFICATION, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);

        GroovyShell g = new GroovyShell(getClassLoader(), SpecificationBinding.getBinding(listener));
        bindShellVariables(g);

        g.evaluate(getFile());
        listener.stopStep();
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }
}
