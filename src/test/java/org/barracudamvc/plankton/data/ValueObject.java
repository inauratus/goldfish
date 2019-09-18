/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: ValueObject.java 
 * Created: Oct 7, 2013 8:56:40 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */

package org.barracudamvc.plankton.data;

import java.io.File;

public class ValueObject {
    public static Class TEST_CLASS = Object.class;
    public static String TEST_STRING = null;
    public static String TEST_STRING2 = null;
    public static String TEST_STRING3 = null;
    public static int TEST_INT = -1;
    public static Integer TEST_INT2 = new Integer(-1);
    public static short TEST_SHORT = -1;
    public static Short TEST_SHORT2 = new Short((short) -1);
    public static long TEST_LONG = -1;
    public static Long TEST_LONG2 = new Long(-1);
    public static double TEST_DOUBLE = -1;
    public static Double TEST_DOUBLE2 = new Double(-1);
    public static float TEST_FLOAT = -1;
    public static Float TEST_FLOAT2 = new Float(-1);
    public static boolean TEST_BOOLEAN = false;
    public static Boolean TEST_BOOLEAN2 = Boolean.FALSE;
    public static String TEST_STRING88 = null;
    public static String TEST_STRING99 = null;
    public static StringBuffer TEST_VALUE_1 = null;
    public static File HOME = null;

    public static void setTestString2(String s1) {
        TEST_STRING2 = s1;
    }

    public static void setTestString3(String s1, String s2) {
        TEST_STRING3 = s1 + s2;
    }
    
    public static void setTestValue1(StringBuffer buffer) {
        TEST_VALUE_1 = buffer;
    }

}
