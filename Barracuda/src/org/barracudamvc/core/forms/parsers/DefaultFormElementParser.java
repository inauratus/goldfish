/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: DefaultFormElementParser.java 
 * Created: Jul 2, 2014 11:45:07 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */

package org.barracudamvc.core.forms.parsers;

import java.util.Locale;
import org.barracudamvc.core.forms.ParseException;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public abstract class DefaultFormElementParser<T> implements StringElementParser<T> {

    /**
     * Returns the class associated with this particular form type.
     */
    public abstract Class<T> getFormClass();

    /**
     * Parses an object based on the specific form type.
     */
    public T parse(String origVal) throws ParseException {
        return parse(origVal, null);
    }

    /**
     * Pre-parse a value, and just keeps the current value if it matches the FormType
     * expected type.
     * BEWARE, if your FormType expects an Object, or a String, you must override this method
     * if you want the abstract method parse(String val, Locale loc) to ever be called
     * @see StringFormType for an example
     * @param val
     * @param locale
     * @return
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    public T parse(Object val, Locale locale) throws ParseException {
        if (val == null) {
            return parse((String) val, locale);
        } else if (getFormClass().isInstance(val)) {
            return (T) val;
        } else {
            return parse(val.toString(), locale);
        }
    }

    /** 
     * Create an array of the FormType's type - if heterogenous types
     * are returned, an array of Object will be returned.
     */
    public abstract T[] getTypeArray(int size);
    
    @Override
    public String toString() {
        return this.getFormClass().getName();
    }
}
