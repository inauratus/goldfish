/*
 * Copyright (C) 2004 ATM Express, Inc [christianc@atmreports.com]
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
 * Christian Cryder, Diez B. Roggisch
 *
 * $Id: ValidatorListValidator.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import java.util.*;
import org.barracudamvc.core.forms.*;


/**
 * Validate a list of zero or more Validators.
 *
 * This is much like the And validator except it makes it easier when validating an arbitrary number
 * of validators. Also unlike the And validator, this will test validatation on <i>all</i> the
 * included validators (i.e. it won't stop after the first failure).
 *
 * @author  christianc@atmreports.com
 * @since   csc_110304_1
 */
public class ValidatorListValidator extends DefaultFormValidator {

    private Set<FormValidator> validators;

    public ValidatorListValidator(Collection<FormValidator> validators) {
        this.validators = new HashSet<FormValidator>(validators);
    }

    public ValidatorListValidator(FormValidator[] validators) {
        this.validators = new HashSet<FormValidator>(Arrays.asList(validators));
    }

    @Override
    public void validate(FormElement element, FormMap map, boolean deferExceptions) throws ValidationException {

        DeferredValidationException dve = null;

        for (FormValidator val : validators) {
            if (val !=null) {
                try { val.validate(element, map, deferExceptions); }
                catch (DeferredValidationException dve1) {
                    if (dve==null) dve = dve1;
                    else dve.addSubException(dve1);
                }
            }
        }

        if (dve!=null) throw dve;
    }
    
    @Override
    public void addValidator(FormValidator aValidator) {
    	validators.add(aValidator);
    }
    
    public void addAll(Collection<FormValidator> someValidators) {
    	validators.addAll(someValidators);
    }
    
}
