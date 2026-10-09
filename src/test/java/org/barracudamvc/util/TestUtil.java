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
