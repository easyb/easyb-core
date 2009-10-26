package org.easyb.domain;

import org.easyb.BehaviorStep;
import org.easyb.StoryBinding;
import org.easyb.plugin.EasybPlugin;
import org.easyb.plugin.PluginFactory;
import org.easyb.plugin.NullPlugin;
import org.easyb.util.BehaviorStepType;
import org.easyb.util.NonExecutableStoryKeywords;
import org.easyb.listener.ExecutionListener;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyShell;

/**
 *
 */
public class NonExecutableStory extends Story {
    
    public NonExecutableStory(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        super(gShellConfig, phrase, file);
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.STORY, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);

        File file = getFile();
        String story = readStory(file);

        EasybPlugin activePlugin = new NullPlugin();

        StoryBinding binding = StoryBinding.getBinding(listener, activePlugin);
        //override binding object
        binding.setStory(new NonExecutableStoryKeywords(listener));

        GroovyShell g = new GroovyShell(getClassLoader(), binding);
        bindShellVariables(g);

        setBinding(binding);
        activePlugin.beforeStory(binding);
        listener.startStep(new BehaviorStep(BehaviorStepType.EXECUTE, getPhrase()));

        //Pass in path to original file so it can be used in debuggers
        g.evaluate(story,file.getAbsolutePath());
        listener.stopStep(); // EXEC
        activePlugin.afterStory(binding);

        listener.stopStep();
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }
}
