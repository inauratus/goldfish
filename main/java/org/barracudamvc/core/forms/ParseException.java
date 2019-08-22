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
 * $Id: ParseException.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

/**
 * This exception indicates a value was unable to be converted to
 * a valid value for the specified FormType.
 */
public class ParseException extends ValidationException {

    /**
     * The noargs public contructor for ParseException
     */
    public ParseException() {
        super(null);
    }

    /**
     * The public contructor for ParseException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     */
    public ParseException(Object source) {
        super(source, null);
    }

    /**
     * The public contructor for ParseException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     * @param s a String describing the exception
     */
    public ParseException(Object source, String s) {
        super(source, s, null);
    }

    /**
     * The public contructor for ParseException
     *
     * @param source the object which caused this error (usually a
     *        FormElement)
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public ParseException(Object source, String s, Exception ibaseException) {
        super(source, s, ibaseException);
    }

}
