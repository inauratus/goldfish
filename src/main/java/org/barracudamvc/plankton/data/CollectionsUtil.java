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
 * $Id: CollectionsUtil.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Collections utilities
 */
public class CollectionsUtil {

    private static byte[] sep = System.getProperty("line.separator").getBytes();

    /**
     * Check if o1 is equivalent to o2. Takes into java.sql.Dates (where .equals())
     * doesn't work as you'd expect) and Comparables (where you really want to use
     * .compareTo() rather than .equals()). 
     *
     * @param o1 object1
     * @param o2 object2
     * @returns true if o1 is equivalent to o2
     */
    @SuppressWarnings("unchecked")
    public static boolean checkEquals(Object o1, Object o2) {
        if (o1!=o2) {
            if (o1==null || o2==null) return false;
            else {
                if (o1 instanceof java.sql.Date) o1 = (java.util.Date) o1;
                if (o2 instanceof java.sql.Date) o2 = (java.util.Date) o2;
                if (o1 instanceof Comparable) {
                    try {
                        if (((Comparable) o1).compareTo(o2)!=0) return false;
                    } catch (ClassCastException e) {
                        return false;
                    }
                } else if (!(o1.equals(o2))) return false;
            }
        }
        return true;
    }


    /**
     * <p>utility method to recursively print the stack trace for a Map. 
     *
     * @param map the Map we wish to dump
     */
    public static void printStackTrace(Map map) {
        printStackTrace(map, System.out);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a Map. 
     *
     * @param map the Map we wish to dump
     * @param extLogger the Logger we wish to dump it to
     */
    public static void printStackTrace(Map map, Logger extLogger) {
        printStackTrace(map, 0, extLogger, null);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a Map.
     *
     * @param map the Map we wish to dump
     * @param out the output stream we wish to dump it to
     */
    public static void printStackTrace(Map map, OutputStream out) {
        if (out==null) out = System.out;
        printStackTrace(map, 0, null, out);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a Map</p>
     *
     * <p>Map Specification:
     * <ol>
     *   <li>depth < 0 will be remapped to 0</li>
     *   <li>depth > 25 will be remapped to 25</li>
     * </ol>
     *
     * <p>Passing in a null value for the output stream will cause all the 
     * necessary output information to be generated, but nothing will
     * actually print anywhere. This is useful for bounds testing when
     * you want to make sure the routine is working but you don't really 
     * care about the output.
     *
     * @param map the Map we wish to dump
     * @param depth inset depth at which to start printing
     * @param extLogger the Logger we wish to dump it to (may be null)
     * @param out OutputStream to print to (only used if extLogger is null)
     */
    public static void printStackTrace(Map map, int depth, Logger extLogger, OutputStream out) {
        if (depth<0) depth = 0;
        if (depth>25) depth = 25;
        String spaces = "                                                                              ";
        String inset = spaces.substring(0,depth*3);
        if (map==null) {
            print (extLogger, out, inset+"map: null");
            return;
        }
        print (extLogger, out, inset+map.getClass().getName() + "@" + Integer.toHexString(map.hashCode()));            
        print (extLogger, out, inset+"   properties: ");
        Object keys[] = map.keySet().toArray();
        if (keys!=null) {
            int max = keys.length;
            for (int i=0; i<max; i++) {
                Object value = map.get(keys[i]);
                if (value instanceof Map) {
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(Map)");
                    printStackTrace((Map) value, depth+2, extLogger, out);
                } else if (value instanceof List) {
                    List l = (List) value;
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(List - "+l.size()+" items)");
                    if (l.size()>0) printStackTrace(l, depth+2, extLogger, out);
                } else if (value instanceof Object[]) {
                    Object[] objarray = (Object[]) value;
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(Object[] - "+objarray.length+" items)");
                    if (objarray.length>0) printStackTrace(objarray, depth+2, extLogger, out);
                } else if (value instanceof StateMap) {
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(StateMap)");
                    printStackTrace((StateMap) value, depth+2, extLogger, out);
                } else {
                    if ("password".equalsIgnoreCase(""+keys[i]) || "pwd".equalsIgnoreCase(""+keys[i])) value = "********";    //csc_030104_1
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]:"+value);
                }
            }
        }
        print (extLogger, out, inset+"   /end properties");
        print (extLogger, out, inset+"/end @" + Integer.toHexString(map.hashCode()));
        if (out!=null) try {out.flush();} catch (IOException ioe) {}
    }
    
    protected static void printStackTrace(StateMap map, int depth, Logger extLogger, OutputStream out) {
        if (depth<0) depth = 0;
        if (depth>25) depth = 25;
        String spaces = "                                                                              ";
        String inset = spaces.substring(0,depth*3);
        if (map==null) {
            print (extLogger, out, inset+"map: null");
            return;
        }
        print (extLogger, out, inset+map.getClass().getName() + "@" + Integer.toHexString(map.hashCode()));            
        print (extLogger, out, inset+"   properties: ");
        @SuppressWarnings("unchecked")
        Set<Object> skeys = map.getStateKeys();
        if (skeys!=null && skeys.size()>0) {
            Object keys[] = new ArrayList<Object>(skeys).toArray();
            int max = keys.length;
            for (int i=0; i<max; i++) {
                Object value = map.getState(keys[i]);
                if (value instanceof Map) {
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(Map)");
                    printStackTrace((Map) value, depth+2, extLogger, out);
                } else if (value instanceof List) {
                    List l = (List) value;
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(List - "+l.size()+" items)");
                    if (l.size()>0) printStackTrace(l, depth+2, extLogger, out);
                } else if (value instanceof Object[]) {
                    Object[] objarray = (Object[]) value;
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(Object[] - "+objarray.length+" items)");
                    if (objarray.length>0) printStackTrace(objarray, depth+2, extLogger, out);
                } else if (value instanceof StateMap) {
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]: ...(StateMap)");
                    printStackTrace((StateMap) value, depth+2, extLogger, out);
                } else {
                    if ("password".equalsIgnoreCase(""+keys[i]) || "pwd".equalsIgnoreCase(""+keys[i])) value = "********";    //csc_030104_1
                    print (extLogger, out, inset+"   [k]:"+keys[i]+" [v]:"+value);
                }
            }
        }
        print (extLogger, out, inset+"   /end properties");
        print (extLogger, out, inset+"/end @" + Integer.toHexString(map.hashCode()));
        if (out!=null) try {out.flush();} catch (IOException ioe) {}
    }

    /**
     * <p>utility method to recursively print the stack trace for a List. 
     * 
     * @param list the List we wish to dump
     */
    public static void printStackTrace(List list) {
        printStackTrace(list, System.out);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a List. 
     * 
     * @param list the List we wish to dump
     * @param extLogger the Logger we wish to dump it to
     */
    public static void printStackTrace(List list, Logger extLogger) {
        printStackTrace(list, 0, extLogger, null);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a List.
     *
     * @param list the Map we wish to dump
     * @param out the output stream we wish to dump it to
     */
    public static void printStackTrace(List list, OutputStream out) {
        if (out==null) out = System.out;
        printStackTrace(list, 0, null, out);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a List</p>
     *
     * <p>Bounds: <br>
     * If depth < 0, the method returns immediately</p>
     *
     * @param list the List we wish to dump
     * @param depth inset depth at which to start printing
     * @param extLogger the Logger we wish to dump it to (may be null)
     * @param out OutputStream to print to (only used if extLogger is null)
     */
    public static void printStackTrace(List list, int depth, Logger extLogger, OutputStream out) {
        if (depth<0) depth = 0;
        if (depth>25) depth = 25;
        String spaces = "                                                                              ";
        String inset = spaces.substring(0,depth*3);
        if (list==null) {
            print (extLogger, out, inset+"list: null");
            return;
        }
        print (extLogger, out, inset+list.getClass().getName() + "@" + Integer.toHexString(list.hashCode()));            
        print (extLogger, out, inset+"   items: ");
        Object items[] = list.toArray();
        if (items!=null) {
            int max = items.length;
            for (int i=0; i<max; i++) {
                if (items[i] instanceof Map) {
                    print (extLogger, out, inset+"   ["+i+"]: ...(Map)");
                    printStackTrace((Map) items[i], depth+2, extLogger, out);
                } else if (items[i] instanceof List) {
                    List l = (List) items[i];
                    print (extLogger, out, inset+"   ["+i+"]: ...(List - "+l.size()+" items)");
                    if (l.size()>0) printStackTrace(l, depth+2, extLogger, out);
                } else if (items[i] instanceof Object[]) {
                    Object[] objarray = (Object[]) items[i];
                    print (extLogger, out, inset+"   ["+i+"]: ...(Object[] - "+objarray.length+" items)");
                    if (objarray.length>0) printStackTrace(objarray, depth+2, extLogger, out);
                } else if (items[i] instanceof StateMap) {
                    print (extLogger, out, inset+"   ["+i+"]: ...(StateMap)");
                    printStackTrace((StateMap) items[i], depth+2, extLogger, out);
                } else {
                    print (extLogger, out, inset+"   ["+i+"]: "+items[i]);
                }
            }
        }
        print (extLogger, out, inset+"   /end items");
        print (extLogger, out, inset+"/end @" + Integer.toHexString(list.hashCode()));
        if (out!=null) try {out.flush();} catch (IOException ioe) {}
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a Object[]. 
     *
     * @param objarray the Object[] we wish to dump
     */
    public static void printStackTrace(Object[] objarray) {
        printStackTrace(objarray, System.out);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a Object[]. 
     *
     * @param objarray the Object[] we wish to dump
     * @param extLogger the Logger we wish to dump it to
     */
    public static void printStackTrace(Object[] objarray, Logger extLogger) {
        printStackTrace(objarray, 0, extLogger, null);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for a Object[].
     *
     * @param objarray the Object[] we wish to dump
     * @param out the output stream we wish to dump it to
     */
    public static void printStackTrace(Object[] objarray, OutputStream out) {
        if (out==null) out = System.out;
        printStackTrace(objarray, 0, null, out);
    }
    
    /**
     * <p>utility method to recursively print the stack trace for an Object[]</p>
     *
     * <p>Bounds: <br>
     * If depth < 0, the method returns immediately</p>
     *
     * @param objarray the Object[] we wish to dump
     * @param depth inset depth at which to start printing
     * @param extLogger the Logger we wish to dump it to (may be null)
     * @param out the OutputStream to print to (only used if extLogger is null)
     */
    public static void printStackTrace(Object[] objarray, int depth, Logger extLogger, OutputStream out) {
        if (depth<0) depth = 0;
        if (depth>25) depth = 25;
        String spaces = "                                                                              ";
        String inset = spaces.substring(0,depth*3);
        print (extLogger, out, inset+objarray.getClass().getName() + "@" + Integer.toHexString(objarray.hashCode()));            
        print (extLogger, out, inset+"   items: ");
        if (objarray!=null) {
            int max = objarray.length;
            for (int i=0; i<max; i++) {
                if (objarray[i] instanceof Map) {
                    print (extLogger, out, inset+"   ["+i+"]: ...(Map)");
                    printStackTrace((Map) objarray[i], depth+2, extLogger, out);
                } else if (objarray[i] instanceof List) {
                    List l = (List) objarray[i];
                    print (extLogger, out, inset+"   ["+i+"]: ...(List - "+l.size()+" items)");
                    if (l.size()>0) printStackTrace(l, depth+2, extLogger, out);
                } else {
                    print (extLogger, out, inset+"   ["+i+"]: "+objarray[i]);
                }
            }
        }
        print (extLogger, out, inset+"   /end items");
        print (extLogger, out, inset+"/end @" + Integer.toHexString(objarray.hashCode()));
        if (out!=null) try {out.flush();} catch (IOException ioe) {}
    }
    
    protected static void print(Logger extLogger, OutputStream out, String s) {
        if (extLogger!=null) {
            if (extLogger.isDebugEnabled()) extLogger.debug(s);
        } else if (out!=null) {
            try {
                out.write(s.getBytes());
                out.write(sep);
            } catch (IOException ioe) {}
        }
    }

}
