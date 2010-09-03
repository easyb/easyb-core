package org.easyb.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * listeners that wish to be instanciated when a new broadcast listener is created need to register themselves here.
 *
 * should this be services? like plugins?
 */
public class ListenerFactory {
  private static List<ListenerBuilder> listenerBuilders = new ArrayList<ListenerBuilder>();
  private static ConcurrentLinkedQueue<ExecutionListener> proxies = new ConcurrentLinkedQueue<ExecutionListener>();

  public static void registerBuilder(ListenerBuilder lb) {
    listenerBuilders.add(new ListenerBuilderProxy(lb));
  }

  public static List<ExecutionListener> getActiveList() {
    List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();

    for( ListenerBuilder lb : listenerBuilders ) {
      listeners.add( lb.get() );
    }

    return listeners;
  }

  /**
   * this should be called when all testing is complete to notify any/all execution listeners that all testing
   * is now complete and we are about to end the session.
   */
  public static void notifyTestingCompleted() {
    for( ExecutionListener el : proxies ) {
      el.completeTesting();
    }
  }

  static class ListenerBuilderProxy implements ListenerBuilder {
    private ListenerBuilder proxying;

    ListenerBuilderProxy(ListenerBuilder proxying) {
      this.proxying = proxying;
    }

    public ExecutionListener get() {
      ExecutionListener el = proxying.get();

      proxies.add(el); // keep track so when we complete, we can tell them all

      return el;
    }
  }
}
