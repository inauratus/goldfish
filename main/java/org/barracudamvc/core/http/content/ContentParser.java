/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.http.content;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface ContentParser {

    public Map<String, List<Object>> getContent(HttpServletRequest request);
}
