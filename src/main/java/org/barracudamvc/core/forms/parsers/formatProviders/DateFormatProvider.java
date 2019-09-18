package org.barracudamvc.core.forms.parsers.formatProviders;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public interface DateFormatProvider {

    public DateFormat getDateFormat(Locale local);
}
