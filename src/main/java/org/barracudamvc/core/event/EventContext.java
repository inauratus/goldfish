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
 * $Id: EventContext.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.event;

import org.barracudamvc.core.view.ViewCapabilities;
import org.barracudamvc.plankton.data.StateMap;

/**
 * This interface defines the event context. The context 
 * contains information about the event (event, queue, 
 * plus request and response info if appropriate). The
 * context also implements StateMap, so you can use it
 * to pass information between event handlers. The scope 
 * of the context is only for the duration of an event
 * dispatch cycle (ie. from Http Request to Http Response)
 */
public interface EventContext extends StateMap {

    public static final String BASE_EVENT = EventContext.class.getName()+".BaseEvent";
    public static final String DISPATCH_QUEUE = EventContext.class.getName()+".DispatchQueue";
    public static final String VIEW_CAPABILITIES = EventContext.class.getName()+".ViewCapabilities";

    /**
     * Get the underlying BaseEvent
     *
     * @return the underlying BaseEvent
     */
    public BaseEvent getEvent();

    /**
     * Get the underlying DispatchQueue
     *
     * @return the underlying DispatchQueue
     */
    public DispatchQueue getQueue();

    /**
     * Get the underlying ViewCapabilities
     */
    public ViewCapabilities getViewCapabilities();
    
    /**
     * The event context must be able to persist its statemap so that it can 
     * reconstruct itself after a ClientSideRedirectException. Developers should
     * not normally need to call this method directly.
     */
    public void persistContext(ClientSideRedirectException re);
}
