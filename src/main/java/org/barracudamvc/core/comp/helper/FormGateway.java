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
 * $Id: FormGateway.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.helper;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>The purpose of this servlet is simply to process requests 
 * that come from a form and redirect based on the name of the button 
 * pressed.
 */
public class FormGateway extends HttpServlet {

    //public vars...eventually, these should probably be final
    protected static final Logger logger = Logger.getLogger(FormGateway.class.getName());

    //these are intentionally non-final, so that you can programatically
    //change them if need be
    public static String FORM_EXT = ".form_forward";
    public static String FORM_TARGET = "ft::";

    //-------------------- FormGateway ---------------------------
    /**
     * <p>Handle the default HttpRequest. 
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
        //iterate through all the parameters, looking for those which indicate
        //the key name is really an event handler or url
        Enumeration enumeration = req.getParameterNames();
        int matches = 0;
        String target = null;
        if (logger.isDebugEnabled()) logger.debug("Looking for target parameters");
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String val = (String) req.getParameter(key);
            if (logger.isDebugEnabled()) logger.debug("key:"+key+" val:"+val);
            
            if (key.startsWith(FORM_TARGET)) {
                if (logger.isDebugEnabled()) logger.debug("(form target match)");
                target = key.substring(FORM_TARGET.length());
                matches++;
            }
        }
//csc_101901.1        target = URLRewriter.encodeURL(req, resp, target);   //csc_101701.1
        
        //this is basically checks to ensure that only one of the req parameters 
        //matches a known form handler or URL. If more than one matches, then there's 
        //no way of knowing which one it's really supposed to be delivered to, so 
        //throw an exception
        if (matches!=1) throw new ServletException ("Unable to determine destination handler");
        else {        
            //find the RequestDispatcher and forward it there
            RequestDispatcher rd = req.getRequestDispatcher(target);
            if (logger.isDebugEnabled()) logger.debug("Redirecting to url:"+target);
            if (rd!=null) {
                rd.forward(req, resp);
            } else {
                throw new ServletException ("Unable to locate RequestDispatcher");
            }
        }
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
    
    /**
     * <p>By default the OPTIONS request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }
    
    /**
     * <p>By default the DELETE request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }
    
    /**
     * <p>By default the PUT request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }
    
    /**
     * <p>By default the TRACE request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
        handleDefault(req, resp);
    }
}
