package org.barracudamvc.core.forms;

import org.barracudamvc.core.forms.parsers.FormElementParser;

public class FileFormElement extends DefaultFormElement {

    public FileFormElement(String name, FormElementParser parser, FormValidator validator) {
        super(name, parser, null, validator);
    }

    public Object getVal() {
        return val;
    }
}
