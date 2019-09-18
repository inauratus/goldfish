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
 * $Id: DefaultEventGateway.java 268 2014-05-05 16:58:42Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation of an EventGateway. An event
 * gateway just indicates that the class has an interest in
 * events, and as such needs access to the EventBroker in 
 * order to register its listeners there.
 */
public class DefaultEventGateway implements EventGateway {

    private static byte[] sep = System.getProperty("line.separator").getBytes();
    /**
     * @supplierCardinality 0..* 
     */
    private EventGateway parent = null;
    private List<EventGateway> gateways = new ArrayList<>();
    private List<Interest> interests = null;
    private List<Class> aliases = null;

    /**
     * Set the parent event gateway. Null indicates its the root.
     *
     * @param eg the parent event gateway
     */
    @Override
    public void setParent(EventGateway eg) {
        parent = eg;
    }

    /**
     * Get the parent event gateway. Returns null if it's the root.
     *
     * @return the parent event gateway
     */
    @Override
    public EventGateway getParent() {
        return parent;
    }

    /**
     * Add an event gateway to this one
     *
     * @param eg the event gateway to be added
     */
    @Override
    public void add(EventGateway eg) {
        if (eg == null) {
            return;
        }
        gateways.add(eg);
        eg.setParent(this);
    }

    /**
     * Remove an event gateway from this one
     *
     * @param eg the event gateway to be removed
     */
    @Override
    public void remove(EventGateway eg) {
        if (eg == null) {
            return;
        }
        gateways.remove(eg);
        eg.setParent(null);
    }

    /**
     * Get a list of child gateways. The list returned is a copy of the
     * underlying child gateway list.
     *
     * @return a list of child gateways
     */
    @Override
    public List getChildren() {
        return new ArrayList<>(gateways);
    }

    /**
     * Ask all interested parties to register with
     * the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        register for events
     */
    @Override
    public void register(EventBroker eb) {
        //register any specified interests
        if (interests != null) {
            for (Interest interest : interests) {
                try {
                    if (interest.eventClass != null) {
                        eb.addEventListener(interest.factory, interest.eventClass);
                    } else {
                        eb.addEventListener(interest.factory);
                    }
                } catch (org.barracudamvc.core.event.InvalidClassException e) {
                }
            }
        }

        //register any specified aliases
        if (aliases != null) {
            for (Class event : aliases) {
                try {
                    eb.addEventAlias(event);
                } catch (org.barracudamvc.core.event.InvalidClassException e) {
                }
            }
            aliases = null;
        }

        registerLocalEventInterests(eb);
        registerLocalEventAliases(eb);
        for (EventGateway eg : gateways) {
            eg.register(eb);
        }
    }

    /**
     * Register any local interests in the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        register for local events
     */
    @Override
    public void registerLocalEventInterests(EventBroker eb) {
        //this is where users would extend and override
    }

    /**
     * Register any local event aliases in the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        register aliases for local events
     */
    @Override
    public void registerLocalEventAliases(EventBroker eb) {
        //this is where users would extend and override
    }

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
    @Override
    public void specifyLocalEventInterests(ListenerFactory factory) {
        specifyLocalEventInterests(factory, null);
    }

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
    @Override
    public void specifyLocalEventInterests(ListenerFactory factory, Class<? extends BaseEvent> event) {
        if (interests == null) {
            interests = new ArrayList<>();
        }
        interests.add(new Interest(factory, event));
    }

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
    @Override
    public void specifyLocalEventAliases(Class event) {
        if (aliases == null) {
            aliases = new ArrayList<>();
        }
        aliases.add(event);
    }

    @Override
    public void cleanUp() {
        Iterator<EventGateway> iterator = gateways.iterator();
        while (iterator.hasNext()){
            EventGateway gateway = iterator.next();
            gateway.cleanUp();
            iterator.remove();
        }

    }

    /**
     * Ask all interested parties to de-register with
     * the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        de-register for events
     */
    @Override
    public void deregister(EventBroker eb) {
        if (interests != null) {
            for (Interest interest : interests) {
                try {
                    if (interest.eventClass != null) {
                        eb.removeEventListener(interest.factory, interest.eventClass);
                    } else {
                        eb.removeEventListener(interest.factory);
                    }
                } catch (org.barracudamvc.core.event.InvalidClassException e) {
                }
            }
            interests = null;
        }
        deregisterLocalEventInterests(eb);

        for (EventGateway eg : gateways) {
            eg.deregister(eb);
        }
    }

    /**
     * Deregister any local interests in the EventBroker
     *
     * @param eb the event broker this gateway should use to 
     *        de-register for local events
     */
    @Override
    public void deregisterLocalEventInterests(EventBroker eb) {
        //this is where users would extend and override
    }

    class Interest {

        ListenerFactory factory = null;
        Class<? extends BaseEvent> eventClass = null;

        public Interest(ListenerFactory ifactory, Class<? extends BaseEvent> ieventClass) {
            factory = ifactory;
            eventClass = ieventClass;
        }

        @Override
        public String toString() {
            return "Interest {factory:" + factory + " event:" + eventClass + "}";
        }
    }
}
