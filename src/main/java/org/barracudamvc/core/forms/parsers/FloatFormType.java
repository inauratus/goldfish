/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: FloatFormType.java 
 * Created: Aug 15, 2013 2:44:37 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.plankton.StringUtil;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class FloatFormType extends FormType<Float> implements NumberComparator {

    @Override
    public Class<Float> getFormClass() {
        return Float.class;
    }

    @Override
    public Float parse(String origVal, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(origVal);
        if (trimmed == null)
            return null;

        try {
            return new Float(trimmed);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public Float[] getTypeArray(int size) {
        return new Float[size];
    }

    public int compare(Number o1, Number o2) {
        float n1 = o1.shortValue();
        float n2 = o2.shortValue();

        if (n1 > n2) {
            return 1;
        } else if (n1 < n2) {
            return -1;
        } else {
            return 0;
        }
    }
}
