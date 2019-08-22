/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.helper.servlet;

import java.util.List;
import java.util.Map;

public interface HttpRequest {

    public Map<String, List<Object>> getContentValues();
}
