/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: LongFormTypeTest.java 
 * Created: Aug 15, 2013 10:27:22 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class LongFormTypeTest extends AbstractWholeNumber {

    @Override
    public FormType getParser() {
        return new LongFormType();
    }

    @Override
    public Object convertType(Object value) {
        return Long.parseLong(value.toString());
    }
}
