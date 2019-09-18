/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: CommonElementValueParser.java 
 * Created: Jul 2, 2014 11:12:19 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;

public interface StringElementParser<T> extends FormElementParser<T> {

    /**
     * Parses an object based on the specific form type.
     * BEWARE, if your FormType expects an Object, or a String, you must also override 
     * parse(Object val, Locale locale) if you want this method to ever be called
     * @see StringFormType for an example
     */
    public T parse(String origVal, Locale loc) throws ParseException;
}
