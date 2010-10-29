package org.easyb.listener;


/**
 * a class that implements this knows how to create a new ExecutionListener. It is important because of
 * parallelization.  
 */
public interface ListenerBuilder {
  ExecutionListener get();
}
