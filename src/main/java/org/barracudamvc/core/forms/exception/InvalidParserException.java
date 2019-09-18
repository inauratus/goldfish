/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.exception;

import org.barracudamvc.core.forms.ParseException;

public class InvalidParserException extends ParseException{

    public InvalidParserException() {
        super(null, "No form parser provided for type provided.", null);
    }
}
