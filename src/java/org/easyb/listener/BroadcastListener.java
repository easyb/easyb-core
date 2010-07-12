package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.result.ReportingTag;
import org.easyb.result.Result;

import java.util.List;


public class BroadcastListener implements ExecutionListener {
  private final List<ExecutionListener> listeners;

  private ResultsCollector resultsCollector;
  private FailureDetector failureDetected;

  public BroadcastListener() {
    this.listeners = ListenerFactory.getActiveList();
    resultsCollector = new ResultsCollector();
    failureDetected = new FailureDetector();

    listeners.add(resultsCollector);
    listeners.add(failureDetected);

    listeners.add(new ConsoleReporterListener());
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
    for (ExecutionListener el : listeners) {
      if (el.getClass() == clazz) {
        return el;
      }
    }

    return null;
  }

  public void startBehavior(Behavior behavior) {
    for (ExecutionListener listener : listeners) {
      listener.startBehavior(behavior);
    }
  }

  public void stopBehavior(BehaviorStep step, Behavior behavior) {
    for (ExecutionListener listener : listeners) {
      listener.stopBehavior(step, behavior);
    }
  }

  public void tag(ReportingTag tag) {
    for (ExecutionListener listener : listeners) {
      listener.tag(tag);
    }
  }

  public void startStep(BehaviorStep step) {
    for (ExecutionListener listener : listeners) {
      listener.startStep(step);
    }
  }

  public void describeStep(String description) {
    for (ExecutionListener listener : listeners) {
      listener.describeStep(description);
    }
  }

  public void completeTesting() {
    for (ExecutionListener listener : listeners) {
      listener.completeTesting();
    }
  }

  public void stopStep() {
    for (ExecutionListener listener : listeners) {
      listener.stopStep();
    }
  }

  public void gotResult(Result result) {
    for (ExecutionListener listener : listeners) {
      listener.gotResult(result);
    }
  }
}
