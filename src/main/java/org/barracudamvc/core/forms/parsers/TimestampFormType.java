/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: TimestampFormType.java 
 * Created: Aug 15, 2013 5:48:44 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import org.barracudamvc.core.forms.parsers.formatProviders.DateFormatProvider;
import org.barracudamvc.core.forms.parsers.formatProviders.DateTimeInstance;
import org.barracudamvc.core.forms.parsers.formatProviders.DateTimeParser;
import org.barracudamvc.core.forms.parsers.formatProviders.SimpleDateFormatFactory;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class TimestampFormType extends AbstractDateTimeFormType<Timestamp> {

    @Override
    protected DateFormatProvider[] getProvider() {
        return new DateFormatProvider[]{
            new SimpleDateFormatFactory("yyyy-MM-dd HH:mm:ss.S"),
            new DateTimeInstance(DateFormat.FULL, DateFormat.FULL),
            new DateTimeInstance(DateFormat.LONG, DateFormat.LONG),
            new DateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM),
            new DateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),};
    }

    @Override
    protected DateTimeParser<Timestamp> getParser() {
        return new DateTimeParser<Timestamp>() {
            @Override
            public Timestamp parse(DateFormatProvider provider, Locale locale, String data) throws ParseException {
                return new Timestamp(provider.getDateFormat(locale).parse(data).getTime());
            }
        };
    }

    @Override
    public Class<Timestamp> getFormClass() {
        return Timestamp.class;
    }

    @Override
    public Timestamp[] getTypeArray(int size) {
        return new Timestamp[size];
    }
}
