/*
 * Copyright (C) 2003-2012  Christian Cryder [christianc@granitepeaks.com]
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
 * $Id: AbstractFormValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.Arrays;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.barracudamvc.core.forms.parsers.FormElementParser;

/**
 * The root implementation of the FormValidator interface
 */
public abstract class AbstractFormValidator implements FormValidator {

    protected static final Logger localLogger = Logger.getLogger(AbstractFormValidator.class.getName());
    private String myErrMsg = null;
    private LinkedList<FormElement> assoicatedFormElements = new LinkedList<FormElement>();

    @Override
    @SuppressWarnings("unchecked")
    public <DeclaredType extends FormValidator> //
            DeclaredType setErrorMessage(String errorMessage) {
        this.myErrMsg = errorMessage;
        return (DeclaredType) this;
    }

    @Override
    public String getErrorMessage() {
        return this.myErrMsg;
    }

    /**
     * Will generate an Validation Exception based upon the users defined error
     * message, associated fields, and defer command. 
     * <p>
     * If the user specifies an error message their error message is used and
     * not the message provided to this method.
     * 
     * @param source the source of the exception
     * @param deferExceptions <tt>true</tt> if we want to generate a 
     * {@see DeferredValidationException}
     * @param defaultMsg the error message to use if no error message specified 
     * in the constructor of the validator
     * @return a validation exception 
     */
    protected ValidationException generateException(Object source, boolean deferExceptions, String defaultMsg) {
        if (localLogger.isDebugEnabled()) {
            localLogger.debug("Generating exception... defer=" + deferExceptions);
        }

        String errmsg = this.getErrorMessage() == null ? defaultMsg : this.getErrorMessage();
        
        if (deferExceptions || assoicatedFormElements.size() > 0) {
            DeferredValidationException exceptions = new DeferredValidationException(new ValidationException(source, errmsg));

            for (FormElement formElement : assoicatedFormElements) {
                exceptions.addSubException(new ValidationException(formElement, errmsg));
            }
            return exceptions;
        } else {
            return new ValidationException(source, errmsg);
        }
    }

    @Override
    public boolean isNull(Object val, FormElement element) {
        return (val == null || val.toString().trim().isEmpty());
    }

    /**
     * Returns the value of the element that corresponds to the elementName
     * provided. The value is only returned if the FormElement matches the
     * form type provided and the value is NOT <tt>null</tt> and not empty.
     * 
     * @param <RequestedType> The type the user would like to be returned
     * @param map The container that hold the form element
     * @param type The type the form element must be to get a value
     * @param elementName the element that should be retrieved
     * @return The value of the form element if and only if it matches the 
     * required type and is not empty, else <tt>null</tt>
     */
    @SuppressWarnings("unchecked")
    public <RequestedType> RequestedType getValueIfAppropriate(FormMap map, FormType type, String elementName) {
        FormElement element = map.getElement(elementName);
        if (element == null) {
            return null;
        }
        FormElementParser elementType = element.getType();
        if (elementType.equals(type)) {
            Object value = element.getVal();
            if (isNull(value, element)) {
                return null;
            } else {
                return (RequestedType) value;
            }
        } else {
            return null;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <DeclaredType extends FormValidator> //
            DeclaredType addAssociatedFields(FormElement... elements) {
        assoicatedFormElements.addAll(Arrays.asList(elements));
        return (DeclaredType) this;
    }
}
