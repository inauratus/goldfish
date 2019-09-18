/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */

package org.barracudamvc.core.helper.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ContentServletRequestWrapper extends HttpServletRequestWrapper   {
    
    HttpServletRequest request;
    
    public ContentServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }
    
    
    
}
