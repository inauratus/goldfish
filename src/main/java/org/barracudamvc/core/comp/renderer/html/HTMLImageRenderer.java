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
import org.barracudamvc.core.comp.BImage;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.plankton.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLImageElement;

/**
 * This class handles the default rendering of image into an HTML view.
 */
public class HTMLImageRenderer extends HTMLComponentRenderer {

    protected static final Logger log = Logger.getLogger(HTMLImageRenderer.class.getName());

    
    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {
        
    	Node defaultNode = null;
    	
    	if (vc.getTemplateNode() instanceof HTMLImageElement) {
            defaultNode = super.createDefaultNode(doc, comp, vc);
        } else {
        	defaultNode = doc.createElement("IMG");
        }
        return defaultNode;
    }

    /**
     * 
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is an image component
        if (!(comp instanceof BImage))
        	throw new NoSuitableRendererException("This renderer can only render BImage components");

        //show what we're doing
        //showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        BImage bimage = (BImage) comp;
        String alt = StringUtil.sanitize(bimage.getAlt());
        String title = StringUtil.sanitize(bimage.getTitle());
        String src = StringUtil.sanitize(bimage.getSrc());
        String height = bimage.getHeight();
        String width = bimage.getWidth();
        
        Node node = view.getNode();
        
        //HTMLElement Interface
        //---------------------
        //Supported Elements:
        //..HTMLImageElement
        if (node instanceof HTMLImageElement) {
            if (log.isInfoEnabled()) log.info("Rendering based on HTMLImageElement interface...");
            ((HTMLImageElement) node).setAlt(alt);
            ((HTMLImageElement) node).setTitle(title); 
            ((HTMLImageElement) node).setSrc(src);
            if (height != null)
            	((HTMLImageElement) node).setHeight(height);
            if (width != null)
            	((HTMLImageElement) node).setWidth(width);
        } else {
            String errmsg = "Node does not implement HTMLImageElement and cannot be rendered: "+node;
            log.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
}
