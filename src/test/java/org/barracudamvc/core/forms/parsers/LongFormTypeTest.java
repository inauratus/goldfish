package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;

public class LongFormTypeTest extends AbstractWholeNumber {

    @Override
    public FormType getParser() {
        return new LongFormType();
    }

    @Override
    public Object convertType(Object value) {
        return Long.parseLong(value.toString());
    }
}
