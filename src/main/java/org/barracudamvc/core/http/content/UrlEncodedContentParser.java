/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.http.content;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.barracudamvc.plankton.io.parser.URLEncoded.URLEncodedParser;

public class UrlEncodedContentParser implements ContentParser {

    private static final URLEncodedParser parser = new URLEncodedParser();

    public Map<String, List<Object>> getContent(HttpServletRequest request) {
        try {
            return (Map) parser.parse(request.getInputStream());
        } catch (IOException ex) {
            return Collections.<String, List<Object>>emptyMap();
        }
    }
}
