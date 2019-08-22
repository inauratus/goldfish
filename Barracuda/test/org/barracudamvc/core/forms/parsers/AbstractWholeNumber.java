/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: AbstractWholeNumberTest.java 
 * Created: Aug 15, 2013 10:25:46 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public abstract class AbstractWholeNumber extends AbstractParser {

    @Test
    public void testNullCases() throws ParseException {
        assertParsed(null, null);
        assertParsed("", null);
        assertParsed("     ", null);
    }

    @Test
    public void testInvalid() {
        assertParseFail("a");
        assertParseFail("1...");
        assertParseFail("|");
        assertParseFail("1-1");
    }

    @Test
    public void testTrailingZeros() throws ParseException {
        assertParsed("1.0", 1);
        assertParsed("1.", 1);
        assertParsed("12.0", 12);
        assertParsed("2.0", 2);
        assertParsed("9.0", 9);
        assertParsed("0.0", 0);
        assertParsed("1.00000000000000000000000000000", 1);
    }

    @Test
    public void testWholeNumbers() throws ParseException {
        for (int i = 0; i < 100; i++) {
            assertParsed(i + "", i);
        }
        for (int i = 0; i < -100; i--) {
            assertParsed(i + "", i);
        }
    }
}
