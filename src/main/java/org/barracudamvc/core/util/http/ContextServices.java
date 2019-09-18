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
 * $Id: ContextServices.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.http;

import javax.servlet.ServletContext;

import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.event.ControlEventContext;
import org.barracudamvc.core.event.EventContext;
import org.barracudamvc.plankton.data.ReferenceFactory;


/**
 * This class defines a convenience method to get the servlet
 * context. It also provides a mechanism to easily cache objects 
 * in the session by Reference (which allows them to automatically 
 * be removed from the session if the system starts running low on memory)
 */
public class ContextServices extends org.barracudamvc.plankton.http.ContextServices {

    /**
     * get the servlet context from a ViewContext
     *
     * @param vc the ControlEventContext object
     * @return the users session
     */
    public static ServletContext getContext(ViewContext vc) {
        EventContext ec = vc.getEventContext();
        if (ec!=null && (ec instanceof ControlEventContext)) {
            return getContext((ControlEventContext) ec);
        } else {
            return null;
        }
    }
    
    /**
     * get the servlet context from a ControlEventContext
     *
     * @param ec the ControlEventContext object
     * @return the users session
     */
    public static ServletContext getContext(ControlEventContext ec) {
        return ec.getConfig().getServletContext();
    }
    
    /**
     * This method retrieves the servlet context from a ViewContext, 
     * and then looks for an object in the servlet context based on a given key. 
     * If the object is not present, it will be created using the 
     * ReferenceFactory and cached in servlet context for future use.
     *
     * @param vc the ViewContext
     * @param key the key that identifies this object
     * @param factory the ReferenceFactory used to create the object
     * @return the object from the cache 
     */
    public static Object getObjectFromCache(ViewContext vc, Object key, ReferenceFactory factory) {
        return getObjectFromCache(getContext(vc), key, factory);
    }
    
    /**
     * This method retrieves the servlet context from a ControlEventContext, 
     * and then looks for an object in the servlet context based on a given key. 
     * If the object is not present, it will be created using the 
     * ReferenceFactory and cached in servlet context for future use.
     *
     * @param ec the ControlEventContext
     * @param key the key that identifies this object
     * @param factory the ReferenceFactory used to create the object
     * @return the object from the cache 
     */
    public static Object getObjectFromCache(ControlEventContext ec, Object key, ReferenceFactory factory) {
        return getObjectFromCache(getContext(ec), key, factory);
    }
    
}
