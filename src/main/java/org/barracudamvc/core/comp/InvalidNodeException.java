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
 * $Id: InvalidNodeException.java 268 2014-05-05 16:58:42Z charleslowery $
 */
package org.barracudamvc.core.comp;

/**
 * This exception indicates the specified format is not
 * supported.
 */
public class InvalidNodeException extends RenderException {

    private static final StackTraceElement[] DEFAULT_TRACE = new StackTraceElement[0];

    /**
     * The noargs public contructor for InvalidNodeException
     */
    public InvalidNodeException() {
        super();
        setStackTrace(DEFAULT_TRACE);
    }

    /**
     * The public contructor for InvalidNodeException
     *
     * @param s a String describing the exception
     */
    public InvalidNodeException(String s) {
        super(s, null);
        setStackTrace(DEFAULT_TRACE);
    }

    /**
     * The public contructor for InvalidNodeException
     *
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public InvalidNodeException(String s, Exception ibaseException) {
        super(s, ibaseException);
    }

}
