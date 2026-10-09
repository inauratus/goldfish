package org.barracudamvc.core.forms.parsers.formatProviders;

import java.util.Date;
import java.util.Locale;

public interface DateTimeParser<Type extends Date> {

    Type parse(DateFormatProvider provider, Locale locae, String data) throws java.text.ParseException;
}
