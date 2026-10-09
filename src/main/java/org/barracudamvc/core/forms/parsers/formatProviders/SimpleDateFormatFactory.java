package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDateFormatFactory implements DateFormatProvider {

    String format;
    boolean lenient;

    public SimpleDateFormatFactory(String format) {
        this.format = format;
        this.lenient = false;
    }

    public SimpleDateFormatFactory(String format, boolean lenient) {
        this.format = format;
        this.lenient = lenient;
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        df.setLenient(lenient);
        return df;
    }
}
