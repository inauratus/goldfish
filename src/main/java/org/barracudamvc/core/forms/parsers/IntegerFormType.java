/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.ParseException;

public class IntegerFormType extends WholeNumberFormType<Integer> implements NumberComparator {

    @Override
    public Class<Integer> getFormClass() {
        return Integer.class;
    }

    @Override
    public Integer[] getTypeArray(int size) {
        return new Integer[size];
    }

    @Override
    protected Integer narrow(Long value) throws ParseException {
        validateBounds(value, (long) Integer.MAX_VALUE, (long) Integer.MIN_VALUE);
        return value.intValue();
    }

    public int compare(Number o1, Number o2) {
        int n1 = o1.intValue();
        int n2 = o2.intValue();

        if (n1 > n2) {
            return 1;
        } else if (n1 < n2) {
            return -1;
        } else {
            return 0;
        }
    }
}
