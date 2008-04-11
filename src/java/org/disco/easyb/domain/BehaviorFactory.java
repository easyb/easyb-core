package org.disco.easyb.domain;

import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;

import org.disco.easyb.util.CamelCaseConverter;

public class BehaviorFactory {

    private static final List<String> STORY_PATTERNS = asList("Story.groovy", ".story");
    private static final List<String> SPECIFICATION_PATTERNS = asList("Specification.groovy", ".specification");

    private BehaviorFactory() {
    }

    public static Behavior createBehavior(File behaviorFile) {
        for (String pattern : STORY_PATTERNS) {
            if (behaviorFile.getName().endsWith(pattern)) {
                return new Story(createPhrase(behaviorFile, pattern), behaviorFile);
            }
        }
        for (String pattern : SPECIFICATION_PATTERNS) {
            if (behaviorFile.getName().endsWith(pattern)) {
                return new Specification(createPhrase(behaviorFile, pattern), behaviorFile);
            }
        }
        throw new IllegalArgumentException("Easyb behavior file must end in Story.groovy, .story, Specification.groovy or .specification. See easyb documentation for more details.");
    }

    private static String createPhrase(File behaviorFile, String pattern) {
        return new CamelCaseConverter(stripPattern(behaviorFile.getName(), pattern)).toPhrase();
    }

    private static String stripPattern(String string, String pattern) {
        return (string.split(pattern)[0]);
    }
}
