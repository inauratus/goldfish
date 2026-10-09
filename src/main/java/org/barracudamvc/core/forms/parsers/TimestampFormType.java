package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.parsers.formatProviders.DateFormatProvider;
import org.barracudamvc.core.forms.parsers.formatProviders.DateTimeInstance;
import org.barracudamvc.core.forms.parsers.formatProviders.DateTimeParser;
import org.barracudamvc.core.forms.parsers.formatProviders.SimpleDateFormatFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

public class TimestampFormType extends AbstractDateTimeFormType<Timestamp> {

    @Override
    protected DateFormatProvider[] getProvider() {
        return new DateFormatProvider[]{
            new SimpleDateFormatFactory("yyyy-MM-dd HH:mm:ss.S"),
            new DateTimeInstance(DateFormat.FULL, DateFormat.FULL),
            new DateTimeInstance(DateFormat.LONG, DateFormat.LONG),
            new DateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM),
            new DateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
            new SimpleDateFormatFactory("E, MMM dd, yyyy hh:mm:ss a zz", true),
            new SimpleDateFormatFactory("MMM dd, yyyy hh:mm:ss a zz", true),
            new SimpleDateFormatFactory("MMM dd, yyyy hh:mm:ss a", true),
            new SimpleDateFormatFactory("M/d/yy h:mm a", true),
        };
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
