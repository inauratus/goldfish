package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

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
