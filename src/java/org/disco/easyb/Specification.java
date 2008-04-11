package org.disco.easyb;

public class Specification implements Behavior {

    private String phrase;

    public Specification(String specificationPhrase) {
        phrase = specificationPhrase;
    }

    public String getPhrase() {
        return phrase;
    }
}
