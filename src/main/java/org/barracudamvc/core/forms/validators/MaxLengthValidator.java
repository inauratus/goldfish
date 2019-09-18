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
 * $Id: MaxLengthValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that the length of a value is <= a maximum length
 */
public class MaxLengthValidator extends DefaultFormValidator {

    protected int max = 0;

    /**
     * Public no-args constructor.
     */
    public MaxLengthValidator() {
        this(0, null);
    }

    /**
     * Public constructor.
     *
     * @param imax the max length
     */
    public MaxLengthValidator(int imax) {
        this(imax, null);
    }

    /**
     * Public constructor.
     *
     * @param imax the max length
     * @param ierrmsg the message associated with this error
     */
    public MaxLengthValidator(int imax, String ierrmsg) {
        super(ierrmsg);
        max = imax;
    }

    /**
     * Get the maximum allowable length
     *
     * @return The maximum allowable length of this field.
     */
    public int getMaxLength() {
        return max;
    }

    /**
     * Validate a FormElement to make sure the length of element does not
     * exceed a Max length. Validation is not supported for FormType.BOOLEAN
     * and FormType.DATE
     *
     * @param val the value to compare the element value to
     * @param element the form element that contains the val
     *        to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (this.isNull(val, element))
            return;

        if (element == null)
            throw new ValidationException(val, "Object val:" + val + " is associated with a null FormElement");
        if (element.getType().equals(FormType.BOOLEAN))
            throw new ValidationException(val, "Unsupported validation: " + val + " is of FormType.BOOLEAN and cannot be validated by this validator");
        if (element.getType().equals(FormType.DATE))
            throw new ValidationException(val, "Unsupported validation: " + val + " is of FormType.DATE and cannot be validated by this validator");

        String s = val.toString();
        if (s.length() > max) {
            throw this.generateException(element, deferExceptions, "Length of val {" + val + "} exceeds maximum length of " + max);
        }
    }

}
