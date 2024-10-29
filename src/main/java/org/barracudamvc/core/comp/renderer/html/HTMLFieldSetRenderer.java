/*
 * HTMLImageRenderer.java
 *
 * Created on 9 dec 2006
 * Copyright (c) 2006 mecadu.org
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
 */

package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BFieldSet;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.plankton.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLFieldSetElement;

/**
 * This class handles the default rendering of a fieldset into an HTML view.
 */
public class HTMLFieldSetRenderer extends HTMLComponentRenderer {

	protected static final Logger log = Logger.getLogger(HTMLFieldSetRenderer.class.getName());

    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc)
            throws UnsupportedFormatException {

        Node defaultNode = null;
    	
    	if (vc.getTemplateNode() instanceof HTMLFieldSetElement) {
            defaultNode = super.createDefaultNode(doc, comp, vc);
        } else {
        	defaultNode = doc.createElement("FIELDSET");
        }
        return defaultNode;
    }

    /**
     * 
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        // make sure the component is an image component
        if (!(comp instanceof BFieldSet))
            throw new NoSuitableRendererException("This renderer can only render BFieldSet components");

        BFieldSet aComp = (BFieldSet) comp;
        Node node = view.getNode();

        // first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);
        
      //..HTMLInputElement - set the "type" and "value" attributes
        if (node instanceof HTMLFieldSetElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLFieldSetElement interface...");
//            HTMLFieldSetElement el = (HTMLFieldSetElement) node;

            //add or modify a legend if specified
            String legend = aComp.getLegend();
            
            if (legend != null) {
            	Node legendNode = vc.getDocument().createElement("LEGEND");
            	// TODO s'insérer dans une 'legend' existante
            	node.insertBefore(legendNode, node.getFirstChild());
                legendNode.setTextContent(StringUtil.sanitize(legend));
            }
            
        } else {
            String errmsg = "Node does not implement HTMLAnchorElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
}
