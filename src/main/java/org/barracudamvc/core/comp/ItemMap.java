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
 * $Id: ItemMap.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;



/**
 * This interface defines the methods needed to implement
 * an ItemMap object. An ItemMap can be returned by the getItem()
 * methods within any of the model implementations. It is used to 
 * associate a key with a particular value. It is primarily used
 * by the BSelect component (which can use both key and value information
 * when rendering). The other components just use the value information.
 */
public interface ItemMap extends Attrs {

    /**
     * Returns the key corresponding to this entry.
     */
    public Object getKey();

    /**
     * Returns the value corresponding to this entry. 
     */           
    public Object getValue();

    /**
     * Replaces the value corresponding to this entry with the specified 
     * value (optional operation). 
     */          
    public Object setValue(Object value);

}