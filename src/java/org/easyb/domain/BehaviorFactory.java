package org.easyb.domain;


import org.easyb.Configuration;
import org.easyb.util.CamelCaseConverter;

import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;

public class BehaviorFactory {

    public static final GroovyShellConfiguration DEFAULT_GROOVY_SHELL_CONFIG = new GroovyShellConfiguration();

    private static final List<String> STORY_PATTERNS = asList("Story.groovy", ".story");
    private static final List<String> SPECIFICATION_PATTERNS = asList("Specification.groovy", ".specification");

    private BehaviorFactory() {
    }

    public static Behavior createBehavior(final GroovyShellConfiguration gShellConfig,
                                          final File behaviorFile, Configuration config) {

       if(!behaviorFile.isFile()){
         throw new IllegalArgumentException(verifyFileError(behaviorFile));  
       }

        for (final String pattern : STORY_PATTERNS) {
            if (behaviorFile.getName().endsWith(pattern)) {
                return new Story(gShellConfig, createPhrase(behaviorFile, pattern), behaviorFile);
            }
        }
        for (final String pattern : SPECIFICATION_PATTERNS) {
            if (behaviorFile.getName().endsWith(pattern)) {
                return new Specification(gShellConfig, createPhrase(behaviorFile, pattern), behaviorFile);
            }
        }

        throw new IllegalArgumentException(verifyFileError(behaviorFile));
    }

    public static Behavior createBehavior(final File behaviorFile) {
        return createBehavior(DEFAULT_GROOVY_SHELL_CONFIG, behaviorFile, null);
    }

    /**
     * This method builds a user friendly error message, which
     * assists in debugging why a particular behavior file
     * can't be run
     */
    private static String verifyFileError(final File behaviorFile) {
        final StringBuffer errorMessage = new StringBuffer("Your file, ")
                .append(behaviorFile.getName()).append(", ");

        if (!behaviorFile.isFile()) {
            if (behaviorFile.getParentFile() != null &&
                    behaviorFile.getParentFile().isDirectory()) {
                /**
                 * the next logical step is to look at this
                 * directory and find a file that is similar in name
                 * to the incoming file and suggest they try that file
                 * instead...
                 */
                errorMessage.append("appears to be mispelled as it doesn't exist in the directory \"")
                        .append(behaviorFile.getParentFile())
                        .append("\" -- verify you have the correct path and that the ")
                        .append("file name is spelled correctly.");
            } else {
                errorMessage.append("doesn't appear to exist. Verify your path and ")
                        .append("file name are spelled correctly.");
            }
        } else {
            errorMessage.append("cannot be run as its name or extension is ambigious. ")
                    .append("easyb behavior files must end in Story.groovy, .story, ")
                    .append("Specification.groovy or .specification. ");
        }
        return errorMessage.toString();
    }

    private static String createPhrase(final File behaviorFile, final String pattern) {
        return new CamelCaseConverter(stripPattern(behaviorFile.getName(), pattern)).toPhrase();
    }

    private static String stripPattern(final String string, final String pattern) {
        return (string.split(pattern)[0]);
    }
}
