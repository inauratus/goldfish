/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

public class DoubleFormTypeTest extends AbstractParser {

    @Test
    public void testValid() {
        assertParsed(".1", (double) .1);
        assertParsed("0.1", (double) .1);
        assertParsed("1.0", (double) 1.0);
        assertParsed("-1.0", (double) -1.0);
        assertParsed("0", (double) 0);
        assertParsed("-0", ((double) 0) * -1);
        assertParsed(Double.MAX_VALUE + "", Double.MAX_VALUE);
        assertParsed(Double.MIN_VALUE + "", Double.MIN_VALUE);
    }

    @Test
    public void testToInfinity() {
        assertParsed("2.7976931348623157E309", Double.POSITIVE_INFINITY);
        assertParsed("-2.7976931348623157E309", Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testInvalid() {
        assertParseFail("$1");
        assertParseFail("(1)");
        assertParseFail("�1");
        assertParseFail("a");
        assertParseFail("1,000.1");
        assertParseFail("1..1");
        assertParseFail("..1");
    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return value;
    }

    @Override
    public FormType getParser() throws ParseException {
        return new DoubleFormType();
    }
}
