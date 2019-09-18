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
 * $Id: UnableToValidateException.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms;

import java.util.List;

/**
 * This exception indicates the Validator was not able to validate
 * the incoming value. Usually thrown due to a type mismatch (ie. the 
 * validator is expecting a number and it gets an alphanumeric string 
 * or something like that), if this occurs it probably indicates you are 
 * improperly using a validator.
 */
public class UnableToValidateException extends ValidationException {

    protected List subExceptions = null;

    /**
     * The noargs public contructor for UnableToValidateException
     */
    public UnableToValidateException () {super(null);}

    /**
     * The public contructor for UnableToValidateException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     */
    public UnableToValidateException (Object source) {super(source, null);}

    /**
     * The public contructor for UnableToValidateException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     * @param s a String describing the exception
     */
    public UnableToValidateException (Object source, String s) {super(source, s, null);}

    /**
     * The public contructor for UnableToValidateException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public UnableToValidateException (Object source, String s, Exception ibaseException) {super(source, s, ibaseException);}

}
