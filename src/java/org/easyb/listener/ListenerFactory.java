package org.easyb.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * listeners that wish to be instanciated when a new broadcast listener is created need to register themselves here.
 *
 * should this be services? like plugins?
 */
public class ListenerFactory {

  private static List<ListenerBuilder> listenerBuilders = new ArrayList<ListenerBuilder>();

  public static void registerBuilder(ListenerBuilder lb) {
    listenerBuilders.add(lb);
  }

  public static List<ExecutionListener> getActiveList() {
    List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();

    for( ListenerBuilder lb : listenerBuilders ) {
      listeners.add( lb.get() );
    }

    return listeners;
  }
}
