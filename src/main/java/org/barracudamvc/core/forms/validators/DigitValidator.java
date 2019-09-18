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
 * $Id: DigitValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that the value contains only digits tested against
 * the field size length (len). If the value's length is not equal to the length
 * or a character other than the numbers 0-9 is found, an exception will be throw.
 *
 * <p>This class can be used 3 ways
 * <p>1) As a digit validator, confirming that only digits exist for the value. The field value
 * "is not required", and will only be tested for digits when the field value "is not null".
 * Use default constructor() or constructor(errorMessage) for this case.
 * <p>2) As a digit/length validator where phoneNumber will default to false. This can be used
 * to test set lengths of digits (credit cards, quantities, soc sec numbers etc.) The field value "is required".
 * Use constructor(len) for this case.
 *
 * <p>Note: There is no current support for  '.' or '-' in the value for phone numbers.
 *
 * <p>This class works very nicely where the input fields are separated into their respective
 * types (i.e Separating a Domestic Phone Number into 3 'input type=text' boxes -- Area Code len=3,
 * Prefix len = 3, and Number len =4) will permit easier implementation of the rules as more are
 * added.
 *
 * <p><p>Rules:<br>
 * --domesticPhone = true<br>
 * if length = 3, then the value may not start with a zero (0)
 *
 * <br>
 * --domesticPhone = false (international)<br>
 * none
 *
 * @author  Paul G. Markovich <pmarkovich@acpinteractive.com>
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @author  Jacob Kjome <hoju@visi.com>
 * @author  Bill Wallace <Bill_Wallace@elementk.com>
 * @author  Diez B. Roggisch <diez.roggisch@artnology.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class DigitValidator extends DefaultFormValidator {

    protected int len = 0;
    protected boolean requireLength = false;

    /**
     * Public constructor.
     */
    public DigitValidator() {
        this(null);
    }

    /**
     * Public constructor.
     *
     * @param ierrmsg the message associated with this validation error
     */
    public DigitValidator(String ierrmsg) {
        super(ierrmsg);
    }

    /**
     * Public constructor.
     *
     * Utilizing this constructor, the field value will be required
     *
     * @param ilen the length of the field to validate all digits
     */
    public DigitValidator(int ilen) {
        this(ilen, null);
        requireLength = true;
    }

    /**
     * Public constructor.
     *
     * @param ilen the length of the field to validate all digits
     * @param ierrmsg the message associated with this validation error
     */
    public DigitValidator(int ilen, String ierrmsg) {
        super(ierrmsg);
        len = ilen;
        requireLength = true;
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
    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (this.isNull(val, element))
            return;
        String s = val.toString().trim();
        char c[] = s.toCharArray();

        if (element.getParseException() != null) {
            throw this.generateException(element, deferExceptions, s + " contains characters other than digits because " + element.getParseException());
        }

        if (!requireLength) {
            if (containsNonDigit(c)) {
                throw this.generateException(element, deferExceptions, "Val {" + val + "} contains characters other than 0123456789");
            }
        } else {
            if (s.length() < 1) {
                throw this.generateException(element, deferExceptions, "Null value fails to meet minimum length of " + len);
            }

            if (s.length() != len) {
                throw this.generateException(element, deferExceptions, "Length of val {" + val + "} is not equal to the field length of " + len);
            }
            if (containsNonDigit(c)) {
                throw this.generateException(element, deferExceptions, "Val {" + val + "} contains characters other than 0123456789");
            }
        }
    }

    protected boolean containsNonDigit(char[] chars) {
        for (char c : chars) {
            if (c < '0' || c > '9') {
                return true;
            }
        }
        return false;
    }
}
