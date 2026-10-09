package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

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
