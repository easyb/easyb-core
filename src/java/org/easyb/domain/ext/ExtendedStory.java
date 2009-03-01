package org.easyb.domain.ext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.domain.GroovyShellConfiguration;
import org.easyb.domain.Story;
import org.easyb.ext.StoryLifeCycleBinding;
import org.easyb.ext.StoryLifeCyleAdaptor;
import org.easyb.listener.ExecutionListener;
import org.easyb.util.BehaviorStepType;

import java.io.File;
import java.io.IOException;

public class ExtendedStory extends Story {
    private StoryLifeCyleAdaptor adaptor;

    public ExtendedStory(GroovyShellConfiguration gShellConfig, String phrase, File file,
                         StoryLifeCyleAdaptor adaptor) {
        super(gShellConfig, phrase, file);
        this.adaptor = adaptor;
    }


    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.STORY, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);

        Binding gBinding = StoryLifeCycleBinding.getBinding(listener, this.adaptor);
        GroovyShell g = new GroovyShell(getClassLoader(), gBinding);
        bindShellVariables(g);

        //add hook here for before/after story?
        this.adaptor.beforeStory(gBinding);

        g.evaluate(readStory(getFile()));

        this.adaptor.afterStory(gBinding);

        listener.stopStep();
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }


}
