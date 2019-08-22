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
 * $Id: DefaultBaseEventListener.java 254 2013-03-01 16:03:20Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 * <p>This class provides the default implementation for 
 * a BaseEventListener. This is an abstract class, meaning
 * you have to extend it if you intend to use it. Typically
 * you would implement it as an inner class and extend 
 * handleReqEvent, handleRespEvent, or handleOtherEvent. Or,
 * you can always just override handleEvent and do whatever
 * you want.
 */
public abstract class DefaultBaseEventListener implements BaseEventListener {

    //public vars
    protected static final Logger localLogger = Logger.getLogger(DefaultBaseEventListener.class.getName());
    //private vars
    protected String idStr = null;

    /**
     * Handle all events. This represents a default implementation 
     * that will route the event on to the convenience respective 
     * convenience methods. 
     *
     * @param context the EventContext for the base event we are handling
     * @throws EventException
     */
    @Override
    public void handleEvent(EventContext context) throws EventException {
        //custom code here
        if (context == null) {
            return;
        }
        BaseEvent event = context.getEvent();
        try {
            if (localLogger.isDebugEnabled()) {
                localLogger.debug("Entering " + this.getListenerID());
            }

            if (event instanceof ControlEvent && context instanceof ControlEventContext) {
                handleControlEvent((ControlEventContext) context);
            } else if (event instanceof ViewEvent && context instanceof ViewEventContext) {
                handleViewEvent((ViewEventContext) context);
            } else {
                handleOtherEvent(context);
            }

        } catch (ServletException e) {
            throw new EventException("Unexpected Servlet Error", e);
        } catch (IOException e) {
            throw new EventException("Unexpected IOException", e);
        } finally {
            event.setHandled(isHandled());
            if (localLogger.isDebugEnabled()) {
                localLogger.debug("Exiting " + this.getListenerID());
            }
        }
    }

    /**
     * Handle HttpRequestEvents
     *
     * @param context the ControlEventContext
     * @throws EventException
     * @throws ServletException
     * @throws IOException
     */
    public void handleControlEvent(ControlEventContext context) throws EventException, ServletException, IOException {
        //custom code here (we throw an exception so that if a developer fails 
        //to extend the proper method, they'll at least get a nice error message 
        //at runtime pointing out the problem. This should help prevent the case
        //where the developer writes handling code but extends the wrong handling 
        //method for the particular type of event being handled)
        String msg = "Invoked handleControlEvent()...this method should have been extended in [" + this.getClass() + "] ";
        localLogger.warn(msg);
        throw new UnhandledEventException(msg, context);
    }

    /**
     * Handle HttpResponseEvents
     *
     * @param context the ViewEventContext
     * @throws EventException
     * @throws ServletException
     * @throws IOException
     */
    public void handleViewEvent(ViewEventContext context) throws EventException, ServletException, IOException {
        //custom code here (we throw an exception so that if a developer fails 
        //to extend the proper method, they'll at least get a nice error message 
        //at runtime pointing out the problem. This should help prevent the case
        //where the developer writes handling code but extends the wrong handling 
        //method for the particular type of event being handled)
        String msg = "Invoked handleViewEvent()...this method should have been extended";
        localLogger.warn(msg);
        throw new UnhandledEventException(msg, context);
    }

    /**
     * Handle all Other events
     *
     * @param context the EventContext
     * @throws EventException
     */
    public void handleOtherEvent(EventContext context) throws EventException {
        //custom code here (we throw an exception so that if a developer fails 
        //to extend the proper method, they'll at least get a nice error message 
        //at runtime pointing out the problem. This should help prevent the case
        //where the developer writes handling code but extends the wrong handling 
        //method for the particular type of event being handled)
        String msg = "Invoked handleOtherEvent()...this method should have been extended";
        localLogger.warn(msg);
        throw new UnhandledEventException(msg, context);
    }

    /**
     * Return true if the event was handled in the handleEvent method. 
     * By default, most implementations will return true for you, so 
     * the only time you'd need to override is if the handler wanted 
     * to specifically indicate that it had NOT handled the event 
     * (ie in logging scenarios).
     *
     * @return true if the event was handled in the handleEvent method
     */
    @Override
    public boolean isHandled() {
        return true;
    }

    /**
     * Get the ID that identifies this listener. This will typically be the 
     * class name.
     *
     * @return a string that uniquely identifies this listener
     */
    @Override
    public String getListenerID() {
        if (idStr == null) {
            idStr = DefaultBaseEvent.getClassID(this.getClass());
        }
        return idStr;
    }
}
