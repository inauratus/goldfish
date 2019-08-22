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
 * $Id: ContextServices.java 269 2014-06-30 18:36:26Z charleslowery $
 */
package org.barracudamvc.plankton.http;

import java.lang.ref.Reference;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.barracudamvc.plankton.data.ReferenceFactory;

/**
 * This class defines a convenience method to get the servlet
 * context. It also provides a mechanism to easily cache objects 
 * in the session by Reference (which allows them to automatically 
 * be removed from the session if the system starts running low on memory)
 */
public class ContextServices {

    public static final String KEY = ContextServices.class.getName() + ".Key";
    protected static final Logger logger = Logger.getLogger(ContextServices.class.getName());

    /**
     * This method looks for an object in the servlet context based on a given key.
     * If the object is not present, it will be created using the ReferenceFactory
     * and cached in session for future use.
     *
     * @param context the ServletContext
     * @param key the key that identifies this object
     * @param factory the ReferenceFactory used to create the object
     * @return the object from the cache 
     */
    public static Object getObjectFromCache(ServletContext context, Object key, ReferenceFactory factory) {
        Reference r = (Reference) context.getAttribute(KEY + key);
        Object obj = null;
        if (r != null) {
            obj = r.get();
        }
        if (r == null || obj == null) {
            r = factory.getObjectReference();
            obj = r.get();
            context.setAttribute(KEY + key, r);
        }
        return obj;
    }
}
