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
 * $Id: Attrs.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import java.util.Map;


/**
 * This interface defines the methods needed to implement
 * an Attrs object (ie. something for which you can get/set markup attrs). 
 * Currently implemented by BComponent and ItemMap, this allows the renderers
 * to update attributes within elements (ie. class, title, alt, etc  attributes
 */
public interface Attrs {

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
    public Attrs setAttr(Object attr, Object val);
    
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
    public Object getAttr(Object attr);
    
    /**
     * get a reference of the underlying component attribute Map
     *
     * @return a reference of the underlying component attribute Map
     */    
    public Map getAttrMap();

}