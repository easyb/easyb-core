package org.easyb.domain;


import org.easyb.BehaviorStep;
import org.easyb.listener.ExecutionListener;

import java.io.File;
import java.io.IOException;

public interface Behavior {
    String getPhrase();

    File getFile();

    String getTypeDescriptor();

    BehaviorStep execute(ExecutionListener listener) throws IOException;
}
