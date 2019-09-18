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
 * $Id: DefaultView.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.Map;
import org.apache.log4j.Logger;
import org.barracudamvc.core.util.dom.DOMUtil;
import org.w3c.dom.Node;

/**
 * This interface defines a View for components to
 * render themselves in. Similar to the java.awt.Graphics
 * object in AWT and Swing.
 *
 * @author  Christian Cryder <christianc@granitepeaks.com>
 * @author  Stephen Peterson <stephen_peterson@agilent.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class DefaultView implements View {

    protected static final Logger logger = Logger.getLogger(DefaultView.class.getName());
    protected Node node = null;
    private String name = null;
    protected Map templateNodes = null;

    //--------------- Constructors -------------------------------
    /**
     * Default constructor to create a view
     */
    public DefaultView() {
        this(null, null);
    }

    /**
     * Create a view and bind it to a node
     *
     * @param name the name of the view
     */
    public DefaultView(String name) {
        this(name, null);
    }

    /**
     * Create a view and bind it to a node
     *
     * @param node the node to which the view is bound
     */
    public DefaultView(Node node) {
        this(null, node);
    }

    /**
     * Create a view and bind it to a node
     *
     * @param name the name of the view
     * @param node the node to which the view is bound
     */
    public DefaultView(String name, Node node) {
        if (name != null)
            setName(name);
        if (node != null)
            setNode(node);
    }

    //--------------- DefaultView --------------------------------
    /**
     * Bind the view to a specific DOM node
     *
     * @param inode the specific DOM node to bind the View to
     */
    @Override
    public void setNode(Node inode) {
        node = inode;
        searchForTemplates(node);
    }

    /**
     * Get the specific DOM node the View is bound to
     *
     * @return the specific DOM node the View is bound to
     */
    @Override
    public Node getNode() {
        return node;
    }

    /**
     * Set the name for this view
     *
     * @param iname the name for this view
     */
    @Override
    public void setName(String iname) {
        name = iname;
    }

    /**
     * Get the name for this view
     *
     * @return the name for this view
     */
    @Override
    public String getName() {
        if (name == null)
            name = "@" + Integer.toHexString(this.hashCode());
        return name;
    }

    /**
     * Get a String describing the view
     */
    @Override
    public String toString() {
        return "View:" + getName() + " (bound to Node:" + DOMUtil.getID(node) + ")";
    }

    /**
     * Allow the view to search the node for any templates. By default, this method
     * now only considers the root node (it used to parse through the entire template,
     * which was not very efficient).
     */
    protected void searchForTemplates(Node curnode) {
        if (curnode == null) {
            return;
        }

        //first allow for any custom searching for templates
        customSearchForTemplates(curnode);
    }

    /**
     * If you want to identify templates based on some custom mechanism,
     * this is the method to override. Look at DefaultTableView for an
     * example...
     */
    protected void customSearchForTemplates(Node curnode) {
        //nop
    }

    /**
     * When a view is cloned, the underlying node that backs the view is
     * set to null; you MUST bind the newly cloned view to a node before
     * you can use it.
     */
    @Override
    public Object clone() {
        try {
            DefaultView dv = (DefaultView) super.clone();
            dv.node = null;
            return dv;
        } catch (Exception e) {
            return null;
        }
    }
}