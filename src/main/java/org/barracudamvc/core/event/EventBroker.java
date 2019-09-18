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
 * $Id: EventBroker.java 265 2014-02-21 17:40:07Z alci $
 */
package org.barracudamvc.core.event;

import java.util.List;

/**
 * This interface defines the methods needed to implement an EventBroker
 */
public interface EventBroker {

    /**
     * Return the event extension handled by this event broker
     *
     * @return the event extension associated with this broker
     */
    public String getEventExtension();

    /**
     * register a listener id, so that events addressed to a 
     * specific listener can be delivered
     *
     * @param factory the listener factory to be added
     */    
    public void addEventListener(ListenerFactory factory);

    /**
     * add an event listener for a particular class of an event.
     * If the class referenced is not an instance of BaseEvent,
     * a ClassNotEventException will be thrown. Note that this 
     * method also registers the listener in the idMap as well.
     *
     * @param factory the listener factory to be added
     * @param event the specific class of event for which the factory is listening
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */    
    public void addEventListener(ListenerFactory factory, Class<? extends BaseEvent> event) throws InvalidClassException;

    /**
     * remove a listener from general availability
     *
     * @param factory the listener factory to be removed
     */
    public void removeEventListener(ListenerFactory factory);

    /**
     * remove an event listener for specific types of events
     * If the class referenced is not an instance of BaseEvent,
     * a InvalidClassException will be thrown. Note that this 
     * method effectively deregisters the listener from the 
     * idMap as well.
     *
     * @param factory the listener factory to be removed
     * @param event the specific class of event for which the factory is listening
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */
    public void removeEventListener(ListenerFactory factory, Class<? extends BaseEvent> event) throws InvalidClassException;

    /**
     * remove all references to an event listener, both for id and
     * for any event classes it has registered an interest in.
     *
     * @param factory the listener factory to be removed
     */
    public void purgeEventListener(ListenerFactory factory);

    /**
     * Manually register aliases for a given event (the aliases
     * will be determined automatically based on the class name
     * and the event ID)
     *
     * @param event the specific class of event we'd like to alias
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */
    public void addEventAlias(Class event) throws InvalidClassException;
 
    /**
     * Manually add an alias for a given event. Note that
     * the alias parameter will be converted into all possible 
     * aliases based on '.' delimiters.
     *
     * @param event the specific class of event we'd like to alias
     * @param alias the alias for this event
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */
    public void addEventAlias(Class event, String alias) throws InvalidClassException;

    /**
     * Get a specific listener based on listener ID
     *
     * @param id the listener id we're looking for
     * @return the ListenerFactory that matches that id
     */
    public ListenerFactory getEventListener(String id);

    /**
     * Get a List of listeners for a type of event. Returns a copy 
     * of the broker's internal list, so you can do what you want 
     * with it. If the class referenced is not an instance of BaseEvent,
     * a InvalidClassException will be thrown. 
     *
     * @param event the event class we are looking for
     * @return a List of listeners that are interested in this class of event
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */
    public List<ListenerFactory> getEventListeners(Class<? extends BaseEvent> event) ;

    /**
     * Given a partial event class name, return the fully qualified class
     * name if it's possible to determine. If it is unknown, throw and
     * InvalidClassException. This method is primarily used by the 
     * ApplicationGateway to support event aliasing.
     *
     * @param eventStr the event name alias
     * @return the fully qualified event class name
     * @throws InvalidClassException if the eventStr cannot be unambiguously 
     *      matched to a class name
     */
    public String matchEventClass(String eventStr) throws InvalidClassException;

    /**
     * Given a partial id name, return the fully qualified listener
     * ID if it's possible to determine. If it is unknown, throw and
     * InvalidClassException. This method is primarily used by the 
     * ApplicationGateway to support id aliasing.
     *
     * @param idStr the id name alias
     * @return the fully qualified listener id name
     * @throws InvalidClassException if the idStr cannot be unambiguously 
     *      matched to a listener id name
     */
    public String matchListenerID(String idStr) throws InvalidClassException;


    /**
     * <p>Dispatch a queue of events. Generally, the queue will only
     * contain one event, however, if you ever need to dispatch
     * multiple events at once, the broker can handle it. All the real 
     * dispatching work is carried out by the underlying event
     * dispatcher.
     *
     * <p>The event queue you pass in should contain several pieces of 
     * state information:
     *
     * <ul>
     *    <li>DefaultEventDispatcher.DEFAULT_RESPONSE_EVENT (BaseEvent)</li>
     *    <li>DefaultBaseEventListener.HTTP_SERVLET_REQUEST (HttpServletRequest) - if dispatching from a servlet</li>
     *    <li>DefaultBaseEventListener.HTTP_SERVLET_RESPONSE (HttpServletResponse) - if dispatching from a servlet</li>
     * </ul>
     *
     * @param context the event context containing event, queue, and, sometimes, http information
     * @throws EventException
     */
    public void dispatchEvent(ViewEventContext context) throws EventException;

    public boolean willHandle(String urlTarget);

    public String findEventName(String urlTarget);

    public BaseEvent locateSourceEvent(String target);
}
