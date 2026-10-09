package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

public class StringFormTypeTest extends AbstractParser {

    @Test
    public void testStringEmptyValue() {
        assertParsed("     ", "     ");
    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return value;
    }

    @Override
    public FormType getParser() throws ParseException {
        return new StringFormType();
    }
}
