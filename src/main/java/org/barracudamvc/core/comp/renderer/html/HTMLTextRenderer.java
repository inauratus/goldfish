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
 * $Id: HTMLTextRenderer.java 194 2007-11-22 20:33:08Z alci $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.util.dom.DOMUtil;
import org.barracudamvc.plankton.Classes;
import org.barracudamvc.plankton.StringUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLAreaElement;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLTitleElement;

/**
 * This class handles the default rendering of text into an HTML view.
 *
 * Q: maybe rather than just setting the text in Text node, the text renderer
 * should represent the string as a CDATASection. This would enable you to 
 * easily set text as chunks of HTML. Hmmm...need to think about this...
 */
public class HTMLTextRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLTextRenderer.class.getName());

    
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

        BText btext = (BText) comp;
        String text = btext.getText();
        
        //Shortcircuit instanceof checks if no text to set
        if (text == null) {
        	logger.info("Null text, skipping rendering.");
        	return;
        }
        
        Node node = view.getNode();
        
        //HTMLDocument Interface - set the "title" attribute
        //----------------------
        if (node instanceof HTMLDocument) {
            //..HTMLDocument - set the "title" attribute
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLDocument interface...");
            ((HTMLDocument) node).setTitle(StringUtil.sanitize(text));
    
        //HTMLElement Interface
        //---------------------
        //Supported Elements:
        //..HTMLAreaElement - set the "alt" attribute
        //..HTMLImageElement - set the "alt" attribute
        //..HTMLInputElement - set the "value" attribute
        //..HTMLTitleElement - set the "text" attribute
        //..For everything else - get the first Text child and set 
        //....value if it exists
        } else if (node instanceof HTMLElement) {

            //..HTMLAreaElement - set the "alt" attribute
            if (node instanceof HTMLAreaElement) {
                if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLAreaElement interface...");
                ((HTMLAreaElement) node).setAlt(StringUtil.sanitize(text));

            //..HTMLImageElement - set the "alt" attribute
            } else if (node instanceof HTMLImageElement) {
                if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLImageElement interface...");
                ((HTMLImageElement) node).setAlt(StringUtil.sanitize(text));

            //..HTMLInputElement - set the "value" attribute
            } else if (node instanceof HTMLInputElement) {
                if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLInputElement interface...");
                ((HTMLInputElement) node).setValue(StringUtil.sanitize(text));
            
            //..HTMLTitleElement - set the "text" attribute
            } else if (node instanceof HTMLTitleElement) {
                if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLTitleElement interface...");
                ((HTMLTitleElement) node).setText(StringUtil.sanitize(text));
            
            //..For everything else - get the first Text child and set 
            //....value if it exists
            } else {
                if (logger.isInfoEnabled())
                    logger.info("Rendering based on " + Classes.getShortClassName(node.getClass()) + " interface in first child Text element...");
                DOMUtil.setTextInNode(
                        (Element) node,
                        btext.allowMarkupInText() ? text : StringUtil.sanitize(text),
                        btext.allowMarkupInText(),
                        btext.insertBefore()
                ); // fro_112207
            }
        } else {
            String errmsg = "Node does not implement HTMLElement or HTMLDocument and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
    
}