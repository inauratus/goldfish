
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

public class BooleanFormTypeTest extends AbstractParser {

    @Test
    public void testTrueValues() throws ParseException {
        assertParsed("yes", Boolean.TRUE);
        assertParsed("y", Boolean.TRUE);
        assertParsed("true", Boolean.TRUE);
        assertParsed("on", Boolean.TRUE);

        assertParsed("YES", Boolean.TRUE);
        assertParsed("Y", Boolean.TRUE);
        assertParsed("TRUE", Boolean.TRUE);
        assertParsed("ON", Boolean.TRUE);
    }

    @Test
    public void testFalseValues() throws ParseException {
        assertParsed("no", Boolean.FALSE);
        assertParsed("n", Boolean.FALSE);
        assertParsed("false", Boolean.FALSE);
        assertParsed("off", Boolean.FALSE);

        assertParsed("NO", Boolean.FALSE);
        assertParsed("N", Boolean.FALSE);
        assertParsed("false", Boolean.FALSE);
        assertParsed("off", Boolean.FALSE);
    }

    @Test
    public void testNullValues() throws ParseException {
        assertParseFail("yes1");
        assertParseFail("junk and stuff");
        assertParseFail("1");
        assertParseFail("wyatt");
        assertParseFail("[]");
    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return value;
    }

    @Override
    public FormType getParser() throws ParseException {
        return new BooleanFormType();
    }
}
