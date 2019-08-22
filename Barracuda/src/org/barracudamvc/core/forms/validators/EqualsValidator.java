/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: EqualsValidator.java 262 2013-10-24 21:33:44Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that the original value constitues a
 * valid type
 */
public class EqualsValidator extends DefaultFormValidator {

    protected Object obj = null;

    /**
     * Public no-args constructor.
     */
    public EqualsValidator() {
        this(null, null);
    }

    /**
     * Public constructor.
     *
     * @param iobj the object with which we wish to compare equality
     */
    public EqualsValidator(Object iobj) {
        this(iobj, null);
    }

    /**
     * Public constructor.
     *
     * @param iobj the object with which we wish to compare equality
     * @param ierrmsg the message associated with this error
     */
    public EqualsValidator(Object iobj, String ierrmsg) {
        super(ierrmsg);
        obj = iobj;
    }

    //bw_102501.1 - added
    /**
     * Return the value that this object must equal.
     */
    public Object getObject() {
       return obj;
    }

    /**
     * Validate a FormElement to make see if the element equals() a
     * given object
     *
     * @param val the value to compare the element value to
     * @param element the form element that contains the val
     *        to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    @Override
    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (isNull(val, element))
            return;

        if (val == obj)
            return;

        if (element.getParseException() == null) {
            if (element.getVal().equals(obj))
                return;
            else
                throw this.generateException(element, deferExceptions, "Value {" + val + "} is not equal to {" + obj + "}");
        } else
            throw this.generateException(element, deferExceptions, "Unable to parse: " + element.getParseException().getMessage());
    }

}
