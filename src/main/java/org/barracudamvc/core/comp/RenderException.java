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
 * $Id: RenderException.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import org.barracudamvc.plankton.exceptions.NestableException;

/**
 * This exception indicates the specified format is not
 * supported.
 */
public class RenderException extends NestableException {

    /**
     * The noargs public contructor for RenderException
     */
    public RenderException () {super();}

    /**
     * The public contructor for RenderException
     *
     * @param s a String describing the exception
     */
    public RenderException (String s) {super(s, null);}

    /**
     * The public contructor for RenderException
     *
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public RenderException (String s, Exception ibaseException) {super(s, ibaseException);}
}
