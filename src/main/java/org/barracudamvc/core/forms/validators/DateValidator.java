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
 * $Id: DateValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import java.util.Date;
import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.core.forms.ValidationException;
import org.barracudamvc.core.forms.parsers.FormElementParser;

/**
 * This validator ensures that a value is a valid date
 *
 * @author  Robert Leftwich <digital@ix.net.au>
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @author  Jacob Kjome <hoju@visi.com>
 * @author  Diez B. Roggisch <diez.roggisch@artnology.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class DateValidator extends DefaultFormValidator {

    /**
     * Public constructor.
     *
     */
    public DateValidator() {
        super();
    }

    /**
     * Public constructor.
     *
     * @param ierrmsg the message associated with this error
     */
    public DateValidator(String ierrmsg) {
        super(ierrmsg);
    }

    /**
     * Validate a FormElement to make sure the value is a valid date
     * Validation is not supported for
     *    FormType.BOOLEAN
     *    FormType,INTEGER
     *    FormType.LONG
     *    FormType.SHORT
     *    FormType.DOUBLE
     *    FormType.FLOAT
     *
     * @param val the value to compare the element value to
     * @param element the form element that contains the val
     *      to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *      and attempt to validate all elements so that we can process
     *      all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (this.isNull(val, element))
            return;

        if (element == null)
            throw new ValidationException(val, "Object val:" + val + " is associated with a null FormElement");

        FormElementParser formType = element.getType();
        if (!(formType.equals(FormType.DATE) || formType.equals(FormType.STRING))) {
            throw new ValidationException(val, "Unsupported validation: "
                    + val + " is of FormType "
                    + formType.toString()
                    + " and cannot be validated by this validator");
        }

        if (val instanceof Date) {
            return;
        } else if (formType.equals(FormType.DATE) && element.getParseException() != null) {
            throw this.generateException(element, deferExceptions, val.toString()
                    + " is not a valid date, because "
                    + element.getParseException());
        } else if (formType.equals(FormType.STRING)) {
            try {
                Date dateVal = (Date) FormType.DATE.parse(val.toString());
            } catch (ParseException ex) {
                throw this.generateException(element, deferExceptions, val.toString()
                        + " is not a valid date, because "
                        + ex);
            }
        }
    }
}
