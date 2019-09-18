/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.util;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.runner.JUnitCore;

public class TestUtil {

    public static void run(Class<?> toRun) {
        DOMConfigurator.configure("log4j.xml");
        
        JUnitCore core = new JUnitCore();
        core.addListener(new JUnitLogger());
        core.run(toRun);
    }
}
