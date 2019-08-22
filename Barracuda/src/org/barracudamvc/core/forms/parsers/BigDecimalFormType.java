/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: BigDecimalFormType.java 
 * Created: Aug 15, 2013 3:03:29 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.math.BigDecimal;
import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.plankton.StringUtil;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class BigDecimalFormType extends FormType<BigDecimal> implements NumberComparator {

    @Override
    public Class<BigDecimal> getFormClass() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal parse(String val, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(val);
        if (trimmed == null)
            return null;

        String s = sanitize(trimmed);
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public BigDecimal[] getTypeArray(int size) {
        return new BigDecimal[size];
    }

    protected String sanitize(String val) {
        /*
         * TODO why are these only expressed for BigDecimal? Should these be moved into a base type
         * for all floating point types (Double and Float) as well. 
         * Shouldn't these be user defined as well not just our arbitrary choices?
         * BaseFloatingPoint.addPreParser( new PreParser()  public String parse (String source) {  return source.replace(X)  }  );
         */

        val = StringUtil.replace(val, "$", "");  //strip off $ sign
        val = StringUtil.replace(val, "�", "");  //strip off � sign
        val = StringUtil.replace(val, ",", "");  //strip off commas
        if (val.startsWith("(") && val.endsWith(")")) { //if its a debit (ie. in parenthesis), strip off parenthesis and add a - sign
            val = "-" + val.substring(1, val.length() - 1);
        }
        return val;
    }

    @Override
    public int compare(Number o1, Number o2) {
        if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
            return ((BigDecimal) o1).compareTo((BigDecimal) o2);
        } else {
            throw new IllegalArgumentException("Numbers can't be converted to BigDecimal");
        }
    }
}
