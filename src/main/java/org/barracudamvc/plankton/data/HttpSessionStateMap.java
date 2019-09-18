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
 * $Id: HttpSessionStateMap.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.servlet.http.HttpSession;

/**
 * The implementation provides a StateMap bridge to a HttpSession
 * object. By this, we mean that this class allows you to treat an
 * HttpSession as a StateMap. Unlike the HttpSession, this class 
 * will handle null keys, values.
 */
public class HttpSessionStateMap implements StateMap {

    protected HttpSession session = null;
    //private vars
    private String NULL = "~Null~";

    /**
     * Public constructor.
     *
     * @param isession the underlying servlet session structure
     */
    public HttpSessionStateMap(HttpSession isession) {
        session = isession;
    }

    /**
     * set a property in this StateMap
     *
     * @param key the key object
     * @param val the value object
     * @throws IllegalStateException if the session is not active.
     */
    @Override
    public void putState(Object key, Object val) {
        if (key == null) {
            key = NULL;
        }
        if (val == null) {
            val = NULL;
        }
        session.setAttribute(key.toString(), val);
    }

    /**
     * get a property in this StateMap
     *
     * @param key the key object
     * @return the value for the given key
     * @throws IllegalStateException if the session is not active.
     */
    @Override
    public Object getState(Object key) {
        if (key == null) {
            key = NULL;
        }

        try {
            Object val = session.getAttribute(key.toString());
            if (val == null || val.equals(NULL)) {
                return null;
            } else {
                return val;
            }
        } catch (IllegalStateException ex) {
            return null;
        }
    }

    /**
     * remove a property in this StateMap. This function was expanded in 
     * csc_082103_1 to support the notion of wildcarding, allowing you to 
     * remove multiple keys in one fell swoop. Basically, if the key is a 
     * String, which ends with an '*', then any keys that start with that 
     * string will be removed (and in this case, the method returns a Map of
     * key/val pairs that got removed, rather than a single object that got 
     * removed). This approach is not quite as flexible as using regular 
     * expressions, but that would make us  dependent on jdk1.4 (so we won't 
     * go there for now). Note that this class backs the  ObjectRepository data 
     * structures, so this functionality applies there as well.
     *
     * @param key the key object
     * @return the object which was removed
     * @throws IllegalStateException if the session is not active.
     */
    @Override
    public Object removeState(Object key) {
        try {
            //remove all keys that match
            if (key != null && (key instanceof String) && ((String) key).endsWith("*")) {
                Map removed = new HashMap();
                String keystr = (String) key;
                String targetstr = keystr.substring(0, keystr.length() - 1);

                Iterator it = getStateKeys().iterator();
                while (it.hasNext()) {
                    String tkey = (String) it.next();
                    if (tkey != null && tkey.startsWith(targetstr)) {
                        removed.put(tkey, session.getAttribute(tkey));
                        session.removeAttribute(tkey);
                    }
                }
                return (removed.size() > 0 ? removed : null);
            } else {
                if (key == null) {
                    key = NULL;
                }
                Object val = session.getAttribute(key.toString());
                session.removeAttribute(key.toString());
                return ((val == null || val.equals(NULL)) ? null : val);
            }

            // this occasionally happens if the session expires as you are in 
            // the process of taking something out of it...in such a case, just 
            // catch the error and return null
        } catch (NullPointerException e) {
            return null;

            // if the session has been invalidated, its possible an 
            // IllegalStateException will be thrown. Just consume the exception  
            // and return null.    
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation). In this particular case, the set
     * is just a COPY of the underlying data structure
     *
     * @return a Set of keys for this StateMap
     * @throws IllegalStateException if the session is not active.
     */
    @Override
    public Set<String> getStateKeys() {
        Enumeration e = getAttributeNames();
        Set set = new TreeSet();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        return set;
    }

    private Enumeration<String> getAttributeNames() {
        try {
            return session.getAttributeNames();
        } catch (IllegalStateException ex) {
            return Collections.enumeration(Collections.EMPTY_SET);
        }
    }

    /**
     * get a Map that holds the state values (whether or 
     * not the Map is backed by the data store depends on 
     * the implementation). In this particular case, the map
     * is just a COPY of the underlying data store (session)
     *
     * @return a Map of key/val pairs for this StateMap
     * @throws IllegalStateException if the session is not active.
     */
    @Override
    public Map getStateStore() {
        Enumeration e = session.getAttributeNames();
        Map map = new TreeMap();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            map.put(key, getState(key));
        }
        return map;
    }

    /**
     * clear all state information
     * @throws IllegalStateException if the session is not active.
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
     * get a reference to the underlying HttpSession
     *
     * @return a reference to the underlying HttpSession
     */
    public HttpSession getSession() {
        return session;
    }
    
    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return (DesiredType) getState(key);
    }
}
