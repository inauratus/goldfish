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
 * $Id: ExceptionUtil.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.plankton.exceptions;

import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * Exception utilities
 */
public class ExceptionUtil {

    /**
     * log an exception as HTML
     *
     * @param out the target PrintWriter
     * @param e the nestable exception to dump
     * @param req the servlet request that spawned all this
     */
    public static void logExceptionAsHTML (PrintWriter out, NestableException e, HttpServletRequest req) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Unexpected exception!</title>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>Error: EventException</h1>");
        out.println("<p>There was an unexpected error while servicing this request...please contact your application administrator and notify them of the problem.");
        out.println("<p><pre>Error dispatching request: "+e.getMessage());
        out.println("</pre><p>Exception:<br>");
        out.println("<pre>");   //csc_031103.1
        e.printStackTrace(out);
        out.println("</pre>");  //csc_031103.1
        Exception rootException = NestableException.getRootException(e);
        if (rootException!=e) {
            out.println("<p>Root Exception:<br>");
            out.println("<pre>");   //csc_031103.1
            rootException.printStackTrace(out);
            out.println("</pre>");  //csc_031103.1
        }
        
        out.println("<p>Parameters:");
        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String val = (String) req.getParameter(key);
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;key:"+key+" value:"+val);
        }

        out.println("<br>RequestURI:"+req.getRequestURI());
        out.println("<br>ServletPath:"+req.getServletPath());
        out.println("<br>PathInfo:"+req.getPathInfo());
        out.println("<br>PathTranslated:"+req.getPathTranslated());
    
        out.println("</body>");
        out.println("</html>");
    }

}
