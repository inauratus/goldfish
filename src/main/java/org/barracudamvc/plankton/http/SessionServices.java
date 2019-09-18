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
package org.barracudamvc.plankton.http;

import java.lang.ref.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.data.ReferenceFactory;


/**
 * This class defines a convenience method to get the session 
 * and set the timeout at the same time. It also provides a 
 * mechanism to easily cache objects in the session by Reference
 * (which allows them to automatically be removed from the session
 * if the system starts running low on memory)
 */
public class SessionServices {

    //constants
//    public static int DEFAULT_TIMEOUT = 600;    //in seconds (10 minutes)
    public static final String KEY = SessionServices.class.getName()+".Key";

    protected static final Logger logger = Logger.getLogger(SessionServices.class.getName());

    /**
     * get the session from a request. If no session exists it will
     * create it for us. Ensures that the timeout is set to DEFAULT_TIMEOUT.
     *
     * @param req the ServletRequest object
     * @return the users session
     */
    public static HttpSession getSession(HttpServletRequest req) {
//        return getSession(req, true, DEFAULT_TIMEOUT);
        return getSession(req, true, null);
    }
    
    /**
     * get the session from a request (allowing you to specify 
     * whether or not to create it)
     *
     * @param req the ServletRequest object
     * @param create if true, the session will be created if it does not
     *      already exist
     * @return the users session (may be null if the session does not yet
     *      exist and create is false)
     */
    public static HttpSession getSession(HttpServletRequest req, boolean create) {
        return getSession(req, create, null);
    }
    
    /**
     * get the session from a request (allowing you to specify 
     * whether or not to create it). If the session exists, it 
     * will set the default timeout for us.
     *
     * @param req the ServletRequest object
     * @param create if true, the session will be created if it does not
     *      already exist
     * @param timeout the default timeout value (null indicates do not set)
     * @return the users session (may be null if the session does not yet
     *      exist and create is false)
     */
    public static HttpSession getSession(HttpServletRequest req, boolean create, Integer timeout) {
        HttpSession session = req.getSession(false);
        if (session==null && create) {
            session = req.getSession(create);
            if (timeout!=null) session.setMaxInactiveInterval(timeout.intValue());
        }
        return session;
    }

    /**
     * This method looks for an object in the session based on a given key.
     * If the object is not present, it will be created using the ReferenceFactory
     * and cached in session for future use.
     *
     * @param session the HttpSession
     * @param key the key that identifies this object
     * @param factory the ReferenceFactory used to create the object
     * @return the object from the cache 
     */
    public static Object getObjectFromCache(HttpSession session, Object key, ReferenceFactory factory) {
        Reference r = (Reference) session.getAttribute(KEY+key);
        Object obj = null;
        if (r!=null) obj = r.get();
        if (r==null || obj==null) {
            r = factory.getObjectReference();
            obj = r.get();
            session.setAttribute(KEY+key, r);
            if (logger.isDebugEnabled()) logger.debug("Created reference:"+r);
        }
        if (logger.isDebugEnabled()) logger.debug("Returning object from cache:"+obj);
        return obj;    
    }
    

    
}
