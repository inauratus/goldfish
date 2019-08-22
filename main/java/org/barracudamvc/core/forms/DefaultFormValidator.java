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
 * $Id: DefaultFormValidator.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>This class provides the default implementation of FormValidator.
 *
 * <p>A FormValidator is designed to validate one or more FormElements
 * within a FormMap, OR a FormMap itself. The real work occurs in the
 * validate() method. A validator is also capable of containing other
 * validators, which makes it very easy to create a complex set of
 * validation logic by assembling a "master" validator from a set of
 * simpler validators.
 *
 * <p>When the validate method is invoked, we start by performing element
 * level validation by invoking the validateFormElement() methods for each
 * of the elements in the form. If that succeeds, we validate the whole
 * map by invoking validateForm() method to validate the form itself. Finally,
 * we invoke any child validators. In this way, a validator is not considered
 * validated until all it's rules plus those for all of it's children have
 * passed muster.
 *
 * <p>When an invalid condition occurs, typically the developer will throw
 * a ValidationException, which stops the validation process immediately and
 * returns the specified error. There are times, however, where the developer
 * will want to try and validate as many of the rules as possible in order
 * to return all known errors at once. In this case, the developer should throw
 * DeferredValidationExceptions. These are caught internally and added to one
 * master validation exception, which is thrown when the validation process
 * completes.
 *
 * <p>In typical usage the developer would extend this class to
 * override one of the three validateFormElement methods.
 * FormValidators can also act as containers for other FormValidators,
 * thereby allowing the developer to dynamically assemble complex
 * validators from simpler validator objects.
 */
public class DefaultFormValidator extends AbstractFormValidator {

    protected static final Logger localLogger = Logger.getLogger(AbstractFormValidator.class.getName());
    protected List<FormValidator> validators = new ArrayList<FormValidator>();

    /**
     * Public noargs constructor (errorMessage defaults to null)
     */
    public DefaultFormValidator() {
        this(null);
    }

    /**
     * Public constructor with a default errorMessage to be dispatched
     * when an invalid state occurs. The deferExceptions parameter
     * defaults to true.
     *
     * @param ierrorMessage the default error message for this validator
     */
    public DefaultFormValidator(String ierrorMessage) {
        setErrorMessage(ierrorMessage);
    }

    /**
     * Add a child validator
     *
     * @param validator the child validator to be added
     */
    public void addValidator(FormValidator validator) {
        validators.add(validator);
    }

    /**
     * Remove a child validator
     *
     * @param validator the child validator to be removed
     */
    public void removeValidator(FormValidator validator) {
        validators.remove(validator);
    }

    /**
     * Get a list of all child validators
     *
     * @return a list of all child validators
     */
    public List<FormValidator> getValidators() {
        return new ArrayList<FormValidator>(validators);
    }

    /**
     * Validate a FormElement locally and allow any child
     * validators a chance to validate as well.
     *
     * @param element the form element to be validated (null indicates
     *        we want to perform form level validation)
     * @param map the map to which the element belongs (sometimes necessary
     *        to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    @Override
    public void validate(FormElement element, FormMap map, boolean deferExceptions) throws ValidationException, DeferredValidationException {
        DeferredValidationException dfve = new DeferredValidationException();

        try {
            if (element == null) {
                validateForm(map, deferExceptions);
            } else {
                Object val = element.getVal();
                if (val == null)
                    val = element.getOrigVal();
                validateFormElement(val, element, deferExceptions);
                validateFormElement(val, element, map, deferExceptions);
            }
        } catch (DeferredValidationException dve) {
            dfve.addSubException(dve);
        }

        for (FormValidator fv : validators) {
            try {
                fv.validate(element, map, deferExceptions);
            } catch (DeferredValidationException dve) {
                dfve.addSubException(dve);
            }
        }

        if (dfve.hasSubExceptions())
            throw dfve;
    }

    /**
     * Validate an entire FormMap. This is the method developers should
     * override to provide specific validation for the entire form, as
     * opposed to validating a specific element within the form.
     *
     * To indicate a form is invalid, through a ValidationException,
     * which will interrupt the validation process immediately. If you
     * want to indicate an error, but would still like validation to
     * continue (so that you can identify multiple errors in one
     * validation pass) throw a DeferredValidationException instead.
     *
     * @param map the map to be validated
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    public void validateForm(FormMap map, boolean deferExceptions) throws ValidationException {
        //override this if you want to validate an entire form
    }

    /**
     * Validate a FormElement. This is the method developers should
     * override to provide specific validation based on the value
     * and (potentially) information contained in the FormElement.
     *
     * To indicate an element is invalid, through a ValidationException,
     * which will interrupt the validation process immediately. If you
     * want to indicate an error, but would still like validation to
     * continue (so that you can identify multiple errors in one
     * validation pass) throw a DeferredValidationException instead.
     *
     * @param val the actual value to be validated
     * @param element the form element that contains the val
     *        to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException {
        //override one of these two methods    to validate a specific element
    }

    /**
     * Validate a FormElement. This is the method developers should
     * override to provide specific validation based on the value
     * and (potentially) information contained in the FormElement and
     * FormMap structures.
     *
     * To indicate an element is invalid, through a ValidationException,
     * which will interrupt the validation process immediately. If you
     * want to indicate an error, but would still like validation to
     * continue (so that you can identify multiple errors in one
     * validation pass) throw a DeferredValidationException instead.
     *
     * @param val the actual value to be validated
     * @param element the form element that contains the val
     * @param map the map to which the element belongs (sometimes necessary
     *        to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    public void validateFormElement(Object val, FormElement element, FormMap map, boolean deferExceptions) throws ValidationException {
        //override one of these two methods    to validate a specific element
    }
}
