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
 * $Id: Or.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.List;

/**
 * This validator ensures that at least one of the given validators is valid,
 * (effectively acting as an "OR" or "||") or a ValidationException will
 * be generated
 * <p>
 * The sub-validators can be specified as array, list or by using the convenience constructors.
 */
public class Or extends AbstractFormValidator {

    protected FormValidator _fv[];

    /**
     * Public constructor.
     *
     * @param fv the list of validators
     */
    public Or(List<FormValidator> fv) {
        this(fv, null);
    }

    /**
     * Public constructor.
     *
     * @param fv the list of validators
     * @param ierrmsg the message associated with this error
     */
    @SuppressWarnings("unchecked")
    public Or(List<FormValidator> fv, String ierrmsg) {
        this((FormValidator[]) fv.toArray(new FormValidator[1]), ierrmsg);
    }

    /**
     * Public constructor.
     *
     * @param fv the array of validators
     */
    public Or(FormValidator[] fv) {
        this(fv, null);
    }

    /**
     * Public constructor.
     *
     * @param fv the array of validators
     * @param ierrmsg the message associated with this error
     */
    public Or(FormValidator[] fv, String ierrmsg) {
        setErrorMessage(ierrmsg);
        _fv = fv;
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     */
    public Or(FormValidator fv1, FormValidator fv2) {
        this(new FormValidator[]{fv1, fv2}, null);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param ierrmsg the message associated with this error
     */
    public Or(FormValidator fv1, FormValidator fv2, String ierrmsg) {
        this(new FormValidator[]{fv1, fv2}, ierrmsg);
    }

    /**
     * Public constructor.
     *
     * @param fv1 the first subvalidator
     * @param fv2 the second subvalidator
     * @param fv3 the third subvalidator
     */
    public Or(FormValidator fv1, FormValidator fv2, FormValidator fv3) {
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
    public Or(FormValidator fv1, FormValidator fv2, FormValidator fv3, String ierrmsg) {
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
    public Or(FormValidator fv1, FormValidator fv2, FormValidator fv3, FormValidator fv4) {
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
    public Or(FormValidator fv1, FormValidator fv2, FormValidator fv3, FormValidator fv4, String ierrmsg) {
        this(new FormValidator[]{fv1, fv2, fv3, fv4}, ierrmsg);
    }

    //bw_102501.1 - added
    /**
     * Return the sub-validators that are or'ed together
     * Do not assume this array is of length 2, in order to allow for future
     * use.  Also, do not change the returned array, as it maybe cached
     * in the future.
     *
     * @return Sub-validators as an array
     */
    public FormValidator[] getSubValidators() {
        return _fv;
    }

    /**
     * Rather than calling all the sub validate methods, we make sure
     * at least one of the sub-validators is valid
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
    public void validate(FormElement element, FormMap map, boolean deferExceptions) throws ValidationException {
        if (localLogger.isDebugEnabled()) {
            localLogger.debug("Making sure validators" + _fv + " are valid");
        }

        DeferredValidationException veNew = new DeferredValidationException();
        int exCount = 0;
        for (int i = 0; i < _fv.length; i++) {
            try {
                if (_fv[i] != null) {
                    _fv[i].validate(element, map, deferExceptions);
                    // The first valid subvalidator will leave the loop
                    break;
                } else {
                    //veNew = this.generateException(element, deferExceptions, "A given Validator was null");
                    exCount = _fv.length;
                    break;
                }
            } catch (ValidationException ve) {
                veNew.addSubException(ve);
                exCount++;
            }
        }
        // If and only if all validators have failed, we throw an exceptio too
        if (exCount == _fv.length) {
            throw veNew;
        }
    }
}
