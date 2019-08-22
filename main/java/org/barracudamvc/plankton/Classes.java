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
 * $Id: Classes.java 264 2013-11-07 20:42:49Z charleslowery $
 */
package org.barracudamvc.plankton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Simple Class utilities
 */
public class Classes {

    protected static final Logger logger = Logger.getLogger(Classes.class.getName());
    protected static Map<String, Class> clCache = null;
    protected static final Class EMPTY = Empty.class;

    /**
     * This method provides a cached interface to get a class. If the class exists in the 
     * cache, it is simply returned; if it does not, it is created via Class.forName() and then
     * returned (saving a copy in the cache of course). If there is a problem creating the class
     * the exception will be logged and a null value will be returned (which means its up to you
     * to handle it).
     *
     * @param clName the fully qualified class name
     * @return a reference to the Class 
     */
    public synchronized static Class getClass(String clName) {
        if (clName == null) {
            return null;
        }
        if (clCache == null) {
            clCache = new HashMap<String, Class>();
        }
        Class cl = null;
        try {
            Object o = clCache.get(clName);
            if (EMPTY.equals(o)) {
                return null;
            }
            if (o != null) {
                cl = (Class) o;
            } else {
                cl = Class.forName(clName, true, Thread.currentThread().getContextClassLoader());
                clCache.put(clName, cl);
            }
        } catch (Exception e) {
            clCache.put(clName, EMPTY);
            cl = null;
        }
        return cl;
    }

    /**
     * Get a new instance of the class, without throwing an InstantiationException. If there is 
     * a problem creating the class the exception will be logged and a null value will be 
     * returned (which means its up to you to handle it). Note that this means the class must 
     * provide a no-args constructor
     *
     * @param cl the class we wish to obtain an instance from
     * @return a new instance of the class
     */
    public static Object newInstance(Class cl) {
        if (cl == null) {
            return null;
        }
        try {
            return cl.newInstance();
        } catch (Exception e) {
            logger.error("Error instantiating class:" + cl + ", err:" + e, e);
            return null;
        }
    }

    /**
     * Get a new instance of the class, without throwing an InstantiationException. This is 
     * a convenience method which takes a String version of the class name, looks up the
     * class via getClass(), and then tries to get an instance of it via newInstance().
     *
     * @param clName the fully qualified class name
     * @return a new instance of the class
     */
    public static Object newInstance(String clName) {
        return newInstance(getClass(clName));
    }

    /**
     * Get a List of all interfaces that are implemented
     * by an object
     */
    public static List getAllInterfaces(Object obj) {
        List<Class> list = new ArrayList<Class>();
        return new Classes().getAllInterfaces(obj.getClass(), list);
    }

    public static List<Class> getAllInterfaces(Class cl) {
        List<Class> list = new ArrayList<Class>();
        list.add(cl);
        return new Classes().getAllInterfaces(cl, list);
    }

    private List<Class> getAllInterfaces(Class cl, List<Class> list) {
        Class[] classes = cl.getInterfaces();
        for (int i = 0; i < classes.length; i++) {
            if (!list.contains(classes[i])) {
                list.add(classes[i]);
                getAllInterfaces(classes[i], list);
            }
        }

        Class supercl = cl.getSuperclass();
        if (supercl != null) {
            getAllInterfaces(supercl, list);
        }

        return list;
    }

    /**
     * Get a short version of a class name
     */
    public static String getShortClassName(Class cl) {
        String className = cl.getName();
        int spos = className.lastIndexOf(".");
        if (spos < 0) {
            return className;
        } else {
            return className.substring(spos + 1);
        }
    }

    /**
     * Class Represents an empty or invalid node
     */
    private static class Empty {
    };
}