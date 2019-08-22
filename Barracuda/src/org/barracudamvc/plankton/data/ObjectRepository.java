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
 * $Id: ObjectRepository.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class provides access to several different generic statemap repositories,
 * scoped for Global, Session, Local, or custom (NameSpace or Name...you provide 
 * the cleanup). The basic idea here is to make it easy to access object repositories
 * without having to pass references around.
 *
 * The Global object repository represents a common (threadsafe) statemap that is 
 * shared across the JVM. You would typically put things here like DataSources, global
 * variables, etc. Any objects that you make available globally need to be threadsafe.
 *
 * The Session object repository is a statemap wrapper around HttpSession. This provides
 * a convenient bridge to the Session interface, without having to have a reference to 
 * the HttpServletRequest. Storing items in this object places them in the underlying session.
 * Note that ApplicationGateway and ComponentGateway classes invoke the setupSessionRepository()
 * call for you, so all you have to do is call getSessionRepository() and you're in business.
 *
 * The Local object repository is a statemap object that lasts for the duration of a req-resp
 * cycle. Note that the ApplicationGateway and ComponentGateway classes clean up both Session and
 * Local repositories.
 *
 * The Weak and Soft maps are backed by WeakHashMap and SoftHashMap maps respectively (the basic
 * distinction being that weak references will generally be cleaned up as soon as possible while
 * soft references will generally be held on to as long as possible). The soft map is generally
 * a better choice for caching.
 *
 * You can also get non-scoped ObjectRepositories by using either NameSpace or String keys. IF you
 * use this approach, its up to you to manually remove the repositories when you're done with them
 * (or else you'll end up leaving them in memory taking up space).
 *
 * @author Christian Cryder
 * @version 1.0
 */
public class ObjectRepository extends DefaultStateMap implements Serializable {

    //custom repository types
    private static final long serialVersionUID = 1;

    public static final int DEFAULT = 0;
    public static final int WEAK = 1;
    public static final int SOFT = 2;
    public static final int THREADSAFE = 3;
    public static final int WEAK_THREADSAFE = 4;
    public static final int SOFT_THREADSAFE = 5;
    
    protected static ObjectRepository global = new ThreadsafeRepository("GlobalOR");
    protected static ObjectRepository weakGlobal = new WeakThreadsafeRepository("WeakGlobalOR");
    protected static ObjectRepository softGlobal = new SoftThreadsafeRepository("SoftGlobalOR");
    protected static SoftHashMap<String, HttpSession> rawSessions = new SoftHashMap<>();
    protected static Map<String, ObjectRepository> session = new HashMap<>();
    protected static Map<String, String> sessionIDs = new HashMap<>();
    protected static Map weaksession = new HashMap();
    protected static Map softsession = new HashMap();
    protected static Map<String, ObjectRepository> local = new HashMap<>();
    protected static Map<Object, ObjectRepository> custom = new HashMap<>();
    protected String name = "[unnamed]";

    /**
     * protected constructor
     */
    protected ObjectRepository() {
        this(null);
    }

    protected ObjectRepository(String iname) {
        if (iname != null) {
            name = iname;
        }
    }

    public static void destroy(){
        global.clearState();
        global = null;
        
        weakGlobal.clearState();
        weakGlobal = null;
        
        softGlobal.clearState();
        softGlobal = null;
        
        rawSessions.clear();
        rawSessions = null;

        for (Entry<String, ObjectRepository> stringObjectRepositoryEntry : session.entrySet()) {
            stringObjectRepositoryEntry.getValue().clearState();
        }
        session.clear();
        session = null;
        
        sessionIDs.clear();
        sessionIDs = null;
        
        weaksession.clear();
        softsession.clear();

        for (Entry<String, ObjectRepository> entry : local.entrySet()) {
            entry.getValue().clearState();            
        }
        local.clear();
        local = null;

        for (Entry<Object, ObjectRepository> entry : custom.entrySet()) {
            entry.getValue().clearState();
        }
        custom.clear();
        custom = null;       

    }
    
    
    /**
     * Get a reference to the Global repository. This repository
     * is shared across the entire JVM (consequently its threadsafe). 
     * The global repository never really goes away, so you don't need 
     * to do any cleanup on this one.
     */
    public static ObjectRepository getGlobalRepository() {
        return global;
    }

    //csc_082302.2 - added
    /**
     * Get a reference to the Weak Global repository. This repository
     * is shared across the entire JVM (consequently its threadsafe),
     * but like the Weak Session repository, its backed by a weak hash
     * map, allowing object which are placed in here to be gc'd as needed.
     */
    public static ObjectRepository getWeakGlobalRepository() {
        return weakGlobal;
    }

    //csc_082302.2 - added
    /**
     * Get a reference to the Soft Global repository. This repository
     * is shared across the entire JVM (consequently its threadsafe),
     * but like the Soft Session repository, its backed by a soft hash
     * map, allowing object which are placed in here to be gc'd as needed,
     * but only when really needed.
     */
    public static ObjectRepository getSoftGlobalRepository() {
        return softGlobal;
    }

    /**
     * Set up the Session repository for this particular thread. You must invoke this 
     * method before actually getting the session repository, IFF you actually
     * want the repository to wrapp the Servlet's Session structure. Otherwise, 
     * when you get the Session repository (below) it will simply return a regular
     * state map.
     */
    public static void setupSessionRepository(HttpServletRequest req) {
        String key = "SessionOR_" + Thread.currentThread().getName();
        synchronized (session) {
            session.put(key, new SessionRepository(req));
        }
    }

    /**
     * Get a reference to the Session repository. If you are using this 
     * within Barracuda (ie. ApplicationGateway or ComponentGateway), this
     * will return a StateMap that wraps the Servlet session.
     *
     * Note that this class returns an instance of ObjectRepository, NOT
     * ObjectRepository.SessionRepository. This is because you are not
     * always guaranteed to have a ObjectRepository.SessionRepository under
     * the covers (ie. if you are running in a non-servlet environment, or
     * within a servlet context which did not properly set up the the session
     * repository the way ApplicationGateway does. 
     *
     * Consequently, if you want to upcast to ObjectRepository.SessionRepository,
     * you would be wise to check instanceof first...
     */
    public static ObjectRepository getSessionRepository() {
        String key = "SessionOR_" + Thread.currentThread().getName();
        return getSessionRepository(key);
    }

    protected static ObjectRepository getSessionRepository(String key) {

        ObjectRepository or = (ObjectRepository) session.get(key);
        if (or == null) {
            or = new ObjectRepository(key);
            synchronized (session) {
                session.put(key, or);
            }
        }
        return or;
    }

    /**
     * Release the Session repository
     */
    public static void removeSessionRepository() {
        String key = "SessionOR_" + Thread.currentThread().getName();
        synchronized (session) {
            ObjectRepository or = session.remove(key);

            // If the Object Repository stored in the session slot is not a
            // SessionRepository we can't do anything further. 
            if (!(or instanceof SessionRepository)) {
                return;
            }

            SessionRepository sessionRepository = (SessionRepository) or;
            HttpServletRequest hsr = sessionRepository.req;
            HttpSession hs = (hsr != null ? hsr.getSession(false) : null);

            //now, if the underlying session exists, we save a reference to 
            //it since it may have been created this pass
            if (hs != null) {
                try {
                    //this will test to make sure its not invalidated
                    hs.getAttributeNames();
                    synchronized (rawSessions) {
                        rawSessions.put(hs.getId(), hs);
                    }
                } catch (IllegalStateException e) {
                    //not a big deal - just means that the session was 
                    //invalidated (perhaps programatically) during
                    //the req cycle, and therefor there is no point in 
                    //trying to store a reference to this in rawSessions                
                }
                sessionIDs.remove(hs.getId());
            }

            //regardless of the state of the session, we want to completely 
            //clean up the statemap that we were using to wrap it and remove 
            //it from the session OR we do this because next time around, 
            //it'll be registered under a different thread name anyway)
            sessionRepository.req = null;
            sessionRepository.map = null;
        }
    }

    /**
     * Get a reference to a Weak Session repository. Objects placed in here
     * are scoped to Session, but may be reclaimed by garbage collecter as needed
     * (so you must always check for null and reinitialize if necessary)
     */
    public static ObjectRepository getWeakSessionRepository() {
        String key = "WeakSessionOR_$$";
        ObjectRepository session_or = getSessionRepository();
        ObjectRepository weak_or = session_or.getState(key);
        if (weak_or == null) {
            weak_or = new WeakRepository(key);
            synchronized (session_or) {
                session_or.putState(key, weak_or);
            }
        }
        return weak_or;
    }

    //csc_052703.1 - added
    /**
     * Get a reference to a Soft Session repository. Objects placed in here
     * are scoped to Session, but may be reclaimed by garbage collecter as needed
     * (so you must always check for null and reinitialize if necessary)
     */
    public static ObjectRepository getSoftSessionRepository() {
        String key = "SoftSessionOR_$$";
        ObjectRepository session_or = getSessionRepository();
        ObjectRepository soft_or = session_or.getState(key);
        if (soft_or == null) {
            soft_or = new SoftRepository(key);
            synchronized (session_or) {
                session_or.putState(key, soft_or);
            }
        }
        return soft_or;
    }

    /**
     * Get a reference to the Local repository. This repository is shared
     * across the thread. If you use this repository, it is your responsibility 
     * to clean things up (unless something else is cleaning it up for you,
     * like the Barracuda ApplicationGateway or ComponentGateway)
     */
    public static ObjectRepository getLocalRepository() {
        String key = "LocalOR_" + Thread.currentThread().getName();
        ObjectRepository or = local.get(key);
        if (or == null) {
            or = new ObjectRepository(key);
            local.put(key, or);
        }
        return or;
    }

    /**
     * Release the Local repository.
     */
    public static void removeLocalRepository() {
        String key = "LocalOR_" + Thread.currentThread().getName();
        local.remove(key);
    }

    /**
     * Get an object repository associated with a given NameSpace. If
     * the object repository for this namespace does not already exist,
     * it will be created automatically.
     */
    public static ObjectRepository getObjectRepository(NameSpace ns) {
        ObjectRepository or = (ObjectRepository) custom.get(ns);
        if (or == null) {
            or = new ObjectRepository(ns.getName());
            synchronized (custom) {
                custom.put(ns, or);
            }
        }
        return or;
    }

    /**
     * Release an object repository associated with a given NameSpace
     */
    public static void removeObjectRepository(NameSpace ns) {
        synchronized (custom) {
            custom.remove(ns);
        }
    }

    /**
     * Get an object repository based on a given name. If
     * the object repository for this namespace does not already exist,
     * it will be created automatically.
     */
    public static ObjectRepository getObjectRepository(String name) {
        return getObjectRepository(name, DEFAULT);
    }

    /**
     * Get an object repository based on a given name. If the object repository 
     * for this namespace does not already exist, it will be created automatically,
     * and you can specify what type you wish created: DEFAULT, WEAK, SOFT, THREADSAFE
     */
    public static ObjectRepository getObjectRepository(String name, int type) {
        synchronized (custom) {
            ObjectRepository or = custom.get(name);
            if (or == null) {
                or = createObjectRepository(name, type);
                custom.put(name, or);
            }
            return or;
        }
    }

    private static ObjectRepository createObjectRepository(String name, int type) {
        switch (type) {
            case WEAK:
                return new WeakRepository(name);
            case SOFT:
                return new SoftRepository(name);
            case THREADSAFE:
                return new ThreadsafeRepository(name);
            case WEAK_THREADSAFE:
                return new WeakThreadsafeRepository(name);
            case SOFT_THREADSAFE:
                return new SoftThreadsafeRepository(name);
            default:
                return new ObjectRepository(name);
        }
    }

    /**
     * Release an object repository associated with a given name
     */
    public static void removeObjectRepository(String name) {
        synchronized (custom) {
            custom.remove(name);
        }
    }

    /**
     * Return the name of this object repository
     */
    public String getName() {
        return name;
    }

    //csc_011704_1 - added
    /**
     * get raw session - this gives you access to the underlying session object
     */
    public static HttpSession getRawSession() {
        HttpServletRequest req = ((SessionRepository) getSessionRepository()).req;
        return (req != null ? req.getSession() : null);
    }

    /**
     * allows you to invalidate a session by its unique identifier. You should only need to call
     * this if you are performing large scale session management (ie. logging out all users, etc)
     */
    public static void invalidateSession() {
        if (getSessionRepository() instanceof SessionRepository) {
            HttpServletRequest req = ((SessionRepository) getSessionRepository()).req;
            HttpSession hs = (req != null ? req.getSession(false) : null);
            if (hs != null) {
                invalidateSession(hs.getId());
            }
        }
    }

    /**
     * allows you to invalidate a session by its unique identifier. You should only need to call
     * this if you are performing large scale session management (ie. logging out all users, etc)
     */
    public static void invalidateSession(String sessionID) {
        //first, clean up any Barracuda structures that might be pointing to this session
        synchronized (session) {
            String key = (String) sessionIDs.remove(sessionID);
            if (key != null) {
                Object possibleSessionRepository = session.remove(key);
                if (possibleSessionRepository instanceof SessionRepository) {
                    SessionRepository sessionRepository = (SessionRepository) possibleSessionRepository;
                    sessionRepository.map = null;
                    sessionRepository.registered = false;
                }
            }
        }

        //now see if we can clean up the real session for this id
        synchronized (rawSessions) {
            HttpSession hs = (HttpSession) rawSessions.get(sessionID);
            if (hs != null) {
                hs.invalidate();
            }
            rawSessions.remove(sessionID);
        }
    }

    //csc_011704_1 - added
    /**
     * allows you to invalidate all rawSessions. You should only need to call
     * this if you are performing large scale session management (ie. logging out all users, etc)
     */
    public static void invalidateAllSessions() {
        synchronized (session) {
            session.clear();
            sessionIDs = new HashMap<String, String>();
        }

        //now see if we can clean up the real session for this id
        synchronized (rawSessions) {
            Iterator it = rawSessions.values().iterator();
            while (it.hasNext()) {
                Object reference = it.next();
                if (reference instanceof SoftReference) {
                    Object hs = ((SoftReference) reference).get();
                    if (hs instanceof HttpSession) {
                        ((HttpSession) hs).invalidate();
                    }
                }
            }
        }
        rawSessions = new SoftHashMap<String, HttpSession>();
    }

    /**
     * This method gives you direct access to the underlying HttpSession data structures (as many as 
     * are registered by Barracuda apps). Developers should almost never need to interact
     * with these objects directly - this method is primarily intended for debugging purposes.
     * Note that the Map returned is actually a copy of the underlying store, with keys
     * conveniently extracted from their SoftReference wrappers.
     */
    public static Map getSessionStore() {
        //for the session list, we want to pull the underlying session objects out of their soft referents
        synchronized (rawSessions) {
            Map<String, HttpSession> smap = new HashMap<String, HttpSession>(rawSessions.size());

            for (Entry<String, HttpSession> entry : rawSessions.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                smap.put(entry.getKey(), entry.getValue());
            }

            return smap;
        }
    }

    //csc_011704_1 - added
    /**
     * This method gives you direct access to the underlying HttpSession data structure. You shouldn't
     * ever need to use this method unless you really know what you are doing.
     */
    public static SoftHashMap getRawSessionStore() {
        return rawSessions;
    }

    //csc_011604_1 - added
    /**
     * This method gives you direct access to the underlying data structures in which everything
     * related the the object repository is kept. Developers should almost never need to interact
     * with these objects directly - this method is primarily intended for debugging purposes
     */
    public static Map getObjectRepositoryStore() {
        //now build a map of all the structures and return it
        Map<String, Object> m = new TreeMap<String, Object>();
        m.put("OR1-global", global);
        m.put("OR2-weak global", weakGlobal);
        m.put("OR3-soft global", softGlobal);
        m.put("OR4-rawSessions", getSessionStore());
        m.put("OR5-weak session", weaksession);
        m.put("OR6-soft session", softsession);
        m.put("OR7-local", local);
        m.put("OR8-custom", custom);

        return m;
    }

    public static void printStackTrace() {
        printStackTrace(null);
    }

    public static void printStackTrace(String msg) {
        printStackTrace(msg, System.out);
    }

    public static void printStackTrace(String msg, OutputStream out) {
        try {
            out.write(("\n\n\n\n\nObjectRepository StackTrace: " + (msg != null ? "[" + msg + "]" : "") + "\n").getBytes());
            CollectionsUtil.printStackTrace(getObjectRepositoryStore(), out);
            out.write("\ndone.\n\n\n\n".getBytes());
        } catch (IOException e) {
        }
    }

    /**
     * A threadsafe wrapper around ObjectRepository
     */
    static class ThreadsafeRepository extends ObjectRepository implements Serializable {

        private static final long serialVersionUID = 1;

        public ThreadsafeRepository() {
            super();
            props = Collections.synchronizedMap(new HashMap<Object, Object>());
        }

        public ThreadsafeRepository(String iname) {
            super(iname);
            props = Collections.synchronizedMap(new HashMap<Object, Object>());
        }
    }

    /**
     * A weak wrapper around ObjectRepository (things that get put in 
     * here can get reclaimed by the garbage collecter as needed, so
     * you must alwasy check for null as you retrieve objects from this 
     * space)
     */
    static class WeakRepository extends ObjectRepository implements Serializable {

        private static final long serialVersionUID = 1;

        public WeakRepository() {
            super();
            props = new WeakHashMap<Object, Object>();
        }

        public WeakRepository(String iname) {
            super(iname);
            props = new WeakHashMap<Object, Object>();
        }
    }

    /**
     * A weak threadsafe wrapper around ObjectRepository
     */
    static class WeakThreadsafeRepository extends WeakRepository implements Serializable {

        private static final long serialVersionUID = 1;

        public WeakThreadsafeRepository() {
            super();
            props = Collections.synchronizedMap(props);
        }

        public WeakThreadsafeRepository(String iname) {
            super(iname);
            props = Collections.synchronizedMap(props);
        }
    }

    /**
     * A soft wrapper around ObjectRepository (things that get put in 
     * here can get reclaimed by the garbage collecter as needed, so
     * you must alwasy check for null as you retrieve objects from this 
     * space)
     */
    static class SoftRepository extends ObjectRepository implements Serializable {

        private static final long serialVersionUID = 1;

        public SoftRepository() {
            super();
            props = new SoftHashMap<>();
        }

        public SoftRepository(String iname) {
            super(iname);
            props = new SoftHashMap<>();
        }
    }

    /**
     * A soft threadsafe wrapper around ObjectRepository
     */
    static class SoftThreadsafeRepository extends SoftRepository implements Serializable {

        private static final long serialVersionUID = 1;

        public SoftThreadsafeRepository() {
            super();
            props = Collections.synchronizedMap(props);
        }

        public SoftThreadsafeRepository(String iname) {
            super(iname);
            props = Collections.synchronizedMap(props);
        }
    }

    /**
     * The basic idea behind this is that we want to wrap the session
     * as a ObjectRepository object. In order to make this work, it's important
     * that we don't actually cause the session to be instantiated
     * unless absolutely necessary
     */
    public static class SessionRepository extends ObjectRepository implements Serializable {

        private static final long serialVersionUID = 1;

        HttpServletRequest req = null;
        HttpSessionStateMap map = null;
        boolean registered = false;

        public SessionRepository() {
            super();
        }

        public SessionRepository(String iname) {
            super(iname);
        }

        public SessionRepository(HttpServletRequest ireq) {
            req = ireq;
            registerSession();
        }

        //csc_011704_1 - added
        protected void registerSession() {
            if (registered) {
                return;
            }
            HttpSession hs = req.getSession(false);
            if (hs != null) {
                try {
                    hs.getCreationTime();
                    String key = "SessionOR_" + Thread.currentThread().getName();
                    synchronized (session) {
                        synchronized (rawSessions) {
                            if (!rawSessions.containsKey(hs.getId())) {
                                rawSessions.put(hs.getId(), hs);
                            }
                        }
                        if (!sessionIDs.containsKey(hs.getId())) {
                            sessionIDs.put(hs.getId(), key);
                        }
                        registered = true;
                    }
                } catch (IllegalStateException e) {
                    //ok - just means the session has been invalidated
                }
            }
        }

        @Override
        public synchronized void putState(Object key, Object val) {
            if (map == null) {
                HttpSession hs = req.getSession();
                map = new HttpSessionStateMap(hs);
                registerSession();
            }
            try {
                map.putState(key, val);
            } catch (IllegalStateException ex) {
                if (!ex.getMessage().equals("Session already invalidated")) {
                    throw ex;
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public synchronized <DesiredType> DesiredType getState(Object key) {
            if (map == null) {
                HttpSession hs = req.getSession(false);
                if (hs == null) {
                    return null;
                } else {
                    map = new HttpSessionStateMap(hs);
                }
            }
            return (DesiredType) map.getState(key);
        }

        @Override
        public synchronized Object removeState(Object key) {
            if (map == null) {
                HttpSession hs = req.getSession(false);
                if (hs == null) {
                    return null;
                } else {
                    map = new HttpSessionStateMap(hs);
                }
            }
            return map.removeState(key);
        }

        @Override
        public synchronized Set getStateKeys() {
            if (map == null) {
                HttpSession hs = req.getSession(false);
                if (hs == null) {
                    return new HashSet();
                } else {
                    map = new HttpSessionStateMap(hs);
                }
            }
            return map.getStateKeys();
        }

        @Override
        public synchronized Map getStateStore() {
            if (map == null) {
                HttpSession hs = req.getSession(false);
                if (hs == null) {
                    return new HashMap();
                } else {
                    map = new HttpSessionStateMap(hs);
                }
            }
            return map.getStateStore();
        }

        @Override
        public synchronized void clearState() {
            if (map == null) {
                HttpSession hs = req.getSession(false);
                if (hs == null) {
                    return;
                } else {
                    map = new HttpSessionStateMap(hs);
                }
            }
            map.clearState();
        }

        public HttpSession getSession() {
            HttpSession hs = req.getSession();
            registerSession();
            return hs;
        }
    }
}
