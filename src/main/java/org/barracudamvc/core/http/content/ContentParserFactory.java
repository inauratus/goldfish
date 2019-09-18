/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.http.content;

import java.util.HashMap;

public class ContentParserFactory {

    private final HashMap<String, ContentParser> parsers;
    private static final ContentParser PASS_THROUGH_PARSER = new NoActionContentParser();

    public ContentParserFactory() {
        parsers = new HashMap<>();
        parsers.put("application/x-www-form-urlencoded", new UrlEncodedContentParser());
        parsers.put("multipart/form-data", new MultipartFileContentParser());
    }

    public ContentParser createParser(String contentType) {
        ContentParser parser = parsers.get(getContentType(contentType));
        if (parser == null) {
            return PASS_THROUGH_PARSER;
        } else {
            return parser;
        }
    }

    private String getContentType(String ct) {
        if (ct == null) {
            return null;
        } else {
            return ct.split(";")[0].trim();
        }
    }
}
