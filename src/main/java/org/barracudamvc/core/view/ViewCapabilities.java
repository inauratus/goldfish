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
 * $Id: ViewCapabilities.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.view;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barracudamvc.plankton.l10n.Locales;

/**
 * This class defines the the cpabilities & preferences of the 
 * client view. Specifically, it provides the component with 
 * 4 useful pieces of information:
 *
 * <ol>
 *        <li>FormatType - what format is the output expected to be in (HTML, WML, 
 *            XMLC, etc.)</li>
 *        <li>ScriptingType - what scripting capabilities does the client 
 *            support (JavaScript, VBScript, WMLScript, etc)</li>
 *        <li>ClientType - what browser is the client using (HTML_3x, HTML_4x, 
 *            WML_1_2, etc)</li>
 *        <li>ClientLocale - what Locale does the client prefer content in?</li>
 * </ol>
 *
 * <p>Note that it's up to the components to actually respond to this. Just because
 * a client supports HTML 4.0 doesn't mean the component can't render in HTML 3.2
 * (although the component does have an implicit responsibility to be well behaved
 * and send back meaningful content that will work in the clients configuration)
 *
 * <p>It should also be noted that the FormatType, ScriptingType, and ClientType 
 * classes are all defined hierarchically. This is particularly important because
 * it allows components to support a given range of functionality...if a component
 * supports HTML 4x and HTML 4.1 comes out, the component should still work just fine.
 *
 * <p>Note that we don't determine the various types from the source until they are
 * actually requested (unless of course they are manually specified).
 */
public class ViewCapabilities {

    protected HttpServletRequest req = null;
    protected HttpServletResponse resp = null;

    protected FormatType formatType = null;
    protected ScriptingType scriptingType = null;
    protected ClientType clientType = null;
    protected Locale clientLocale = null;
    
    /**
     * Create an empty ViewCapabilities object. In order to use this 
     * class, you will either need to manually set the req/resp source,
     * or manually specify the various type properties.
     */
    public ViewCapabilities() {
        this(null, null, null, null);    
    }
    
    /**
     * This constructor creates a ViewCapabilities object from the servlet
     * response and request objects.
     *
     * @param req the servlet request
     * @param resp the servlet response
     */
    public ViewCapabilities(HttpServletRequest req, HttpServletResponse resp) {
        setSource(req, resp);
    }
    
    /**
     * This constructor allows you to manually specify the various
     * types for a ViewCapabilities object.
     *
     * @param formatType the format type for the view
     * @param clientType the actual client view type
     * @param scriptingType the scripting type supported by the view
     * @param clientLocale the target client locale
     */
    public ViewCapabilities(FormatType formatType, ClientType clientType, ScriptingType scriptingType, Locale clientLocale) {
        setFormatType(formatType);
        setClientType(clientType);
        setScriptingType(scriptingType);
        setClientLocale(clientLocale);
    }
    
    /**
     * This convenience method allows you to specify the servlet req/resp 
     * source objects (from which the types will be automagically determined)
     *
     * @param ireq the servlet request
     * @param iresp the servlet response
     */
    public void setSource(HttpServletRequest ireq, HttpServletResponse iresp) {
        req = ireq;
        resp = iresp;
    }
    
    /**
     * Manually specify a format type
     *
     * @param iformatType the format type for the view
     */
    public void setFormatType(FormatType iformatType) {
        formatType = iformatType;
    }
    
    /**
     * Get the current format type
     *
     * @return the current format type
     */
    public FormatType getFormatType() {
        if (formatType==null) {
            formatType = ViewUtil.getFormatType(req);
        }
        return formatType;
    }

    /**
     * Manually specify a client type
     *
     * @param iclientType the actual client view type
     */
    public void setClientType(ClientType iclientType) {
        clientType = iclientType;
    }
    
    /**
     * Get the current client type
     *
     * @return the current client type
     */
    public ClientType getClientType() {
        if (clientType==null) {
            clientType = ViewUtil.getClientType(req);
        }
        return clientType;
    }

    /**
     * Manually specify a scripting type
     *
     * @param iscriptingType the scripting type supported by the view
     */
    public void setScriptingType(ScriptingType iscriptingType) {
        scriptingType = iscriptingType;
    }
    
    /**
     * Get the current scripting type
     *
     * @return the current scripting type
     */
    public ScriptingType getScriptingType() {
        if (scriptingType==null) {
            scriptingType = ViewUtil.getScriptingType(req);
        }
        return scriptingType;
    }

    /**
     * Manually specify a target locale
     *
     * @param iclientLocale the target client locale
     */
    public void setClientLocale(Locale iclientLocale) {
        clientLocale = iclientLocale;
    }
    
    /**
     * Get the current target locale
     *
     * @return the current target locale
     */
    public Locale getClientLocale() {
        if (clientLocale==null) {
            clientLocale = Locales.getClientLocale(req, resp);
        }
        return clientLocale;
    }
    
    /**
     * get a string representation of the ViewCapabilities
     *
     * @return a string representation of the ViewCapabilities
     */
    public String toString() {
        return super.toString()+" {"+
           "ft="+getFormatType()+", "+
           "ct="+getClientType()+", "+
           "st="+getScriptingType()+", "+
           "loc="+(clientLocale==null ? "default" : getClientLocale().toString())+"}";
    }
}