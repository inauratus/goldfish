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
 * $Id: HttpConverter.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.http;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * <p>This class provides a simple series of static methods
 * to convert a Map to a URL String and vica-versa</p>
 */
public class HttpConverter {

    /**
     * Convert the values in a Map to a URL String (ie. key1=val1&key2=val2&...)
     */
    public static String cvtMapToURLString(Map map) {
        return cvtMapToURLString(map, "&");
    }

    /**
     * Convert the values in a Map to a URL String (ie.
     * key1=val1&key2=val2&...). You
     * can specify an alternate delimeter here.
     */
    public static String cvtMapToURLString(Map map, String delimiter) {
        //eliminate the obvious
        if (map == null) {
            return null;
        }

        //if the map is not an instance of TreeMap, convert it so
        //that the keys are ordered by default. By doing this automatically,
        //we ensure that you can count on the order always being the same - 
        //you can easily tell if url parameters are the same between requests 
        //simply by comparing the resulting strings
        if (!(map instanceof SortedMap)) {    //csc_013104_1
            map = new TreeMap<Object, Object>(map);
        }

        //run through the map and build a new string
        StringBuffer sb = new StringBuffer(200);
        String sep = "";
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Object value = map.get(key);
            if (value instanceof Set) {
                Set set = (Set) value;
                Iterator it2 = set.iterator();
                while (it2.hasNext()) {
                    sb.append(concat(sep, key, it2.next()));
                }
            } else {
                sb.append(concat(sep, key, value));
            }
            sep = delimiter;
        }
        return sb.toString();
    }

    private static String concat(String sep, Object key, Object value) {
        if (key == null) {
            key = "";
        }
        if (value == null) {
            value = "";
        }

        return sep + encode(key.toString()) + "=" + encode(value.toString());
    }

    public static Map<String, String> flatten(Map<Object, Object> fieldsMap) {
        Map<String, String> rtv = new HashMap<>();
        if (fieldsMap.isEmpty())
            return rtv;

        for (Map.Entry<Object, Object> o : fieldsMap.entrySet()) {
            Object value = o.getValue();
            if (String.class.isAssignableFrom(value.getClass()))
                rtv.put(o.getKey().toString(), value.toString());
            if (Set.class.isAssignableFrom(value.getClass())) {
                List<String> v = new ArrayList<>((Set<String>) value);
                rtv.put(o.getKey().toString(), v.get(0));
            }
        }

        return rtv;
    }

    /**
     * Convert the values in a URL String (ie. key1=val1&key2=val2&...) to a Map
     * of key/val pairs
     */
    public static Map cvtURLStringToMap(String paramStr) {
        return cvtURLStringToMap(paramStr, "&");
    }

    /**
     * Convert the values in a URL String (ie. key1=val1&key2=val2&...) to a Map
     * of key/val pairs. You can specify an alternate delimeter here.
     */
    public static Map cvtURLStringToMap(String paramStr, String delimiter) {
        TreeMap map = new TreeMap();
        if (paramStr == null) {
            return map;
        }

        int spos = 0;
        int epos = -1;
        int eqpos = -1;
        if (paramStr.startsWith("?")) {
            paramStr = paramStr.substring(1);
        }
        int max = paramStr.length();
        while (spos >= 0 && spos < max) {
            eqpos = paramStr.indexOf("=", spos + 1);
            if (eqpos < 0) {
                break;
            }
            epos = paramStr.indexOf(delimiter, eqpos + 1);
            if (epos < 0 && eqpos > -1) {
                epos = max;
            }
            if (eqpos > spos || eqpos < epos) {
                String key = decode(paramStr.substring(spos, eqpos));
                String value = decode(paramStr.substring(eqpos + 1, epos));
                if (map.containsKey(key)) {
                    Object mapVal = map.get(key);
                    if (mapVal instanceof Set) {
                        ((Set) mapVal).add(value);
                    } else {
                        Set set = new HashSet();
                        map.put(key, set);
                        set.add(mapVal);
                        set.add(value);
                    }
                } else {
                    map.put(key, value);
                }
            }
            spos = epos + 1;
        }
        return map;
    }

    /**
     * Decode an encoded String
     */
    public static String decode(String s) {
        if (s == null) {
            return null;
        }
        String ds = s;
        try {
            ds = java.net.URLDecoder.decode(s, "UTF-8");
        } catch (java.lang.IllegalArgumentException ex) {
            ds = s;
        } catch (UnsupportedEncodingException ex) {
            ds = "";
        }
        return ds;
    }

    /**
     * Encode a String
     */
    public static String encode(String s) {
        if (s == null) {
            return null;
        }
        try {
            s = java.net.URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            s = "";
        }
        return s;
    }
}
