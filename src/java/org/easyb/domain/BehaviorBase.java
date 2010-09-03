package org.easyb.domain;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.easyb.Configuration;
import org.easyb.listener.BroadcastListener;
import org.easyb.util.TagRegExHelper;

@SuppressWarnings("serial")
public abstract class BehaviorBase implements Behavior, Serializable {
  private String phrase;
  private File file;
  private transient GroovyShellConfiguration gShellConfig;
  private Binding binding;
  private Configuration config;
  protected BroadcastListener listener;

  protected BehaviorBase(GroovyShellConfiguration gShellConfig, String phrase, File file) {
    this.gShellConfig = gShellConfig;
    this.phrase = phrase;
    this.file = file;

    listener = new BroadcastListener();
  }

  protected BehaviorBase(GroovyShellConfiguration gShellConfig, String phrase, File file, Configuration config) {
    this(gShellConfig, phrase, file);
    this.config = config;
  }

  public String[] getTags() {
    if (this.config != null) {
      return this.config.getTags();
    } else {
      return null;
    }
  }

  public String getPhrase() {
    return phrase;
  }

  public File getFile() {
    return file;
  }

  public ClassLoader getClassLoader() {
    return gShellConfig.getClassLoader();
  }

  protected void bindShellVariables(GroovyShell groovyShell) {
    Map<String, Object> shellContextVariables = gShellConfig.getShellContextVariables();
    for (String key : shellContextVariables.keySet()) {
      groovyShell.getContext().setVariable(key, shellContextVariables.get(key));
    }
  }

  @SuppressWarnings("RedundantIfStatement")
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BehaviorBase that = (BehaviorBase) o;

    if (phrase != null ? !phrase.equals(that.phrase) : that.phrase != null) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result;
    result = ( phrase != null ? phrase.hashCode() : 0 );
    result = 31 * result + ( file != null ? file.hashCode() : 0 );
    return result;
  }

  public Binding getBinding() {
    return binding;
  }

  public void setBinding(Binding binding) {
    this.binding = binding;
  }

  //todo implement this correctly - can processing be broken out at some point?
  protected boolean containsTag(String behavior, String[] tags) {
    if (tags != null) {
      TagRegExHelper hlpr = new TagRegExHelper();
      String[] lines = behavior.split("\n");
      Arrays.sort(tags); //TODO do this somewhere else?
      for (int x = 0; x < lines.length; x++) {
        if (hlpr.lineStartsWithTag(lines[x])) {
          String[] behaviorcats = hlpr.getTags(lines[x]);
          for (int y = 0; y < behaviorcats.length; y++) {
            if (Arrays.binarySearch(tags, behaviorcats[y]) >= 0) {
              return true;
            }
          }
        }
      }
      return false;
    } else {
      return true;
    }
  }

  public BroadcastListener getBroadcastListener() {
    return listener;
  }
}
