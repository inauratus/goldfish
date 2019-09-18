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

public class ShortFormTypeTest extends AbstractWholeNumber {

    @Test
    public void testMaxMin() throws ParseException {
        assertParsed(Short.MAX_VALUE + "", Short.MAX_VALUE);
        assertParsed(Short.MIN_VALUE + "", Short.MIN_VALUE);
        assertParseFail((Short.MAX_VALUE + 1) + "");
        assertParseFail((Short.MIN_VALUE - 1) + "");
    }

    @Override
    public Object convertType(Object value) {
        return Short.parseShort(value.toString());
    }

    @Override
    public FormType getParser() {
        return new ShortFormType();
    }
}
