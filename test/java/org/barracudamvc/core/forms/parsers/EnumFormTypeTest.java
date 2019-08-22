package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.junit.Test;

public class EnumFormTypeTest extends AbstractParser {

    @Test
    public void canParseEnumValues() {
        assertParsed("EXAMPLE_A", Example.EXAMPLE_A);
        assertParsed("EXAMPLE_B", Example.EXAMPLE_B);
        assertParsed(null, null);
        assertParsed("", null);
    }

    @Test
    public void cannotParseNonEnumValues() {
        assertParseFail("not in the enum");
    }

    public Object convertType(Object value) {
        return value;
    }

    @SuppressWarnings("unchecked")
    public FormType getParser() {
        return new EnumFormType(Example.class);
    }

    enum Example{
        EXAMPLE_A,
        EXAMPLE_B
    }
}