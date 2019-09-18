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
 * $Id: EventForwardingFactory.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event.helper;

import org.apache.log4j.Logger;
import org.barracudamvc.core.event.BaseEvent;
import org.barracudamvc.core.event.BaseEventListener;
import org.barracudamvc.core.event.DefaultBaseEvent;
import org.barracudamvc.core.event.DefaultBaseEventListener;
import org.barracudamvc.core.event.DefaultListenerFactory;
import org.barracudamvc.core.event.DispatchQueue;
import org.barracudamvc.core.event.EventContext;
import org.barracudamvc.core.event.EventException;

/**
 * This class provides a simple factory that will handle
 * events by simply adding a new event to the queue, in
 * effect acting as an event forwarder. This type of functionality
 * is useful when you have request event handlers that simply 
 * forward flow onto response eventhandlers without any intermediary 
 * processing.
 */
public class EventForwardingFactory extends DefaultListenerFactory {

    //public constants
    protected static final Logger logger = Logger.getLogger(EventForwardingFactory.class.getName());

    //private vars
    protected String id = null;
    protected BaseEvent fevent = null;

    /**
     * Public constructor. Note that when actually forwarding the
     * event, a new instance will be generated via reflection.
     *
     * @param ifevent the event to be generated
     */
    public EventForwardingFactory (BaseEvent ifevent) {
        if (logger.isInfoEnabled()) logger.info("Creating new EventForwardingFactory -->"+ifevent.getClass().getName());
        fevent = ifevent;
    }

    /**
     * Get an instance of the underlying BaseEventListener
     *
     * @return get an instance of the BaseEventListener
     */
    public BaseEventListener getInstance() {
        return new EventHandler();
    }
    
    /**
     * Get the Listener ID associated with this class of listener. This will
     * generally either be the class name of the listener that the factory 
     * creates
     *
     * @return the listener ID that describes this factory
     */
    public String getListenerID() {
        return getID(fevent.getClass());
    }

    /**
     * EventHandler - 
     */
    class EventHandler extends DefaultBaseEventListener {

        protected String idStr = null;

        public void handleEvent(EventContext context) throws EventException {
            BaseEvent event = context.getEvent();
            DispatchQueue queue = context.getQueue();
            if (logger.isInfoEnabled()) logger.info("Got event:"+event);

            //in this case, we're not really doing anything special, so just
            //redirect to the RenderLogin view
            try {
                BaseEvent newEvent = (BaseEvent) fevent.getClass().newInstance();
                if (logger.isInfoEnabled()) logger.info("Forwarding to:"+newEvent);
                newEvent.setSource(event);
                queue.addEvent(newEvent);
                event.setHandled(true);
            } catch (Exception e) {
                throw new EventException("Error forwarding Event", e);
            }
        }
        
        /**
         * Get the ID that identifies this listener. This will typically be the 
         * class name.
         *
         * @return a string that uniquely identifies this listener
         */
        public String getListenerID() {
            if (idStr==null) {
                idStr = DefaultBaseEvent.getClassID(fevent.getClass());
            }
            return idStr;
        }
    }
}
