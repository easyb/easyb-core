package org.disco.easyb;

import org.disco.easyb.core.util.CamelCaseConverter;

import java.io.File;
import static java.util.Arrays.*;
import java.util.List;

public class Specification {
    private static final List<String> STORY_PATTERNS = asList("Story.groovy", ".story");

    private boolean story;
    private String phrase;

    public Specification(File specificationFile) {
        for (String pattern : STORY_PATTERNS) {
            if (specificationFile.getName().endsWith(pattern)) {
                story = true;
                phrase = new CamelCaseConverter(stripPattern(specificationFile.getName(), pattern)).toPhrase();
            }
        }
        if (!story) {
            phrase = new CamelCaseConverter(stripPattern(specificationFile.getName(), "\\.")).toPhrase();
        }
    }

    private String stripPattern(String string, String pattern) {
        return (string.split(pattern)[0]);
    }

    public boolean isStory() {
        return story;
    }

    public String getPhrase() {
        return phrase;
    }
}
