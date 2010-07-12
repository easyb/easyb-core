package org.easyb.domain;


import groovy.lang.Binding;
import org.easyb.BehaviorStep;
import org.easyb.listener.BroadcastListener;

import java.io.File;
import java.io.IOException;

public interface Behavior {
  String getPhrase();

  File getFile();

  String getTypeDescriptor();

  Binding getBinding();

  BroadcastListener getBroadcastListener();

  BehaviorStep execute() throws IOException;
}
