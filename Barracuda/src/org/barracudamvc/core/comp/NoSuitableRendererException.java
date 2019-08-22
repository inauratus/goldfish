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
 * $Id: NoSuitableRendererException.java 268 2014-05-05 16:58:42Z charleslowery $
 */
package org.barracudamvc.core.comp;

/**
 * This exception indicates we were unable to locate a suitable
 * renderer.
 */
public class NoSuitableRendererException extends RenderException {

    /**
     * The noargs public contructor for NoSuitableRendererException
     */
    public NoSuitableRendererException() {
        super();
        setStackTrace(new StackTraceElement[]{});
    }

    /**
     * The public contructor for NoSuitableRendererException
     *
     * @param s a String describing the exception
     */
    public NoSuitableRendererException(String s) {
        super(s, null);
        setStackTrace(new StackTraceElement[]{});
    }

    /**
     * The public contructor for NoSuitableRendererException
     *
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public NoSuitableRendererException(String s, Exception ibaseException) {
        super(s, ibaseException);
        setStackTrace(new StackTraceElement[]{});
    }
}
