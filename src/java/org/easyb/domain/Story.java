package org.easyb.domain;

import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.StoryBinding;
import org.easyb.listener.ExecutionListener;
import org.easyb.plugin.EasybPlugin;
import org.easyb.plugin.PluginFactory;
import org.easyb.util.BehaviorStepType;
import org.easyb.util.PreProcessorable;

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

        String story = readStory(getFile());
        EasybPlugin activePlugin = new PluginFactory().pluginForStory(story);

        StoryBinding binding = StoryBinding.getBinding(listener, activePlugin);
        GroovyShell g = new GroovyShell(getClassLoader(), binding);
        bindShellVariables(g);

        setBinding(binding);
        activePlugin.beforeStory(binding);
        listener.startStep(new BehaviorStep(BehaviorStepType.EXECUTE, getPhrase()));
        g.evaluate(story);
        listener.stopStep(); // EXEC
        activePlugin.afterStory(binding);

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

        PreProcessorable sharedProcessor = new SharedBehaviorPreProcessor(new NarrativePreProcessor());
        return sharedProcessor.process(output.toString());
    }
}
