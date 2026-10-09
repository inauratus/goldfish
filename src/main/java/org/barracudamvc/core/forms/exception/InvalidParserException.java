package org.barracudamvc.core.forms.exception;

import org.barracudamvc.core.forms.ParseException;

public class InvalidParserException extends ParseException{

    public InvalidParserException() {
        super(null, "No form parser provided for type provided.", null);
    }
}
