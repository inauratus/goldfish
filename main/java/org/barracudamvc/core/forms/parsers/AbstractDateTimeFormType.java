/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: AbstractDateTimeFormType.java 
 * Created: Aug 15, 2013 5:29:02 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.util.Date;
import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.core.forms.parsers.formatProviders.DateFormatProvider;
import org.barracudamvc.core.forms.parsers.formatProviders.DateTimeParser;
import org.barracudamvc.plankton.StringUtil;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public abstract class AbstractDateTimeFormType<DateType extends Date> extends FormType<DateType>  {

    @Override
    public DateType parse(String origVal, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(origVal);
        if (trimmed == null)
            return null;

        if (locale == null)
            locale = Locale.getDefault();

        for (DateFormatProvider provider : getProvider()) {
            try {
                return getParser().parse(provider, locale, trimmed);
            } catch (java.text.ParseException e1) {
            }
        }
        throw new ParseException(origVal, "Could not parse the date [" + trimmed + "]   Locale is " + locale.getCountry());
    }

    protected abstract DateFormatProvider[] getProvider();

    protected abstract DateTimeParser<DateType> getParser();
}
