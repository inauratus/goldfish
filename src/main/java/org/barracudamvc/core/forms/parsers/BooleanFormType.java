package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.plankton.StringUtil;

public class BooleanFormType extends FormType<Boolean> {

    @Override
    public Class<Boolean> getFormClass() {
        return Boolean.class;
    }

    @Override
    public Boolean parse(String val, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(val);
        if (trimmed == null)
            return null;

        String tval = trimmed.toLowerCase();
        
        if (tval.equals("on") || tval.equals("yes") || tval.equals("true") || tval.equals("y")) {
            return Boolean.TRUE;
        } else if (tval.equals("off") || tval.equals("no") || tval.equals("false") || tval.equals("n")) {
            return Boolean.FALSE;
        } else {
            throw new ParseException(trimmed);
        }
    }

    @Override
    public Boolean[] getTypeArray(int size) {
        return new Boolean[size];
    }
}
