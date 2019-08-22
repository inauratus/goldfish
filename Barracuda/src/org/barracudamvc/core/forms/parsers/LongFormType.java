/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

public class LongFormType extends WholeNumberFormType<Long> implements NumberComparator {

    @Override
    public Class<Long> getFormClass() {
        return Long.class;
    }

    protected Long narrow(Long value) {
        return value;
    }

    @Override
    public Long[] getTypeArray(int size) {
        return new Long[size];
    }

    public int compare(Number o1, Number o2) {
        long n1 = o1.longValue();
        long n2 = o2.longValue();

        if (n1 > n2) {
            return 1;
        } else if (n1 < n2) {
            return -1;
        } else {
            return 0;
        }
    }
}
