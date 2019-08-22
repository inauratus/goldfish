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

public class IntegerFormTypeTest extends AbstractWholeNumber {

    @Test
    public void testMaxMin() throws ParseException {
        assertParsed(Integer.MAX_VALUE + "", Integer.MAX_VALUE);
        assertParsed(Integer.MIN_VALUE + "", Integer.MIN_VALUE);
        assertParseFail(((Long.valueOf(Integer.MAX_VALUE) + 1) + ""));
        assertParseFail(((Long.valueOf(Integer.MIN_VALUE) - 1) + ""));
    }

    @Override
    public Object convertType(Object value) {
        return Integer.parseInt(value.toString());
    }

    @Override
    public FormType getParser() {
        return new IntegerFormType();
    }
}
