package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.result.ReportingTag;
import org.easyb.result.Result;

import java.util.ArrayList;
import java.util.List;


public class BroadcastListener implements ExecutionListener {
  private final List<ExecutionListener> coreListeners;

  private ResultsCollector resultsCollector;
  private FailureDetector failureDetected;

  public BroadcastListener() {
    this.coreListeners = new ArrayList<ExecutionListener>();
    resultsCollector = new ResultsCollector();
    failureDetected = new FailureDetector();

    coreListeners.add(resultsCollector);
    coreListeners.add(failureDetected);
    coreListeners.add(new ConsoleReporterListener());
  }

  public List<ExecutionListener> getListeners() {
      List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();
      listeners.addAll(coreListeners);
      listeners.addAll(ListenerFactory.getActiveList());
      return listeners;
  }

  public ResultsCollector getResultsCollector() {
    return resultsCollector;
  }

  /**
   * allows us to get a specific listener from this list
   *
   * @param clazz - the class of the listener
   * @return null if not found or the found listener
   */
  public ExecutionListener getTypedListener(Class clazz) {
    for (ExecutionListener el : getListeners()) {
      if (el.getClass() == clazz) {
        return el;
      }
    }

    return null;
  }

  public void startBehavior(Behavior behavior) {
    for (ExecutionListener listener : getListeners()) {
      listener.startBehavior(behavior);
    }
  }

  public void stopBehavior(BehaviorStep step, Behavior behavior) {
    for (ExecutionListener listener : getListeners()) {
      listener.stopBehavior(step, behavior);
    }
  }

  public void tag(ReportingTag tag) {
    for (ExecutionListener listener : getListeners()) {
      listener.tag(tag);
    }
  }

  public void startStep(BehaviorStep step) {
    for (ExecutionListener listener : getListeners()) {
      listener.startStep(step);
    }
  }

  public void describeStep(String description) {
    for (ExecutionListener listener : getListeners()) {
      listener.describeStep(description);
    }
  }

  public void completeTesting() {
    for (ExecutionListener listener : getListeners()) {
      listener.completeTesting();
    }
  }

  public void stopStep() {
    for (ExecutionListener listener : getListeners()) {
      listener.stopStep();
    }
  }

  public void gotResult(Result result) {
    for (ExecutionListener listener : getListeners()) {
      listener.gotResult(result);
    }
  }
}
