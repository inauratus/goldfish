/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: DateInstance.java 
 * Created: Aug 16, 2013 9:12:01 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class DateInstance implements DateFormatProvider {

    final int format;

    public DateInstance(int format ) {
        this.format = format;
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
        DateFormat df = DateFormat.getDateInstance(format, locale);
        df.setLenient(false);
        return df;
    }
}
