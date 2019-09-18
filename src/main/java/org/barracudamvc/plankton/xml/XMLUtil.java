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
 * $Id: XMLUtil.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.xml;


/**
 * Utility functions for XML related text conversion. The functions 
 * we have in place at this time are used to convert xml based unicode
 * text into Java based unicode and vica versa.
 */
public class XMLUtil {
    
    private static String sep = System.getProperty("line.separator");
    
    /**
     * Convert a String from XML unicode string. Basically, we look for
     * anything starting with &# followed by a semicolon and convert it to
     * the actual Java character representation
     *
     * @param s the String to be converted
     * @return the converted string
     */
    public static String fromXMLUnicodeString(String s) {
        StringBuffer sb = new StringBuffer(s.length());
        
//        char c[] = s.toCharArray();
        int cpos = 0;
        int spos = -1;
        int epos = -1;
        int mpos = s.length();
        while (cpos<mpos) {
            spos = s.indexOf("&#",cpos);
            if (spos>-1) epos = s.indexOf(";", spos);
            
            if (spos>-1 && epos>-1) {
                sb.append(s.substring(cpos,spos));
                String unicode = s.substring(spos+2,epos);
                try {
                    int newi = Integer.decode("0"+unicode).intValue();
                    char newch = (char) newi;
                    sb.append(newch);
                    cpos = epos+1;
                } catch (Exception e) {
                    sb.append(s.substring(spos,spos+2));
                    cpos = spos + 2;
                }
                    
            } else {
                sb.append(s.substring(cpos,mpos));
                cpos = mpos;
            }
        }
        return sb.toString();
    }
        
    /**
     * Convert a String to legal XML unicode string. Basically, we look
     * for special chars (&,<,>,',") and replace them with their XML 
     * equivalents. In addition, replace anything higher than ~ with the
     * XML unicode version (&#nnn;). Note that this method is smart enough
     * to keep track of the number of characters which need to get converted
     * to Unicode, and if that number exceeds about 15 percent of the size
     * of the String it'll just return the whole String blocked within a CDATA
     * section.
     *
     * @param s the String to be converted
     * @return the converted string
     */
    public static String toXMLUnicodeString(String s) {
        StringBuffer sb = new StringBuffer(s.length());
        char c[] = s.toCharArray();
        int max = c.length;
        int code = -1;
        int epos = max-1;
        int convCntr = 0;
//        int convThreshold = max/15;
        
        for (int i=0; i<max; i++) {
            code = c[i];
            
            //the purpose of this line is to strip out CR/LF's and replace them with separator character
            if (code==10 || code==13) {
                sb.append(sep);
                int nextCode = (i<epos ? c[i+1] : -1);
                if ((code==10 && nextCode==13) || (code==13 && nextCode==10)) i++;
                continue;
            }
            
            //leave these characters alone:
            // a) anything between space and ~, except for &,<,>,',"
            // b) \t, \n, \r
            if (((code>=' ') && (code<='~') && (code!='&') && (code!='<') && (code!='>') && (code!='\'') && (code!='"')) || 
                (code=='\t') || (code=='\n') || (code=='\r')) {
                sb.append(c[i]);    
            } else {
                String uc = null;
                String hex = Integer.toHexString(c[i]);

                //this converts it correctly for IE and XML browsers
                uc = "&#x"+hex+";";
                
                sb.append(uc);                
                convCntr++;
            }
            
            //the purpose of this is so that if we end up converting more than about 10% of the characters
            //we just just bail and return the text in a CDATA block. This will be a more efficient use of 
            //processing and bandwidth resources...
//            if (convThreshold>5 && convCntr>convThreshold) {
//                return "<![CDATA["+s+"]]>";    
//            }
        }
        return sb.toString();
    }
    

    /**
     * Main method. Run this to perform a simple little test of the
     * class conversion methods.
     */    
    public static void main (String args[]) {
        String target = null;
        String dest = null;
        String result = null;
        
        //FROM...
        System.out.println ("");
        System.out.println ("From XML to Java...");
        
        //shouldn't be any changes
        target = "blah blah blah";
        dest = target;
        result = fromXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
        //shouldn't be any changes
        target = "blah < blah > blah";
        dest = target;
        result = fromXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
        //should be 3 changes
        target = "Test &#xa9;1 and Test&#xa9;2 and &#xa9;";
        dest = "Test \u00a91 and Test\u00a92 and \u00a9";
        result = fromXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));

        //try leading pos
        target = "&#xa9;1 and Test&#xa9;2 and &#xa9; sdf";
        dest = "\u00a91 and Test\u00a92 and \u00a9 sdf";
        result = fromXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
        //try leading pos
        target = "&#xa9;";
        dest = "\u00a9";
        result = fromXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));

        
        
        //TO...
        System.out.println ("");
        System.out.println ("From Java to XML...");
        
        //shouldn't be any changes
        target = "blah blah blah";
        dest = target;
        result = toXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
        //should be two changes
        target = "blah < blah > blah";
        dest = "blah &#x3c; blah &#x3e; blah";
        result = toXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
        //should be 3 changes
        target = "Test \u00a91 and Test\u00a92 and \u00a9";
        dest = "Test &#xa9;1 and Test&#xa9;2 and &#xa9;";
        result = toXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));

        //try leading pos
        target = "\u00a91 and Test\u00a92 and \u00a9 sdf";
        dest = "&#xa9;1 and Test&#xa9;2 and &#xa9; sdf";
        result = toXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
        //try leading pos
        target = "\u00a9";
        dest = "&#xa9;";
        result = toXMLUnicodeString(target);
        System.out.println ("S/B:["+dest+"] Result:["+result+"]..."+(dest.equals(result) ? "ok" : "failed"));
        
    }
    
}
