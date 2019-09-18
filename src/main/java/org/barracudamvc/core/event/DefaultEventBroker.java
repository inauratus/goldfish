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
 * $Id: DefaultEventBroker.java 265 2014-02-21 17:40:07Z alci $
 */
package org.barracudamvc.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.barracudamvc.plankton.Classes;

/**
 * <p>An EventBroker is responsible for two basic tasks:
 *
 * <ol>
 *    <li>it serves as a registry for listeners to express interest in
 *           in events (or to make themselves generally available)</li>
 *    <li>it serves as the central point in a system for dispatching events
 *          to interested parties.<li>
 * </ol>
 *
 * <p>Typically, only the ApplicationGateway will actually dispatch events 
 * using the event broker.
 *
 * <p>There are two ways to register for Events. One is to specify the
 * type of Event you're interested in. The other is simply to register
 * a listener for general availability...anything addressed to that
 * listener will be delivered. Note that when we're registering listeners,
 * we're really registering listener factories. This allows the broker
 * not to worry about syncronization issues, assuming that the factory
 * will take care of that detail.
 *
 * <p>Note that when adding listeners, the broker calculates aliases for
 * both listener IDs and events. This allows you to reference listeners
 * and events by using just the class name instead of having to use the
 * fully qualified class name. You can also add/remove aliases manually.
 * Note however, that aliases are never automatically removed by the system,
 * even when you remove listeners. This is because there is no way of knowing
 * how _many_ listeners correspond with a particular alias. So...we just leave 
 * the aliases registered. In practice, this shouldn't really impact things
 * as most listeners will only be deregistered at the end of the servlet 
 * lifecycle.
 *
 * <p>When there is more than one id for a given alias, the broker treats
 * it as an ambiguous case and returns no match for that particular alias.
 *
 * <p>The event extension is used by whatever instantiates the broker (usually
 * an ApplicationGateway) to define the particular event extension this
 * broker is handling).
 */
public class DefaultEventBroker implements EventBroker {

    protected Map<String, ListenerFactory> listenerFactories = new HashMap<>();
    protected Map<Class<? extends BaseEvent>, List<ListenerFactory>> eventListenerFactories = new HashMap<>();
    protected Map<String, Object> idXref = new HashMap<>();
    protected Map<String, Object> eventXref = new HashMap<>();
    protected String extension = null;
    protected DispatcherFactory dispatcherFactory = null;

    /**
     * Public constructor
     *
     * @param idispatcherFactory a reference to the dispatcher factory 
     *        to be used in creating dispatchers
     * @param iextension the extension to be associated with all events
     *        delivered through the dispatcher
     */
    public DefaultEventBroker(DispatcherFactory idispatcherFactory, String iextension) {
        dispatcherFactory = idispatcherFactory;
        if (dispatcherFactory == null) {
            dispatcherFactory = new DefaultDispatcherFactory();
        }
        extension = iextension;
    }

    /**
     * Return the event extension handled by this event broker
     *
     * @return the event extension associated with this broker
     */
    @Override
    public String getEventExtension() {
        return extension;
    }

    /**
     * register a listener id, so that events addressed to a 
     * specific listener can be delivered
     *
     * @param factory the listener factory to be added
     */
    @Override
    public void addEventListener(ListenerFactory factory) {
        //eliminate the obvious
        if (factory == null) {
            return;
        }
        String lid = factory.getListenerID();

        //add it to the idMap
        listenerFactories.put(lid, factory);

        //get the aliases and add it to the id xref
        List<String> lAliases = getAliases(lid);
        addAliases(lid, lAliases, idXref);
    }

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
    @Override
    public void addEventListener(ListenerFactory factory, Class<? extends BaseEvent> event) throws InvalidClassException {
        //eliminate the obvious
        if (factory == null || event == null) {
            return;
        }
        //add it to the eventMap
        List<ListenerFactory> eventList = eventListenerFactories.get(event);
        if (eventList == null) {
            eventList = new ArrayList<>();
            eventListenerFactories.put(event, eventList);
        }
        eventList.add(factory);

        //get the event aliases and add it to the event xref
        String eid = event.getName();
        addAliases(eid, getAliases(eid), eventXref);

        //make sure it's also in the idMap
        String lid = factory.getListenerID();
        if (listenerFactories.get(lid) == null) {
            listenerFactories.put(lid, factory);
        }

        //get the aliases and add it to the id xref
        List<String> lAliases = getAliases(lid);
        addAliases(lid, lAliases, idXref);
    }

    /**
     * remove a listener from general availability
     *
     * @param factory the listener factory to be removed
     */
    @Override
    public void removeEventListener(ListenerFactory factory) {
        //eliminate the obvious
        if (factory == null) {
            return;
        }
        String lid = factory.getListenerID();

        //remove it from the idMap
        listenerFactories.remove(lid);
    }

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
    @Override
    public void removeEventListener(ListenerFactory factory, Class<? extends BaseEvent> event) throws InvalidClassException {
        //eliminate the obvious
        if (factory == null || event == null) {
            return;
        }

        //remove it from the eventMap
        List<ListenerFactory> eventList = eventListenerFactories.get(event);
        if (eventList != null) {
            while (eventList.contains(factory)) {
                eventList.remove(factory);
            }
            if (eventList.size() < 1) {
                eventListenerFactories.remove(event);
            }
        }

        //make sure we also remove it from the idMap
        listenerFactories.remove(factory.getListenerID());
    }

    /**
     * remove all references to an event listener, both for id and
     * for any event classes it has registered an interest in.
     *
     * @param factory the listener factory to be removed
     */
    @Override
    public void purgeEventListener(ListenerFactory factory) {
        //eliminate the obvious
        if (factory == null) {
            return;
        }

        //remove it from the idMap
        String lid = factory.getListenerID();
        listenerFactories.remove(lid);

        //remove it from all lists in the eventMap
        Iterator<Map.Entry<Class<? extends BaseEvent>, List<ListenerFactory>>> it = eventListenerFactories.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Class<? extends BaseEvent>, List<ListenerFactory>> entry = it.next();

            List eventList = entry.getValue();
            while (eventList.contains(factory)) {
                eventList.remove(factory);
            }
            if (eventList.size() < 1) {
                it.remove();
            }
        }
    }

    /**
     * Get a specific listener based on listener ID
     *
     * @param id the listener id we're looking for
     * @return the ListenerFactory that matches that id
     */
    @Override
    public ListenerFactory getEventListener(String id) {
        if (id == null) {
            return null;
        }

        //return a reference if it exists
        return (ListenerFactory) listenerFactories.get(id);
    }

    /**
     * Get a List of listeners for a type of event. Returns a copy 
     * of the broker's internal list, so you can do what you want 
     * with it. If the class referenced is not an instance of BaseEvent,
     * a InvalidClassException will be thrown. 
     *
     * @param event the event class we are looking for
     * @return a List of listeners that are interested in this class of event
     */
    @Override
    public List<ListenerFactory> getEventListeners(Class<? extends BaseEvent> event) {
        if (event == null) {
            return Collections.emptyList();
        }
        final List<ListenerFactory> listeners = eventListenerFactories.get(event);
        return listeners == null ? Collections.<ListenerFactory>emptyList() : listeners;
    }

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
    @Override
    public String matchEventClass(String eventStr) throws InvalidClassException {
        if (eventStr == null) {
            throw new InvalidClassException();
        }
        Object result = eventXref.get(eventStr.toLowerCase());
        if (result == null || result instanceof List) {
            throw new InvalidClassException();
        } else {
            return (String) result;
        }
    }

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
    @Override
    public String matchListenerID(String idStr) throws InvalidClassException {
        if (idStr == null) {
            throw new InvalidClassException();
        }
        Object result = idXref.get(idStr.toLowerCase());
        if (result == null || result instanceof List) {
            throw new InvalidClassException();
        } else {
            return (String) result;
        }
    }

    /**
     * The purpose of this method is to take a fully qualified
     * class name and return a list of aliases for it. For instance,
     * foo.blah.event.Test would generate the following aliases:
     * <ul>
     *   <li>foo.blah.event.Test</li>
     *   <li>blah.event.Test</li>
     *   <li>event.Test</li>
     *   <li>Test</li>
     * </ul>
     */
    protected List<String> getAliases(String className) {
        List<String> list = new ArrayList<>();

        //start by appending the full class name
        list.add(className);

        //now break it into pieces
        int spos = 0;
        while ((spos = className.indexOf('.', spos)) > -1) {
            String alias = className.substring(spos + 1);
            list.add(alias);
            spos += 1;
        }

        return list;
    }

    /**
     * Manually register aliases for a given event (the aliases
     * will be determined automatically based on the class name
     * and the event ID)
     *
     * @param event the specific class of event we'd like to alias
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */
    @Override
    public void addEventAlias(Class event) throws InvalidClassException {
        if (event == null) {
            return;
        }
        if (!((BaseEvent.class).isAssignableFrom(event))) {
            throw new InvalidClassException("Class " + event.getName() + " is not a BaseEvent");
        }

        //start by aliasing off the class name
        addEventAlias(event, event.getName());

        //if the event id is different than the class name alias off that too
        try {
            BaseEvent be = (BaseEvent) event.newInstance();
            if (!be.getEventID().equals(event.getName())) {
                addEventAlias(event, be.getEventID());
            }
        } catch (Exception e) {
        }
    }

    /**
     * Manually add an alias for a given event. Note that
     * the alias parameter will be converted into all possible 
     * aliases based on '.' delimiters.
     *
     * @param event the specific class of event we'd like to alias
     * @param alias the alias for this event
     * @throws InvalidClassException if the event class does not implement BaseEvent
     */
    @Override
    public void addEventAlias(Class event, String alias) throws InvalidClassException {
        if (event == null || alias == null) {
            return;
        }
        if (!((BaseEvent.class).isAssignableFrom(event))) {
            throw new InvalidClassException("Class " + event.getName() + " is not a BaseEvent");
        }
        List<String> lAliases = getAliases(alias);
        addAliases(event.getName(), lAliases, eventXref);
    }

    /**
     * Given an id and a list of aliases, add them to the 
     * specified xref
     */
    @SuppressWarnings("unchecked")
    protected void addAliases(String id, List<String> aliases, Map<String, Object> xref) {
        for (String alias : aliases) {
            alias = alias.toLowerCase();
            Object ref = xref.get(alias);

            //if it's not, just add it
            if (ref == null) {
                xref.put(alias, id);
            } else {
                //if the ref is not a list, create one and add it to the list
                List<Object> list;
                if (ref instanceof List) {
                    list = (List<Object>) ref;
                } else {
                    //first make sure this isn't a dupe (if it is, just continue)
                    if (id.equals(ref)) {
                        continue;
                    }

                    //go ahead and create the list
                    list = new ArrayList<>();
                    list.add(ref);
                    xref.put(alias, list);
                }

                //now add the reference to the list if the id is not
                //already in there (this prevents dupes)
                if (!list.contains(id)) {
                    list.add(id);
                }
            }
        }
    }

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
     * @param context the EventContext to be dispatched
     * @throws EventException
     */
    @Override
    public void dispatchEvent(ViewEventContext context) throws EventException {
        if (context == null) {
            return;
        }
        
        String[] ids = context.getRequest().getParameterValues(BaseEvent.EVENT_ID);
        if (ids != null) {
            for (String id : ids) {
                try {
                    context.getEvent().addListenerID(matchListenerID(id));
                } catch (InvalidClassException e) {
                }
            }
        }
        
        dispatcherFactory.getInstance().dispatchEvent(this, context);
    }

    public String findEventName(String target) {
        if (target == null) {
            return null;
        }

        int spos = target.lastIndexOf("/");
        if (spos > -1) {
            target = target.substring(spos + 1);
        }

        spos = target.lastIndexOf("\\");
        if (spos > -1) {
            target = target.substring(spos + 1);
        }
        if (target.endsWith(getEventExtension())) {
            target = target.substring(0, target.length() - getEventExtension().length());
        }
        try {
            String fullEventName = matchEventClass(target);
            if (fullEventName != null) {
                target = fullEventName;
            }
        } catch (InvalidClassException e) {
        }
        return target;
    }
    
    public BaseEvent locateSourceEvent(String target) {
        if(target == null) {
            return null;
        }
        if(listenerFactories.containsKey(target)) {
            HttpRequestEvent psuedoControl = new HttpRequestEvent() {};
            psuedoControl.addListenerID(target);
            return psuedoControl;
        }
        
        Class cl = Classes.getClass(target);
        if (cl == null) {
            return null;
        }
        try {
            return (BaseEvent) cl.newInstance();
        } catch (IllegalAccessException | InstantiationException ex) {
            return null;
        }
    }

    public boolean willHandle(String urlTarget) {
        return urlTarget.endsWith(getEventExtension());
    }
}
