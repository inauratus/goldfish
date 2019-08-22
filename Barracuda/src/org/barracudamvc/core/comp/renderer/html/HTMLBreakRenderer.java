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
import org.barracudamvc.core.comp.BBreak;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the default rendering of image into an HTML view.
 */
public class HTMLBreakRenderer extends HTMLComponentRenderer {

	protected static final Logger logger = Logger.getLogger(HTMLBreakRenderer.class.getName());

    @Override
	public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc)
            throws UnsupportedFormatException {

        //Node defaultNode = null;
        Element elm = doc.createElement("BR");
        //defaultNode = doc.createElement("SPAN");
 
        if ((comp instanceof BBreak) && ((BBreak)comp).getAttrMap() != null && ((BBreak)comp).getAttrMap().containsKey("class")) {
        	elm.setAttribute("class", (String) ((BBreak)comp).getAttr("class"));
        }

        //return defaultNode;
        return elm;
    }

    /**
     * 
     */
    @Override
	public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        // make sure the component is an image component
        if (!(comp instanceof BBreak))
            throw new NoSuitableRendererException(
                    "This renderer can only render BBreak components");

        // show what we're doing
        // showNodeInterfaces(view, logger);

        // first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);
    }
}
