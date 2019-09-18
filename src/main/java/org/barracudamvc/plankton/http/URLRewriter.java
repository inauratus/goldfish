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
 * $Id: URLRewriter.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This just contains some Servlet utility routines
 */
public class URLRewriter {

    /**
     * This option is used to control whether or not URL rewriting occurs. It 
     * exists to handle bugs in Enhydra 3.x in which invoking the URL rewriting
     * methods causes a bogus value to be returned. If you are using Enhydra 3.x,
     * you will want to set this value to false (you can do this without coding
     * changes via the your assembler xml file).  Defaults to true. To see whether
     * or not your appserver correctly implements URL rewriting, look at 
     * http://localhost/Barracuda/RedirectEx1
     */
    public static boolean REWRITE_URLS = true;
//    public static boolean REWRITE_URLS = false;            //need it false if we're running against Enhydra 3.x
    
    /**
     * Encode a URL if the REWRITE_URLS option is set. Using this option
     * makes it easy to reconfigure Barracuda if your server has problems
     * with URL rewriting (ie. Enhydra 3.x)
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @param url the target url
     * @return an encoded url (unless REWRITE_URLS = false, in which case
     *        it will just return the original url value)
     */
    public static String encodeURL(HttpServletRequest req, HttpServletResponse resp, String url) {
        //jrk_20030508 - should be able to remove the req parameter, but leaving
        //to avoid issues with an interface change.
        if (REWRITE_URLS && resp != null) {
            return resp.encodeURL(url);
        }
        return url;
    }

    /**
     * Encode a redirect URL if the REWRITE_URLS option is set. Using this option
     * makes it easy to reconfigure Barracuda if your server has problems
     * with URL rewriting (ie. Enhydra 3.x)
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @param url the target url
     * @return an encoded url (unless REWRITE_URLS = false, in which case
     *        it will just return the original url value)
     */
    public static String encodeRedirectURL(HttpServletRequest req, HttpServletResponse resp, String url) {
        //jrk_20030508 - should be able to remove the req parameter, but leaving
        //to avoid issues with an interface change.
        if (REWRITE_URLS && resp != null) {
            return resp.encodeRedirectURL(url);
        }
        return url;
    }

}    
