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
 * $Id: CardinalityValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.FormValidator;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that a value is a valid date
 *
 * @author  Diez B. Roggisch <diez.roggisch@artnology.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class CardinalityValidator extends DefaultFormValidator {

    int _min, _max;

    public static final FormValidator ONE = new CardinalityValidator(1, 1);
    public static final FormValidator ONE_TO_MANY = new CardinalityValidator(1, Integer.MAX_VALUE);
    public static final FormValidator ZERO_TO_MANY = new CardinalityValidator(0, Integer.MAX_VALUE);
    public static final FormValidator ZERO_TO_ONE = new CardinalityValidator(0, 1);

    /**
     * Public constructor.
     *
     */
    public CardinalityValidator(int min, int max) {
        super();
        _min = min;
        _max = max;
    }

    /**
     * Public constructor.
     *
     */
    public CardinalityValidator(String ierrmsg, int min, int max) {
        super(ierrmsg);
        _min = min;
        _max = max;
    }

    /**
     * Validate a FormElement to make sure the number of values is in the specified range
     */

    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
//csc_112202.2        Object [] values = element.getMultipleValues();
        Object [] values = element.getVals();   //csc_112202.2

        if (values.length < _min || values.length > _max) {
            if (super.getErrorMessage()!=null) {
                throw this.generateException(element, deferExceptions, super.getErrorMessage());
            } else {
                throw this.generateException(element, deferExceptions, values.toString()
                                             + " with length " + values.length
                                             +  " is not in the valid range [" + _min + ":" + _max + "]");
            }

        }
    }

}
