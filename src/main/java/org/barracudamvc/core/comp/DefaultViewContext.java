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
 * $Id: DefaultViewContext.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.comp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.barracudamvc.core.event.EventContext;
import org.barracudamvc.core.event.ViewEventContext;
import org.barracudamvc.core.view.ViewCapabilities;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.plankton.data.StateMap;
import org.barracudamvc.plankton.data.StateMapContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This class provides the default implementation of a ViewContext. A View
 * Context basically exists to provide the component models with the 
 * information they need to actually pass back the proper data to the 
 * component.
 */
public class DefaultViewContext extends StateMapContainer implements ViewContext {

    private BTemplate ongoingTemplate = null;

    public DefaultViewContext(HttpServletRequest ireq, HttpServletResponse iresp) {
        this(null, ireq, iresp);
    }

    public DefaultViewContext() {
        setStateMap(new DefaultStateMap());
    }

    /**
     * Create a DefaultViewContext for a specific EventContext
     */
    public DefaultViewContext(ViewEventContext ivec) {
        this(ivec.getViewCapabilities(), ivec.getRequest(), ivec.getResponse());
        this.putState(ViewContext.EVENT_CONTEXT, ivec);

    }

    /**
     * Create a DefaultViewContext for a specific ViewCapabilities obj
     */
    public DefaultViewContext(ViewCapabilities ivc, HttpServletRequest ireq, HttpServletResponse iresp) {
        setStateMap(new DefaultStateMap());
        if (ivc == null) {
            ivc = new ViewCapabilities(ireq, iresp);
        }
        this.putState(ViewContext.VIEW_CAPABILITIES, ivc);
        this.putState(ViewContext.REQUEST, ireq);
        this.putState(ViewContext.RESPONSE, iresp);
    }

    //--------------- DefaultViewContext -------------------------
    /**
     * Get the underlying ViewCapabilities object
     *
     * @return the underlying ViewCapabilities object
     */
    @Override
    public ViewCapabilities getViewCapabilities() {
        return (ViewCapabilities) this.getState(ViewContext.VIEW_CAPABILITIES);
    }

    /**
     * Get the underlying EventContext object
     *
     * @return the underlying EventContext object
     */
    @Override
    public EventContext getEventContext() {
        return (EventContext) this.getState(ViewContext.EVENT_CONTEXT);
    }

    /**
     * Get the underlying template Node (if it exists)
     *
     * @return the underlying template Node (if it exists)
     */
    @Override
    public Node getTemplateNode() {
        return (Node) this.getState(ViewContext.TEMPLATE_NODE);
    }

    public void setTemplateNode(Node node) {
        putState(ViewContext.TEMPLATE_NODE, node);
    }

    /**
     * Set the underlying template Document (note that this method is NOT part of 
     * the ViewContext interface)
     */
    public void setDocument(Document document) {
        this.putState(ViewContext.DOCUMENT, document);
    }

    /**
     * Get the underlying template Document (if it exists - note that
     * the ViewContext may be constructed BEFORE the Document has actually
     * been loaded, so this property may not be available immediately
     * after object creation. It should always be set, however, by the time
     * models are actually using the ViewContext).
     *
     * @return the underlying template Document (if it exists)
     */
    @Override
    public Document getDocument() {
        return (Document) this.getState(ViewContext.DOCUMENT);
    }

    @Override
    public void setOngoingBTemplate(BTemplate btemp) {
        this.ongoingTemplate = btemp;
    }

    @Override
    public BTemplate getOngoingTemplate() {
        return this.ongoingTemplate;
    }

    @Override
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) this.getState(ViewContext.REQUEST);
    }

    /**
     * Get the underlying HttpServletResponse
     *
     * @return the underlying HttpServletResponse
     */
    @Override
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) this.getState(ViewContext.RESPONSE);
    }

}
