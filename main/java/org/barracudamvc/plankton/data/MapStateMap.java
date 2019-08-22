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
 * $Id: MapStateMap.java 256 2013-03-22 18:56:49Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.*;

/**
 * The implementation provides a StateMap bridge to a Map
 * object. By this, we mean that this class allows you to treat an
 * Map as a StateMap. 
 */
public class MapStateMap implements StateMap {

    //private vars
    protected Map map = null;
    
    /**
     * Public constructor.
     *
     * @param imap the underlying servlet map structure
     */    
    public MapStateMap(Map imap) {
        map = imap;
    }
    
    /**
     * set a property in this StateMap
     *
     * @param key the key object
     * @param val the value object
     */    
    public void putState(Object key, Object val) {
        map.put(key,val);
    }
    
    /**
     * get a property in this StateMap
     *
     * @param key the key object
     * @return the value for the given key
     */    
    public Object getState(Object key) {
        return map.get(key);
    }
    
    /**
     * remove a property in this StateMap
     *
     * @param key the key object
     * @return the object which was removed
     */    
    public Object removeState(Object key) {
        return map.remove(key);
    }
    
    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation). In this particular case, the set
     * is backed by a copy of the underlying data structure
     *
     * @return a Set of keys for this StateMap
     */
    public Set getStateKeys() {
//csc_101304_1        return new ArrayList(map.keySet());
//csc_110504_1        return map.keySet();    //csc_101304_1
        if (map instanceof TreeMap) return new TreeSet(map.keySet());   //csc_110504_1
        else return new HashSet(map.keySet());                          //csc_110504_1
    }
    
    /**
     * get a Map that holds the state values (whether or 
     * not the Map is backed by the data store depends on 
     * the implementation). In this particular case, the map
     * IS the underlying data store itself
     *
     * @return a Map of key/val pairs for this StateMap
     */
    public Map getStateStore() {
        return map;        
    }
    
    /**
     * clear all state information
     */
    public void clearState() {
        map.clear();
    }
    
    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return (DesiredType) getState(key);
    }
}
