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
 * $Id: HTMLToggleRenderer.java 251 2012-11-09 18:49:25Z charleslowery $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BToggleButton;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLInputElement;

/**
 * This class handles the default rendering of a list in an HTML view.
 */
public class HTMLToggleRenderer extends HTMLInputRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLToggleRenderer.class.getName());

    
    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a list component
        if (!(comp instanceof BToggleButton)) throw new NoSuitableRendererException("This renderer can only render BToggleButton components; comp is of type:"+comp.getClass().getName());

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);
        
        Node node = view.getNode();
        
        //..HTMLInputElement - set the "selected" attribute
        if (node instanceof HTMLInputElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLInputElement interface...");
            HTMLInputElement el = (HTMLInputElement) node;

            //set selected
            boolean isChecked = ((BToggleButton)comp).isSelected();
            
            Object checked = comp.getAttr("checked");
            if(checked instanceof String && checked.equals("checked")) {
                isChecked = true;
            }
            if(isChecked) {
                el.setChecked(isChecked);
            }
        } else {
            String errmsg = "Node does not implement HTMLInputElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
}