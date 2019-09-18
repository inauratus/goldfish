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
 * $Id: DispatchQueue.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.List;

/**
 * This interface defines the methods needed to implement a
 * DispatchQueue
 */
public interface DispatchQueue  { //extends StateMap {

    /**
     * Does this queue require a response?
     *
     * @return true if this queue requires a response
     */    
    public boolean requiresResponse();

    /**
     * Programatically tell the dispatcher that a response is required
     *
     * @param true if a response is required
     */    
    public void setRequiresResponse(boolean val);

    /**
     * Has the response for this queue been handled?
     *
     * @return true if the response has been handled
     */    
    public boolean responseHandled();

    /**
     * Programatically tell the dispatcher that the response has been handled
     *
     * @param true if the response has been handled
     */    
    public void setResponseHandled(boolean val);

    /**
     * Adds an event to the queue. When this happens, we first remove
     * any existing events in the queue that this event .equals(), and
     * then this event is added. This has the effect of collapsing 
     * duplicates.
     *
     * @param baseEvent the event to be added to the queue
     */
    public void addEvent(BaseEvent baseEvent);

    /**
     * get the number of events remaining in the queue
     *
     * @return the number of events remaining in the queue
     */
    public int numberOfEventsRemaining();

    /**
     * List all events remaining in the queue (Request events first, then
     * Response events)
     *
     * @return a list of all events remaining in the queue
     */
    public List listRemainingEvents();

    /**
     * get the number of events that have been processed
     *
     * @return the number of events processed in the queue
     */
    public int numberOfEventsProcessed();

    /**
     * List events which have already been processed through the queue
     *
     * @return a list of all events processed in the queue
     */
    public List listProcessedEvents();

    /**
     * Indicates if the queue contains any more control events.
     * 
     * @return <tt>true</tt> if the queue contains any control events that have
     * not been processed
     */
    public boolean hasNextControlEvent();
    
    /**
     * Indicates if the queue contains any more view events.
     * @return <tt>true</tt> if the queue contains any view events that have
     * not been processed
     */
    public boolean hasNextViewEvent();
    
    /**
     * Mark all events as handled
     */
    public void markEventsHandled();
    
    public void removeUnprocessedEvents();
}
