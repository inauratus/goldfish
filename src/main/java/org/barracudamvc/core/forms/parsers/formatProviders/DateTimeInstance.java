package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

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
