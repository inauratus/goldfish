/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: JUnitLogger.java 
 * Created: Jul 31, 2014 3:09:45 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */

package org.barracudamvc.util;

import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


/**
 *
 * @author will.lowery
 */
public class JUnitLogger extends RunListener {

        private static final String testClass = JUnitLogger.class.getName();
        private static Logger logger = Logger.getLogger("test." + testClass);

        @Override
        public void testFailure(Failure failure) {
            logger.info("Test Failed");
            logger.info(failure.getDescription());
            logger.info(failure.getMessage());
            logger.error(failure.getException());
        }

        @Override
        public void testFinished(Description desc){
            logger.info(desc.getMethodName()+ ": has finished");
        }
        
        @Override
        public void testStarted(Description desc){
            logger.info(desc.getMethodName()+" of " + desc.getClassName()+" has Started.");
        }
        

        @Override
        public void testRunFinished(Result result){
            logger.info("The test run has finished with " + result.getRunCount() + 
                    " tests run and, " + result.getFailureCount() + " Failures ");
            
        }
    }