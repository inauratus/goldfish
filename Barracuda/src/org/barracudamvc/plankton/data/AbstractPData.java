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
 * $Id: AbstractPData.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.*;

/**
 * Abstract implementation of the basic PData methods.
 */
public abstract class AbstractPData implements PData {

    protected PData parent = null;
    protected boolean inheritParents = true;
    protected DefaultStateMap state = null;


    //--------------- PData --------------------------------------
    /**
     * set the object's parent. May be some other PData object or 
     * null (indicating hierarchy root). May not be a reference to self.
     *
     * @param p the parent PData object
     */
    public void setParent(PData p) {
        //don't ever allow an object to set itself as the parent...this
        //is meaningless and would result in an endless loop in the case 
        //of getRootParent()
        if (p==this) return;    
        parent = p;
    }

    /**
     * get the objects parent (null if root)
     *
     * @return the PData object
     */
    public PData getParent() {
        return parent;
    }

    /**
     * get the root parent by chaining back up the heirarchy until we find 
     * the highest PData object in the heirarchy.
     *
     * @return the root PData object
     */
    public PData getRootParent() {
        if ((parent!=null) || (parent instanceof PData)) return parent.getRootParent();
        else return this;
    }
    
    /**
     * Do we want to inherit parents. Defaults to true. This indicates that 
     * when an object is added to this data collection, if it implements
     * PData, it should automatically inherit the current object as its 
     * parent. You would typically set this to false if you were going
     * to be storing PData "mini-hierarchies" within a larger PData 
     * hierarchy
     * 
     * @param val true if we should inherit parents
     */
    public void setInheritParents(boolean val) {
        inheritParents = val;
    }
    
    /**
     * Return true if we are inheriting parents.
     * 
     * @return true if we are inheriting parents
     */
    public boolean isInheritParents() {
        return inheritParents;
    }


    //--------------- StateMap -----------------------------------
    /**
     * set a property in this StateMap
     *
     * @param key the key object
     * @param val the value object
     */    
    public void putState(Object key, Object val) {
        if (state==null) state = new DefaultStateMap();
        state.putState(key, val);
    }
    
    /**
     * get a property in this StateMap
     *
     * @param key the key object
     * @return the value for the given key
     */    
    @Override
    public <DesiredType> DesiredType getState(Object key) {
        if (state == null)
            return null;
        else
            return state.getState(key);
    }

    /**
     * remove a property in this StateMap
     *
     * @param key the key object
     * @return the object which was removed
     */    
    public Object removeState(Object key) {
        if (state==null) return null;
        else return state.removeState(key);
    }
    
    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation)
     *
     * @return a Set of keys for this StateMap
     */
    public Set getStateKeys() {
        if (state==null) return null;
        else return state.getStateKeys();
    }
    
    /**
     * get a copy of the underlying Map that holds 
     * the state values
     *
     * @return a copy of the underlying state Map
     */    
    public Map getStateStore() {
        if (state==null) state = new DefaultStateMap();
        return state.getStateStore();
    }

    //csc_052803_2 - added
    /**
     * clear all state information
     */
    public void clearState() {
        if (state!=null) state.clearState();
    }



}
