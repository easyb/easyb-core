package org.easyb

import org.easyb.listener.ExecutionListener;


public class ExtensionPoint {
  def closure
  def params


  def process(BehaviorStep behaviorStep, Binding binding, ExecutionListener listener) {
    closure(listener, binding, behaviorStep, params)
  }
}