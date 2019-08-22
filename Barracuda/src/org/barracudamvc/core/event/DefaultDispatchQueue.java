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
 * $Id: DefaultDispatchQueue.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>The DispatchQueue as a relatively shortlived structure...the 
 * Queue would be created by the dispatcher, and it would be discarded 
 * when the events have been delivered. It should be made available to 
 * the listeners, however, so that they can add events to the queue 
 * if they'd like. 
 *
 * <p>While the DispatchQueue interface views the queue as one list of events,
 * the DefaultDispatchQueue is designed to be aware of two different kinds
 * of events: Control events, and View events. This corresponds nicely
 * with both the Http Req/Response model and the Model 2 Controller/View
 * distinction.
 *
 * <p>This means that when you add an event to the queue, internally gets 
 * filtered into either a control list or a view list. Consequently,
 * when iterating through the queue, you will always get all control events
 * first, then you will get all view events. The DefaultEventDispatcher
 * makes use of this distinction to implement a 2 phase event dispatch:
 * request events are dispatched first, followed by response events.
 *
 * <p>The queue can also be used for a single phased dispatch by setting 
 * requiresResponse = false.
 *
 * <p>The queue is smart enough so that as you add events to it, duplicate 
 * events are collapsed together and redundancies are eliminated (ie. if 
 * I add 3 RepaintView events, they would be combined into one, which appears
 * at the end of the queue).
 *
 * <p>As a final note, observe that not all of the methods are public. This 
 * is to limit BaseEventListener access: listeners add events to the queue,
 * see what events are in the queue, and even mark them handled. But they
 * cannot actually remove events from the queue. That duty belongs to the 
 * Event Dispatcher, which of necessity must be in the same package as
 * the DispatchQueue in order to be able to access all methods.
 */
public class DefaultDispatchQueue implements DispatchQueue {

    public static int MAX_POLY_CHAIN_DEPTH = 15;
    protected boolean requiresResponse = false;
    protected boolean responseHandled = false;
    protected List<BaseEvent> controlList = new ArrayList<>();
    protected List<ViewEvent> viewList = new ArrayList<>();
    protected List<BaseEvent> procList = new ArrayList<>();

    /**
     * Public noargs constructor. A queue must be defined as requiring a
     * response or not. This value can not be changed during the life of
     * the queue. Typically, any queue created to service an HTTP request
     * will set this value to true.
     *
     * @param irequiresResponse true if this queue requires a response
     */
    public DefaultDispatchQueue(boolean irequiresResponse) {
        setRequiresResponse(irequiresResponse);
    }

    /**
     * Does this queue require a response?
     *
     * @return true if this queue requires a response
     */
    @Override
    public boolean requiresResponse() {
        return requiresResponse;
    }

    //csc_011504_1 - added
    /**
     * Programatically tell the dispatcher that a response is required
     *
     * @param true if a response is required
     */
    @Override
    public final void setRequiresResponse(boolean val) {
        requiresResponse = val;
    }

    /**
     * Has the response for this queue been handled?
     *
     * @return true if the response has been handled
     */
    @Override
    public boolean responseHandled() {
        return responseHandled;
    }

    /**
     * Mark the response as handled
     *
     * @param val whether or not the response has been handled
     */
    @Override
    public void setResponseHandled(boolean val) {
        responseHandled = val;
    }

    /**
     * Removes all events from the current dispatch queue
     */
    void resetQueue() {
        controlList = new ArrayList<>();
        viewList = new ArrayList<>();
        procList = new ArrayList<>();
    }

    /**
     * Adds an event to the queue. When this happens, we first remove
     * any existing events in the queue that this event .equals(), and
     * then this event is added. This has the effect of collapsing 
     * duplicates.
     *
     * @param baseEvent the event to be added to the queue
     */
    @Override
    public void addEvent(BaseEvent baseEvent) {
        if (baseEvent == null) {
            return;
        }
        for (BaseEvent event : expandEvent(baseEvent)) {
            add(event);
        }
    }

    private void add(BaseEvent event) {
        removeEvent(event);
        if (event instanceof ViewEvent) {
            viewList.add((ViewEvent) event);
        } else {
            controlList.add(event);
        }
    }

    private List<BaseEvent> expandEvent(BaseEvent baseEvent) {
        List<BaseEvent> list = new ArrayList<>();
        //add the initial event
        list.add(baseEvent);
        if (baseEvent.isHandled()) {
            //if the event has been handled, ignore Polymorphic connotations
            return list;
        }
        BaseEvent curEvent = baseEvent;
        Class superclazz = curEvent.getClass().getSuperclass();
        int cntr = 0;
        while ((Polymorphic.class.isAssignableFrom(superclazz))
                && (++cntr < MAX_POLY_CHAIN_DEPTH)) {
            //get the parent event
            BaseEvent parentEvent = null;
            try {
                parentEvent = (BaseEvent) superclazz.newInstance();
                parentEvent.setSource(curEvent);
            } catch (InstantiationException | IllegalAccessException ie) {
                throw new IllegalStateException(ie);
            }

            //insert it at the beginning of the list
            list.add(0, parentEvent);

            //keep a reference to the cur event
            curEvent = parentEvent;
            superclazz = curEvent.getClass().getSuperclass();
        }
        return list;
    }

    /**
     * Remove all instances of an event from the queue that the 
     * incoming event .equals()
     *
     * @param baseEvent the event to be removed from the queue
     */
    void removeEvent(BaseEvent baseEvent) {
        if (baseEvent == null) {
            return;
        }
        List list = (baseEvent instanceof ViewEvent ? viewList : controlList);
        Object items[] = list.toArray();
        for (int max = items.length, i = max - 1; i > -1; i--) {
            if (baseEvent.equals(items[i])) {
                list.remove(i);
            }
        }
    }

    @Override
    public boolean hasNextControlEvent() {
        return (controlList.size() > 0);
    }

    @Override
    public boolean hasNextViewEvent() {
        return (viewList.size() > 0);
    }

    /**
     * Take a peek at the next ControlEvent without actually removing it
     *
     * @return the next control event in the queue
     */
    BaseEvent peekNextControlEvent() {
        return (hasNextControlEvent() ? (BaseEvent) controlList.get(0) : null);
    }

    /**
     * Take a peek at the next ViewEvent without actually removing it
     *
     * @return the next view event in the queue
     */
    ViewEvent peekNextViewEvent() {
        return (hasNextViewEvent() ? (ViewEvent) viewList.get(0) : null);
    }

    /**
     * Get the next ControlEvent, effectively removing it from the queue
     *
     * @return the next control event in the queue
     */
    BaseEvent getNextControlEvent() {
        BaseEvent controlEvent = peekNextControlEvent();
        controlList.remove(0);
        procList.add(controlEvent);
        return controlEvent;
    }

    /**
     * Get the next ViewEvent, effectively removing it from the queue
     *
     * @return the next view event in the queue
     */
    ViewEvent getNextViewEvent() {
        ViewEvent viewEvent = peekNextViewEvent();
        viewList.remove(0);
        procList.add(viewEvent);
        return viewEvent;
    }

    /**
     * get the number of events remaining in the queue
     *
     * @return the number of events remaining in the queue
     */
    @Override
    public int numberOfEventsRemaining() {
        return controlList.size() + viewList.size();
    }

    /**
     * List all events remaining in the queue (Request events first, then
     * Response events)
     *
     * @return a list of all events remaining in the queue
     */
    @Override
    public List listRemainingEvents() {
        ArrayList<BaseEvent> list = new ArrayList<>(controlList);
        list.addAll(viewList);
        return list;
    }

    /**
     * List the ControlEvents
     *
     * @return a list of all request events remaining in the queue
     */
    List<BaseEvent> listControlEvents() {
        return new ArrayList<>(controlList);
    }

    List<BaseEvent> listViewEvents() {
        return new ArrayList<BaseEvent>(viewList);
    }

    @Override
    public int numberOfEventsProcessed() {
        return procList.size();
    }

    @Override
    public List<BaseEvent> listProcessedEvents() {
        return new ArrayList<>(procList);
    }

    @Override
    public void markEventsHandled() {
        markHandled(controlList);
        markHandled(viewList);
    }

    private void markHandled(List<? extends BaseEvent> eventList) {
        for (BaseEvent be : eventList) {
            be.setHandled(true);
        }
    }

    public void removeUnprocessedEvents() {
        controlList.clear();
        viewList.clear();
    }
}
