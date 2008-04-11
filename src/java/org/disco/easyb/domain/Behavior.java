package org.disco.easyb.domain;

import java.io.File;
import java.io.IOException;

import org.disco.easyb.listener.StepListener;
import org.disco.easyb.BehaviorStep;

public interface Behavior {
    String getPhrase();

    File getFile();

    String getTypeDescriptor();

    BehaviorStep execute(StepListener listener) throws IOException;
}
