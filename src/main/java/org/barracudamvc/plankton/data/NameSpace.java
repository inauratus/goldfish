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
 * $Id: NameSpace.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.data;


/**
 * This class defines a NameSpace entity (like a key) that can be used to 
 * set/retrieve objects from the ObjectRepository
 */
public class NameSpace {

    protected String name;
    protected static NameSpace globalNameSpace;
    protected static long lastTime = 0;

    /**
     * protected no args constructor (use the factory methods)
     */
    protected NameSpace() {
        this(null);
    }

    protected NameSpace(String iname) {
        if (iname==null) name = "Global";
        else name = iname;
    }

    /**
     * get the name of this NameSpace
     *
     * @return the name of this NameSpace
     */
    public String getName() {
        return name;
    }

    /**
     * return the String represenation of the NameSpace
     *
     * @return the String represenation of the NameSpace
     */
    public String toString() {
        return "NameSpace:"+name;
    }

    /**
     * get the global NameSpace instance
     *
     * @return the global NameSpace instance
     */
    public synchronized static NameSpace globalInstance() {
        if (globalNameSpace==null) globalNameSpace = new NameSpace();
        return globalNameSpace;
    }

    /**
     * get a new NameSpace instance using the default name
     *
     * @return new NameSpace instance using the default name
     */
    public synchronized static NameSpace newInstance() {
        return newInstance("NS");
    }

    /**
     * get a new NameSpace instance using the specified name
     *
     * @param key the namespace key
     * @return the corresponding NameSpace
     */
    public synchronized static NameSpace newInstance(String key) {
        long newTime = 0;
        do {newTime = new java.util.Date().getTime();}
        while (newTime<=lastTime);
        lastTime = newTime;
        NameSpace ns = new NameSpace(key+"_"+newTime);
        return ns;
    }


}