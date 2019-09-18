/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.http.content;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class NoActionContentParser implements ContentParser {
    @Override
    public Map<String, List<Object>> getContent(HttpServletRequest request) {
        return Collections.<String, List<Object>>emptyMap();
    }
}
