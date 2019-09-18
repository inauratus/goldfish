/*
 * Copyright (C) 2007  Franck Routier
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
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormElement;
import org.barracudamvc.core.forms.ValidationException;

/**
 * This validator ensures that the original value (String)
 * belong to a given enum.
 */
public class EnumValidator<T extends Enum<T>> extends DefaultFormValidator {

    protected Class<T> enumCl = null;

    /**
     * Public no-args constructor.
     */
    public EnumValidator() {
        this(null, null);
    }

    /**
     * Public constructor.
     *
     * @param ienumCl the enum type we want to check against
     */
    public EnumValidator(Class<T> ienumCl) {
        this(ienumCl, null);
    }

    /**
     * Public constructor.
     *
     * @param ienumCl the enum type we want to check against
     * @param ierrmsg the message associated with this error
     */
    public EnumValidator(Class<T> ienumCl, String ierrmsg) {
        super(ierrmsg);
        enumCl = ienumCl;
    }

    /**
     * Return the value that this object must equal.
     */
    public Class<T> getEnum() {
       return enumCl;
    }

    /**
     * Validate a FormElement to see if the element belongs to a given enum
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
        if (localLogger.isInfoEnabled()) localLogger.info("validating to see if val {"+val+"} belongs to {"+enumCl+"}");
        // cannot validate a null value, use NotNullvalidator if you want to disallow nulls
        if (isNull(val, element))
          return;
        // cannot validate if validator has not been correctly initialized
        if (enumCl == null)
        	return;
        //if element is null, or not form type=STRING or DATE, err because this should
        //not be considered a valid option (note that these exceptions are always
        //immediate since they typically represent a programming error)
        if (element==null) throw new ValidationException(val, "Object val:"+val+" is associated with a null FormElement");


        boolean valid = false;		
		
		T[] consts = enumCl.getEnumConstants();
		for (T con : consts) {
			if (con.name().equals(val.toString())) {
				valid = true;
			}
		}
		if ( ! valid) throw this.generateException(element, deferExceptions, "Value {"+val+"} is not in enum {"+enumCl+"}");

//        if (element.getParseException()==null) {
//          if (element.getVal().equals(obj))
//            return;
//          else
//            throw this.generateException(element, deferExceptions, "Value {"+val+"} is not equal to {"+obj+"}");
//        }
//        else
//          throw this.generateException(element, deferExceptions, "Unable to parse: " + element.getParseException().getMessage());
    }

}
