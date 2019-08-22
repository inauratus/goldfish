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
 * $Id: ScriptDetector.java 254 2013-03-01 16:03:20Z charleslowery $
 */
package org.barracudamvc.core.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BScript;
import org.barracudamvc.core.comp.BScriptResource;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.view.ScriptingType;
import org.barracudamvc.core.view.ViewCapabilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * This class simply detects whether or not the
 * client has scripting enabled.
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0.1 (2001-10-22)
 */
public class ScriptDetector {
    //public vars...eventually, these should probably be final

    protected static final Logger logger = Logger.getLogger(ScriptDetector.class.getName());
    /**
     * Flag indicating whether or not to check for client-side
     * scripting
     */
//temporarily turned off...there seems to be a bug in IE 5.5 where the client side script rewriting
//hoses the URL...I need to think about how to handle this. If you need to override this value
//you can do so through the assembler xml file - csc_013002
//    public static boolean DETECT_CLIENT_SCRIPTING_ENABLED = true;
    public static boolean DETECT_CLIENT_SCRIPTING_ENABLED = false;
    /**
     * Flag added to URL's and Forms and sent with requests
     * allowing the server to determine if the client
     * supports scripting
     */
    public static final String SCRIPT_FLAG = "$csjs";

    /**
     * Flag added to URL's and Forms and sent with requests
     * to avoid browser and proxy caching
     */
//    private static final String UNIQUE_FLAG = "$u";
    //-------------------- ScriptDetector ------------------------
    /**
     * This method checks an incoming request to see if it has
     * a scripting flag which indicates whether or not the client
     * supports scripting. If not, it writes a response that
     * is sent back to the client which immediately causes the
     * client to return again. However, this time, with the scripting flag
     * set.
     * <p>Right now, we only call this method if we are handling a GET
     * request (the assumption being that if the client is POSTing data,
     * it probably came from the server in the first place and, thus, should
     * already have the script flag).</p>
     *
     * @param  req   the servlet request
     * @param  resp  the servlet response
     * @return       <code>true</code> if we actually wrote a response
     * @throws       java.io.IOException
     * @throws       javax.servlet.ServletException
     */
    public static boolean checkClientReq(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //the basic idea here is that we want to determine whether or
        //not a client has scripting enabled. So...we write out a response
        if (DETECT_CLIENT_SCRIPTING_ENABLED
                && req.getParameter(SCRIPT_FLAG) == null) {

            //csc_061202.1 - added
            //set the caching hdrs to prevent caching
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expires", System.currentTimeMillis());

            //build the response
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();

            //Note the following pseudo-code: decoded(getRequestURI)==decoded(getContextPath) + getServletPath + getPathInfo
            //Notice that getRequestURI already includes getPathInfo.  So, we don't need to append that to getRequestURI (comment based on old removed code)
            String queryStr = req.getQueryString();
            if (queryStr == null) {
                queryStr = SCRIPT_FLAG;
            } else {
                queryStr += "&" + SCRIPT_FLAG;
            }
            String url = new StringBuffer(60).append(req.getRequestURI()).append("?").append(queryStr).toString();

            out.println("<html>");
            out.println("  <head>");
            out.println("    <title></title>");
            out.print("    <script type=\"text/javascript\">location.replace('");
            out.print(url);
            out.println("=true');</script>");
            out.print("    <noscript><meta http-equiv=\"REFRESH\" CONTENT=\"0; URL=");
            out.print(url);
            out.println("=false\"></noscript>");
            out.println("  </head>");
            out.println("  <body>");
            out.print("    <h3>Redirecting...</h3><p>If you are not automatically redirected, please click <a href=\"");
            out.print(url);
            out.println("=false\">here</a></p>");
            out.println("  </body>");
            out.println("</html>");

            //out.flush();
            resp.flushBuffer(); //instead of out.flush for Servlet 2.2 compatibility (Craig McClanahan)
            return true;
        }
        return false;
    }

    /**
     * The purpose of this method is to prepare an outgoing <code>HTMLDocument</code>
     * with a scripting flag. This ensures that, upon follow-up requests,
     * client scripting support can be determined.
     * <p>This method adjusts all links and forms with a flag marking the client
     * as not supporting scripting. In the case of links it adds the flag
     * to the query string and in the case of forms, a hidden field. When the page
     * is rendered on the client, if the client supports scripting, a script is
     * invoked which re-sets the value of flags to indicate that scripting is
     * supported.</p>
     * <p>Note that this method should not be called until AFTER all standard
     * DOM manipulation has been completed (in other words, think of this as
     * a filter that should be applied right before the final page gets sent back
     * to the client).</p>
     *
     * @param  doc  the DOM <code>HTMLDocument</code> that we wish to process
     * @param  vc   the <code>ViewContext</code> in which this doc is to be rendered
     */
    public static void prepareClientResp(HTMLDocument doc, ViewContext vc) {
        if (DETECT_CLIENT_SCRIPTING_ENABLED) {
            String unique = generateUniqueString(); //make the requests unique to eliminate caching problems

            //first find the body element and add a script component to it
            //this will cause the page to be checked when it loads on the client
            HTMLElement bodyEl = doc.getBody();
            if (bodyEl != null) {
                BScript bsComp = new BScript(BScript.ON_LOAD, "sc_CheckPage();");
                bsComp.addResource(ResourceGateway.EXT_RESOURCE_ID + BScriptResource.JS_SCRIPTING_CHECK);
                bsComp.setView(new DefaultView(bodyEl));
                bsComp.initCycle();
                try {
                    bsComp.render(vc);
                } catch (RenderException e) {
                    logger.error("Fatal error rendering ScriptDetector code");
                }
                bsComp.destroyCycle();
            }

            //next find all the forms and add a SCRIPT_FLAG input element to each
            HTMLCollection forms = doc.getForms();
            for (int i = 0; i < forms.getLength(); i++) {
                Node form = forms.item(i);

                //add a script flag
                Element el = doc.createElement("input");
                el.setAttribute("name", SCRIPT_FLAG);
                el.setAttribute("value", "false");
                el.setAttribute("type", "hidden");
                form.appendChild(el);
            }

            //finally adjust all the Links as well
            HTMLCollection links = doc.getLinks(); //All HTMLAnchorElements and HTMLAreaElements which have a value for the "href" attribute
            for (int i = 0; i < links.getLength(); i++) {
                Element el = (Element) links.item(i);
                String href = el.getAttribute("href");
                el.setAttribute("href", getURLWithScriptFlag(href, unique, false, true));
            }
        }
    }

    /**
     * The purpose of this method is to prepare an outgoing <code>WMLDocument</code>
     * with a scripting flag. This ensures that, upon follow-up requests,
     * client scripting support can be determined.
     *
     * @param  doc  the DOM <code>WMLDocument</code> that we wish to process
     * @param  vc   the <code>ViewContext</code> in which this doc is to be rendered
     */
//    public static void prepareClientResp(WMLDocument doc, ViewContext vc) {
//        TODO: add support for WML Documents
//    }
    /**
     * The purpose of this method is to prepare an outgoing <code>Document</code>
     * with a scripting flag. This ensures that, upon follow-up requests,
     * client scripting support can be determined.
     * <p>Note that it is preferable to send a specific type of document
     * to this method such as an <code>HTMLDocument</code> or a
     * <code>WMLDocument</code> since they provide the advantage of
     * compile-time checking for the type where here, given a generic
     * <code>Document</code>, we use <code>instanceof</code> to check the
     * type of document and then cast <code>Document</code> to the appropriate
     * document type. For instance, use the {@link #prepareClientResp(HTMLDocument, ViewContext)}
     * form if you have an <code>HTMLDocument</code> to prepare</p>
     * <p>This method claims to throw a DOMException for unsupported DOM's.
     * However, since we don't have explicit support for WML, XHTML, or XML documents
     * yet, we'll just let them pass unchanged rather than throw the exception.
     * Leaving the throws clause here for future use to avoid modifying the interface.</p>
     *
     * @param  doc  the DOM <code>Document</code> that we wish to process
     * @param  vc   the <code>ViewContext</code> in which this doc is to be rendered
     * @throws      org.w3c.dom.DOMException - if the document passed in cannot be
     *                cast to one of the currently supported types
     */
    public static void prepareClientResp(Document doc, ViewContext vc) throws DOMException {
        if (DETECT_CLIENT_SCRIPTING_ENABLED) {
            if (doc instanceof HTMLDocument) {
                prepareClientResp((HTMLDocument) doc, vc);
//            } else if (doc instanceof WMLDocument) {
//                //TODO: add support for WML Documents
            } else {
                //throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "The document passed in is not currently supported");
                //do nothing
            }
        }
    }

    /**
     * This method prepares a URL for redirect by adding the appropriate
     * client scripting flag.
     *
     * @param  url  the URL to redirect the client to
     * @param  vc   the <code>ViewCapabilities</code> of the client
     * @return      a modified url if script detection is enabled, unmodified if not
     */
    public static String prepareRedirectURL(String url, ViewCapabilities vc) {
        if (DETECT_CLIENT_SCRIPTING_ENABLED) {
            String unique = generateUniqueString(); //make the requests unique to eliminate caching problems
            boolean scriptingEnabled = !(vc.getScriptingType() instanceof ScriptingType.None);
            return getURLWithScriptFlag(url, unique, scriptingEnabled);
        } else {
            return url;
        }
    }

    /**
     * This method evaluates a client request to see whether or not
     * scripting is enabled (based on the presence of the SCRIPT_FLAG
     * in the parameters). This function will either return true, false,
     * or null (which means indeterminate).
     *
     * @param  req  the servlet request
     * @return      <code>true</code> if we can tell for sure that the client
     *                has scripting enabled, <code>false</code> if we know for
     *                sure that it does not, and <code>null</code> if we cannot
     *                determine for sure
     */
    public static Boolean scriptingEnabled(HttpServletRequest req) {
        String s = req.getParameter(SCRIPT_FLAG);
        if (s == null) {
            return null;
        } else {
            return new Boolean(s.equals("true"));
        }
    }

    /**
     * This method modifies URL Strings by adding a script flag. It allows for a
     * special check for Strings derived from the href attribute of link elements
     * including HTMLAnchorElement and HTMLAreaElement to make sure that only the
     * appropriate href values get modified (ie... skipping values containing 'mailto:',
     * 'javascript:', 'data:', or 'jar:' protocols as well as cases where the script
     * flag already exists).
     *
     * @param  url               the url in which to embed the script flag
     * @param  unique            a moderately random String to make the url unique
     *                             (to avoid browser caching)
     * @param  scriptingEnabled  the value to give to the <code>SCRIPT_FLAG</code>
     * @param  doHrefCheck       whether or not to check for special cases where the
     *                             <code>SCRIPT_FLAG</code> should not be applied
     * @return                   a modified url (or unmodified if <code>doHrefCheck</code>
     *                             is <code>true</code> and a special case is matched)
     */
    private static String getURLWithScriptFlag(String url, String unique, boolean scriptingEnabled, boolean doHrefCheck) {
        if (doHrefCheck) {
            if (!url.startsWith("mailto:") && !url.startsWith("javascript:")
                    && !url.startsWith("data:")
                    && !url.startsWith("jar:")
                    && !(url.indexOf(SCRIPT_FLAG) > -1)) { //&& url.indexOf(UNIQUE_FLAG)>-1)) {
                return getURLWithScriptFlag(url, unique, scriptingEnabled);
            } else {
                return url;
            }
        }
        return getURLWithScriptFlag(url, unique, scriptingEnabled);
    }

    /**
     * This method modifies URL Strings by adding a <code>SCRIPT_FLAG</code>.
     *
     * @param url               the url in which to embed the script flag
     * @param unique            a moderately random String to make the URL unique
     *                            (to avoid browser caching)
     * @param scriptingEnabled  the value to give to the <code>SCRIPT_FLAG</code>
     * @return                  a modified url
     */
    private static String getURLWithScriptFlag(String url, String unique, boolean scriptingEnabled) {
        String url2 = url;
        String hash = "";
        int hashPos = url.indexOf("#");
        String sep = "?";
        if (url.indexOf(sep) > -1) {
            sep = "&";
        }
        if (hashPos > -1) {
            url2 = url.substring(0, hashPos - 1);
            hash = url.substring(hashPos, url.length());
        }
        return new StringBuffer(60).append(url2).append(sep).append(SCRIPT_FLAG).append("=").append(scriptingEnabled).append(hash).toString(); //.append("&").append(UNIQUE_FLAG).append("=").append(unique)
    }

    /**
     * This method generates a pseudo-random string which can be used to
     * make http requests unique to eliminate browser and proxy
     * caching problems.
     *
     * @return  a unique string
     */
    private static String generateUniqueString() {
        return String.valueOf(new Object().hashCode()); //avoid StringBuffer object creation from concatenation
    }
}
