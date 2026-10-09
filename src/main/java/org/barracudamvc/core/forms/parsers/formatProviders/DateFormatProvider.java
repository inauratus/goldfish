package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

public interface DateFormatProvider {

    DateFormat getDateFormat(Locale local);
}
