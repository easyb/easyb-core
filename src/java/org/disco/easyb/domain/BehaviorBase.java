package org.disco.easyb.domain;

import java.io.File;

public abstract class BehaviorBase implements Behavior {
    private String phrase;
    private File file;

    protected BehaviorBase(String phrase, File file) {
        this.phrase = phrase;
        this.file = file;
    }

    public String getPhrase() {
        return phrase;
    }

    public File getFile() {
        return file;
    }
}
