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
 * $Id: ParamGateway.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.barracudamvc.core.helper.state.ParamPersister;
import org.barracudamvc.plankton.http.ServletUtil;
import org.barracudamvc.plankton.http.URLRewriter;


/**
 * <p>The purpose of this servlet is take a set of req parameters
 * and persist them in the users session. Then return the real target
 * url back to the calling application
 */
public class ParamGateway extends HttpServlet {

    protected static final Logger logger = Logger.getLogger(ParamGateway.class.getName());

    //these are intentionally non-final, so that you can programatically
    //change them if need be
    public static String PARAM_TARGET = "pt_";
    public static String PARAM_EXT = ".param_map";


    //-------------------- ParamGateway --------------------------
    /**
     * <p>Handle the default HttpRequest.
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //persist the params
        if (logger.isDebugEnabled()) {
            logger.debug("Persisting parameter information");
            ServletUtil.showParams(req, logger);
        }
        ParamPersister.persistReqParamState(req);

        //find the client context id
        String cid = req.getParameter("pm_cid");

        //csc_061202.1 - added
        //set the caching hdrs to prevent caching
        resp.setHeader("Cache-Control","no-cache");
        resp.setDateHeader("Expires", System.currentTimeMillis());

        //figure out where we need to go from here by deriving it from
        //the actual URL used to get us here
        String src = req.getRequestURI();
        if (logger.isDebugEnabled()) logger.debug("Request URI:"+src);
        int spos = src.indexOf(PARAM_TARGET);
        int epos = src.indexOf(PARAM_EXT);
        String base = src.substring(spos+PARAM_TARGET.length(), epos);
        String param = src.substring(epos+PARAM_EXT.length());
        String target = URLRewriter.encodeURL(req, resp, base+param);
        if (logger.isDebugEnabled()) logger.debug("Param Target:"+target);
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html>");
        out.println("  <head>");
        out.println("    <title></title>");
//        out.println("    <script type=\"text/javascript\">window.onerror=new Function('return true');</script>");
        out.println("  </head>");
        out.print  ("  <body onload=\"p=document.layers?parentLayer:window.parent;p.jsrsLoaded('");out.print(cid);out.println("');\">");
        out.print  ("    <h3>Redirecting to real target...</h3><p>If you are not automatically redirected, please click <a href=\"");out.print(target);out.println("\" onclick=\"location.replace(this.href);return false;\">here</a></p>");
        out.print  ("    <form name=\"jsrs_Form\"><div style=\"visibility:hidden;\"><textarea name=\"jsrs_Payload\" rows=\"2\" cols=\"20\">");out.print(target);out.println("</textarea></div></form>");
        out.println("  </body>");
        out.println("</html>");

        //out.flush();
        resp.flushBuffer(); //instead of out.flush for Servlet 2.2 compatibility (Craig McClanahan http://w4.metronet.com/~wjm/tomcat/2000/Nov/msg00174.html)
    }


    //-------------------- HTTPServlet ---------------------------
    /**
     * <p>By default the GET request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }

    /**
     * <p>By default the POST request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }

    //-------------------- Servlet -------------------------------
    /**
     * <p>Here's where we initialize the servlet.
     */
    public void init() throws ServletException {
        logger.info("initializing servlet");
    }

}
