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
 * $Id: DefaultTableView.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.Iterator;
import org.apache.log4j.Logger;
import org.barracudamvc.core.util.dom.DOMUtil;
import org.barracudamvc.plankton.Classes;
import org.w3c.dom.*;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableSectionElement;

/**
 * This class provides the default implementation for 
 * a TableView. It provides a View for components to 
 * render themselves in. Unlike other views, the TableView
 * provides convenience methods to access the header, body,
 * and footer elements of the view.
 */
public class DefaultTableView extends DefaultView implements TableView {

    protected static final Logger logger = Logger.getLogger(DefaultTableView.class.getName());
    protected Element header = null;
    protected Element body = null;
    protected Element footer = null;
    protected Element caption = null;    //ndc_101202.1

    /**
     * Public noargs constructor. This creates a view which is not bound 
     * to any particular node. You must bind the view to a node before you 
     * can actually use it for anything
     */
    public DefaultTableView() {
        this(null);
    }

    /**
     * Create a view and bind it to a node.
     *
     * @param node the node the view should be bound to
     */
    public DefaultTableView(Node node) {
        if (node != null)
            setNode(node);
    }

    /**
     * Get the header element from the particular Node that
     * backs this view.
     *
     * @return the header element (may be null if there is 
     *      no recognized header)
     */
    public Element getHeaderElement() {
        return header;
    }

    /**
     * Get the body element from the particular Node that
     * backs this view. There will always be a body element.
     * It will either correspond to the <tbody> element or
     * to the entire table (ie. the whole table is body)
     *
     * @return the body element
     */
    public Element getBodyElement() {
        return (body != null ? body : (Element) node);
    }

    /**
     * Get the footer element from the particular Node that
     * backs this view.
     *
     * @return the footer element (may be null if there is 
     *      no recognized footer)
     */
    public Element getFooterElement() {
        return footer;
    }

    //ndc_101202.1 - added
    /**
     * Get the caption element from the particular Node that
     * backs this view.
     *
     * @return the caption element (may be null if there is
     *      no recognized caption)
     */
    public Element getCaptionElement() {
        return caption;
    }

    //csc_012605_1 - added
    /**
     * Instead of the parent behavior (which will only process the root node, not the children),
     * here we need to iterate through all of them in order to call customSearchForTemplates() on
     * every node. This used to be the default for all views; now it only happens for table views
     */
    protected void searchForTemplates(Node curnode) {
        if (curnode == null) {
            // Some (incorrect?) setups come here with curnode==null. Make this safe.  -srp Agilent Technologies 11/12/2001
            logger.warn("The current node in which templates are to be searched is null");
            return;
        }

        if (logger.isDebugEnabled())
            logger.debug("Looking for templates for Node:" + curnode + "...");

        //first allow for any custom searching for templates
        customSearchForTemplates(curnode);

        //next see if we can find any template nodes
        if (curnode.hasChildNodes()) {
            NodeList nl = curnode.getChildNodes();

            //iterate through the children
            for (int i = 0, max = nl.getLength(); i < max; i++) {
                Node child = nl.item(i);
                if (logger.isDebugEnabled())
                    logger.debug("Found child:" + child);

                //search for templates in the child
                searchForTemplates(child);

                if (logger.isDebugEnabled())
                    logger.debug("Finished check on child!");
            }
        }
    }

    /**
     * Here we are going to look for custom header, footer, and
     * body elements
     */
    @Override
    protected void customSearchForTemplates(Node curnode) {
        //if we already have the header, body, and footer just return
        if (header != null && body != null && footer != null)
            return;

        //make sure its an element
        if (!(curnode instanceof Element))
            return;
        Element el = (Element) curnode;
        //see if its an instance of HTMLTableElement
        if (curnode instanceof HTMLTableSectionElement) {
            String tagName = el.getTagName().toUpperCase();
            if (tagName.equals("THEAD")) {
                if (header == null)
                    header = el;
            } else if (tagName.equals("TBODY")) {
                if (body == null)
                    body = el;
            } else if (tagName.equals("TFOOT")) {
                if (footer == null)
                    footer = el;
            }
        }

        if (curnode instanceof HTMLTableCaptionElement) {
            String tagName = el.getTagName().toUpperCase();
            if (tagName.equals("CAPTION")) {
                if (caption == null)
                    caption = el;
            }
        }
    }

    /*
     * @return a String representation of a TableView and the node it is bound to 
     */
    @Override
    public String toString() {
        return "TableView:" + getName() + " (bound to Node:" + DOMUtil.getID(node) + ")";
    }
}