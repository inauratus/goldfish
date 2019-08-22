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
 * $Id: StringUtil.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.plankton;

import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * Simple utility functions that work on Strings
 * TODO - can't this be relaced by java.lang.String.replaceAll() [jdk 1.4] - A: probably not; that funtion uses regexps; this one doesn't
 */
public class StringUtil {

    //public constants
    protected static final Logger logger = Logger.getLogger(StringUtil.class.getName());

    /**
     * Replace all occurences of a pattern in a String with a new pattern
     *
     * @param sourceStr the source string
     * @param oldPattern the old pattern
     * @param newPattern the new pattern
     * @return an adjusted String
     */
    public static String replace(String sourceStr, String oldPattern, String newPattern) {
        if (sourceStr!=null && sourceStr.length()<1) return sourceStr;  //no way we can match on this, so just return the source str
        String s = _replace(sourceStr, oldPattern, newPattern);
        if (s!=null && s.length()<1) {
            return (newPattern==null ? null : s);
        } else {
            return s;
        }
    }
    private static String _replace(String sourceStr, String oldPattern, String newPattern) {
        //eliminate the obvious
        if (sourceStr==null) {
            if (oldPattern==null) return newPattern;
            else return sourceStr;
        }
        else if (oldPattern==null) return sourceStr;
        if (newPattern==null) newPattern = "";

        //see if the pattern exists
        int i = sourceStr.indexOf(oldPattern);
        if (i<0) return sourceStr;
        else return sourceStr.substring(0,i)+newPattern+_replace(sourceStr.substring(i+oldPattern.length()),oldPattern,newPattern);
    }

    /**
     * get a Calendar representing an elapsed amt of time
     *
     * @since csc_010404_1
     */
    public static Calendar getElapsed(long elapsed) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setTimeInMillis(cal.getTimeInMillis()+elapsed);
        return cal;    
    }
    
    /**
     * get a String describing an elapsed amt of time (format: "XX hrs, YY mins, ZZ secs (MMMM millis)")
     *
     * @since csc_010404_1
     */
    public static String getElapsedStr(long elapsed) {
        return getElapsedStr(elapsed, Calendar.MILLISECOND);
    }    

    /**
     * get a String describing an elapsed amt of time (format: "XX hrs, YY mins, ZZ secs"). 
     * Granularity are calendar constants MILLISECOND, SECOND, MINUTE, or HOUR.
     *
     * @since csc_010404_1
     */
    public static String getElapsedStr(long elapsed, int granularity) {
        if (elapsed==0) return "0 secs";
        Calendar cal = getElapsed(elapsed);
        int hrs = cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);
        int secs = cal.get(Calendar.SECOND);
        StringBuffer sb = new StringBuffer(100);
        String sep = "";
        if (hrs>0) {
            sb.append(hrs+" hr"+(hrs>1 ? "s" : ""));
            sep = ", ";
        }
        if ((mins>0) && (granularity==Calendar.MINUTE || granularity==Calendar.SECOND || granularity==Calendar.MILLISECOND)) {
            sb.append(sep+mins+" min"+(mins>1 ? "s" : ""));
            sep = ", ";
        }
        if ((secs>0) && (granularity==Calendar.SECOND || granularity==Calendar.MILLISECOND)) {
            sb.append(sep+secs+" sec"+(secs>1 ? "s" : ""));
            sep = ", ";
        }
        if (granularity==Calendar.MILLISECOND) {
            sb.append(" ("+elapsed+" millis)");
        }
        return sb.toString();
    }    

    /**
     * Return an identity string for the given object.
     * @since   saw_102103_1
     */
    public static String getIdentity(Object obj) {
        return (obj==null ? "null" : obj.getClass().getName()+"@"+Integer.toHexString(System.identityHashCode(obj)));
    }
    public static String getDescr(Object obj) {
        String s = ""+obj;
        if (s.length()>100) s = s.substring(0,60)+"...";
        return " {"+s+"}";
    }

    
    public static String trim(String val) {
        if (val == null)
            return null;
        String trimmedValue = val.trim();
        if (trimmedValue.isEmpty())
            return null;
        else
            return trimmedValue;
    }
}