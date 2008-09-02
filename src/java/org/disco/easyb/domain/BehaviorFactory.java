package org.disco.easyb.domain;

import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;

import org.disco.easyb.util.CamelCaseConverter;

public class BehaviorFactory {

    public static final GroovyShellConfiguration DEFAULT_GROOVY_SHELL_CONFIG = new GroovyShellConfiguration();

    private static final List<String> STORY_PATTERNS = asList("Story.groovy", ".story");
    private static final List<String> SPECIFICATION_PATTERNS = asList("Specification.groovy", ".specification");

    private BehaviorFactory() {
    }

	public static Behavior createBehavior(GroovyShellConfiguration gShellConfig, File behaviorFile) {
		for (String pattern : STORY_PATTERNS) {
			if (behaviorFile.getName().endsWith(pattern)) {
				return new Story(gShellConfig, createPhrase(behaviorFile, pattern), behaviorFile);
			}
		}
		for (String pattern : SPECIFICATION_PATTERNS) {
			if (behaviorFile.getName().endsWith(pattern)) {
				return new Specification(gShellConfig, createPhrase(behaviorFile, pattern), behaviorFile);
			}
		}

		throw new IllegalArgumentException(verifyFile(behaviorFile));
	}

	public static Behavior createBehavior(File behaviorFile) {
		return createBehavior(DEFAULT_GROOVY_SHELL_CONFIG, behaviorFile);
    }

    /**
     * This method builds a user friendly error message, which
     * assists in debugging why a particular behavior file
     * can't be run
     */
    // TODO this method appears to be named wrong. its function is not to verify a file at all.. it appears to solely just build up an error message
    private static String verifyFile(File behaviorFile) {
        StringBuffer errorMessage = new StringBuffer("Your file, ")
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

    private static String createPhrase(File behaviorFile, String pattern) {
        return new CamelCaseConverter(stripPattern(behaviorFile.getName(), pattern)).toPhrase();
    }

    private static String stripPattern(String string, String pattern) {
        return (string.split(pattern)[0]);
    }
}
