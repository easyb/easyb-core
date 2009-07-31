package org.easyb.domain;


import org.easyb.BehaviorStep;
import org.easyb.listener.ExecutionListener;

import java.io.File;
import java.io.IOException;

import groovy.lang.Binding;

public interface Behavior {
    String getPhrase();

    File getFile();

    String getTypeDescriptor();

    Binding getBinding();

    BehaviorStep execute(ExecutionListener listener) throws IOException;
}
