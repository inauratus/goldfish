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
import org.barracudamvc.core.comp.BAnchor;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLAnchorElement;

/**
 * This class handles the default rendering of an anchor into an HTML view.
 */
public class HTMLAnchorRenderer extends HTMLComponentRenderer {

	protected static final Logger logger = Logger.getLogger(HTMLAnchorRenderer.class.getName());

    @Override
	public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc)
            throws UnsupportedFormatException {

    	Node defaultNode = null;
    	
    	if (vc.getTemplateNode() instanceof HTMLAnchorElement) {
            defaultNode = super.createDefaultNode(doc, comp, vc);
        } else {
        	defaultNode = doc.createElement("A");
        }
        return defaultNode;
    }

    /**
     * 
     */
    @Override
	public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        // make sure the component is an anchor component
        if (!(comp instanceof BAnchor))
        	throw new NoSuitableRendererException("This renderer can only render BAnchor components");
        
        BAnchor aComp = (BAnchor) comp;
        Node node = view.getNode();

        // first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);
        
        //..HTMLInputElement - set the "type" and "value" attributes
        if (node instanceof HTMLAnchorElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLAnchorElement interface...");
            HTMLAnchorElement el = (HTMLAnchorElement) node;

            //set the href (if specified)
            String href = aComp.getHref();
            if (href!=null) el.setHref(href);
            
            //set the target (if specified)
            String target = aComp.getTarget();
            if (target != null) {
            	el.setTarget(target);
            }

            String text = aComp.getText();
            if (text != null) {
                BText textComp = new BText(text);
                textComp.setView(new DefaultView(node));
                textComp.setAllowMarkupInText(aComp.allowMarkupInText());
                aComp.addStepChild(textComp, true);
            }
            
        } else {
            String errmsg = "Node does not implement HTMLAnchorElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
}
