/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: FloatFormTypeTest.java 
 * Created: Aug 17, 2013 12:38:35 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class FloatFormTypeTest extends AbstractParser {

    @Test
    public void testValid() {
        assertParsed(".1", (float) .1);
        assertParsed("0.1", (float) .1);
        assertParsed("1.0", (float) 1.0);
        assertParsed("-1.0", (float) -1.0);
        assertParsed("0", (float) 0);
        assertParsed("-0", ((float) 0) * -1);
        assertParsed(Float.MAX_VALUE + "", Float.MAX_VALUE);
        assertParsed(Float.MIN_VALUE + "", Float.MIN_VALUE);
    }

    @Test
    public void testToInfinity() {
        assertParsed("4.4028235E38", Float.POSITIVE_INFINITY);
        assertParsed("-4.4028235E38", Float.NEGATIVE_INFINITY);
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
        return new FloatFormType();
    }
}
