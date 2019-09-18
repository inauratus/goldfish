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
 * $Id: SessionServices.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.http;

import javax.servlet.http.HttpSession;

import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.event.ControlEventContext;
import org.barracudamvc.plankton.data.ReferenceFactory;


/**
 * This class defines a convenience method to get the session 
 * and set the timeout at the same time. It also provides a 
 * mechanism to easily cache objects in the session by Reference
 * (which allows them to automatically be removed from the session
 * if the system starts running low on memory)
 */
public class SessionServices extends org.barracudamvc.plankton.http.SessionServices {

    /**
     * get the session from the view context (it will automatically extract
     * the event context and cast it to ControlEventContext for you, then 
     * retrieve the request from there and use that to get the session info). 
     * If no session exists it will create it for us. Ensures that the timeout 
     * is set to DEFAULT_TIMEOUT.
     *
     * @param vc the ViewContext
     * @return the users session
     */
    public static HttpSession getSession(ViewContext vc) {
        return getSession(((ControlEventContext) vc.getEventContext()).getRequest());
    }
    
    /**
     * get the session from the view context (allowing you to specify whether or 
     * not to create it)
     *
     * @param vc the ViewContext
     * @param create if true, the session will be created if it does not
     *      already exist
     * @return the users session (may be null if the session does not yet
     *      exist and create is false)
     */
    public static HttpSession getSession(ViewContext vc, boolean create) {
        return getSession(((ControlEventContext) vc.getEventContext()).getRequest(), create);
    }
    
    /**
     * get the session from the view context (allowing you to specify 
     * whether or not to create it). If the session exists, it 
     * will set the default timeout for us.
     *
     * @param vc the ViewContext
     * @param create if true, the session will be created if it does not
     *      already exist
     * @param timeout the default timeout value (null indicates do not set)
     * @return the users session (may be null if the session does not yet
     *      exist and create is false)
     */
    public static HttpSession getSession(ViewContext vc, boolean create, Integer timeout) {
        return getSession(((ControlEventContext) vc.getEventContext()).getRequest(), create, timeout);
    }


    /**
     * This method retrieves the session from the Context, and then looks for an 
     * object in the session based on a given key. If the object is not present, 
     * it will be created using the ReferenceFactory and cached in session for 
     * future use.
     *
     * @param context the ControlEventContext
     * @param key the key that identifies this object
     * @param factory the ReferenceFactory used to create the object
     * @return the object from the cache 
     */
    public static Object getObjectFromCache(ControlEventContext context, Object key, ReferenceFactory factory) {
        HttpSession session = SessionServices.getSession(context.getRequest());
        return getObjectFromCache(session, key, factory);
    }
    
}
