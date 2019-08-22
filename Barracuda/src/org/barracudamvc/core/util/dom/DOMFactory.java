/*
 * Copyright (C) 2001  Christian Cryder [christianc@granitepeaks.com]
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
 * $Id: DOMFactory.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.dom;

import java.io.IOException;

import org.w3c.dom.Document;


/**
 * This interface defines the methods needed to implement a DOMFactory. A dom
 * factory is not meant to be called directly, but by the chosen dom loader
 * implementation.
 *
 * <p>There are two possible ways for loading a DOM: from a
 * class and from a path to a document.  It is not required for both to be
 * supported at the same time.</p>
 *
 * <p>Where Implementations do not support a particular getInstance() method,
 * they should simply throw an IOException and document their lack of support.</p>
 *
 * @see DOMLoader
 */
public interface DOMFactory {
    /**
     * Obtain an instance of the DOM from a loaded class.  This is here to
     * support dom implementations such as XMLC which wrap the DOM up in a
     * compiled class.
     *
     * @param clazz the class to be loaded as a Document object
     * @return a Document object
     * @throws IOException
     */
    public Document getInstance(Class clazz) throws IOException;
    
    /**
     * Obtain an instance of the DOM from a path to a document.  The syntax of
     * the path depends on the implementation.
     *
     * <p>Depending on the dom factory implementation, the docPath may be an
     * OS-specifc hardcoded path, a path relative to a known hardcoded path,
     * a path to a document located within the classloader, or anything else
     * one can imagine. See the doc of the various dom factory implementations
     * for details.</p>
     *
     * @param docPath the path to the document to be loaded as a Document object
     * @return a Document object
     * @throws IOException
     */
    public Document getInstance(String docPath) throws IOException;
}
