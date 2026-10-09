package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;

public class StringFormType extends FormType<String> {

    @Override
    public Class<String> getFormClass() {
        return String.class;
    }
    
    @Override
    public String parse(String origVal, Locale locale) throws ParseException {
        if (origVal == null || origVal.isEmpty()) {
            return null;
        }

        return origVal;
    }

    @Override
    public String parse(Object val, Locale locale) throws ParseException {
        return parse(val == null ? null : val.toString(), locale);
    }

    @Override
    public String[] getTypeArray(int size) {
        return new String[size];
    }
}
