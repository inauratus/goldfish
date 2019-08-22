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
 * $Id: ServletUtil.java 253 2013-02-21 19:32:57Z charleslowery $
 */
package org.barracudamvc.plankton.http;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * This just contains some Servlet utility routines
 */
public class ServletUtil {

    //-------------------- Utility stuff -------------------------
    /**
     * print all request info
     */
    public static void printAllRequestInfo(HttpServletRequest req, Logger logger) {
        showGeneral(req, logger);
        showHeader(req, logger);
        showAttrs(req, logger);
        showParams(req, logger);
        showCookies(req, logger);
    }
    
    /**
     * print the general request info
     */
    public static void showGeneral(HttpServletRequest req, Logger logger) {
        logger.info("General Request Info:");
        logger.info("...Method:"+req.getMethod());
        logger.info("...Protocol:"+req.getProtocol());
        logger.info("...Scheme:"+req.getScheme());
        logger.info("...AuthType:"+req.getAuthType());
        logger.info("...IsSecure:"+req.isSecure());
        logger.info("...ContextPath:"+req.getContextPath());
        logger.info("...PathInfo:"+req.getPathInfo());
        logger.info("...PathTranslated:"+req.getPathTranslated());
        logger.info("...QueryString:"+req.getQueryString());
        logger.info("...RequestURI:"+req.getRequestURI());
        logger.info("...ServletPath:"+req.getServletPath());
    }
    
    /**
     * print the header request info
     */
    public static void showHeader(HttpServletRequest req, Logger logger) {
        logger.info("Headers:");
        Enumeration enumeration = req.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key =(String) enumeration.nextElement();
            String val =(String) req.getHeader(key);
            logger.info("...name:"+key+" val:"+val);
        }
    }
    
    /**
     * print the attr request info
     */
    public static void showAttrs(HttpServletRequest req, Logger logger) {
        logger.info("Attributes:");
        Enumeration enum2 = req.getAttributeNames();
        while (enum2.hasMoreElements()) {
            String key =(String) enum2.nextElement();
            String val =(String) req.getAttribute(key);
            logger.info("...name:"+key+" attr:"+val);
        }
    }
    
    /**
     * print the param request info
     */
    public static void showParams(HttpServletRequest req, Logger logger) {
        logger.info("Parameters:");
        Enumeration enum3 = req.getParameterNames();
        while (enum3.hasMoreElements()) {
            String key = (String) enum3.nextElement();
            String vals[] = req.getParameterValues(key);
            for (int i = 0, max = vals.length; i < max; i++) {
                //don't actually modify vals[i] here, as Tomcat seems to actually
                //change the underlying values when you do this - which I take to 
                //be an implementation oversight...I've emailed the Tomcat-Dev list
                //about this to ask for clarification. At any rate, we don't actually
                //want to modify the value here - just waht prints out.
                String s = vals[i];
                if (key.equalsIgnoreCase("password")
                        || key.equalsIgnoreCase("pwd")) {
                    s = "********";
                }
                logger.info("...key:" + key + " value:" + s);
            }
        }
    }
    
    /**
     * print the cookie request info
     */
    public static void showCookies(HttpServletRequest req, Logger logger) {
        logger.info("Cookies:");
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            logger.info("No cookies in session");
            return;
        }
        for (Cookie cookie : cookies) {
            logger.info("\n=Cookie======="
                    + "\n\t name:" + cookie.getName()
                    + "\n\t value:" + cookie.getValue()
                    + "\n\t ver:" + cookie.getVersion()
                    + "\n\t dom:" + cookie.getDomain()
                    + "\n\t comment:" + cookie.getComment()
                    + "\n\t max age:" + cookie.getMaxAge());
        }
    }
}
