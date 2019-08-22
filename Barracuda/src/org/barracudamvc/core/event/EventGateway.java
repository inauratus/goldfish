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
 * $Id: EventGateway.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.List;

/**
 * An EventGateway simply represents a gateway to a set of event 
 * handlers within a system. 
 *
 * Gateways are heirarchical in that they may contain other 
 * EventGateways, and may have a parent gateway. Invoking register 
 * on a gateway should cause it to 
 *
 * a) register all known enties that are interested in receiving 
 *    events from the EventBroker
 * b) invoke register for any gateways it contains
 *
 * Invoking deregister has the opposite effect.
 */
public interface EventGateway {

    /**
     * Set the parent event gateway. Null indicates its the root.
     *
     * @param eg the parent event gateway
     */
    public void setParent(EventGateway eg);

    /**
     * Get the parent event gateway. Returns null if it's the root.
     *
     * @return the parent event gateway
     */
    public EventGateway getParent();

    /**
     * Add an event gateway to this one
     *
     * @param eg the event gateway to be added
     */
    public void add(EventGateway eg);

    /**
     * Remove an event gateway from this one
     *
     * @param eg the event gateway to be removed
     */
    public void remove(EventGateway eg);

    /**
     * Get a list of child gateways
     *
     * @return a list of child gateways
     */
    public List getChildren();

    /**
     * Ask all interested parties to register with
     * the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        register for events
     */
    public void register(EventBroker eb);

    /**
     * Ask all interested parties to de-register with
     * the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        de-register for events
     */
    public void deregister(EventBroker eb);

    /**
     * Register any local interests in the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        register for local events
     */
    public void registerLocalEventInterests(EventBroker eb);

    /**
     * Deregister any local interests in the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        de-register for local events
     */
    public void deregisterLocalEventInterests(EventBroker eb);

    /**
     * Register any local event aliases in the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        register aliases for local events
     */
    public void registerLocalEventAliases(EventBroker eb);

    /**
     * Rather than overriding the registerLocalEventInterests 
     * method, you can just invoke this method instead for each 
     * interest you'd like to register. The gateway will keep track
     * of all factories specified, and register/deregister when 
     * appropriate (so you don't have to worry about it). Notice that
     * this method registers just the listener id (not for a specific
     * class of event).
     *
     * The only real reason for using the registerLocalEventInterests 
     * method would be if you actually need access to the EventBroker.
     *
     * Note that if the event class is not an instance of BaseEvent, the
     * request is just silently ignored (unlike the event broker, which 
     * throws an exception).
     *
     * @param factory the factory we wish to register with the event broker
     */
    public void specifyLocalEventInterests(ListenerFactory factory);

    /**
     * Rather than overriding the registerLocalEventInterests 
     * method, you can just invoke this method instead for each 
     * interest you'd like to register. The gateway will keep track
     * of all factories specified, and register/deregister when 
     * appropriate (so you don't have to worry about it). 
     *
     * The only real reason for using the registerLocalEventInterests 
     * method would be if you actually need access to the EventBroker.
     *
     * Note that if the event class is not an instance of BaseEvent, the
     * request is just silently ignored (unlike the event broker, which 
     * throws an exception).
     *
     * @param factory the factory we wish to register with the event broker
     * @param event the class of events we are interested in
     */
    public void specifyLocalEventInterests(ListenerFactory factory, Class<? extends BaseEvent> event);

    /**
     * Rather than overriding the registerLocalEventAliases 
     * method, you can just invoke this method instead for type
     * of event you want to manually alias.
     *
     * The only real reason for using the registerLocalEventAliases 
     * method would be if you actually need access to the EventBroker.
     *
     * Note that if the event class is not an instance of BaseEvent, the
     * request is just silently ignored (unlike the event broker, which 
     * throws an exception).
     *
     * @param event the class of events we are interested in registering 
     *        aliases for
     */
    public void specifyLocalEventAliases(Class event);
    
    void cleanUp();

}
