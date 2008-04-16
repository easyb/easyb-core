package org.disco.easyb.domain;

import java.io.File;
import java.io.Serializable;

public abstract class BehaviorBase implements Behavior, Serializable {
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
}
