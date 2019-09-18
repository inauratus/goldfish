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
 * $Id: And.java 250 2012-09-19 14:35:19Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * This validator ensures that all of the given validators are valid,
 * (effectively acting as an "AND" or "&&") or a ValidationException will
 * be generated.
 * <p>
 * The sub-validators can be specified as array, list or by using the
 * convenience constructors.
 */
public class And extends AbstractFormValidator {

    protected HashSet<FormValidator> validators = new HashSet<FormValidator>();

    /**
     * Public constructor.
     *
     * @param fv the list of validators
     */
    public And(List<FormValidator> fv) {
        this(fv, null);
    }

    /**
     * Public constructor.
     *
     * @param fv the list of validators
     * @param ierrmsg the message associated with this error
     */
    public And(List<FormValidator> fv, String ierrmsg) {
        this((FormValidator[]) fv.toArray(new FormValidator[1]), ierrmsg);
    }

    /**
     * Public constructor.
     *
     * @param fv the array of validators
     */
    public And(FormValidator... fv) {
        this(fv, null);
    }

    /**
     * Public constructor.
     *
     * @param fv the array of validators
     * @param ierrmsg the message associated with this error
     */
    public And(FormValidator[] fv, String ierrmsg) {
        setErrorMessage(ierrmsg);
        validators.addAll(Arrays.asList(fv));
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     */
    public And(FormValidator fv1, FormValidator fv2) {
        this(new FormValidator[]{fv1, fv2}, null);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param ierrmsg the message associated with this error
     */
    public And(FormValidator fv1, FormValidator fv2, String ierrmsg) {
        this(new FormValidator[]{fv1, fv2}, ierrmsg);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param fv3 the third subvalidator
     */
    public And(FormValidator fv1, FormValidator fv2, FormValidator fv3) {
        this(new FormValidator[]{fv1, fv2, fv3}, null);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param fv3 the third subvalidator
     * @param ierrmsg the message associated with this error
     */
    public And(FormValidator fv1, FormValidator fv2, FormValidator fv3, String ierrmsg) {
        this(new FormValidator[]{fv1, fv2, fv3}, ierrmsg);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param fv3 the third subvalidator
     * @param fv4 the fourth subvalidator
     */
    public And(FormValidator fv1, FormValidator fv2, FormValidator fv3, FormValidator fv4) {
        this(new FormValidator[]{fv1, fv2, fv3, fv4}, null);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param fv3 the third subvalidator
     * @param fv4 the fourth subvalidator
     * @param ierrmsg the message associated with this error
     */
    public And(FormValidator fv1, FormValidator fv2, FormValidator fv3, FormValidator fv4, String ierrmsg) {
        this(new FormValidator[]{fv1, fv2, fv3, fv4}, ierrmsg);
    }

    //bw_102501.1 - added
    /**
     * Return the sub-validators that are or'ed together
     * Do not assume this array is of length 2, in order to allow for future
     * use. Also, do not change the returned array, as it maybe cached
     * in the future.
     *
     * @return Sub-validators as an array
     */
    public FormValidator[] getSubValidators() {
        return validators.toArray(new FormValidator[validators.size()]);
    }
    
    /**
     * Adds a validator to the set of validators that will be fired.
     * @param validator The validator that will be fired.
     */
    public And addValidator(FormValidator validator) {
        validators.add(validator);
        return this;
    }
    

    /**
     * Rather than calling all the sub validate methods, we make sure
     * at least one of the sub-validators is valid
     *
     * @param element the form element to be validated (null indicates
     * we want to perform form level validation)
     * @param map the map to which the element belongs (sometimes necessary
     * to validate elements by comparing them with other elements)
     * @param deferExceptions do we want to deferValidation exceptions
     * and attempt to validate all elements so that we can process
     * all the exceptions at once
     * @throws ValidationException if the element is not valid
     */
    @Override
    public void validate(FormElement element, FormMap map, boolean deferExceptions) throws ValidationException, DeferredValidationException {
        DeferredValidationException validationExceptions = new DeferredValidationException();

        for (FormValidator validator : validators) {
            try {
                if (validator == null) {
                    // Not sure why we are doing this but I will maintain for now
                    throw this.generateException(element, deferExceptions, "A given Validator was null");
                } else {
                    validator.validate(element, map, deferExceptions);
                }

            } catch (ValidationException validationException) {
                validationExceptions.addSubException(validationException);
            }
        }
        if (validationExceptions.hasSubExceptions()) {
            throw validationExceptions;
        }
    }
}
