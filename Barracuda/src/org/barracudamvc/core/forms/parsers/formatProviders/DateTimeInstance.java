/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: DateTimeInstance.java 
 * Created: Aug 16, 2013 11:44:42 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class DateTimeInstance implements DateFormatProvider {

    private final int dateStyle;
    private final int timeStyle;

    public DateTimeInstance(int dateStyle, int timeStyle) {
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
        return DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
    }
}
