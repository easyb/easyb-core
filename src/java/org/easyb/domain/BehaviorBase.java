package org.easyb.domain;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import groovy.lang.Script;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

public abstract class BehaviorBase implements Behavior, Serializable {
    private String phrase;
    private File file;
    private transient GroovyShellConfiguration gShellConfig;
    private Binding binding;

    protected BehaviorBase(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        this.gShellConfig = gShellConfig;
        this.phrase = phrase;
        this.file = file;
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
        result = (phrase != null ? phrase.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }

    public Binding getBinding() {
      return binding;
    }

    public void setBinding(Binding binding) {
      this.binding = binding;
    }
}
