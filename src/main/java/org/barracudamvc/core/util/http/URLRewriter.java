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
 * $Id: URLRewriter.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.util.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.barracudamvc.core.comp.ViewContext;

/**
 * This just contains some Servlet utility routines
 */
public class URLRewriter extends org.barracudamvc.plankton.http.URLRewriter {

    //-------------------- URL Rewriting -------------------------
    /**
     * Encode a URL if the REWRITE_URLS option is set. Using this option
     * makes it easy to reconfigure Barracuda if your server has problems
     * with URL rewriting (ie. Enhydra 3.x)
     *
     * @param vc the ViewContext
     * @param url the target url
     * @return an encoded url (unless REWRITE_URLS = false, in which case
     *        it will just return the original url value)
     */
    public static String encodeURL(ViewContext vc, String url) {
        if (vc != null) {
            HttpServletRequest request = (HttpServletRequest) vc.getState(ViewContext.REQUEST);
            HttpServletResponse responce = (HttpServletResponse) vc.getState(ViewContext.RESPONSE);

            return encodeURL(request, responce, url);
        }
        return url;
    }

    /**
     * Encode a redirect URL if the REWRITE_URLS option is set. Using this 
     * option makes it easy to reconfigure Barracuda if your server has 
     * problems with URL rewriting (ie. Enhydra 3.x)
     *
     * @param vc the ViewContext
     * @param url the target url
     * @return an encoded url (unless REWRITE_URLS = false, in which case
     *        it will just return the original url value)
     */
    public static String encodeRedirectURL(ViewContext vc, String url) {
        if (vc != null) {
            HttpServletRequest request = (HttpServletRequest) vc.getState(ViewContext.REQUEST);
            HttpServletResponse responce = (HttpServletResponse) vc.getState(ViewContext.RESPONSE);

            return encodeRedirectURL(request, responce, url);
        }
        return url;
    }
}
