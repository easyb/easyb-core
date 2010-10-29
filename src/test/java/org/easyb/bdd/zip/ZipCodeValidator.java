package org.easyb.bdd.zip;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ZipCodeValidator implements Validateable {

    private String zipRegEx = "^\\d{5}([\\-]\\d{4})?$";
    private Pattern pattern;

    public ZipCodeValidator() {
        try {
            this.pattern = Pattern.compile(this.zipRegEx);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("pattern: " + this.pattern + " was invalid!");
        }
    }

    public boolean validate(String value) {
        return this.pattern.matcher(value).matches();
    }
}
