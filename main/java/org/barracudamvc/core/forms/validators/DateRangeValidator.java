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
 * $Id: DateRangeValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import java.util.Date;
import java.util.Locale;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.core.forms.ValidationException;
import org.barracudamvc.core.forms.parsers.FormElementParser;

/**
 * This validator ensures that a value is within a given date range
 */
public class DateRangeValidator extends DefaultFormValidator {

    protected Date myStartDate = null;
    protected Date myEndDate = null;

    /**
     * Public no-args constructor.
     */
    public DateRangeValidator() {
        this(null, null, null);
    }

    /**
     * Public constructor.
     *
     * @param theStartDate the low end of the range
     * @param theEndDate the high end of the range
     */
    public DateRangeValidator(Date theStartDate, Date theEndDate) {
        this(theStartDate, theEndDate, null);
    }

    /**
     * Public constructor.
     *
     * @param theStartDate the low end of the range
     * @param theEndDate the high end of the range
     * @param theErrMsg the message associated with this error
     */
    public DateRangeValidator(Date theStartDate, Date theEndDate, String theErrMsg) {
        super(theErrMsg);
        myStartDate = theStartDate;
        myEndDate = theEndDate;
    }

    //bw_102501.1 - added
    /**
     * Return the minimum date value
     *
     * @return The date being compared against
     */
    public Date getStartDate() {
        return myStartDate;
    }

    //bw_102501.1 - added
    /**
     * Return the maximum date value
     *
     * @return The date being compared against
     */
    public Date getEndDate() {
        return myEndDate;
    }

    /**
     * Validate a FormElement to make see if the element equals() a
     * given object
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

        if (myStartDate == null && myEndDate == null) {
            throw this.generateException(element,
                    deferExceptions,
                    "Invalid range:" + myStartDate + " to " + myEndDate);
        }

        if (element == null)
            throw new ValidationException(val, "Object val:" + val + " is associated with a null FormElement");

        FormElementParser formType = element.getType();

        if (!(formType.equals(FormType.DATE) || formType.equals(FormType.STRING))) {
            throw new ValidationException(val, "Unsupported validation: "
                    + val + " is of FormType "
                    + formType.toString()
                    + " and cannot be validated by this validator");
        }

        Date dateVal = null;

        // ilc_022202.1_start
        // added the following
        // technically we're suppose to be validating origVal and it
        // is always suppose to be a string or ArrayList.  I check the
        // type of val because the test cases test against a Date type
        if (val instanceof Date) {
            dateVal = (Date) val;
        } else {
            if (formType.equals(FormType.DATE)) {
                if (element.getParseException() == null)
                    dateVal = (Date) element.getVal();
                else
                    throw this.generateException(element, deferExceptions, val.toString() + " is not a valid date string");
            } else {
                try {
                    if (element.getParseException() == null) {
                        dateVal = (Date) (FormType.DATE.parse(val.toString(), Locale.getDefault()));
                    }
                } catch (ParseException ex) {
                    throw this.generateException(element, deferExceptions, val.toString() + " is not a valid date string");
                }
            }
        }

        String defaultErrMsg = null;

        if (myEndDate == null) {
            defaultErrMsg = "Value: " + val + " is not after " + this.myStartDate;
        } else if (myStartDate == null) {
            defaultErrMsg = "Value: " + val + " is not before " + this.myEndDate;
        } else {
            defaultErrMsg = "Value: " + val + " is not in the range between " + this.myStartDate + " and " + this.myEndDate;
        }

        if (null != this.myStartDate && dateVal.before(this.myStartDate)) {
            throw this.generateException(element, deferExceptions, defaultErrMsg);
        }

        if (null != this.myEndDate && dateVal.after(this.myEndDate)) {
            throw this.generateException(element, deferExceptions, defaultErrMsg);
        }

    }

}
