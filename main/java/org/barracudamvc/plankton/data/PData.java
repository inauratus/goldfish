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
 * $Id: PData.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.data;

import java.io.Serializable;

/**
 * This interface defines the methods required to establish 
 * a heirarchical relationship between objects.
 */
public interface PData extends StateMap, Cloneable, Serializable {

    /**
     * set the object's parent
     *
     * @param   p  
     */
    public void setParent(PData p);

    /**
     * get the objects parent (null if root)
     *
     * @return the PData object
     */
    public PData getParent();

    /**
     * get the root parent by chaining back up the heirarchy until we find 
     * the highest PData object in the heirarchy.
     *
     * @return the root PData object
     */
    public PData getRootParent();
    
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
    public void setInheritParents(boolean val);
    
    /**
     * Return true if we are inheriting parents.
     * 
     * @return true if we are inheriting parents
     */
    public boolean isInheritParents();
}
