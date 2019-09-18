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
 * $Id: DefaultEventPool.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class acts as a pool for Events. Should significantly improve 
 * performance by allowing us to reuse event objects.
 */
public class DefaultEventPool implements EventPool {

    //public constants
    protected static final Logger logger = Logger.getLogger(DefaultEventPool.class.getName());
    //public vars 
    public static int DEFAULT_POOL_SIZE = 50;               //csc_060903_1
    public static long DEFAULT_TIMEOUT = 60000;             //csc_060903_1
    public static long DEFAULT_RETRY_INTERVAL = 50;         //csc_060903_1
    public static int DEFAULT_MAX_RETRIES = 3;              //csc_060903_1
    public static long DEFAULT_CLEANUP_INTERVAL = 600000;   //csc_060903_1
    //private vars
    protected Map<Class, EventList> eventMap = null;
    protected int poolSize = -1;
    protected long timeout = -1;
    protected long retryInterval = -1;
    protected int maxRetries = -1;
    protected long cleanupInterval = -1;
    protected long lastUpdate = -1;
    protected long lastCleanup = -1;
    protected Object sync = new Object();
    private Thread thread = null;
    private boolean stayAlive = true;

    /**
     * Default constructor. Defaults to pool size of 50 with
     * a timeout of 60 seconds. Retry interval of 50 millis, 
     * with max retries of 3. Cleanup thread runs every 10 
     * minutes.
     */
    public DefaultEventPool() {
//        this(50,15000, 50, 3, 90000);    //short version for testing purposes
//csc_060903_1        this(50,60000, 50, 3, 600000);
        this(DEFAULT_POOL_SIZE, DEFAULT_TIMEOUT, DEFAULT_RETRY_INTERVAL, DEFAULT_MAX_RETRIES, DEFAULT_CLEANUP_INTERVAL);    //csc_060903_1
    }

    /**
     * Public constructor. 
     *
     * @param ipoolSize - how many event instances to keep for each 
     *        class of event)
     * @param itimeout - how long may an event be checked out before
     *         it may be reclaimed by the pool
     * @param iretryInterval - how long between retries if there are no 
     *        events currently available
     * @param imaxRetries - maximum number of retries
     * @param icleanupInterval - how often should the cleanup thread run (millisecs).
     */
    public DefaultEventPool(int ipoolSize, long itimeout, long iretryInterval, int imaxRetries, long icleanupInterval) {
        //set up working vars
        eventMap = new HashMap<Class, EventList>();
        poolSize = ipoolSize;
        timeout = itimeout;
        retryInterval = iretryInterval;
        maxRetries = imaxRetries;
        cleanupInterval = icleanupInterval;
        if (logger.isInfoEnabled())
            logger.info("Instantiating EventPool:" + this + " Pool size:" + poolSize + " Timeout:" + timeout + " Retry Interval:" + retryInterval + " Max Retries:" + maxRetries + " Cleanup Interval:" + cleanupInterval);

        //start the cleanup thread
        thread = new Thread(new EventListCleanerUpper());
        thread.setName("Barracuda event pool cleanup thread");
        thread.start();
    }

    /**
     * check out an event from the EventPool.
     *
     * @param event the class of event we are interested in checking out
     * @return the checked out event
     * @throws NoAvailableEventsException if there are no available events in the queue
     * @throws InvalidClassException if the event class is not valid
     */
    @Override
    public BaseEvent checkoutEvent(Class event) throws NoAvailableEventsException, InvalidClassException {
        if (!((BaseEvent.class).isAssignableFrom(event)))
            throw new InvalidClassException("Class " + event.getName() + " is not a BaseEvent");

        //we catch NoAvailbleEventsExceptions at this level so as
        //to allow for an unlock on the sync object (thus allowing other
        //threads to check in events while we sleep).
        int cntr = 1;
        BaseEvent be = null;
        while (be == null) {
            try {
                synchronized (sync) {
                    EventList el = eventMap.get(event);
                    if (el == null) {
                        el = new EventList();
                        eventMap.put(event, el);
                    }
                    be = el.lock(event);
                }
            } catch (NoAvailableEventsException e) {
                //if we have to wait for more than 3 iterations (150 millisecs) return
                try {
                    if (++cntr > maxRetries) {
                        logger.warn("ALERT: EventPool timeout. You might want to consider upping your pool size to avoid this condition");
                        throw e;
                    }
                    if (logger.isDebugEnabled())
                        logger.debug("Waiting " + cntr + " for next available event " + event.getName());
                    Thread.yield();
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                }
            }
        }

        return be;
    }

    /**
     * check the event back in, allowing someone 
     * else to have access to it.
     *
     * @param event the event we're releasing our lock on
     */
    public void releaseEvent(BaseEvent event) {
        if (logger.isInfoEnabled())
            logger.info("Releasing event " + event);
        synchronized (sync) {
            EventList el = eventMap.get(event.getClass());
            if (el == null)
                return;
            el.release(event);
        }
    }

    /**
     * Cleanup any locked events which weren't released 
     * (they should all be). You should not ever really need
     * to run this method. It will get invoked automatically
     * when the cleaner-upper runs
     */
    public void cleanupLockedEvents() {
        lastCleanup = System.currentTimeMillis();
        if (logger.isInfoEnabled())
            logger.info("Cleaning up locked events @" + lastCleanup);
        synchronized (sync) {
            Iterator it = eventMap.values().iterator();
            while (it.hasNext()) {
                EventList el = (EventList) it.next();
                el.cleanup();
            }
        }
    }

    //lb_032801_start - Patch submitted by Larry Brasfield 
    //[larry.brasfield@theplatform.com] so JVM can exit once 
    //the servlet's destroy() method is called.  This method 
    //allows us to force the event pool cleanup thread to stop.
    //You can find related changes by searching for lb_032801
    /**
     * Shutdown the event pool
     */
    public void shutdown() {
        stayAlive = false;
        if (thread != null) {
            thread.interrupt();
            // Let it loose so it can be gc'ed.
            thread = null;
        }
    }
    //lb_032801_end

    /**
     * This inner class is used to store individual lists
     * of event instances
     */
    class EventList {

        String name = null;
        List<BaseEvent> freeList = new ArrayList<BaseEvent>(poolSize);
        List<BaseEvent> lockedList = new ArrayList<BaseEvent>(poolSize);

        /**
         * lock an event within the local event list
         *
         * @param event the class of event to get a lock on
         * @return a locked instance of the event
         */
        public BaseEvent lock(Class event) throws InvalidClassException, NoAvailableEventsException {
            //set the name
            if (name == null)
                name = event.getName();
            if (logger.isDebugEnabled())
                logger.debug("Attempting to lock event " + name);


            if (freeList.size() < 1 && lockedList.size() >= poolSize)
                throw new NoAvailableEventsException("No available events:" + event);

            //try and get the next available event
            BaseEvent be = null;
            if (freeList.size() > 0) {
                if (logger.isDebugEnabled())
                    logger.debug("Looking up next event " + name);
                be = (BaseEvent) freeList.get(0);
                freeList.remove(0);
            }

            //instantiate it if need be
            if (be == null) {
                if (logger.isDebugEnabled())
                    logger.debug("Instantiating event " + name);
                try {
                    //Note: the event pool assumes all event objects must have
                    //a noargs constructor
                    if (logger.isDebugEnabled())
                        logger.debug("Instantiating Event");
                    be = (BaseEvent) event.newInstance();
                } catch (Exception e) {
                    throw new InvalidClassException("Error instantiating event:" + event, e);
                }
            }

            //now move the event to the locked list, set the timestamp,
            //and return the event
            if (logger.isDebugEnabled())
                logger.debug("Locking event " + be + " in EventList: " + name);
            be.touch();
            lockedList.add(be);
            lastUpdate = be.getTimestamp();
            return be;
        }

        /**
         * release an event within the local event list
         *
         * @param be the locked event to be released
         */
        public void release(BaseEvent be) {
            if (logger.isDebugEnabled())
                logger.debug("Releasing event " + be + " in EventList: " + name);
            lockedList.remove(be);
            be.reset();
            freeList.add(be);
        }

        /**
         * Cleanup the locked list
         */
        public void cleanup() {
            if (logger.isDebugEnabled())
                logger.debug("Cleaning up EventList: " + name);
            boolean gotSome = false;
            Iterator it = lockedList.iterator();
            long curTime = System.currentTimeMillis();
            while (it.hasNext()) {
                BaseEvent be = (BaseEvent) it.next();
                if (be.getTimestamp() - curTime > timeout) {
                    if (logger.isDebugEnabled())
                        logger.debug("Forcing release for event:" + be);
                    release(be);
                    gotSome = true;
                }
            }
            if (logger.isDebugEnabled() && !gotSome)
                logger.debug("All was clean...no events needed to be released");
        }

        protected void finalize() {
            if (logger.isInfoEnabled())
                logger.info("Finalizing event pool...");
            stayAlive = false;

            //lb_032801_start - Patch submitted by Larry Brasfield 
            //[larry.brasfield@theplatform.com] so JVM can exit once 
            //the servlet's destroy() method is called. You can find 
            //related changes by searching for lb_032801
//            if (thread!=null) thread.interrupt();
            if (thread != null) {
                thread.interrupt();
                // Let it loose to break circular reference.
                thread = null;
            }
            //lb_032801_end
        }
    }

    /**
     * This inner class cleans up events which for some reason
     * weren't released.
     */
    class EventListCleanerUpper implements Runnable {

        public void run() {
            if (logger.isInfoEnabled())
                logger.info("Starting EventListCleanerUpper (ELCU)...");

            //loop infinitely
            while (stayAlive) {
                try {
                    //sleep for a while
                    if (logger.isDebugEnabled())
                        logger.debug("ELCU...Going to sleep");
                    Thread.yield();
                    Thread.sleep(cleanupInterval);

                    //see if anything has even changed since we last checked
                    if (logger.isDebugEnabled())
                        logger.debug("ELCU...Checking to see if cleanup necessary");
                    if (lastCleanup >= lastUpdate)
                        continue;

                    //run the check
                    if (logger.isDebugEnabled())
                        logger.debug("ELCU...Running cleanup");
                    cleanupLockedEvents();
                } catch (InterruptedException e) {
                }
            }
            if (logger.isInfoEnabled())
                logger.info("Shutting down ELCU...Goodbye.");
        }
    }
}
