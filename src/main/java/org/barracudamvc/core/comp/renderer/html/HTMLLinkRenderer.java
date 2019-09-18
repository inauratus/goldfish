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
 * $Id: HTMLLinkRenderer.java 250 2012-09-19 14:35:19Z charleslowery $
 */
package org.barracudamvc.core.comp.renderer.html;

import java.util.*;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * This class handles the default rendering of a link into an HTML view.
 */
public class HTMLLinkRenderer extends HTMLActionRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLLinkRenderer.class.getName());
    
    
    /**
     * The purpose of this method is to create a default Node to be used when
     * the component is not bound to any specific view. In the case of BLink,
     * it will attempt to use the default template node if possible (ie. if
     * it is an &lt;a&gt;, &lt;button&gt;, or &lt;input&gt; element); if anything else, it will
     * create an &lt;a&gt; node from scratch and append an empty text node to it
     *
     * @param doc the master Document which can be used to create elements
     *        from scratch
     * @param comp the component that we're dealing with for the current request
     * @param vc the view context for the current request
     * @return a default node (created from scratch)
     * @throws UnsupportedFormatException if the renderer has no default node
     */
    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {
        Node templateNode = vc.getTemplateNode();
        Node defaultNode = null;
        if (templateNode instanceof HTMLAnchorElement || 
            templateNode instanceof HTMLButtonElement ||
            templateNode instanceof HTMLInputElement) {
            defaultNode = super.createDefaultNode(doc, comp, vc);        
        } else {

            if (templateNode instanceof HTMLOListElement ||
                templateNode instanceof HTMLUListElement) {
                defaultNode = doc.createElement("LI");
            } else if (templateNode instanceof HTMLDListElement) {
                defaultNode = doc.createElement("DD");

            } else if (templateNode instanceof HTMLTableRowElement) {
                defaultNode = doc.createElement("TD");

            } else {
                defaultNode = templateNode.cloneNode(false);
            }
            
            if (defaultNode!=null && defaultNode.hasAttributes()) {   
                NamedNodeMap nnm = defaultNode.getAttributes();
                
                //check the class attribute
                Attr attr = (Attr) nnm.getNamedItem("class");
                if (attr!=null) {                    
                    String value = attr.getValue();
                    if (value!=null) {
                        StringBuffer sb = new StringBuffer(100);
                        String sep = " ";
                        for (StringTokenizer st = new StringTokenizer(value); st.hasMoreTokens();) {
                            String s = st.nextToken();
                            if (!s.startsWith(TemplateDirective.DIR_PREFIX)) {
                                sb.append(sep+s);
                            }
                        }
                        String newValue = sb.toString().trim();
                        if (newValue.length()>0) attr.setValue(newValue);
                        else nnm.removeNamedItem("class");
                    }
                }            
                
                //check the id attribute (there shouldn't be multiple nodes w/ the same id,
                //so go ahead and strip that)
                attr = (Attr) nnm.getNamedItem("id");   //csc_102405_1
                if (attr!=null) {                       //csc_102405_1
                    nnm.removeNamedItem("id");          //csc_102405_1
                }                                       //csc_102405_1
            }
                        
            Element linkEl = (Element) doc.createElement("A");
            linkEl.appendChild(doc.createTextNode("foo99")); //shouldn't this be empty???
            if (defaultNode!=null) {                    //csc_102405_1
                try {
                    addChildToParent(defaultNode, linkEl);
                } catch (InvalidNodeException e) {
                    throw new UnsupportedFormatException("Error creating default link node:"+e);
                }
            } else {                                    //csc_102405_1
                defaultNode = linkEl;                   //csc_102405_1
            }                                           //csc_102405_1
                        
            //now create a view for the component
            comp.addTempView(new DefaultView(linkEl));
        }
        if (logger.isInfoEnabled()) logger.info("Creating default node:"+defaultNode);
        return defaultNode;
    }

    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a BLink component
        if (!(comp instanceof BLink)) throw new NoSuitableRendererException("This renderer can only render BLink components; comp is of type:"+comp.getClass().getName());

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        BLink linkComp = (BLink) comp;
        Element node = (Element) view.getNode();
        
        boolean supportedElement = false;
        if (node instanceof HTMLAnchorElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLAnchorElement interface...");
            //set the "target" attribute, if it exists
            String target = linkComp.getTarget();
            if (target!=null) ((HTMLAnchorElement) node).setTarget(target);
            supportedElement = true;
        } else if (node instanceof HTMLButtonElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLButtonElement interface...");
            supportedElement = true;
        } else if (node instanceof HTMLInputElement) {
            if (logger.isInfoEnabled()) logger.info("Rendering based on HTMLInputElement interface...");
            //input elements can't contain markup, so don't bother allowing it
            linkComp.setAllowMarkupInText(false);
            supportedElement = true;
        }
        
        if (supportedElement) {
        	//set the text value (if it exists) by delegating to a text component
        	String text = linkComp.getText();
            if (text != null) {
                BText textComp = new BText(text);
                textComp.setView(new DefaultView(node));
                textComp.setAllowMarkupInText(linkComp.allowMarkupInText());
                linkComp.addStepChild(textComp, true);
            }
        } else {
            String errmsg = "Node does not implement HTMLAnchorElement, HTMLButtonElement, or HTMLInputElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
               
        //finally, make sure we reflect the components enabled/disabled status
        EnabledHelper.setEnabled(node, comp.isEnabled());
    }
    
}
