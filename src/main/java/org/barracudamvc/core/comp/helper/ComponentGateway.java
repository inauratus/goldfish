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
 * $Id: ComponentGateway.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.helper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BlockIterator;
import org.barracudamvc.core.comp.DefaultViewContext;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.TemplateDirective;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.event.ApplicationGateway;
import org.barracudamvc.core.event.ClientSideRedirectException;
import org.barracudamvc.core.event.DefaultEventContext;
import org.barracudamvc.core.event.EventException;
import org.barracudamvc.core.event.ViewEventContext;
import org.barracudamvc.core.event.helper.BlockIterateHandler;
import org.barracudamvc.core.helper.servlet.DefaultServletRequestWrapper;
import org.barracudamvc.core.helper.servlet.DefaultServletResponseWrapper;
import org.barracudamvc.core.helper.servlet.ScriptDetector;
import org.barracudamvc.core.util.dom.DOMWriter;
import org.barracudamvc.core.util.dom.DefaultDOMWriter;
import org.barracudamvc.core.view.ViewCapabilities;
import org.barracudamvc.plankton.data.ObjectRepository;
import org.barracudamvc.plankton.exceptions.ExceptionUtil;
import org.barracudamvc.plankton.exceptions.NestableException;
import org.barracudamvc.plankton.http.URLRewriter;
import org.w3c.dom.Document;

/**
 * <p>The component gateway is a servlet that provides a very simple
 * interface to the Barracuda component model
 */
public abstract class ComponentGateway extends HttpServlet {

    //public vars...eventually, these should probably be final
    protected static final Logger logger = Logger.getLogger(ComponentGateway.class.getName());
//    public static boolean printPretty = false;
//    public static boolean preventCaching = false;
    public boolean recycleChildren = false;

    //...LocalObjectRepository constants (available for apps to access)
    public static final String HTTP_SERVLET_REQUEST = ApplicationGateway.HTTP_SERVLET_REQUEST;                  //(HttpServletReques)
    public static final String HTTP_SERVLET_RESPONSE = ApplicationGateway.HTTP_SERVLET_RESPONSE;                //(HttpServletResponse)



    //-------------------- ComponentGateway ----------------------
    /**
     * <p>Handle the default HttpRequest. This is the method developers
     * will typically override. The developers shaould add any components
     * to the root component and then return the underlying DOM Document (that 
     * backs their components) so it can be rendered
     *
     * @param root the root component which will get rendered as a result 
     *         of this request
     * @param vc the ViewContext object describes what features the 
     *         client view is capable of supporting
     * @param req the servlet request
     * @param resp the servlet response
     * @return the Document to be rendered
     * @throws ServletException
     * @throws IOException
     */
    public abstract Document handleDefault (BComponent root, ViewContext vc, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, RenderException, EventException;

    /**
     * <p>Handle an EventException. Basically, this is where we handle the 
     * really bad, unexpected type of event exceptions. Generally, as you code,
     * if you want to interrupt the dispatch and fire a new event, you should
     * throw an InterruptDispatchException. Only throw EventExceptions in
     * truly exceptional circumstances.
     *
     * @param e the EventException to handle
     * @param vc the ViewContext object describes what features the 
     *         client view is capable of supporting
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleEventException (EventException e, ViewContext vc, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //first see what the base exception is...if it's a servlet exception
        //or an IOException, rethrow it...
        Exception rootException = NestableException.getRootException(e);
        if (rootException instanceof ServletException) throw (ServletException) rootException;
        if (rootException instanceof IOException) throw (IOException) rootException;

        //csc_061202.1 - added    
        //set the caching hdrs (this will allow the static resources to be cached by the browser)
        resp.setHeader("Cache-Control","max-age=0");
        resp.setDateHeader("Last-Modified", System.currentTimeMillis());

        //if we get an EventException, log it and print the base exception
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        ExceptionUtil.logExceptionAsHTML(out, e, req);
        logger.warn("Unexpected event exception: ", e);
        if (rootException!=e) {
            logger.warn("Root Exception: ", rootException);
        }
    }

    /**
     * <p>Handle a RenderException. Basically, this is where we handle the 
     * really bad, unexpected type of errors that occur while unexpectedldy 
     * rendering the component hierarchy.
     *
     * @param e the RenderException to handle
     * @param vc the ViewContext object describes what features the 
     *         client view is capable of supporting
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleRenderException (RenderException e, ViewContext vc, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //first see what the base exception is...if it's a servlet exception
        //or an IOException, rethrow it...
        Exception rootException = NestableException.getRootException(e);
        if (rootException instanceof ServletException) throw (ServletException) rootException;
        if (rootException instanceof IOException) throw (IOException) rootException;

        //csc_061202.1 - added    
        //set the caching hdrs (this will allow the static resources to be cached by the browser)
        resp.setHeader("Cache-Control","max-age=0");
        resp.setDateHeader("Last-Modified", System.currentTimeMillis());

        //if we get a RenderException, log it and print the base exception
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        ExceptionUtil.logExceptionAsHTML(out, e, req);
        logger.warn("Unexpected event exception: ", e);
        if (rootException!=e) {
            logger.warn("Root Exception:", rootException);
        }
    }

    /**
     * <p>Get a DOMWriter. By default, we use a DefaultDOMWriter. If
     * you'd like to use something else, override this method.
     *
     * @return a DOMWriter to be used to render the DOM
     */
    public DOMWriter getDOMWriter() {
        //return DefaultDOM writer
//        return new DefaultDOMWriter(printPretty, preventCaching);
        //since we don't override defaults here and there are no setter methods for
        //printPretty, preventCaching, and maxAge in this class, don't bother storing
        //this stuff in this class.  Let DefaultDOMWriter deal with the defaults.
        //If one wants specific behavior, override this method and set up the DOMWriter
        //however you want.
        return new DefaultDOMWriter();
    }

    /**
     * Get a block iterator (optional)
     */
    public BlockIterator getIterator(String key) {
        return null;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean handled = ScriptDetector.checkClientReq(req, resp);
        if (!handled) handleDefault(new DefaultServletRequestWrapper(req), new DefaultServletResponseWrapper(resp));
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(new DefaultServletRequestWrapper(req), new DefaultServletResponseWrapper(resp));
    }
    
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(new DefaultServletRequestWrapper(req), new DefaultServletResponseWrapper(resp));
    }
    
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(new DefaultServletRequestWrapper(req), new DefaultServletResponseWrapper(resp)); 
    }
    
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(new DefaultServletRequestWrapper(req), new DefaultServletResponseWrapper(resp));
    }
    
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
        handleDefault(new DefaultServletRequestWrapper(req), new DefaultServletResponseWrapper(resp));
    }

    protected void handleDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DefaultViewContext vc = null;
        try {
            ObjectRepository.setupSessionRepository(req);
            
            ObjectRepository lor = ObjectRepository.getLocalRepository(); 
            lor.putState(HTTP_SERVLET_REQUEST, req);                     
            lor.putState(HTTP_SERVLET_RESPONSE, resp);                           


            vc = new DefaultViewContext(new ViewCapabilities(req, resp), req, resp);

            BComponent broot = new BComponent();
            broot.setName("Root");

            Document doc = handleDefault(broot, vc, req, resp);
            vc.setDocument(doc); //csc_072604_2
            broot.initCycle();
            broot.render(vc);

            //its possible the implementor may want to recycle children...if so, remove them
            //prior to calling destroy on the root
            if (recycleChildren) {
                List children = broot.getChildren();
                if (children!=null) {
                    for (int i=children.size()-1; i>=0; i--) {
                        broot.removeChild(i);            
                    }
                }
            }

            broot.destroyCycle();

   
            lor = ObjectRepository.getLocalRepository();
            Boolean b = (Boolean) lor.getState(TemplateDirective.HAS_BLOCK_ITERATOR);
            boolean hasbi = (b!=null ? b.booleanValue() : false);

            //if so, we want to delegate rendering to BlockIterateHandler
            if (hasbi) {
                DefaultEventContext vec = new DefaultEventContext(null, vc.getViewCapabilities(), this.getServletConfig(), req, resp, null);    //this is a hack - we don't have a vec readily available, so construct one since the block iterate handler expects it
                BlockIterateHandler bih = new LocalBlockIterateHandler();
                bih.handleViewEvent(vec, doc);
            
            //otherwise, we're just going to render it out ourselves (normal)
            } else {
                ScriptDetector.prepareClientResp(doc, vc);
                this.getDOMWriter().write(doc, resp);
            }



        } catch (ClientSideRedirectException re) {

            String url = URLRewriter.encodeRedirectURL(req, resp, re.getRedirectURL());
            url = ScriptDetector.prepareRedirectURL(url, vc.getViewCapabilities());     //csc_102501.2
            resp.sendRedirect(url);    

        } catch (RenderException e) {
            handleRenderException(e, vc, req, resp);
            
        } catch (EventException e) {
            handleEventException(e, vc, req, resp);
            
        } finally {
            //make sure we always clean up session/local repository stuff
            ObjectRepository.removeSessionRepository();                                     //csc_022101.1
            ObjectRepository.removeLocalRepository();                                       //csc_022101.1
        }
    }

    class LocalBlockIterateHandler extends BlockIterateHandler {
        public Class getTemplateClass() {
            return null;
        }

        /**
         * delegate the getting of the DOMWriter back to the handler
         */
        public DOMWriter getDOMWriter() {
            return ComponentGateway.this.getDOMWriter();
        }

        /**
         * this is where you provide iterators for blocks as they are encountered in the template
         */
        public BlockIterator getIterator(String key) {
            return ComponentGateway.this.getIterator(key);
        }

        /**
         * Handle the ViewEvent
         */
        public void handleViewEvent(ViewEventContext vec) throws EventException, ServletException, IOException {
            throw new RuntimeException("SimpleBlockIterateHandler does not support the handleViewEvent(ViewEventContext vec) method");
        }
    }
}
