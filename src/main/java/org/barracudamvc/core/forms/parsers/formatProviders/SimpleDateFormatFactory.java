/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: SimpleFormat.java 
 * Created: Aug 16, 2013 9:13:28 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDateFormatFactory implements DateFormatProvider {

    String format;

    public SimpleDateFormatFactory(String format) {
        this.format = format;
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        df.setLenient(false);
        return df;
    }
}
