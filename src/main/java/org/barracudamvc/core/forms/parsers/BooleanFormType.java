/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: BooleanFormType.java 
 * Created: Aug 15, 2013 8:44:52 AM
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
public class BooleanFormType extends FormType<Boolean> {

    @Override
    public Class<Boolean> getFormClass() {
        return Boolean.class;
    }

    @Override
    public Boolean parse(String val, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(val);
        if (trimmed == null)
            return null;

        String tval = trimmed.toLowerCase();
        
        if (tval.equals("on") || tval.equals("yes") || tval.equals("true") || tval.equals("y")) {
            return Boolean.TRUE;
        } else if (tval.equals("off") || tval.equals("no") || tval.equals("false") || tval.equals("n")) {
            return Boolean.FALSE;
        } else {
            throw new ParseException(trimmed);
        }
    }

    @Override
    public Boolean[] getTypeArray(int size) {
        return new Boolean[size];
    }
}
