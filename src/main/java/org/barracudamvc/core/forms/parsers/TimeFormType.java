/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: TimeFormType.java 
 * Created: Aug 16, 2013 4:55:50 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import org.barracudamvc.core.forms.parsers.formatProviders.DateFormatProvider;
import org.barracudamvc.core.forms.parsers.formatProviders.DateTimeParser;
import org.barracudamvc.core.forms.parsers.formatProviders.TimeInstanceProvider;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class TimeFormType extends AbstractDateTimeFormType<Time> {

    @Override
    protected DateFormatProvider[] getProvider() {
        return new DateFormatProvider[]{
            new TimeInstanceProvider(DateFormat.FULL),
            new TimeInstanceProvider(DateFormat.LONG),
            new TimeInstanceProvider(DateFormat.MEDIUM),
            new TimeInstanceProvider(DateFormat.SHORT) 
        };
    }

    @Override
    protected DateTimeParser<Time> getParser() {
        return new DateTimeParser<Time>() {
            @Override
            public Time parse(DateFormatProvider provider, Locale locae, String data) throws ParseException {
                return new Time(provider.getDateFormat(locae).parse(data).getTime());
            }
        };
    }

    @Override
    public Class<Time> getFormClass() {
        return Time.class;
    }

    @Override
    public Time[] getTypeArray(int size) {
        return new Time[size];
    }
}
