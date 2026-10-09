package org.barracudamvc.core.forms.exception;

import org.barracudamvc.core.forms.ParseException;

public class FileNotParsableException extends ParseException {

    public FileNotParsableException(String description, Exception ex) {
        super(null, description, ex);
    }

}
