/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: DoubleFormType.java 
 * Created: Aug 15, 2013 2:38:50 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.plankton.StringUtil;

public class DoubleFormType extends FormType<Double> implements NumberComparator {

    @Override
    public Class<Double> getFormClass() {
        return Double.class;
    }

    @Override
    public Double parse(String val, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(val);
        if (trimmed == null)
            return null;

        try {
            return new Double(trimmed);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public Double[] getTypeArray(int size) {
        return new Double[size];
    }

    public int compare(Number o1, Number o2) {
        double n1 = o1.shortValue();
        double n2 = o2.shortValue();

        if (n1 > n2) {
            return 1;
        } else if (n1 < n2) {
            return -1;
        } else {
            return 0;
        }
    }
}
