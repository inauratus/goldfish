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
 * $Id: HTMLSelectRenderer.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BSelect;
import org.barracudamvc.core.comp.ItemMap;
import org.barracudamvc.core.comp.ListModel;
import org.barracudamvc.core.comp.ListSelectionModel;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;

/**
 * This class handles the default rendering of a list in an HTML view.
 */
public class HTMLSelectRenderer extends HTMLListRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLSelectRenderer.class.getName());

    
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
    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {  //csc_110501.1
        //ask the renderer to create the default Node
        if (vc.getTemplateNode() instanceof HTMLSelectElement) {
            return super.createDefaultNode(doc, comp, vc);        
        }
        
        Node defaultNode = doc.createElement("SELECT");
        if (logger.isInfoEnabled()) logger.info("Creating default node:"+defaultNode);
        return defaultNode;
    }

    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a list component
        if (!(comp instanceof BSelect)) throw new NoSuitableRendererException("This renderer can only render BSelect components; comp is of type:"+comp.getClass().getName());

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        BSelect bscomp = (BSelect) comp;
        ListModel lm = bscomp.getModel();
        ListSelectionModel lsm = bscomp.getSelectionModel();
        Node node = view.getNode();
        
        //..HTMLSelectElement - set the "multiples" and "size" attributes,
        //        and then mark items selected/unselected. Finally, set the
        //        "value" attribute for each option element.
        if (node instanceof HTMLSelectElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLSelectElement interface...");
            HTMLSelectElement el = (HTMLSelectElement) node;

            //set the size (if specified)
            if (bscomp.getViewSize()!=null) {
                el.setSize(bscomp.getViewSize().intValue());
            }

            //set allow multiples (get this from the selection model)
            el.setMultiple(lsm.getSelectionMode()!=ListSelectionModel.SINGLE_SELECTION);
            
            //now iterate through all the children (this list will 
            //already have been populated by the parent class renderer 
            //from the model)                
            HTMLCollection options = el.getOptions();
            for (int i=0, max=options.getLength(); i<max; i++) {
                HTMLOptionElement opt = (HTMLOptionElement) options.item(i);
                
                //mark them selected/unselected by looking at the selection model

                //jkr_20030520.1 - setSelected(boolean) isn't actually part of the
                //html dom level1 api. Xerces1 included an early version of the
                //html dom level2 draft interfaces which were released with the
                //same package as the level1 dom. The level2 dom is now in the
                //namespace org.w3c.dom.html2. The switch in namespaces was made
                //sometime between the following releases...
                //http://www.w3.org/TR/2001/WD-DOM-Level-2-HTML-20011210/java-binding.html
                //and
                //http://www.w3.org/TR/2002/CR-DOM-Level-2-HTML-20020605/java-binding.html
                //Xerces2 includes the original level1
                //html dom. In order to be compatible with that, we can't use the
                //setSelected(boolean) method (and, apparently, never should have).
//                opt.setSelected(lsm.isSelectedIndex(i));
                if (lsm.isSelectedIndex(i)) opt.setAttribute("selected", "selected");
                else opt.removeAttribute("selected");
                
                //set the value: if the item in the model implements ItemMap,
                //use that value; otherwise, just set it equal to the text
                Object item = lm.getItemAt(i);
                if (item==null) continue;
                if (item instanceof ItemMap) {
                    opt.setValue(((ItemMap) item).getKey().toString());
                } else {
                    opt.setValue(item.toString());
                }
            }
            
        } else {
            String errmsg = "Node does not implement HTMLSelectElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
}