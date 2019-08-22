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
 * $Id: DefaultStateMap.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>A StateMap is an object that is capable of carrying state 
 * information along with it--you can put properties into the 
 * state and then get them back out.
 *
 * <p>This class provides the default implementation of a StateMap. 
 * The underlying storage structure is a HashMap, so it a) isn't 
 * thread safe and b) accepts nulls. If you need more than the minimal 
 * functionality exposed in the StateMap interface, you should work 
 * with the underlying Map structure.
 *
 * <p>Key entities that implement StateMap:
 * <ul>
 *        <li>BaseEvent</li>
 *        <li>EventContext</li>
 *        <li>FormMap</li>
 * </ul>
 */
public class DefaultStateMap implements StateMap, Serializable {

    private static final long serialVersionUID = 1;
    protected Map<Object, Object> props = null;

    /**
     * set a property in this StateMap
     *
     * @param key the key object
     * @param val the value object
     */
    @Override
    public void putState(Object key, Object val) {
        if (props == null) {
            props = new HashMap<Object, Object>();
        }
        props.put(key, val);
    }

    /**
     * get a property in this StateMap
     *
     * @param key the key object
     * @return the value for the given key
     */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType> DesiredType getState(Object key) {
        if (props == null) {
            return null;
        } else {
            return (DesiredType) props.get(key);
        }
    }

    /**
     * Remove a property in this StateMap. 
     * <p> Supports Wildcarding: allowing you to remove multiple keys in one 
     * fell swoop. Basically, if the key is a String, which ends with an '*', 
     * then any keys that start with that string will be removed (and in this 
     * case, the method returns a Map of key/val pairs that got removed, rather 
     * than a single object that got removed). This  approach is not quite as 
     * flexible as using regular expressions. 
     *
     * @param key the key object
     * @return the object which was removed or a Map of the objects which were
     * removed
     */
    @Override
    public Object removeState(Object key) {
        //eliminate the obvious
        if (props == null) {
            return null;
        }

        //remove all keys that match
        if (key instanceof String && ((String) key).endsWith("*")) {
            Map<Object, Object> removed = new HashMap<Object, Object>();
            String keystr = (String) key;
            String targetstr = keystr.substring(0, keystr.length() - 1);

            Iterator<Map.Entry<Object, Object>> iterator = props.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> entry = iterator.next();
                Object okey = entry.getKey();
                if ((okey instanceof String) && ((String) okey).startsWith(targetstr)) {
                    removed.put(okey, entry.getValue());
                    iterator.remove();
                }
            }

            return (removed.size() > 0 ? removed : null);
        } else {
            return props.remove(key);
        }
    }

    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation). In this particular case, the set
     * is backed by a copy of the underlying data structure
     *
     * @return a Set of keys for this StateMap
     */
    @Override
    public Set getStateKeys() {

        if (props == null) {
            return new HashSet<Object>();
        } else {
            return new HashSet<Object>(props.keySet());
        }
    }

    /**
     * get a Map that holds the state values (whether or 
     * not the Map is backed by the data store depends on 
     * the implementation). In this particular case, the map
     * IS the underlying data store itself
     *
     * @return a Map of key/val pairs for this StateMap
     */
    @Override
    public Map getStateStore() {
        if (props == null) {
            props = new HashMap<Object, Object>();
        }
        return new HashMap<Object, Object>(props);
    }

    /**
     * clear all state information
     */
    @Override
    public void clearState() {
        if (props != null) {
            props.clear();
        }
    }

    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return getState(key);
    }
}
