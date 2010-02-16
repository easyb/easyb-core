package org.easyb.domain;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import groovy.lang.Script;
import org.easyb.Configuration;
import org.easyb.util.CategoryRegExHelper;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public abstract class BehaviorBase implements Behavior, Serializable {
    private String phrase;
    private File file;
    private transient GroovyShellConfiguration gShellConfig;
    private Binding binding;
    private Configuration config;

    protected BehaviorBase(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        this.gShellConfig = gShellConfig;
        this.phrase = phrase;
        this.file = file;
    }

    protected BehaviorBase(GroovyShellConfiguration gShellConfig, String phrase, File file, Configuration config) {
        this(gShellConfig, phrase, file);
        this.config = config;
    }

    public String[] getCategories() {
        if (this.config != null) {
            return this.config.getCategories();
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

    //todo implement this correctly - can processing be broken out at some point?
    protected boolean isMemberOfCategory(String story, String[] categories) {
        if (categories != null) {
            CategoryRegExHelper hlpr = new CategoryRegExHelper();
            String[] lines = story.split("\n");
            Arrays.sort(categories); //TODO do this somewhere else?
            for (int x = 0; x < lines.length; x++) {
                if (hlpr.lineStartsWithCategory(lines[x])) {
                    String[] storycats = hlpr.getCategories(lines[x]);
                    for (int y = 0; y < storycats.length; y++) {
                        if (Arrays.binarySearch(categories, storycats[y]) >= 0) {
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
}
