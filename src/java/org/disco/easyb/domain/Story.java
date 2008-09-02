package org.disco.easyb.domain;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import groovy.lang.GroovyShell;
import org.disco.easyb.BehaviorStep;
import org.disco.easyb.StoryBinding;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.util.BehaviorStepType;

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

    private String readStory(File story) throws IOException {
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
