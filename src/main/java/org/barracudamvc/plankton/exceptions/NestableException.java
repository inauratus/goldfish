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
 * $Id: NestableException.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.exceptions;

/**
 * This class simply defines a NestableException.
 * A NestableException can contain other exceptions
 * (which may have triggered this exception). The
 * getRootException method provides a means to find the
 * root exception in a NestableException chain.
 */
public class NestableException extends Exception {

    private Exception baseException = null;

    /**
     * The noargs public contructor for NestableException
     */
    public NestableException () {
        this("Unidentified Exception");
    }

    /**
     * The public contructor for NestableException
     *
     * @param s a String describing the exception
     */
    public NestableException (String s) {
        this(s, null);
    }

    /**
     * The public contructor for NestableException
     *
     * @param s a String describing the exception
     * @param ibaseException the original exception to wrap within this exception
     */
    public NestableException (String s, Exception ibaseException) {
        super(s);
        baseException = ibaseException;
    }

    /**
     * get the BaseException behind this exception
     *
     * @return Exception baseException
     */
    public Exception getBaseException () {
        return baseException;
    }

    /**
     * get the RootException behind this exception. Will look for
     * a baseException, and if it happens to be an instance of a
     * NestableException, will recursively work deeper until it finds
     * the exception which caused it all
     *
     * @param ne a NestableException for which we wish to find
     *      the root exception
     * @return Exception the exception which caused it all
     */
    public synchronized static Exception getRootException (NestableException ne) {
        Exception e = ne.getBaseException();
        if (e!=null && e instanceof NestableException) return getRootException ((NestableException) e);
        return ne;
    }
}