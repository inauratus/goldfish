/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;

public interface FileElementParser<T> extends FormElementParser<T> {

    public T parse(FileField field, Locale locale) throws ParseException;
}
