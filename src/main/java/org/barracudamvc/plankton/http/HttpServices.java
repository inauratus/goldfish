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
 * $Id: HttpServices.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.http;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

//saw_121102.2 - created
/**
 * This class provides HTTP-related utility methods.
 *
 * @author shawn@shawn-wilson.com
 */
public class HttpServices {

    protected static final DateFormat cookieDF = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");

    /**
     * Return a Cookie from a single 'Set-Cookie' header value string from the server.
     * The value string must conform to either the Version 0 (by Netscape) or Version 1
     * (by RFC 2109) cookie specification.
     *
     * @throws ParseException if the string cannot be parsed into a valid cookie
     *
     * @see <a href="http://wp.netscape.com/newsref/std/cookie_spec.html">Cookie Specification, Version 0</a>
     * @see <a href="http://rfc-2109.rfc-list.org/">Cookie Specification, Version 1</a>
     */
    public static Cookie parseCookie(String str) throws ParseException {
        Cookie cookie;
        StringTokenizer st = new StringTokenizer(str, ";");
        int length = 0; // to keep track of position for ParseExceptions

        String str1 = st.nextToken();
        length += str1.length();
        str1 = str1.trim();
        int index = str1.indexOf('=');

        if(index < 0) throw new ParseException("Missing name=value pair", 0);
        else if(index == 0) throw new ParseException("Missing name for name=value pair", 0);
        else if(index == str1.length()) throw new ParseException("Missing value for name=value pair", 0);
        else cookie = new Cookie(str1.substring(0, index), str1.substring(index+1));

        while(st.hasMoreTokens()) {
            str1 = st.nextToken();
            index = str1.indexOf('=');

            if(index < 0) {
                if(str1.trim().equalsIgnoreCase("secure")) {
                    cookie.setSecure(true);
                } else {
                    //throw new ParseException("Unrecognized option: "+str1, length);
                    // for compatibility with future cookie specifications, we will simply
                    // silently ignore any unrecognized fields
                }
            } else if(index > 0) {
                String key = str1.substring(0, index).trim().toLowerCase();
                String val = str1.substring(index+1);

                if(key.equals("comment")) {
                    cookie.setComment(val);
                } else if(key.equals("domain")) {
                    cookie.setDomain(val);
                } else if(key.equals("max-age")) {
                    try { cookie.setMaxAge(Integer.parseInt(val)); }
                    catch(NumberFormatException e) {
                        ParseException ee = new ParseException("Not an integer for 'max-age' field", length+8);
                        // dbr_032601
                        // The 1.3 api doesn't support this
                        // ee.initCause(e);
                        throw ee;
                    }
                } else if(key.equals("path")) {
                    cookie.setPath(val);
                } else if(key.equals("version")) {
                    try { cookie.setVersion(Integer.parseInt(val)); }
                    catch(NumberFormatException e) {
                        ParseException ee = new ParseException("Not an integer for 'version' field", length+8);
                        // dbr_032601
                        // The 1.3 api doesn't support this
                        // ee.initCause(e);
                        throw ee;
                    }
                } else if(key.equals("expires")) {
                    // provided for Version 0 compatibility
                    try { cookie.setMaxAge( (int)((cookieDF.parse(val).getTime()-System.currentTimeMillis())/1000) ); }
                    catch(ParseException e) {
                        ParseException ee = new ParseException("Invalid date format for 'expires' field", length+8);
                        // dbr_032601
                        // The 1.3 api doesn't support this
                        // ee.initCause(e);
                        throw ee;
                    }
                } else {
                    //throw new ParseException("Unrecognized option: "+str1, length);
                    // for compatibility with future cookie specifications, we will simply
                    // silently ignore any unrecognized fields
                }
            } else {
                throw new ParseException("Missing option: "+str1, length);
            }

            length += 1+str1.length();  // (+1 for the semicolon delimiter)
        }

        return cookie;
    }

    /**
     * Return a formatted cookie string for use in a 'Set-Cookie' header.
     */
    public static String formatCookie(Cookie cookie) {
        StringBuffer sb = new StringBuffer(cookie.getName()+"="+cookie.getValue());
        if(cookie.getComment() != null) sb.append(";Comment=").append(cookie.getComment());
        if(cookie.getDomain() != null) sb.append(";Domain=").append(cookie.getDomain());
        if(cookie.getPath() != null) sb.append(";Path=").append(cookie.getPath());
        if(cookie.getSecure()) sb.append(";Secure");
        // saw_030204_1 - actually there's no way to print the expiration date
        //      without knowing when the cookie was created
//        if(cookie.getVersion() == 0) {
//            if(cookie.getMaxAge() >= 0) sb.append(";Expires=").append(cookieDF.format(new Date(cookie.getMaxAge())));
//        } else {
            sb.append(";Version=").append(cookie.getVersion());
            sb.append(";Max-Age=").append(cookie.getMaxAge());
//        }

        return sb.toString();
    }
    
    /**
     * Convenience method to get a cookie by name from an HttpServletRequest
     */
    public static Cookie getCookie(String cookieName, HttpServletRequest req) {
        if (cookieName==null || req==null) return null;
        Cookie cookie = null;
        Cookie[] cookies = req.getCookies();
        if (cookies!=null) {
            for (int i=0; i<cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    cookie = cookies[i];
                    break;
                }
            }    
        }
        return cookie;
    }
    
};
