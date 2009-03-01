package org.easyb.domain;

import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.StoryBinding;
import org.easyb.util.BehaviorStepType;
import org.easyb.listener.ExecutionListener;

import java.io.*;

public class Story extends BehaviorBase {
    public Story(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        super(gShellConfig, phrase, file);
    }

    public String getTypeDescriptor() {
        return "story";
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.STORY, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);

        GroovyShell g = new GroovyShell(getClassLoader(), StoryBinding.getBinding(listener));
        bindShellVariables(g);

        g.evaluate(readStory(getFile()));
        listener.stopStep();
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }

    protected String readStory(File story) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        InputStream input = new BufferedInputStream(new FileInputStream(story));
        int character;
        while ((character = input.read()) != -1) {
            output.write(character);
        }
        input.close();

        NarrativePreProcessor preProcessor = new NarrativePreProcessor();
        return preProcessor.process(output.toString());
    }
}
