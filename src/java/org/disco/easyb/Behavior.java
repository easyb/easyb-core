package org.disco.easyb;

public abstract class Behavior {

    private String phrase;

    protected Behavior(String behaviorPhrase) {
        phrase = behaviorPhrase;
    }

    public String getPhrase() {
        return phrase;
    }
}
