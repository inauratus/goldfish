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
 * $Id: ValidTypeValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.ValidationException;
import org.barracudamvc.core.forms.parsers.FormElementParser;

/**
 * This validator ensures that the original value constitues a
 * valid type
 */
public class ValidTypeValidator extends DefaultFormValidator {

    public ValidTypeValidator() {
        super();
    }

    /**
     * @param ierrorMessage the message associated with this error
     */
    public ValidTypeValidator(String ierrorMessage) {
        super(ierrorMessage);
    }

    /**
     * Validate a FormElement to make sure that the non-null orig values are valid
     * for the specified element type
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
        FormElementParser ft = element.getType();

        if (isNull(val, element))
            return;

        if (element.getParseException() != null) {
            throw this.generateException(element, deferExceptions, "Invalid value '" + val + "' for form type " + ft);
        }
    }

}
