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
 * $Id: LightweightURL.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.http;



/**
 * A lightweight URL class
 */
public class LightweightURL {
    String url = null;
    String base = null;
    String param = null;

    /**
     * Create a lightweight url
     *
     * @param iurl a URL string
     */
    public LightweightURL(String iurl) {
        url = iurl;
    }

    /**
     * Return the whole URL string 
     * (ie. "http://foo.com?foo1=blah1&foo2=blah2")
     */
    public String getURLStr() {
        if (url==null) return "";
        return url;
    }

    /**
     * Return the base part of the URL (everything up to the ?)
     * (ie. "http://foo.com")
     */
    public String getBaseStr() {
        if (url==null) return "";
        if (base==null) {
            int epos = url.indexOf("?");
            if (epos>-1) base = url.substring(0,epos);
            else base = url;
        }
        return base;
    }

    /**
     * Return the whole URL string (from the ? to the end)
     * (ie. "?foo1=blah1&foo2=blah2")
     */
    public String getParamStr() {
        if (url==null) return "";
        if (param==null) {
            int spos = url.indexOf("?");
            if (spos>-1) param = url.substring(spos);
            else param = "";
        }
        return param;
    }
    
    /**
     * Return a String representation of the URL
     */
    public String toString() {
        return url;
    }
}    
