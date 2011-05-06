/*
Copyright (c) 2005, Pete Bevin.
<http://markdownj.petebevin.com>

All rights reserved.
*/
package org.easyb.report.markdown;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class CharacterProtector {
    private Map<String, String> protectMap = new HashMap<String, String>();
    private Map<String, String> unprotectMap = new HashMap<String, String>();
    private static final String GOOD_CHARS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private Random rnd = new Random();


    public String encode(String literal) {
        if (!protectMap.containsKey(literal)) {
            addToken(literal);
        }
        return protectMap.get(literal);
    }

    public String decode(String coded) {
        return unprotectMap.get(coded);
    }

    public Collection getAllEncodedTokens() {
        return unprotectMap.keySet();
    }

    private void addToken(String literal) {
        String encoded = longRandomString();
        protectMap.put(literal, encoded);
        unprotectMap.put(encoded, literal);
    }

    private String longRandomString() {
        StringBuffer sb = new StringBuffer();
        final int CHAR_MAX = GOOD_CHARS.length();
        for (int i = 0; i < 20; i++) {
            sb.append(GOOD_CHARS.charAt(rnd.nextInt(CHAR_MAX)));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return protectMap.toString();
    }
}
