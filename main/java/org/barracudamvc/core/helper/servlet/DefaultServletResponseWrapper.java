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
 * $Id: DefaultServletResponseWrapper.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.helper.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

//csc_010404_1 - created
/**
 * <p>This class acts as a thin wrapper around a ServletResponse. 
 * To follow good practice in Barracuda programming, you should generally
 * use the PrintWriter object to render a response back to the browser
 */
public class DefaultServletResponseWrapper extends HttpServletResponseWrapper implements BarracudaServletResponseWrapper {

    protected static final Logger logger = Logger.getLogger(DefaultServletResponseWrapper.class.getName());

    /**
     * Create a DefaultServletResponseWrapper around some other
     * HttpServletResponse impl.
     *
     * @param iresp the underlying HttpServletResponse
     */
    public DefaultServletResponseWrapper(HttpServletResponse iresp) {
        super(iresp);
    }





}
