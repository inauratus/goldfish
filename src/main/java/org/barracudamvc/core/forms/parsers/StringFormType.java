/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: StringFormType.java 
 * Created: Aug 15, 2013 8:39:05 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */

package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;


/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class StringFormType extends FormType<String> {

    @Override
    public Class<String> getFormClass() {
        return String.class;
    }
    
    @Override
    public String parse(String origVal, Locale locale) throws ParseException {
        if (origVal == null || origVal.isEmpty()) {
            return null;
        }

        return origVal;
    }

    @Override
    public String parse(Object val, Locale locale) throws ParseException {
        return parse(val == null ? null : val.toString(), locale);
    }

    @Override
    public String[] getTypeArray(int size) {
        return new String[size];
    }
}
