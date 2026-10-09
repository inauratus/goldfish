package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;

public interface FileElementParser<T> extends FormElementParser<T> {

    T parse(FileField field, Locale locale) throws ParseException;
}
