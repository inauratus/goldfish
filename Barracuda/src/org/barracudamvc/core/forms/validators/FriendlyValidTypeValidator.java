/*
 * Copyright (C) 2004 ATM Express, Inc [christianc@atmreports.com]
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
 * Christian Cryder, Diez B. Roggisch
 *
 * $Id: FriendlyValidTypeValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

// 3rd-party imports:
import org.barracudamvc.core.forms.*;
import org.barracudamvc.core.forms.parsers.FormElementParser;


/**
 * Custom extension of ValidTypeValidator that provides friendlier error messages.
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class FriendlyValidTypeValidator extends ValidTypeValidator {

    public FriendlyValidTypeValidator() {
        super();
    }

    public FriendlyValidTypeValidator(String message) {
        super(message);
    }

    public void validateFormElement(Object val, FormElement element, boolean deferExceptions)
            throws ValidationException {

        try {
            super.validateFormElement(val, element, deferExceptions);
        } catch (ValidationException ve) {
            FormElementParser type = element.getType();
            if (type==FormType.INTEGER || type==FormType.LONG || type==FormType.SHORT) {
                throw this.generateException(element, deferExceptions, "Value is not a valid integer");
            } else if (type==FormType.BIG_DECIMAL || type==FormType.DOUBLE || type==FormType.FLOAT) {
                throw this.generateException(element, deferExceptions, "Value is not a valid decimal");
            } else if (type==FormType.DATE) {
                throw this.generateException(element, deferExceptions, "Value is not a valid date (use MM/DD/YYYY format)");
            } else if (type==FormType.TIME) {
                throw this.generateException(element, deferExceptions, "Value is not a valid time (use HH:MM:SS format)");
            } else if (type==FormType.TIMESTAMP) {
                throw this.generateException(element, deferExceptions, "Value is not a valid timestamp (use MM/DD/YYYY HH:MM:SS format)");
            } else {
                throw ve;
            }
        }
    }
}
