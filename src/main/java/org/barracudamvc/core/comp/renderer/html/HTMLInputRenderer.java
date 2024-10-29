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
 * $Id: HTMLInputRenderer.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BInput;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.plankton.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLInputElement;

/**
 * This class handles the default rendering of a list in an HTML view.
 */
public class HTMLInputRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLInputRenderer.class.getName());
    
    /**
     * The purpose of this method is to create a default Node to be used when
     * the component is not bound to any specific view. 
     *
     * @param doc the master Document which can be used to create elements
     *        from scratch
     * @param comp the component that we're dealing with for the current request
     * @param vc the view context for the current request
     * @return a default node (created from scratch)
     * @throws UnsupportedFormatException if the renderer has no default node
     */
    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {
        if (vc.getTemplateNode() instanceof HTMLInputElement) {
            return super.createDefaultNode(doc, comp, vc);
        }
        
        Node defaultNode = doc.createElement("INPUT");
        if (logger.isInfoEnabled()) logger.info("Creating default node:"+defaultNode);
        return defaultNode;
    }

    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a list component
        if (!(comp instanceof BInput)) throw new NoSuitableRendererException("This renderer can only render BInput components; comp is of type:"+comp.getClass().getName());

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        
        BInput bicomp = (BInput) comp;
        Node node = view.getNode();
        
        //..HTMLInputElement - set the "type" and "value" attributes
        if (node instanceof HTMLInputElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLInputElement interface...");
            HTMLInputElement el = (HTMLInputElement) node;

            //set the type (if specified)
            String type = bicomp.getType();
            if (type!=null) el.setAttribute("type", type);

            //set the value (if specified)
            String value = bicomp.getValue();
            if (value!=null) el.setValue(StringUtil.sanitize(value));
            
        } else {
            String errmsg = "Node does not implement HTMLInputElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
}