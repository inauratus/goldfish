/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms;

import java.util.Locale;

public class MockFormType extends FormType<byte[]> {

    @Override
    public Class<byte[]> getFormClass() {
        return byte[].class;
    }

    @Override
    public byte[][] getTypeArray(int size) {
        return new byte[size][];
    }

    @Override
    public byte[] parse(String origVal, Locale loc) throws ParseException {
        return new byte[]{1, 2, 3};
    }

}
