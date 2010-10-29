package org.easyb.domain;

import org.easyb.util.PreProcessorable;

/**
 *
 */
public class SharedBehaviorPreProcessor implements PreProcessorable {

    private PreProcessorable preprocessor;

    public SharedBehaviorPreProcessor(PreProcessorable preproc) {
        this.preprocessor = preproc;
    }

    public String process(String behavior) {
        return this.preprocessor.process(behavior)
                .replaceAll("shared behavior\\b", "shared_behavior")
                .replaceAll("it behaves as\\b", "it_behaves_as");
    }
}
