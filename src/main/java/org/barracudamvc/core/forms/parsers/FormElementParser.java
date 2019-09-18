/*
 * Copyright (C) 2014 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.io.Serializable;

public interface FormElementParser<T> extends Serializable {

    /** 
     * Create an array of the FormType's type - if heterogenous types
     * are returned, an array of Object will be returned.
     */
    public T[] getTypeArray(int size);

    public abstract Class<T> getFormClass();
}
