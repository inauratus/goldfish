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
 * $Id: PHashMap.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>This class extends AbstractPData (which provides the
 * parental/statemap functionality) and delegates most of 
 * the Map functionality back to an underlying HashMap
 */
public class PHashMap extends AbstractPData implements PMap {

    private Map<Object, Object> map = new HashMap<Object, Object> ();

    //--------------- PHashMap -----------------------------------
    /**
     * Set the underlying store (you only really need to use
     * this method if you want to store the data in something 
     * other than a HashMap, which is the default)
     *
     * @param imap the Map structure to be used as the underlying 
     *        store
     */
    public void setStore(Map imap) {
        map = imap;
    }

    //--------------- PMap ---------------------------------------
    /**
     * Removes all mappings from this map (optional operation). 
     */
    public void clear() {
        //we need to start by clearing parents for any PData values in the list
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            Object el = it.next();
            if (el != null && el instanceof PData) {
                PData pdata = (PData) el;
//csc_012003.1                if (pdata.isInheritParents()) pdata.setParent(null);
                if (pdata.isInheritParents() && pdata.getParent() == this)
                    pdata.setParent(null);    //csc_012003.1
            }
        }

        //now clear the list
        map.clear();
    }

    /**
     * Returns true if this map contains a mapping for the specified key. 
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value. 
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * Returns a set view of the mappings contained in this map. 
     */
    public Set entrySet() {
        return map.entrySet();
    }

    /**
     * Returns the value to which this map maps the specified key. 
     */
    public Object get(Object key) {
        return map.get(key);
    }

    /**
     * Returns true if this map contains no key-value mappings. 
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns a set view of the keys contained in this map. 
     */
    public Set keySet() {
        return map.keySet();
    }

    /**
     * Associates the specified value with the specified key in this map (optional operation). 
     */
    public Object put(Object key, Object value) {

        //this check is to ensure that the parental relationship is automatically
        //cleaned up from the item currently backing the specified key. The idea here
        //is that if you're removing an element (which you are effectively doing
        //via a put) then that element should no longer point to this object as its 
        //parent.
        Object curEl = map.get(key);
        if (curEl != null && curEl instanceof PData) {
            PData pdata = (PData) curEl;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(null);
            if (pdata.isInheritParents() && pdata.getParent() == this)
                pdata.setParent(null);    //csc_012003.1
        }

        //this check is used to ensure the parental hierarchy is automatically
        //maintained. If you add an element to this list and that element implements
        //PData and that element has inheritParents=true, then this list should
        //automatically automatically become that objects parent
        if (value != null && value instanceof PData) {
            PData pdata = (PData) value;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(this);
            if (pdata.isInheritParents() && pdata.getParent() == null)
                pdata.setParent(this);    //csc_012003.1
        }

        //put the value in the map
        return map.put(key, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map (optional operation). 
     */
    public void putAll(Map imap) {

        //iterate through all the key/values in the map...if the key already
        //exists in the current map it will be replacing data, so we need to 
        //clear the parental value on the existing data. Otherwise, treat it
        //like a straight "set" and just update the parental value on the incoming
        //data
        Iterator it = imap.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();

            //see if there is an existing value for this key
            Object ovalue = map.get(key);
            if (ovalue != null && ovalue instanceof PData) {
                PData pdata = (PData) ovalue;
//csc_012003.1                if (pdata.isInheritParents()) pdata.setParent(null);
                if (pdata.isInheritParents() && pdata.getParent() == this)
                    pdata.setParent(null);    //csc_012003.1
            }

            //now look at the new value for the key
            Object nvalue = imap.get(key);
            if (nvalue != null && nvalue instanceof PData) {
                PData pdata = (PData) nvalue;
//csc_012003.1                if (pdata.isInheritParents()) pdata.setParent(this);
                if (pdata.isInheritParents() && pdata.getParent() == null)
                    pdata.setParent(this);    //csc_012003.1
            }
        }

        //put the map into our map
        map.putAll(imap);
    }

    /**
     * Removes the mapping for this key from this map if present (optional operation). 
     */
    @Override
    public Object remove(Object key) {
        Object value = map.get(key);
        if (value != null && value instanceof PData) {
            PData pdata = (PData) value;
            if (pdata.isInheritParents() && pdata.getParent() == this)
                pdata.setParent(null);
        } 
        return map.remove(key);
    }

    /**
     * Returns the number of key-value mappings in this map. 
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns a collection view of the values contained in this map. 
     */
    public Collection values() {
        return map.values();
    }

    //--------------- Cloneable ----------------------------------
    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance. (The
     * elements themselves are not copied.)
     *
     * @return  a clone of this <tt>ArrayList</tt> instance.
     */
    public Object clone() {
        try {
            PHashMap phm = (PHashMap) super.clone();
            phm.map = new HashMap(map);
            return phm;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    //--------------- Object -------------------------------------
    /**
     * Check object for equality. Will return true if the incoming 
     * object is a) non-null, b) the size of the underlying list 
     * structures is the same and c) the list containsAll() the
     * same elements
     *
     * @param obj the object we're comparing against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof PMap))
            return false;
        PMap pm = (PMap) obj;
        if (this.size() != pm.size())
            return false;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (!pm.containsKey(entry.getKey()) || !pm.containsValue(entry.getValue()))
                return false;
        }
        return true;
    }

    /**
     * Returns the hash code value for this list. 
     */
    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return (DesiredType) getState(key);
    }
}
