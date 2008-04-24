package org.disco.easyb.domain;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyShell;
import org.disco.easyb.BehaviorStep;
import org.disco.easyb.StoryBinding;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.util.BehaviorStepType;

public class Story extends BehaviorBase {
    public Story(String phrase, File file) {
        super(phrase, file);
    }

    public String getTypeDescriptor() {
        return "story";
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.STORY, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);
        new GroovyShell(StoryBinding.getBinding(listener)).evaluate(getFile());
        listener.stopStep();
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }
}
