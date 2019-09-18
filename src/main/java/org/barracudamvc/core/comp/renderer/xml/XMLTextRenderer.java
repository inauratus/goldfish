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
 * $Id: XMLTextRenderer.java 229 2010-04-06 13:13:34Z alci $
 */
package org.barracudamvc.core.comp.renderer.xml;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.TextImpl;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.util.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the default rendering of text into an XML view.
 */
public class XMLTextRenderer extends XMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(XMLTextRenderer.class.getName());

    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a text component
        if (!(comp instanceof BText)) throw new NoSuitableRendererException("This renderer can only render BText components");

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        
        Node node = view.getNode();
        
        //Element Interface - get the first Text child and set value
        //----------------------
        if (node instanceof Element) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on Element interface...");
            BText btext = (BText) comp;
            String text = btext.getText();
            if (text!=null) DOMUtil.setTextInNode((Element) node, text, btext.allowMarkupInText());
        } else if (node instanceof TextImpl) {
        	BText btext = (BText) comp;
            String text = btext.getText();
            if (text!=null) ((TextImpl) node).setTextContent(text);
//            if (text!=null) DOMUtil.setTextInNode((Element) node, text, btext.allowMarkupInText());
        }
        else {
            String errmsg = "Node does not implement Element and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
    
}