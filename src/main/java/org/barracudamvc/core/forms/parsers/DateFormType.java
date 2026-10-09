package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.parsers.formatProviders.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormType extends AbstractDateTimeFormType<Date> {

    public static DateFormatProvider[] providers = new DateFormatProvider[]{
            new SimpleDateFormatFactory("MM-dd-yyyy"),
            new YearFirstDateParser("yyyy-MM-dd"),
            new SimpleDateFormatFactory("yyyy-MM-dd HH:mm:ss.S"),
            new SimpleDateFormatFactory("MM/dd/yy hh:mm:ss a", true),
            new SimpleDateFormatFactory("MM/dd/yy hh:mm a", true),
            new DateInstance(DateFormat.SHORT),
            new DateInstance(DateFormat.MEDIUM),
            new DateInstance(DateFormat.LONG),
            new DateInstance(DateFormat.FULL),
    };

    @Override
    public Class<Date> getFormClass() {
        return Date.class;
    }

    @Override
    public Date[] getTypeArray(int size) {
        return new Date[size];
    }

    @Override
    protected DateFormatProvider[] getProvider() {
        return providers;
    }

    @Override
    protected DateTimeParser<Date> getParser() {
        return new SimpleDateTimeParser();
    }

    protected static class SimpleDateTimeParser implements DateTimeParser<Date> {

        @Override
        public Date parse(DateFormatProvider provider, Locale locale, String data) throws java.text.ParseException {
            return provider.getDateFormat(locale).parse(data);
        }
    }
}
