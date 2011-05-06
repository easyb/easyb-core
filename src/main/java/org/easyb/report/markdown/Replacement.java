/*
Copyright (c) 2005, Pete Bevin.
<http://markdownj.petebevin.com>

All rights reserved.
*/
package org.easyb.report.markdown;

import java.util.regex.Matcher;

public interface Replacement {
    String replacement(Matcher m);
}