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
 * $Id: StateMap.java 256 2013-03-22 18:56:49Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.Serializable;
import java.util.*;

/**
 * <p>This interface defines the methods needed to implement
 * state in an object. By this we mean that an object is
 * capable of carrying state information along with it--you
 * can put properties into the state and then get them back
 * out.
 *
 * <p>Key entities that implement StateMap:
 * <ul>
 *        <li>BaseEvent</li>
 *        <li>EventContext</li>
 *        <li>FormMap</li>
 * </ul>
 */
public interface StateMap extends Serializable {

    /**
     * set a property in this StateMap
     *
     * @param key the key object
     * @param val the value object
     */
    public void putState(Object key, Object val);

    /**
     * get a property in this StateMap
     *
     * @param key the key object
     * @return the value for the given key
     */
    public <DesiredType> DesiredType getState(Object key);

    /**
     * Get a property in the StateMap and perform the type cast to the
     * requested type.
     * @param <DesiredType>
     * @param type
     * @param key
     * @return 
     */
    public <DesiredType extends Object> DesiredType getState(Class<DesiredType> type, String key);
    
    /**
     * remove a property in this StateMap
     *
     * @param key the key object
     * @return the object which was removed
     */
    public Object removeState(Object key);

    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation)
     *
     * @return a Set of keys for this StateMap
     */
    public Set getStateKeys();

    /**
     * get a Map that holds the state values (whether or 
     * not the Map is backed by the data store depends on 
     * the implementation)
     *
     * @return a Map of key/val pairs for this StateMap
     */
    public Map getStateStore();

    /**
     * clear all state information
     */
    public void clearState();
}
