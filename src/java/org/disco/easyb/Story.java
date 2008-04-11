package org.disco.easyb;

public class Story implements Behavior {

    private String phrase;

    public Story(String storyPhrase) {
        phrase = storyPhrase;
    }

    public String getPhrase() {
        return phrase;
    }
}
