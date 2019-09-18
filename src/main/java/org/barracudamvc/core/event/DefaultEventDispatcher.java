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
 * $Id: DefaultEventDispatcher.java 264 2013-11-07 20:42:49Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.barracudamvc.core.helper.servlet.BarracudaServletRequestWrapper;

/**
 * <p>This class is responsible for dispatching a queue of events
 * to interested listeners. If you ever want to create a custom
 * dispatching policy, this is the class to extend.
 *
 * <p>This particular implementation takes a repeating two phased
 * dispatch approach in order to provide a Model 2 flow control
 * mechanism. In order to pull this off, the incoming DispatchQueue
 * must be an instance of DefaultDispatchQueue. If it is not, an
 * EventException will be thrown.
 *
 * <p>Basically, we dispatch all the non-Response events in the
 * queue first. Listeners which handle these events loosely correspond
 * to controllers. Then, we dispatch all the Response events. Listeners
 * that handle these loosely correspond to views. If a response is required
 * and there is no response event, we look to the queue state to locate
 * a DEFAULT_RESPONSE_EVENT, which defaults to HttpResponseEvent. To
 * implement a single phase dispatch, just define the queue as not
 * requiring a response and avoid adding Response events to it.
 *
 * <p>Note that when we are dispatching, if we encounter an event that
 * implements Polymorphic, we create and dispatch parent events first.
 * If we enouncter an event that implements Exceptional and that event
 * is NOT handled, then we add it's parent event to the queue.
 *
 * <p>Note that the getEventChain method could probably be better optimized...
 */
public class DefaultEventDispatcher implements EventDispatcher {

    //public constants
    public final static String DEFAULT_RESPONSE_EVENT = "DefaultEventDispatcher.DefaultResponseEvent"; //(BaseEvent)
    public static int MAX_DISPATCH_QUEUE_DEPTH = 35;    //this prevents infinite looping
    protected static final Logger logger = Logger.getLogger(DefaultEventDispatcher.class.getName());
    //private vars
    private static int REQ_PHASE = 0;
    private static int RESP_PHASE = 1;

    /**
     * <p>Dispatch a queue of events. The incoming queue must be an instance
     * of DefaultDispatchQueue or an EventException will be thrown. This
     * means that if you're going to provide a custom event broker, you
     * may need to provide a custom dispatcher as well.
     *
     * <p>Note: this is really a repeating 2 Phase approach -- we will continue
     * to do dispatch until there are no remaining unhandled events in the queue.
     * This allows response event handlers to add non-response events back into
     * the queue (ie. for logging purposes, etc) and we are guaranteed that
     * they will get dispatched (barring an exception getting thrown)
     *
     * @param eb the event broker to be used to match events to listeners
     * @param context the event context for this whole dispatch cycle
     * @throws EventException
     */
    @Override
    public void dispatchEvent(EventBroker eb, EventContext context) throws EventException {
        // make sure the incoming queue is an instance of DefaultDispatchQueue
        DispatchQueue ieventQueue = context.getQueue();
        if (!(ieventQueue instanceof DefaultDispatchQueue)) {
            throw new EventException("DispatchQueue is not an instance of DefaultDispatchQueue");
        }
        DefaultDispatchQueue eventQueue = (DefaultDispatchQueue) ieventQueue;
        eventQueue.setResponseHandled(false);

        // recursive 2 Phase dispatch loop
        while (eventQueue.hasNextControlEvent() || eventQueue.hasNextViewEvent()) {
            //dispatch the request phase
            dispatch(REQ_PHASE, eb, context, eventQueue);

            // check to see if default response event is needed
            if (eventQueue.requiresResponse()
                    && !eventQueue.responseHandled()
                    && eventQueue.peekNextViewEvent() == null) {
                BaseEvent defaultResponseEvent = (BaseEvent) context.getState(DEFAULT_RESPONSE_EVENT);
                eventQueue.addEvent(defaultResponseEvent);
            }

            // dispatch the response phase
            dispatch(RESP_PHASE, eb, context, eventQueue);

            // if the queue required a response and we didn't get one, throw
            // an UnhandledEventException
            if (eventQueue.requiresResponse() && !eventQueue.responseHandled()) {
                throw new UnhandledEventException(
                        "Unhandled Event - a reponse was required but no "
                        + "response event was handled!", context);
            }
        }
    }

    /**
     * Dispatch a queue of events
     *
     * @param dispatchPhase an internal flag to determine whether we're in REQ or RESP phase
     * @param eb the event broker to be used to match events to listeners
     * @param context the event context for this whole dispatch cycle
     * @param eventQueue the default dispatch queue
     * @throws EventException
     */
    protected void dispatch(int dispatchPhase, EventBroker eventBroker, EventContext context, DefaultDispatchQueue eventQueue) throws EventException {
        String ext = eventBroker.getEventExtension();

        //dispatch all the request events in the queue:
        //iterate through all events. Once an event is handled, all
        //subsequent events in the queue will be marked as handled,
        //meaning only this listeners with NotifyAlways=true will still
        //get invoked.
        while (hasMoreEvents(dispatchPhase, eventQueue)) {
            //get the next event
            BaseEvent event = getNextEvent(dispatchPhase, eventQueue);
            event.setEventExtension(ext);

            try {
                notifyListeners(event, findListeners(event, eventBroker), context);
            } catch (InterruptDispatchException e) {
                eventQueue.markEventsHandled();
                eventQueue.addEvent(e.getNewEvent());
            }

            // if we're in the response phase and the event was handled,
            // update the responseHandled flag in the queue
            if (dispatchPhase == RESP_PHASE && event.isHandled()) {
                eventQueue.setResponseHandled(true);
            }

            // if the primary event implements Exceptional and was not handled, 
            // add it's parent event to the queue...
            if (!event.isHandled() && event instanceof Exceptional) {
                try {
                    BaseEvent newEvent = (BaseEvent) event.getClass().getSuperclass().newInstance();
                    newEvent.setSource(event);
                    eventQueue.addEvent(newEvent);
                } catch (InstantiationException ie) {
                    throw new EventException("Error instantiating parent event:" + ie + " child: " + event + " was handled " + event.isHandled(), ie);
                } catch (IllegalAccessException iae) {
                    throw new EventException("Error instantiating parent event:" + iae, iae);
                }
            }

            if (eventQueue.numberOfEventsProcessed() > MAX_DISPATCH_QUEUE_DEPTH) {
                throw new UnhandledEventException("Max Dispatch Queue Depth exceeded...could indicate a recursive dispatch problem", context);
            }
        }
    }

    private BaseEvent getNextEvent(int dispatchPhase, DefaultDispatchQueue eventQueue) {
        return dispatchPhase == RESP_PHASE ? eventQueue.getNextViewEvent() : eventQueue.getNextControlEvent();
    }

    private static boolean hasMoreEvents(int dispatchPhase, DefaultDispatchQueue eventQueue) {
        return dispatchPhase == RESP_PHASE ? eventQueue.hasNextViewEvent() : eventQueue.hasNextControlEvent();
    }


    /**
     * Find the listeners for an event
     *
     * @param event the base event for which we're trying to find listeners
     * @param eb the event broker to use when looking up listeners
     */
    @SuppressWarnings("unchecked")
    protected List<ListenerFactory> findListeners(BaseEvent event, EventBroker eb) {
        //see if this event is targeted for a specific group of listeners
        List<ListenerFactory> factoryList = null;
        List<String> listenerIDs = event.getListenerIDs();
        if (listenerIDs != null && !listenerIDs.isEmpty()) {
            factoryList = new ArrayList<>();
            for (String id : listenerIDs) {
                ListenerFactory factory = eb.getEventListener(id);
                if (factory != null) {
                    factoryList.add(factory);
                }
            }
        }

        //if not, get all listeners for this particular event
        if (factoryList == null || factoryList.isEmpty()) {
            factoryList = eb.getEventListeners((Class<BaseEvent>) event.getClass());
        }

        return factoryList;
    }

    /**
     * Actually dispatch the specific event to the list of
     * listener factories
     *
     * @param event the event to be dispatched
     * @param list the list of listeners to be notified for this particular 
     * event
     * @param context the event context containing event, queue, and, sometimes,
     * http information
     * @throws EventException
     */
    protected void notifyListeners(BaseEvent event, List<ListenerFactory> listenerFactories, EventContext context) throws EventException {
        //make sure we have listeners
        if (listenerFactories == null || listenerFactories.size() < 1) {
            return;
        }

        if (context instanceof ControlEventContext) {
            HttpServletRequest req = ((ControlEventContext) context).getRequest();
            Map<String, Object> params = event.getParams();

            if (req instanceof BarracudaServletRequestWrapper && params != null) {
                BarracudaServletRequestWrapper wreq = (BarracudaServletRequestWrapper) req;
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    String key = param.getKey();
                    Object val = param.getValue();
                    if (val instanceof String[]) {
                        for (String string : (String[]) val) {
                            wreq.addParameter(key, string);
                        }
                    } else {
                        wreq.addParameter(key, val.toString());
                    }
                }
            }
        }

        //set the current event in the context (this ensures that the
        //listener always receives the correct event)
        context.putState(EventContext.BASE_EVENT, event);

        for (ListenerFactory factory : listenerFactories) {
            //see if it even needs to be notified
            if (event.isHandled() && !factory.notifyAlways()) {
                continue;
            }

            //get the listener
            BaseEventListener listener = factory.getInstance();

            //dispatch the event to the listener
            listener.handleEvent(context);
        }
    }
}
