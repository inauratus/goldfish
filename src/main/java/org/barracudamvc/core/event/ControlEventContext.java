/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: ControlEventContext.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface extends the EventContext to provide
 * access to the HttpServletRequest.
 */
public interface ControlEventContext extends EventContext {

    public static final String SERVLET_CONFIG = ControlEventContext.class.getName()+".ServletConfig";
    public static final String HTTP_SERVLET_REQUEST = ControlEventContext.class.getName()+".ServletRequest";

    /**
     * Get the associated ServletConfig structure
     *
     * @return the associated ServletConfig structure
     */
    public ServletConfig getConfig();

    /**
     * Get the associated HttpServletRequest
     *
     * @return the associated HttpServletRequest
     */
    public HttpServletRequest getRequest();
    
    public HttpServletResponse getResponse();
    
}
