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
 * $Id: BaseEventListener.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;

import java.util.EventListener;

/**
 * This class defines the methods for Event listeners
 */
public interface BaseEventListener extends EventListener {

    /**
     * Handle all events. This represents a default implementation 
     * that will route the event on to the convenience respective 
     * convenience methods. Notice that we get servlet response and
     * request information from the queue state. This means that if
     * you use a custom event broker, it needs to set this information
     * in the queue.
     *
     * @param context the event context containing event, queue, and, sometimes, http information
     * @throws EventException
     */
    public void handleEvent(EventContext context) throws EventException;

    /**
     * Return true if the event was handled in the handleEvent method. 
     * By default, most implementations will return true for you, so 
     * the only time you'd need to override is if the handler wanted 
     * to specifically indicate that it had NOT handled the event 
     * (ie in logging scenarios).
     *
     * @return true if the event was handled in the handleEvent method
     */
    public boolean isHandled();

    /**
     * Get the ID that identifies this listener. This will typically be the 
     * class name.
     *
     * @return a string that uniquely identifies this listener
     */
    public String getListenerID();

}
