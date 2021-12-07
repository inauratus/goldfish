/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.helper.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class CustomRequestWrapper extends HttpServletRequestWrapper implements HttpRequest {

    private final Map<String, List<Object>> content;

    public CustomRequestWrapper(HttpServletRequest request, Map<String, List<Object>> content) {
        super(request);
        this.content = content;
    }

    @Override
    public String[] getParameterValues(String name) {
        List<Object> objects = content.get(name);
        if (objects == null) return new String[0];

        String[] results = new String[content.size()];
        for (int i = 0, objectsSize = objects.size(); i < objectsSize; i++) {
            Object object = objects.get(i);
            results[i] = (String.valueOf(object));
        }

        return results;
    }

    public Map<String, String[]> getParameterMap() {
        HashMap<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, List<Object>> toConvert : content.entrySet()) {
            List<Object> value = toConvert.getValue();
            String[] strings = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                strings[i] = String.valueOf(value.get(i));
            }

            result.put(toConvert.getKey(), strings);
        }

        return result;
    }

    @Override
    public String getParameter(String name) {
        if(content.containsKey(name)){
            List<Object> params = content.get(name);
            
            if(params.isEmpty()) return null;
            
            return String.valueOf(params.get(0));
        }        
        return super.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(content.keySet());        
    }

    @Override
    public Map<String, List<Object>> getContentValues() {
        return content;
    }
}
