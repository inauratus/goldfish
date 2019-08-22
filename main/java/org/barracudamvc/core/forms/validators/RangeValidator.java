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
 * $Id: RangeValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.ValidationException;
import org.barracudamvc.core.forms.parsers.FormElementParser;
import org.barracudamvc.core.forms.parsers.NumberComparator;

/**
 * This validator ensures that a value is within a given number range
 */
public class RangeValidator extends DefaultFormValidator {

    protected Number n1 = null;
    protected Number n2 = null;

    /**
     * Public no-args constructor.
     */
    public RangeValidator() {
        this(null, null, null);
    }

    /**
     * Public constructor.
     *
     * @param low the low end of the range
     * @param high the high end of the range
     */
    public RangeValidator(Number low, Number high) {
        this(low, high, null);
    }

    /**
     * Public constructor.
     *
     * @param low the low end of the range
     * @param high the high end of the range
     * @param ierrmsg the message associated with this error
     */
    public RangeValidator(Number low, Number high, String ierrmsg) {
        super(ierrmsg);
        n1 = low;
        n2 = high;
    }

    /**
     * Get the minimum allowed value
     *
     * @return Number containing the minimum value
     */
    public Number getMinimum() {
        return n1;
    }

    /**
     * Get the maximum allowed value
     *
     * @return Number containing the maximum value
     */
    public Number getMaximum() {
        return n2;
    }

    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        if (n1 == null || n2 == null) {
            throw this.generateException(element, deferExceptions,
                    "Invalid range:" + n1 + " to " + n2);
        }

        if (this.isNull(val, element))
            return;

        FormElementParser formType = element.getType();

        if (element.getParseException() != null) {
            throw this.generateException(element, deferExceptions,
                    "Parse error " + element.getParseException());
        }

        if (!(formType instanceof NumberComparator)) {
            throw this.generateException(element, deferExceptions,
                    "The Form type provided must implement NumberComparator");
        }

        NumberComparator conparator = (NumberComparator) formType;
        if (conparator.compare((Number) element.getValue(), n1) < 0
                || conparator.compare((Number) element.getValue(), n2) > 0) {
            throw this.generateException(element, deferExceptions,
                    "Value: " + val + " is not in the range between " + n1 + " and " + n2);
        }
    }
}
