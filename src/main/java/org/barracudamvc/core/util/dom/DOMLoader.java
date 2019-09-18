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
 * $Id: DOMLoader.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.dom;

import java.io.IOException;
import java.util.Locale;

import org.w3c.dom.Document;


/**
 * This interface defines the methods needed to implement a DOMLoader. A dom
 * loader is used to front various dom factory implementations.
 *
 * <p>Note that the behavior of the getDOM() methods depend upon the
 * backing dom factory implementation(s).
 * Some dom factories only support loading documents from a
 * {@link getDOM(Class) class}, others from a
 * {@link getDOM(String) document path}, while others may support both.
 * Likewise, dom loader implementations may support one or both.  Dom
 * loader implementations must provide at least one backing dom factory
 * and should provide a way for runtime configuration of one or more dom
 * factories.</p>
 *
 * <p>Where Implementations do not support a particular getDOM() method, they
 * should simply throw an IOException and document their lack of support.</p>
 *
 * @see DOMFactory
 */
public interface DOMLoader {
    /**
     * Get the DOM associated with the provided class, based on the default
     * locale
     *
     * @param clazz the class to be loaded as a Document object
     * @return the document that most closely corresponds with the requested
     *         class/locale combination
     * @throws IOException
     * @see DOMFactory#getInstance(Class)
     */
    public Document getDOM(Class clazz) throws IOException;
    
    /**
     * Get the DOM associated with the provided class, based on the specified
     * locale
     *
     * @param clazz the class to be loaded as a Document object
     * @param locale the target Locale (may be null)
     * @return the document that most closely corresponds with the requested
     *         class/locale combination
     * @throws IOException
     * @see DOMFactory#getInstance(Class)
     */
    public Document getDOM(Class clazz, Locale locale) throws IOException;
    
    /**
     * Get the DOM associated with the provided document path, based on the
     * default locale
     *
     * @param docPath the path to the document to be loaded as a Document object
     * @return the document that most closely corresponds with the requested
     *         docPath/locale combination
     * @throws IOException
     * @see DOMFactory#getInstance(String)
     */
    public Document getDOM(String docPath) throws IOException;
    
    /**
     * Get the DOM associated with the provided document path, based on the
     * specified Locale
     *
     * @param docPath the path to the document to be loaded as a Document object
     * @param locale the target Locale (may be null)
     * @return the document that most closely corresponds with the requested
     *         docPath/locale combination
     * @throws IOException
     * @see DOMFactory#getInstance(String)
     */
    public Document getDOM(String docPath, Locale locale) throws IOException;
}
