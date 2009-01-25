package org.disco.easyb.domain;

import org.disco.easyb.BehaviorStep;
import org.disco.easyb.listener.ExecutionListener;

import java.io.File;
import java.io.IOException;

public interface Behavior {
    String getPhrase();

    File getFile();

    String getTypeDescriptor();

    BehaviorStep execute(ExecutionListener listener) throws IOException;
}
