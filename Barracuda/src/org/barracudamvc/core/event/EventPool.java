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
 * $Id: EventPool.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This indterface defines the methods needed to implement an 
 * EventPool
 */
public interface EventPool {

    /**
     * check out an event from the EventPool.
     *
     * @param event the class of event we are interested in checking out
     * @return the checked out event
     * @throws NoAvailableEventsException if there are no available events in the queue
     * @throws InvalidClassException if the event class is not valid
     */    
    public BaseEvent checkoutEvent(Class event) throws NoAvailableEventsException, InvalidClassException;

    /**
     * check the event back in, allowing someone 
     * else to have access to it.
     *
     * @param event the event we're releasing our lock on
     */    
    public void releaseEvent(BaseEvent event);

    /**
     * Cleanup any locked events which weren't released 
     * (they should all be). You should not ever really need
     * to run this method. It will get invoked automatically
     * when the cleaner-upper runs
     */
    public void cleanupLockedEvents();

    //lb_032801 - Added for patch submitted by Larry Brasfield 
    //[larry.brasfield@theplatform.com] so JVM can exit once 
    //the servlet's destroy() method is called.   
    /**
     * Shutdown the event pool
     */
    public void shutdown();
}
