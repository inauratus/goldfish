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
 * $Id: ServletContextStateMap.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.*;
import javax.servlet.ServletContext;

/**
 * The implementation provides a StateMap bridge to a ServletContext
 * object. By this, we mean that this class allows you to treat an
 * ServletContext as a StateMap. Unlike the ServletContext, this class 
 * will handle null keys, values.
 */
public class ServletContextStateMap implements StateMap {

    //private vars
    protected ServletContext context = null;
    private String NULL = "~Null~";

    /**
     * Public constructor.
     *
     * @param icontext the underlying servlet context structure
     */
    public ServletContextStateMap(ServletContext icontext) {
        context = icontext;
    }

    /**
     * set a property in this StateMap
     *
     * @param key the key object
     * @param val the value object
     */
    @Override
    public void putState(Object key, Object val) {
        if (key == null)
            key = NULL;
        if (val == null)
            val = NULL;
        context.setAttribute(key.toString(), val);
    }

    /**
     * get a property in this StateMap
     *
     * @param key the key object
     * @return the value for the given key
     */
    @Override
    public Object getState(Object key) {
        if (key == null)
            key = NULL;
        Object val = context.getAttribute(key.toString());
        return (val.equals(NULL) ? null : val);
    }

    /**
     * remove a property in this StateMap
     *
     * @param key the key object
     * @return the object which was removed
     */
    @Override
    public Object removeState(Object key) {
        if (key == null)
            key = NULL;
        Object val = context.getAttribute(key.toString());
        context.removeAttribute(key.toString());
        return (val.equals(NULL) ? null : val);
    }

    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation). In this particular case, the set
     * is just a COPY of the underlying data structure
     *
     * @return a Set of keys for this StateMap
     */
    @Override
    public Set getStateKeys() {
        Enumeration e = context.getAttributeNames();

        Set set = new TreeSet();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        return set;
    }

    /**
     * get a Map that holds the state values (whether or 
     * not the Map is backed by the data store depends on 
     * the implementation). In this particular case, the map
     * is a COPY of the underlying data store itself (ServletContext)
     *
     * @return a Map of key/val pairs for this StateMap
     */
    @Override
    public Map getStateStore() {
        Enumeration e = context.getAttributeNames();
        Map map = new TreeMap();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            map.put(key, getState(key));
        }
        return map;

    }

    /**
     * clear all state information
     */
    @Override
    public void clearState() {
        Set keys = getStateKeys();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            removeState(key);
        }
    }

    /**
     * get a reference to the underlying ServletContext
     *
     * @return a reference to the underlying ServletContext
     */
    public ServletContext getContext() {
        return context;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return (DesiredType) getState(key);
    }
}
