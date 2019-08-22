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
 * $Id: BarracudaServletRequestWrapper.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.helper.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>This class acts as a thin wrapper around a ServletRequest, adding several additional
 * methods which enabled the request parameters to be modified programatically
 *
 * @author christianc@granitepeaks.com
 * @since //csc_010404_1
 */
public interface BarracudaServletRequestWrapper extends HttpServletRequest {

    /**
     * Set the underlying request method.
     *
     * @param method the underlying request method (GET, POST, etc)
     */
    public void setMethod(String method);

    /**
     * Set a given parameter (note that this is backed by a hashmap,
     * so the structure is slightly different than that of the
     * underlying ServletRequest which allows multiple paramters
     * with the same name). This means that if you attempt to
     * set a parameter whose key already exists you will effectively
     * overwrite the existing value.
     *
     * @param name the key name for the parameter
     * @param value the value associated with the given key
     */
    public void addParameter(String name, Object value);

    /**
     * Remove the first parameter whose key matches the specified name
     *
     * @param name the key name for the parameter
     */
    public void removeParameter(String name);

    /**
     * Remove all parameters for a specified name
     *
     * @param name the key name for the parameter
     */
    public void removeAllParameters(String name);

    /**
     * Reset the parameter values to their original state
     * (ie. the actual values in the request)
     */
    public void resetParameters();
}