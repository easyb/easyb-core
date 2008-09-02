package org.disco.easyb.domain;

import java.util.Map;
import java.util.HashMap;

public class GroovyShellConfiguration {
    private ClassLoader classLoader;
    private Map<String, Object> shellContextVariables;

    public GroovyShellConfiguration() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.shellContextVariables = new HashMap<String, Object>();
    }

    public GroovyShellConfiguration(ClassLoader classLoader, Map<String, Object> shellContextVariables) {
        this.classLoader = classLoader;
        this.shellContextVariables = shellContextVariables;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Map<String, Object> getShellContextVariables() {
        return shellContextVariables;
    }
}
