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
 * $Id: Param.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.data;


/**
 * <p>Define a Param object as having key and value properties
 * (both of which are Strings)
 */
public class Param {

    String key = null;
    Object value = null;

    /**
     * Create an empty Param
     */
    public Param() {
        this(null, null);
    }

    /**
     * Create a Param for a given key/value
     *
     * @param ikey the param key
     * @param ivalue the param value
     */
    public Param(String ikey, Object ivalue) {
        setKey(ikey);
        setValue(ivalue);
    }

    /**
     * Set the key
     *
     * @param ikey the param key
     */
    public void setKey(String ikey) {
        key = ikey;
    }

    /**
     * Get the key
     *
     * @return the param key
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the value
     *
     * @param ivalue the param value
     */
    public void setValue(Object ivalue) {
        value = ivalue;
    }

    /**
     * Get the value
     *
     * @return the param value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Check for equality (if both key & value match)
     *
     * @param o the object we are comparing against
     * @return true if the objects are equal
     */
    public boolean equals(Object o) {
        if (o==null) return false;
        if (o instanceof Param) {
            Param p = (Param) o;
            if (key==null && p.key!=null) return false;
            if (value==null && p.value!=null) return false;
            else return (key.equals(p.key) && value.equals(p.value));
        } else {
            return false;
        }
    }

    /**
     * Return a string representation of the param
     */
    public String toString() {
        return "Param {k:"+key+" v:"+value+"}";
    }
}
