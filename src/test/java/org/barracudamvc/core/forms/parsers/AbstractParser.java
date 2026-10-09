package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class AbstractParser {

    public void testNull_toNull() throws ParseException {
        assertParsed(null, null);
    }

    public void testEmptyString_toNulll() throws ParseException {
        assertParsed("", null);
    }

    public void testStringEmptyValue() {
        assertParsed("     ", null);
    }

    public void assertParseFail(String input) {
        Object result;
        try {
            result = getParser().parse(input);
        } catch (ParseException ex) {
            return;
        }
        assertTrue("Invalid value parsed successfully. Parsing: [" + input + "] Result: [" + result + "]", false);
    }

    public void assertParsed(String input, Object result) {
        try {
            assertEquals(getParser().parse(input), result == null ? null : convertType(result));
        } catch (ParseException ex) {
            ex.printStackTrace();
            throw new AssertionError("Failed to parse: [" + input + "] expected: " + result + " Exception: " + ex.getMessage());
            
        }
    }

    public abstract Object convertType(Object value) throws ParseException;

    public abstract FormType getParser() throws ParseException;
}
