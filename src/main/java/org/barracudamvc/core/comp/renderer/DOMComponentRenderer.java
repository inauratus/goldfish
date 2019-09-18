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
 * $Id: DOMComponentRenderer.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.comp.renderer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BContainer;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.InvalidNodeException;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.util.dom.DOMUtil;
import org.barracudamvc.core.view.FormatType;
import org.barracudamvc.plankton.Classes;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This interface defines the methods needed to implement a Renderer.
 */
public abstract class DOMComponentRenderer implements Renderer {

    protected static final Logger logger = Logger.getLogger(DOMComponentRenderer.class.getName());

    /**
     * The purpose of this method is for a renderer to provide a default node 
     * (if none exists). This is essentially where you would say "if I need to 
     * render myself, and I'm not actually bound to anything, what should the 
     * default markup look like if I had to create it." 
     *
     * In most cases, its perfectly fine to just throw an 
     * UnsupportedFormatException (indicating that there is no default). The 
     * only renderer that currently implements this method is the 
     * HTMLLinkRenderer.
     *
     * @param doc the master Document which can be used to create elements
     *        from scratch
     * @param comp the component that we're dealing with for the current request
     * @param vc the view context for the current request
     * @return a default node (created from scratch)
     * @throws UnsupportedFormatException if the renderer has no default node
     */
    @Override
    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {  //csc_110501.1
        //ask the renderer to create the default Node
        Node templateNode = vc.getTemplateNode();
        if (templateNode == null) {
            throw new UnsupportedFormatException("Cannot create default node");
        }
        Node defaultNode = templateNode.cloneNode(true);
        if (logger.isInfoEnabled()) {
            logger.info("Creating default node:" + defaultNode);
        }
        return defaultNode;
    }

    @Override
    public void addDefaultView(BComponent comp, Node node) {
        View view = new DefaultView(node);
        comp.addTempView(view);
    }

    /**
     * The purpose of this method is to add a child to a parent. In many
     * cases, this method is used to ensure that the resulting markup is 
     * valid, by inserting the appropriate markup in between the child and
     * the parent. In many cases, the generic Component renderer will be the 
     * only renderer that actually implements this method.
     *
     * @param parent the parent Node
     * @param child the child Node
     * @return the resulting parent node
     * @throws InvalidNodeException if teh child cannot be added to the parent
     */
    @Override
    public Node addChildToParent(Node parent, Node child) throws InvalidNodeException {
        //eliminate the obvious
        if (parent == null || child == null) {
            throw new InvalidNodeException("Invalid node: cannot add child:" + child + " to parent:" + parent);
        }

        //make any adjustments specific to the markup
        //(--n/a--)

        //now add the child in
        if (child != null) {
            try {
                parent.appendChild(child);
            } catch (DOMException domException) {
                throw new InvalidNodeException("Invalid node: cannot add child:" + child + " to parent:" + parent + "\n ");
            }
        }

        //return the parent
        return parent;
    }

    /**
     * This method should actually render the data from the component
     * into the view, taking into consideration the specified ViewContext.
     * Generally, every renderer will implement this method.
     *
     * @param comp the component to be rendered
     * @param view the view the component should be rendered in
     * @param vc the view context
     * @throws RenderException if unable to render the component in the 
     *        specified view
     */
    @Override
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        Node node = view.getNode();

        if (logger.isDebugEnabled()) {
            logger.debug("rendering node: " + node);
        }

        //csc_072604_1 - moved here from BComponent (since this is really where it belongs)        
        //set any attributes. What this means is that for ANY component which setts Attrs, those
        //attributes WILL get rendered if the view is backed by Element (ie. for both HTML and XML)
        Map attrs = comp.getAttrMap();
        if (attrs != null && (node instanceof Element)) {
            Iterator it = attrs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                String key = ("" + me.getKey()).trim();
                Object val = me.getValue();
                if (val == null) {
                    ((Element) node).removeAttribute(key);
                } else {
                    // fro_091405 don't override class attibute but rather append
                    // nb: could do the same with other multi-valued attributes (style, ...)
                    // nnb: we don't try to check if a class already exists here, we just append. Should we ? 
                    if ("class".equals(key)) {
                        ((Element) node).setAttribute(key, ((Element) node).getAttribute(key) + " " + val);
                    } else {
                        ((Element) node).setAttribute(key, val.toString());
                    }
                }
            }
        }
        handleChildren(comp, view, vc);
    }

    /**
     * This is just a debugging method to make it easy to show
     * the interfaces of the object being rendered with special
     * treatment for View and BComponent objects.  Generally,
     * there is no need to re-implement this method.
     * 
     * <p>Note: if debugging is not enabled for the external logger,
     * this method will return immediately without doing any extra
     * work.  As such, this method can be called directly without
     * manually checking whether debugging is enabled.</p>
     */
    public void showNodeInterfaces(Object object, Logger extLogger) {
        if (!extLogger.isDebugEnabled()) {
            return;
        }
        Object obj = object;
        //get the node
        if (obj instanceof View) {
            obj = ((View) object).getNode();
            extLogger.debug("node [" + obj + ", id=" + DOMUtil.getID((Node) obj) + "] implements the following interfaces:");
        } else if (obj instanceof BComponent) {
            extLogger.debug("component [" + obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode()) + "] implements the following interfaces:");
        } else {
            extLogger.debug("object [" + obj + "] implements the following interfaces:");
        }
        Iterator it = Classes.getAllInterfaces(obj).iterator();
        while (it.hasNext()) {
            Object o = it.next();
            extLogger.debug("    " + o.toString());
        }
    }
    // fro_020407_begin handle rendering of nested components

    protected void handleChildren(BComponent comp, View view, ViewContext vc) {

        List children = comp.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            BContainer childContainer = (BContainer) it.next();
            if (!(childContainer instanceof BComponent)) {
                logger.warn("Component has a non component child, skipping");
                continue;
            }
            BComponent child = (BComponent) childContainer;
            bindChild(child, view, vc);
        }
    }

    protected void bindChild(BComponent child, View view, ViewContext vc) {

        Node node = view.getNode();
        Renderer childRenderer = null;
        try {
            FormatType ft = vc.getViewCapabilities().getFormatType();
            childRenderer = child.getRenderer(ft.getDOMClass());
        } catch (NoSuitableRendererException ex) {
            logger.warn("No suitable renderer for child: " + child + "\n"
                    + "Skipping...");
            return;
        }
        try {
            Node childNode = childRenderer.createDefaultNode(node.getOwnerDocument(), child, vc);
            if (childNode != null) {
                childRenderer.addDefaultView(child, childNode);
                addChildToParent(node, childNode);
            } else {
                // child want to be bound to its parent's view
                child.addTempView(view);
            }
        } catch (Exception e) {//InvalidNodeException e){
            // adding the exception to the log as we are finding these in
            // our logs but I am not sure what is happening as Exception
            // is being caught. -- CHL (1/23/2013) - PAI
            logger.error("Unable to add a child node to default node", e);
            //throw new UnsupportedFormatException();
        }
        child.invalidate();
    }
    // fro_020407_end
}
