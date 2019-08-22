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
 * $Id: DefaultItemMap.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.Map;
import java.util.TreeMap;

/**
 * An ItemMap can be returned by the getItem() methods within any of 
 * the model implementations. It is used to associate a key with a 
 * particular value. It is primarily used by the BSelect component 
 * (which can use both key and value information when rendering). 
 * The other components just use the value information.
 */
public class DefaultItemMap implements ItemMap {

    protected Object key = null;
    protected Object value = null;
    protected Map<Object, Object> attrs = null;

    /**
     * Create a new DefaultItemMap with the given key
     * and value
     *
     * @param ikey an int key
     * @param ivalue the object value
     */
    public DefaultItemMap(int ikey, Object ivalue) {
        key = new Integer(ikey);
        value = ivalue;
    }

    /**
     * Create a new DefaultItemMap with the given key
     * and value
     *
     * @param ikey an Integer key
     * @param ivalue the object value
     */
    public DefaultItemMap(Object ikey, Object ivalue) {
        key = ikey;
        value = ivalue;
    }

    /**
     * Returns the key corresponding to this entry.
     */
    public Object getKey() {
        return key;
    }

    /**
     * Returns the value corresponding to this entry. 
     */
    public Object getValue() {
        return value;
    }

    /**
     * Replaces the value corresponding to this entry with the specified 
     * value (optional operation). 
     */
    @Override
    public Object setValue(Object ivalue) {
        value = ivalue;
        return value;
    }

    /**
     * set an attribute for this particular component. When the component
     * is rendered, component attributes will be shown as element attributes
     * in the elements that back each of the views associated with this component.
     * This means that if you set an attribute for the component, it will 
     * affect all views associated with the component.If you wish to set an 
     * attribute for a specific view alone, then you should get the view, find
     * the node that backs it, and then set the attribute manually that way.
     *
     * @param attr the attribute name
     * @param val the attribute value
     */
    @Override
    public Attrs setAttr(Object attr, Object val) {
        if (attrs == null)
            attrs = new TreeMap<Object, Object>();
        attrs.put(attr, val);
        return this;
    }

    /**
     * get an attribute associated with this particular component. Note that
     * the attribute map that backs this method only keeps tracks of specific
     * attributes you have added to the component. It does not look at attributes
     * that are physically associated with the underlying elements that back each
     * of the views associated with this component. What this means is that if
     * the template that backs a view has some attribute "foo" and you try to
     * see the value of that attribute using this method, you will not be able 
     * to find it unless you have actually associated an attribute named "foo" 
     * with the specific component.
     *
     * @param attr the attribute name
     * @return the value for the given attribute (may be null)
     */
    @Override
    public Object getAttr(Object attr) {
        if (attrs == null)
            return null;
        return attrs.get(attr);
    }

    /**
     * get a reference of the underlying component attribute Map
     *
     * @return a reference of the underlying component attribute Map
     */
    @Override
    public Map getAttrMap() {
        return attrs;
    }

    /**
     * Return a String representation of the ItemMap
     */
    @Override
    public String toString() {
        return (value != null ? value.toString() : "null");
    }
}