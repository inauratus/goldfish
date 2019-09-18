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
 * $Id: ViewContext.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.comp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barracudamvc.core.event.EventContext;
import org.barracudamvc.core.view.ViewCapabilities;
import org.barracudamvc.plankton.data.StateMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface defines the methods needed to implement a ViewContext.
 */
public interface ViewContext extends StateMap {

    public static final String VIEW_CAPABILITIES = ViewContext.class.getName()+".ViewCapabilities";
    public static final String EVENT_CONTEXT = ViewContext.class.getName()+".EventContext";
    public static final String TEMPLATE_NODE = ViewContext.class.getName()+".TemplateNode";
    public static final String DOCUMENT = ViewContext.class.getName()+".Document";
    public static final String REQUEST = ViewContext.class.getName()+".Request";
    public static final String RESPONSE = ViewContext.class.getName()+".Response";

    public ViewCapabilities getViewCapabilities();
    public EventContext getEventContext();
    public Node getTemplateNode();
    public void setOngoingBTemplate(BTemplate btemp);
    public BTemplate getOngoingTemplate();
    public Document getDocument();
    public HttpServletRequest getRequest();
    public HttpServletResponse getResponse();
}