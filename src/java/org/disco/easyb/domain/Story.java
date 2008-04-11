package org.disco.easyb.domain;

import java.io.File;
import java.io.IOException;

import org.disco.easyb.util.BehaviorStepType;
import org.disco.easyb.StoryBinding;
import org.disco.easyb.BehaviorStep;
import org.disco.easyb.listener.StepListener;
import groovy.lang.GroovyShell;

public class Story extends BehaviorBase {
    public Story(String phrase, File file) {
        super(phrase, file);
    }

    public String getTypeDescriptor() {
        return "story";
    }

    public BehaviorStep execute(StepListener listener) throws IOException {
        BehaviorStep currentStep = listener.startStep(BehaviorStepType.STORY, getPhrase());
        new GroovyShell(StoryBinding.getBinding(listener)).evaluate(getFile());
        listener.stopStep();

        return currentStep;
    }
}
