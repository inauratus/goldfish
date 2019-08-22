/*
 * Copyright (C) 2001  Jacob Kjome [hoju@visi.com]
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
 * $Id: XMLCStdDOMFactory.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.dom;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.enhydra.xml.xmlc.XMLCStdFactory;
import org.enhydra.xml.xmlc.XMLObject;
import org.w3c.dom.Document;


/**
 * XMLC standard implementation of a DOMFactory. This class will load a DOM using
 * XMLCStdFactory.
 */
public class XMLCStdDOMFactory implements DOMFactory {

    /**
     * used for logging
     */
    protected static final Logger logger = Logger.getLogger(XMLCStdDOMFactory.class.getName());

    /**
     * arbitrary object used to synchronize upon
     */
    protected final Object sync = new Object();

    /**
     * used for flagging whether the xmlcFactory has been initialized and
     * avoiding unnecessary synchronization.
     */
    protected boolean initialized = false;

    /**
     * XMLCFactory instance, stored so that it isn't re-created on
     * every request.
     */
    protected XMLCStdFactory xmlcFactory;

    /**
     * Get a new instance of the DOM that is associated with the
     * given class.
     *
     * @param clazz the class to be loaded as a DOM object. In this case, this class
     *        should implement XMLObject or the underlying XMLCFactory will
     *         not be able to instantiate it.
     * @return the document that most closely corresponds with the requested
     *        class/locale combination
     */
    public Document getInstance(Class clazz) throws IOException {
        if (!((XMLObject.class).isAssignableFrom(clazz))) throw new IOException ("Class "+clazz.getName()+" can not be loaded by this DOMFactory because it does not implement XMLOBject");
        initFactory();
        return xmlcFactory.create(clazz);
    }

    /**
     * This method is not supported by this dom factory and will
     * immediately throw an IOException if called!
     */
    public Document getInstance(String docPath) throws IOException {
        throw new IOException("Error: Unimplemented - XMLCStdFactory does not support creation of documents directly from file");
    }

    private void initFactory() {
        if (initialized == false) {
            synchronized (sync) {
                logger.info("initializing an XMLC Std factory for returning XMLC-generated documents");
                xmlcFactory = new XMLCStdFactory(Thread.currentThread().getContextClassLoader(), null);
                initialized = true;
            }
        }
    }

}
