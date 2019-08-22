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
 * $Id: Not.java 251 2012-11-09 18:49:25Z charleslowery $
 */
package org.barracudamvc.core.forms;

/**
 * This validator ensures that something is NOT valid. We do this
 * by validating another validator; if it does NOT generate a ValidationException
 * then we throw an exception because it should have. If it does throw a
 * ValidationException, then all is well. This validator effectively acts as
 * a "NOT" or "!" on another FormValidator.
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @author  Bill Wallace <Bill_Wallace@elementk.com>
 * @author  Diez B. Roggisch <diez.roggisch@artnology.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class Not extends AbstractFormValidator {

    /**
     * @clientCardinality 1 
     */
    protected FormValidator fv = null;

    /**
     * Public constructor.
     *
     * @param ifv the validator we wish to make sure is not valid
     */
    public Not(FormValidator ifv) {
        this(ifv, null);
    }

    /**
     * Public constructor.
     *
     * @param ifv the validator we wish to make sure is not valid
     * @param ierrmsg the message associated with this error
     */
    public Not(FormValidator ifv, String ierrmsg) {
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
     * Rather than calling all the sub validate methods, we instead validate
     * the validator we contain--because this validator is effectively doing
     * a "NOT", we expect to receive a ValidationException; if we don't, then
     * we will throw a ValidationException
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
            localLogger.debug("Making sure " + fv + " is not valid");
        }

        boolean throwEx = true;

        //if fv==true, no error occurs, since a NOT of a null would be considered true...
        if (fv != null) {
            try {
                //validate the internal validator
                fv.validate(element, map, deferExceptions);
            } catch (ValidationException ve) {
                if (localLogger.isDebugEnabled()) {
                    localLogger.debug(fv + " is not valid, so we continue.");
                }
                throwEx = false;
                //ok to continue
            }
        }

        if (throwEx) {
            //uh-oh; we didn't get an exception
            throw this.generateException(element, deferExceptions, 
                    "Validator " + fv + " was valid (and it shouldn't have been!)");
        }
    }
}
