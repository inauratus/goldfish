/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: TimeInstanceProvider.java 
 * Created: Aug 16, 2013 12:06:08 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class TimeInstanceProvider implements DateFormatProvider {

    private final int style;

    public TimeInstanceProvider(int style) {
        this.style = style;
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
        return DateFormat.getTimeInstance(style, locale);
    }
}
