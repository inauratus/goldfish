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
 * $Id: MinLengthValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that the length of a value is >= a minimum length
 */
public class MinLengthValidator extends DefaultFormValidator {

    protected int min = 0;

    /**
     * Public no-args constructor.
     */
    public MinLengthValidator() {
        this(0, null);
    }

    /**
     * Public constructor.
     *
     * @param imin the min length
     */
    public MinLengthValidator(int imin) {
        this(imin, null);
    }

    /**
     * Public constructor.
     *
     * @param imin the min length
     * @param ierrmsg the message associated with this error
     */
    public MinLengthValidator(int imin, String ierrmsg) {
        super(ierrmsg);
        min = imin;
    }

    //bw_102501.1 - added
    /**
     * Get the minimum allowable length
     *
     * @return The minimum allowable length of this field.
     */
    public int getMinLength() {
        return min;
    }

    /**
     * Validate a FormElement to make sure the length of element is not
     * less than a Min length. Validation is not supported for FormType.BOOLEAN
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

        String s = (null != val) ? val.toString() : "";
        if (s.length() < min) {
            throw this.generateException(element, deferExceptions, "Length of val {" + val + "} fails to meet minimum length of " + min);
        }
    }

}
