/*
 * Copyright 2002 ATMReports.com. All Rights Reserved.
 *
 * This software is the proprietary information of ATMReports.com.
 * Use is subject to license terms.
 *
 * $Id:
 */
package org.barracudamvc.testbed;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * The base class from which all other Barracuda test classes should extend.
 * Will automatically configure log4j based on the settings in WEB-INF/log4j.xml
 */
public class DefaultTestCase {

    //common vars (customize for every test class)
    public static String testClass = DefaultTestCase.class.getName();
    public static Logger logger = Logger.getLogger("test." + testClass);

    //variables
    public final static String TEST_SUITE_USER = "TestSuiteUser";

    /**
     * Static initializer, for the first time a test case is accessed. The
     * basic purpose of this is to set up the default ObjectRepository to
     * match what we expect at runtime.
     */
    static {
        //manually configure the log4j stuff
        DOMConfigurator.configure("../../WEB-INF/log4j.xml");
    }

    public static void run(String[] args, Class testClass) {
        TestUtil.parseParams(args);

        org.barracudamvc.util.TestUtil.run(testClass);
    }

}
