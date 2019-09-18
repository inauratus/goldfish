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
 * $Id: SessionManager.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.http;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.data.ObjectRepository;
import org.barracudamvc.plankton.data.SoftHashMap;

/**
 * This class provides a convenient bridge to any sessions which are being managed/registered
 * through Barracuda's SessionObjectRepository class (ie. all events that go through ApplicationGateway
 * use this). You can use this class to get a handle to all the underlying sessions, to log an individual
 * session out, or to log all sessions out.
 *
 * @author christianc@granitepeaks.com
 * @since //csc_011704_1
 */
public class SessionManager {

    protected static Logger logger = Logger.getLogger(SessionManager.class.getName());

    /**
     * allows you to look up a session by its unique identifier. You should only need to call
     * this if you are performing large scale session management (ie. logging out all users, etc)
     */
    public static HttpSession getSession(String sessionID) {
        return (HttpSession) getAllSessions().get(sessionID);
    }
    
    /**
     * get a Map containing soft references to all the HttpSessions. Simply delegates to 
     * ObjectRepository.getRawSessionStore(). Note that you should be very careful using this
     * method - the ObjectRespository stores soft references to the session, allowing them 
     * to be recalimed by the garbage collector when they expire. If you use this method to
     * access the underlying sessions you must be very careful not to hold onto these references
     * or you may run into problems.
     */
    public static SoftHashMap getAllSessions() {
        return ObjectRepository.getRawSessionStore();
    }

    /**
     * allows you to invalidate a session associated with the current thread
     */
    public static void invalidateSession() {
        ObjectRepository.invalidateSession();
    }

    /**
     * allows you to invalidate a session by its unique identifier. You should only need to call
     * this if you are performing large scale session management (ie. logging out all users, etc)
     */
    public static void invalidateSession(String sessionID) {
        ObjectRepository.invalidateSession(sessionID);
    }
    
    /**
     * allows you to invalidate all sessions. You should only need to call
     * this if you are performing large scale session management (ie. logging out all users, etc)
     */
    public static void invalidateAllSessions() {
        ObjectRepository.invalidateAllSessions();
    }

    


}
