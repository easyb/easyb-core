package org.easyb.domain;

import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.Configuration;
import org.easyb.StoryBinding;
import org.easyb.listener.ExecutionListener;
import org.easyb.plugin.EasybPlugin;
import org.easyb.plugin.PluginFactory;
import org.easyb.util.BehaviorStepType;
import org.easyb.util.CategoryRegExHelper;
import org.easyb.util.PreProcessorable;

import java.io.*;
import java.util.Arrays;

public class Story extends BehaviorBase {
    public Story(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        super(gShellConfig, phrase, file);
    }

    public Story(GroovyShellConfiguration gShellConfig, String phrase, File file, Configuration config) {
        super(gShellConfig, phrase, file, config);
    }

    public String getTypeDescriptor() {
        return "story";
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        File file = getFile();
        String story = readStory(file);
        if (isMemberOfCategory(story, this.getCategories())) {
            BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.STORY, getPhrase());

            listener.startBehavior(this);
            listener.startStep(currentStep);

            EasybPlugin activePlugin = new PluginFactory().pluginForStory(story);

            StoryBinding binding = StoryBinding.getBinding(listener, activePlugin);
            GroovyShell g = new GroovyShell(getClassLoader(), binding);
            bindShellVariables(g);

            setBinding(binding);
            activePlugin.beforeStory(binding);
            listener.startStep(new BehaviorStep(BehaviorStepType.EXECUTE, getPhrase()));

            //Pass in path to original file so it can be used in debuggers
            g.evaluate(story, file.getAbsolutePath());
            listener.stopStep(); // EXEC
            activePlugin.afterStory(binding);

            listener.stopStep();
            listener.stopBehavior(currentStep, this);

            return currentStep;
        } else {
            return null;
        }
    }

    //todo implement this correctly - can processing be broken out at some point?
    protected boolean isMemberOfCategory(String story, String[] categories) {
        if (categories != null) {
            CategoryRegExHelper hlpr = new CategoryRegExHelper();
            String[] lines = story.split("\n");
            Arrays.sort(categories); //TODO do this somewhere else?
            for (int x = 0; x < lines.length; x++) {
                if (hlpr.lineStartsWithCategory(lines[x])) {
                    String[] storycats = hlpr.getCategories(lines[x]);
                    for (int y = 0; y < storycats.length; y++) {
                        //System.out.println("story has these categories " + storycats[y]);
                        if (Arrays.binarySearch(categories, storycats[y]) >= 0) {
                            //value was found
                            //System.out.println("value was found!");
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            return true;
        }
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
