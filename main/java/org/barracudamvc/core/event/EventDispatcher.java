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
 * $Id: EventDispatcher.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This interface defines the methods needed to implement an 
 * EventDispatcher
 */
public interface EventDispatcher {

    /**
     * Dispatch a queue of events. The incoming queue must be an instance 
     * of DefaultDispatchQueue or an EventException will be thrown. This 
     * means that if you're going to provide a custom event broker, you
     * may need to provide a custom dispatcher as well.
     *
     * @param eb the event broker to be used to match events to listeners
     * @param context the event context containing event, queue, and, sometimes, http information
     * @throws EventException
     */
    public void dispatchEvent(EventBroker eb, EventContext context) throws EventException;

    /** @link dependency */
    /*#DispatchQueue lnkDispatchQueue;*/
}
