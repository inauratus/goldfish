/*
 * Copyright (C) 2003  Iman L Crawford (icrawford@greatnation.com)
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
 * $Id: ListValidator.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import java.util.List;

import org.barracudamvc.core.forms.AbstractFormValidator;
import org.barracudamvc.core.forms.DefaultFormElement;
import org.barracudamvc.core.forms.DeferredValidationException;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.FormMap;
import org.barracudamvc.core.forms.FormValidator;
import org.barracudamvc.core.forms.ParseException;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator validates all items even if OrigVal is an instance of
 * <code>java.util.List</code>
 *
 * @author  Iman L Crawford (icrawford@greatnation.com)
 */
public class ListValidator extends AbstractFormValidator {

    protected FormValidator fv = null;

    /**
     * Public constructor.
     *
     * @param ifv the validator we wish to make sure is not valid
     */
    public ListValidator(FormValidator ifv) {
        this(ifv, null);
    }

    /**
     * Public constructor.
     *
     * @param ifv the validator we wish to make sure is not valid
     * @param ierrmsg the message associated with this error
     */
    public ListValidator(FormValidator ifv, String ierrmsg) {
        setErrorMessage(ierrmsg);
        fv = ifv;
    }

    /**
     * Return the value that is being not'ed
     *
     * @return The form element to negate
     */
    public FormValidator getSubValidator() {
        return fv;
    }

    /**
     * Will check origVal's type and loop through all the values calling the
     * subValidator.
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
    @Override
    public void validate(FormElement element, FormMap formMap, boolean deferExceptions) throws ValidationException, DeferredValidationException {
        if (localLogger.isDebugEnabled()) localLogger.debug("Validating one or more items");
//csc_112103_1        if (element.getOrigVal() instanceof ArrayList) {
//csc_112103_1            ArrayList origList = (ArrayList)element.getOrigVal();
        if (element.getOrigVal() instanceof List) {          //csc_112103_1
            List origList = (List) element.getOrigVal();     //csc_112103_1
            for (int i=0; i<origList.size(); i++) {
                FormElement newElement = this.getNewElement(i, element);
                fv.validate((FormElement) newElement, formMap, deferExceptions);
            }
        } else if (fv!=null) {
            fv.validate(element, formMap, deferExceptions);
        }
    }


    /**
     * Will return a duplicate of the element passed to it.
     *
     * @param index index of value to map from the original element.
     * @param element the original form element to be validated
     */
    private FormElement getNewElement(int index, FormElement element) {
        DefaultFormElement newElement = new DefaultFormElement();
//csc_112203_1        Object origVal = ((ArrayList)element.getOrigVal()).get(index);
//csc_112203_1        Object val     = ((ArrayList)element.getVal()).get(index);
        Object origVal = ((List) element.getOrigVal()).get(index);   //csc_112203_1
        Object val     = ((List) element.getVal()).get(index);       //csc_112203_1

        newElement.setOrigVal(origVal);
        newElement.setVal(val);
        newElement.setKey(element.getKey());
        newElement.setName(element.getName());
        newElement.setType(element.getType());
        newElement.setDefaultVal(element.getDefaultVal());
        newElement.setAllowMultiples(false);
        newElement.setValidator(fv);

//        try {
//            // don't need to parse if origVal is the expected type
//            Class typeclass = newElement.getType().getFormClass();
////            if (!typeclass.isInstance(origVal))
////              element.getType().parse(origVal.toString());
//        } catch (ParseException e) {
//            if (localLogger.isDebugEnabled()) localLogger.debug("ParseException:", e);
//            newElement.setParseException(e);
//        }


        return (FormElement)newElement;
    }

}
