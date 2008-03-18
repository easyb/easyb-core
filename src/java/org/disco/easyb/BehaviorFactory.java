package org.disco.easyb;

import java.io.File;
import java.util.List;
import static java.util.Arrays.asList;

import org.disco.easyb.core.util.CamelCaseConverter;

public class BehaviorFactory {

    private static final List<String> STORY_PATTERNS = asList("Story.groovy", ".story");
    private static final List<String> SPECIFICATION_PATTERNS = asList("Specification.groovy", ".specification");

    private BehaviorFactory() {
    }

    public static Behavior createBehavior(File behaviorFile) {
        for (String pattern : STORY_PATTERNS) {
            if (behaviorFile.getName().endsWith(pattern)) {
                return new Story(new CamelCaseConverter(stripPattern(behaviorFile.getName(), pattern)).toPhrase());
            }
        }
        for (String pattern : SPECIFICATION_PATTERNS) {
            if (behaviorFile.getName().endsWith(pattern)) {
                return new Specification(new CamelCaseConverter(stripPattern(behaviorFile.getName(), pattern)).toPhrase());
            }
        }
        throw new IllegalArgumentException("Easyb behavior files must end in Story.groovy, .story, Specification.groovy or .specification. See easyb documentation for more details.");
    }

    private static String stripPattern(String string, String pattern) {
        return (string.split(pattern)[0]);
    }
}
