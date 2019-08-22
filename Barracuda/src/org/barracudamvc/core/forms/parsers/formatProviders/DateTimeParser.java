package org.barracudamvc.core.forms.parsers.formatProviders;

import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public interface DateTimeParser<Type extends Date> {

    public Type parse(DateFormatProvider provider, Locale locae, String data) throws java.text.ParseException;
}
