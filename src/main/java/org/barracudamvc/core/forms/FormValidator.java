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
 * $Id: FormValidator.java 251 2012-11-09 18:49:25Z charleslowery $
 */
package org.barracudamvc.core.forms;


import java.io.Serializable;

/**
 * <p>A FormValidator is designed to validate one or more FormElements
 * within a FormMap, OR a FormMap itself. The real work occurs in the
 * validate() method.
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
 */
public interface FormValidator extends Serializable {

    /**
     * Set the error message to be used in the event of an error.
     *
     * @param <DeclaredType> The type that should be returned on call
     * @param ierrorMessage the error message to be used in the event of an error
     * @return {@see FormValidator} of {@see DeclaredType} 
     */
    public <DeclaredType extends FormValidator> //
            DeclaredType setErrorMessage(String ierrorMessage);

    /**
     * Get the error message to be used in the event of an error.
     *
     * @return the error message to be used in the event
     *        of an error
     */
    public String getErrorMessage();

    /**
     * Set our defer policy. True indicates we whould throw
     * DeferredValidationExceptions in the event of an error.
     * By default, we throw regular ValidationExceptions.
     *
     * @param val true if we want to throw DeferredValidationExceptions
     *        as opposed to regular ValidationExceptions
     */
//    public void setDeferExceptions(boolean val);

    /**
     * Get our defer policy. True indicates we whould throw
     * DeferredValidationExceptions in the event of an error.
     *
     * @return true if we want to throw DeferredValidationExceptions
     *        as opposed to regular ValidationExceptions
     */
//    public boolean deferExceptions();

    /**
     * Add a child validator
     *
     * @param validator the child validator to be added
     */
//    public void addValidator(FormValidator validator);

    /**
     * Remove a child validator
     *
     * @param validator the child validator to be removed
     */
//    public void removeValidator(FormValidator validator);

    /**
     * Get a list of all child validators
     *
     * @return a list of all child validators
     */
//    public List getValidators();

    /**
     * Validate a FormElement locally and allow any child
     * validators a chance to validate as well.
     *
     * @param element the form element to be validated (null indicates
     *        we want to perform form level validation)
     * @param formMap the map to which the element belongs (sometimes necessary
     *        to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    public void validate(FormElement element, FormMap formMap, boolean deferExceptions) throws ValidationException, DeferredValidationException;

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
//    public void validateForm(FormMap map, boolean deferExceptions) throws ValidationException;

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
//    public void validateFormElement(Object val, FormElement element, boolean deferExceptions) throws ValidationException;

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
//    public void validateFormElement(Object val, FormElement element, FormMap map, boolean deferExceptions) throws ValidationException;

    /**
     * @supplierCardinality 0..*
     */
    /*#FormValidator lnkFormValidator;*/

    /**
     * @supplierCardinality 0..*
     */
    /*#FormMap lnkFormMap;*/
    
    /**
     * Check if val passed is null in a consistant manner.
     * Most validators should call isNull(val) first thing
     * and return if that is the case.  Leave null validation
     * up to NotNullValidator.
     *
     * @param val the value to test for nullness
     */
    public boolean isNull(Object val, FormElement element);

    /**
     * Add {@see FormElements} that should be associated with any other fields
     * are being tracked by this {@see FormValidator}. These fields will 
     * receive errors if this validator is tripped. 
     * 
     * @param <DeclaredType> The type that should be returned on call
     * @param elements {@see FormElement}s that should be added as associated
     * with this validator.
     * 
     * 
     * @return {@see FormValidator} of {@see DeclaredType} 
     */
    public <DeclaredType extends FormValidator> //
            DeclaredType addAssociatedFields(FormElement... elements);
    
}
