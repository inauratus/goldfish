package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.plankton.StringUtil;

public abstract class WholeNumberFormType<Type extends Number> extends FormType<Type> {

    @Override
    public Type parse(String baseValue, Locale locale) throws ParseException {
        String trimmed = StringUtil.trim(baseValue);
        if (trimmed == null)
            return null;

        validate(trimmed);
        return narrow(stringToType(trimmed));
    }

    abstract Type narrow(Long value) throws ParseException;

    public static void validate(String val) throws ParseException {
        if (!val.matches("[-]?[0-9]+([.][0]*)?"))
            throw new ParseException("The value provided [" + val + "] does not match the criteria of '[0-9]+([.][0]+)*'");
    }

    protected Long stringToType(String string) {
        if (string.contains("."))
            return new Double(string).longValue();
        else
            return new Long(string);
    }

    protected void validateBounds(Long value, Long upperBound, Long lowerBound) throws ParseException {
        if (value < lowerBound)
            throw new ParseException("The value provided was smaller than the supported size. Found ["
                    + value + "] required value greater than [" + lowerBound + "]");

        if (value > upperBound)
            throw new ParseException("The value provided was larger than the supported size. Found ["
                    + value + "] required value less than [" + lowerBound + "]");
    }

    public static void main(String[] args) throws ParseException {
        validate("a");
    }
}
