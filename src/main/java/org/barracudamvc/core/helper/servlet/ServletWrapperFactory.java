/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 *
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.helper.servlet;

import org.apache.log4j.Logger;
import org.barracudamvc.core.http.content.ContentParser;
import org.barracudamvc.core.http.content.ContentParserFactory;
import org.barracudamvc.plankton.io.parser.URLEncoded.URLEncodedParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.util.*;

public class ServletWrapperFactory {
    protected static final Logger logger = Logger.getLogger(ServletWrapperFactory.class.getName());
    private static final ContentParserFactory parser = new ContentParserFactory();
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String URL_FORM_ENCODED = "application/x-www-form-urlencoded";

    public HttpServletRequestWrapper create(HttpServletRequest request) {
        if (request.getMethod().equals(GET))
            return new DefaultServletRequestWrapper(request);

        if (request.getMethod().equals(POST) && !getContentType(request).contains(URL_FORM_ENCODED))
            return new DefaultServletRequestWrapper(request);

        if (request.getMethod().equals(POST)) {
            if (request.getContentLength() > 0 && !request.getParameterNames().hasMoreElements()) {
                logger.fatal("Servlet Container unable to parse data segment - resorting to Custom Request");
                return createCustomWrapper(request);
            }
            return new DefaultServletRequestWrapper(request);
        }

        return createCustomWrapper(request);
    }

    private String getContentType(HttpServletRequest request) {
        if(request.getContentType() == null)
            return "";
        return request.getContentType();
    }

    private CustomRequestWrapper createCustomWrapper(HttpServletRequest request) {
        return new CustomRequestWrapper(
                request,
                combine(parseURLData(request), parserDataSegment(request)));
    }

    private Map<String, String[]> parseURLData(HttpServletRequest request) {
        Map<String, String[]> requestData = request.getParameterMap();
        if (requestData == null || (requestData.isEmpty() && request.getQueryString() != null && request.getQueryString().length() > 0)) {
            logger.fatal("Servlet Container was not able parse the Parameters Names and return an empty enumeration [" + request.getQueryString() + "]");
            return parseURIParameters(request);
        }
        return requestData;
    }

    private Map<String, String[]> parseURIParameters(HttpServletRequest request) {
        Map<String, List<String>> parse = new URLEncodedParser().parse(new ByteArrayInputStream(request.getQueryString().getBytes()));
        return convertAtomicValuesToArray(parse);
    }

    private Map<String, String[]> convertAtomicValuesToArray(Map<String, List<String>> parse) {
        Map<String, String[]> data = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : parse.entrySet()) {
            List<String> val = entry.getValue();
            if (val == null)
                data.put(entry.getKey(), null);
            else
                data.put(entry.getKey(), val.toArray(new String[val.size()]));
        }
        return data;
    }

    private Map<String, List<Object>> parserDataSegment(HttpServletRequest request) {
        return findDataSegementParser(request).getContent(request);
    }

    Map<String, List<Object>> combine(Map<String, String[]> requestDate, Map<String, List<Object>> contentData) {
        Map<String, List<Object>> valueMap = convert(requestDate);
        for (Map.Entry<String, List<Object>> entry : contentData.entrySet()) {
            List<Object> item = valueMap.get(entry.getKey());
            if (item == null) {
                item = new ArrayList<>();
                valueMap.put(entry.getKey(), item);
            }
            item.addAll(entry.getValue());
        }
        return valueMap;
    }

    ContentParser findDataSegementParser(HttpServletRequest request) {
        return parser.createParser(getContentType(request));
    }

    private Map<String, List<Object>> convert(Map<String, String[]> parameterMap) {
        HashMap<String, List<Object>> result = new HashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            result.put(entry.getKey(), convert(entry.getValue()));
        }
        return result;
    }

    private List<Object> convert(String[] values) {
        ArrayList<Object> result = new ArrayList<>(values.length);
        result.addAll(Arrays.asList(values));
        return result;

    }

}
