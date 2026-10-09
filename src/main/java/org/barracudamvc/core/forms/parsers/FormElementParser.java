package org.barracudamvc.core.forms.parsers;

import java.io.Serializable;

public interface FormElementParser<T> extends Serializable {

    /** 
     * Create an array of the FormType's type - if heterogeneous types
     * are returned, an array of Object will be returned.
     */
    T[] getTypeArray(int size);

    Class<T> getFormClass();
}
