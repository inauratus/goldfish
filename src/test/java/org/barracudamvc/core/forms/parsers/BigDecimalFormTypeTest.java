/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.math.BigDecimal;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

public class BigDecimalFormTypeTest extends AbstractParser {

    @Test
    public void testValid() {
        assertParsed(".1", (double) .1);
        assertParsed("0.1", (double) .1);
        assertParsed("1.0", (double) 1.0);
        assertParsed("-1.0", (double) -1.0);
        assertParsed(Double.MAX_VALUE + "", Double.MAX_VALUE);
        assertParsed(Double.MIN_VALUE + "", Double.MIN_VALUE);
    }

    @Test
    public void testParseWithExtra() {
        assertParsed("$1", 1);
        assertParsed("(1)", -1);
        assertParsed("�1", 1);
        assertParsed("1,000.1", "1000.1");
    }

    @Test
    public void testInvalid() {
        assertParseFail("a");
        assertParseFail("(1");
        assertParseFail("1..1");
        assertParseFail("..1");
    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return new BigDecimal(value.toString());
    }

    @Override
    public FormType getParser() throws ParseException {
        return new BigDecimalFormType();
    }
}
