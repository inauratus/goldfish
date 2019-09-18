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
 * $Id: NotNullValidator.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that a form element is not null or blanks.
 */
public class NotNullValidator extends DefaultFormValidator {

    /**
     * Public constructor.
     */
    public NotNullValidator() {
        super();
    }

    /**
     * Public constructor.
     *
     * @param ierrorMessage the message associated with this error
     */
    public NotNullValidator(String ierrorMessage) {
        super(ierrorMessage);
    }

    /**
     * Validate a FormElement to make sure that it is not Null or blanks.
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
        if (localLogger.isInfoEnabled()) {
            localLogger.info("val=" + val + " " + (val == null ? "true" : "false"));
        }
        if (this.isNull(val, element)) {
            throw this.generateException(element, deferExceptions, "Value must be non-null or non-blanks");
        }
    }
}
