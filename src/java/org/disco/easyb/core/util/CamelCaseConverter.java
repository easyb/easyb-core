package org.disco.easyb.core.util;

/**
 * Method object to convert to and from CamelCase
 *
 * @author sevensoft [Ken Brooks]
 */
public class CamelCaseConverter {
    private final char[] chars;
    int pos = 0;

    public CamelCaseConverter(String words) {
        words = words.substring(words.lastIndexOf('.') + 1);
        words = words.substring(words.lastIndexOf('$') + 1);
        chars = words.toCharArray();
    }

    public CamelCaseConverter(Class type) {
        this(type.getName());
    }

    public CamelCaseConverter(Object obj) {
//        this(obj instanceof Named ? ((Named) obj).getName() : obj.getClass().getName());
        this(obj.getClass().getName());
    }

    public String toCamelCase() {
        StringBuffer buf = new StringBuffer();
        while (pos < chars.length) {
            if (Character.isWhitespace(chars[pos])) {
                pos++;
                if (pos < chars.length) {
                    buf.append(Character.toUpperCase(chars[pos]));
                }
            } else if (pos == 0) {
                buf.append(Character.toUpperCase(chars[pos]));
            } else {
                buf.append(chars[pos]);
            }
            pos++;
        }
        return buf.toString();
    }

    public String toPhrase() {
        StringBuffer buf = new StringBuffer();
        while (pos < chars.length) {
            int numUppercase = countUppercase();
            switch (numUppercase) {
                case 0:
                    processRegularChar(buf);
                    break;
                case 1:
                    processSingleUppercaseChar(buf);
                    break;
                case 2:
                    processTwoUppercaseChars(buf);
                    break;
                default:
                    processUppercaseWord(buf, numUppercase);
                    break;
            }
        }
        return buf.toString();
    }

    private int countUppercase() {
        int count = 0;
        while (pos + count < chars.length && Character.isUpperCase(chars[pos + count])) {
            count++;
        }
        return count;
    }

    private void processRegularChar(StringBuffer buf) {
        buf.append(chars[pos++]);
    }

    private void processSingleUppercaseChar(StringBuffer buf) {
        if (buf.length() > 0) {
            buf.append(' ');
        }
        buf.append(Character.toLowerCase(chars[pos++]));
    }

    private void processTwoUppercaseChars(StringBuffer buf) {
        processSingleUppercaseChar(buf);
        processSingleUppercaseChar(buf);
    }

    private void processUppercaseWord(StringBuffer buf, int numUppercase) {
        while (pos < numUppercase - 1) {
            buf.append(chars[pos++]);
        }
        processSingleUppercaseChar(buf);
    }
}
