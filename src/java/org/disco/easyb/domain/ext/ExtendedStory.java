package org.disco.easyb.domain.ext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.disco.easyb.BehaviorStep;
import org.disco.easyb.domain.GroovyShellConfiguration;
import org.disco.easyb.domain.Story;
import org.disco.easyb.ext.StoryLifeCycleBinding;
import org.disco.easyb.ext.StoryLifeCyleAdaptor;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.util.BehaviorStepType;

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
