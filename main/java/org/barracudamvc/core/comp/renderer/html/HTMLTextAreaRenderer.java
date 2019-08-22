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
 * $Id: HTMLTextAreaRenderer.java 194 2007-11-22 20:33:08Z alci $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BTextArea;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTextAreaElement;

/**
 * This class handles the default rendering of textarea into an HTML view.
 * This component is usefull in composed component
 */
public class HTMLTextAreaRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLTextAreaRenderer.class.getName());

    
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
        if (vc.getTemplateNode() instanceof HTMLTextAreaElement) {
            return super.createDefaultNode(doc, comp, vc);
        }
        
        Node defaultNode = doc.createElement("TEXTAREA");
        if (logger.isInfoEnabled()) logger.info("Creating default node:"+defaultNode);
        return defaultNode;
    }
    
    /**
     * 
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a text component
        if (!(comp instanceof BTextArea)) throw new NoSuitableRendererException("This renderer can only render BTextArea components");

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        BTextArea btextArea = (BTextArea) comp;
        
        BTextArea btacomp = (BTextArea) comp;
        Node node = view.getNode();
        
        //..HTMLInputElement - set the "type" and "value" attributes
        if (node instanceof HTMLTextAreaElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLInputElement interface...");
            HTMLTextAreaElement el = (HTMLTextAreaElement) node;

            //set the value (if specified)
            String value = btacomp.getValue();
            if (value!=null) el.setTextContent(value);

            int rows = btextArea.getRows();
            if (rows>0) el.setRows(rows);
            
            int cols = btextArea.getCols();
            if (cols>0) el.setCols(cols);
            
        } else {
            String errmsg = "Node does not implement HTMLInputElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
    
}