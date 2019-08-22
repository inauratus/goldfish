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
 * $Id: DefaultTemplateView.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.*;

import org.apache.log4j.Logger;
import org.barracudamvc.core.util.dom.DOMUtil;
import org.barracudamvc.plankton.data.StateMap;
import org.w3c.dom.Node;

/**
 * This class provides the default implementation for 
 * a TemplateView. It provides a View for components to 
 * render themselves in. 
 */
public class DefaultTemplateView extends DefaultView implements TemplateView {

    protected static final Logger logger = Logger.getLogger(DefaultTemplateView.class.getName());
    protected Node masterTemplate = null;
    protected Map<String, TemplateDirective> dirMap = null;     //map of directives, accessed by directive name
    protected StateMap idMap = null;    //map of directives, accessed by id name
    protected String idAttrName = "id";
    protected String dirAttrName = "class";

    /**
     * Public noargs constructor. This creates a view which is not bound 
     * to any particular node. You must bind the view to a node before you 
     * can actually use it for anything
     */
    public DefaultTemplateView() {
    }

    /**
     * Create a view and bind it to a node.
     *
     * @param node the node the view should be bound to
     */
    public DefaultTemplateView(Node node) {
        if (node != null)
            setNode(node);
    }

    /**
     * Create a view and bind it to a node. Specify the name
     * of the attribute to be used to search for a directive
     * list (defaults to "class")
     *
     * @param node the node the view should be bound to
     * @param idAttrName name of the id attribute 
     * @param dirAttrName name of the attribute to search for 
     *         directives in
     */
    public DefaultTemplateView(Node node, String idAttrName, String dirAttrName) {
        if (idAttrName != null)
            setIDAttrName(idAttrName);
        if (dirAttrName != null)
            setDirAttrName(dirAttrName);
        if (node != null)
            setNode(node);
    }

    /**
     * Create a view and bind it to a node. Pass in a 
     * pre-poulated directive map.
     *
     * @param node the node the view should be bound to
     * @param idAttrName name of the id attribute 
     * @param idMap the directive map to be used
     */
    public DefaultTemplateView(Node node, String idAttrName, StateMap idMap) {
        if (idAttrName != null)
            setIDAttrName(idAttrName);
        if (idMap != null)
            setDirIDMap(idMap);
        if (node != null)
            setNode(node);
    }

    //--------------- DefaultTemplateView ------------------------
    /**
     * Get the master template
     *
     * @return the master template
     */
    public Node getMasterTemplate() {
        return masterTemplate;
    }

    /**
     * Set the directive ID map. This map allows you to lookup 
     * directives associated with a given id. Each item in the 
     * map should correspond to a list of directives.
     * 
     * @param iidMap the directive map
     */
    public void setDirIDMap(StateMap iidMap) {
        idMap = iidMap;
    }

    /**
     * Get the directive ID map. This map allows you to lookup 
     * directives associated with a given id.
     * 
     * @return the idMap for this view
     */
    public StateMap getDirIDMap() {
        return idMap;
    }

    /**
     * Set the id attr name. Specify the name of the 
     * id attribute
     * 
     * @param iidAttrName the name of the id attribute
     */
    public void setIDAttrName(String iidAttrName) {
        idAttrName = iidAttrName;
    }

    /**
     * Get the id attr name (defaults to "id"). 
     * 
     * @return the id attr name
     */
    public String getIDAttrName() {
        return idAttrName;
    }

    /**
     * Set the directive attr name. Specify the name of the 
     * attribute to search for a list of directives in.
     * 
     * @param idirAttrName the name of the attribute to search 
     *        for a list of directives in
     */
    public void setDirAttrName(String idirAttrName) {
        dirAttrName = idirAttrName;
    }

    /**
     * Get the directive attr name (defaults to "class"). 
     * 
     * @return the directive attr name
     */
    public String getDirAttrName() {
        return dirAttrName;
    }

    /**
     * Look up a directive by it's string representation (rather
     * than reparsing it). May be null if the template directive 
     * is not a valid directive
     *
     * @param dirStr a string representation of a directive
     * @return the actual template directive that corresponds to the 
     *        String representation. 
     */
    @Override
    public TemplateDirective lookupDir(String dirStr) {
        TemplateDirective td = dirMap.get(dirStr);
        if (td == null) {
            td = createDirective(dirStr);
            dirMap.put(dirStr, td);
        }

        if (td == TemplateDirective.EMPTY_DIRECTIVE) {
            return null;
        } else {
            return td;
        }
    }

    private TemplateDirective createDirective(String string) {
        try {
            return TemplateDirective.getInstance(string);
        } catch (InvalidDirectiveException e) {
            return TemplateDirective.EMPTY_DIRECTIVE;
        }
    }

    /**
     * Look up a list of directives based on a given id
     *
     * @param idStr the id in question
     * @return a list of template directive for the given id
     */
    @Override
    public List<TemplateDirective> lookupDirsByID(String idStr) {
        if (idMap == null || idStr == null || idStr.trim().length() < 1)
            return null;
        Object val = idMap.getState(idStr);
        if (idMap.getState(idStr) == null) {
            List<TemplateDirective> list = TemplateDirective.getAllInstances(val.toString());
            idMap.putState(idStr, list);
            return list;
        } else {
            return idMap.getState(idStr);
        }
    }

    /**
     * Here we are going to look for custom header, footer, and
     * body elements
     */
    @Override
    protected void customSearchForTemplates(Node curnode) {
        if (masterTemplate == null) {
            masterTemplate = node.cloneNode(true);
            dirMap = new TreeMap<String, TemplateDirective>();
        }
    }

    @Override
    public String toString() {
        return "TemplateView:" + getName() + " (bound to Node:" + DOMUtil.getID(node) + ")";
    }

    /**
     * When a template view is cloned, the underlying masterTemplate node is
     * set to null; this ensures the new node to which this view is bound
     * will in fact be used for the template
     */
    @Override
    public Object clone() {
        try {
            DefaultTemplateView dtv = (DefaultTemplateView) super.clone();
            dtv.masterTemplate = null;
            return dtv;
        } catch (Exception e) {
            return null;
        }
    }
}