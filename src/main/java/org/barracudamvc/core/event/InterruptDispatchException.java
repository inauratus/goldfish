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
 * $Id: InterruptDispatchException.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;

/**
 * This class defines a InterruptDispatchException...it is used to
 * interrupt the dispatch, flag all events currently in the queue 
 * as handled, and then redispatch the new event
 */
public class InterruptDispatchException extends EventException {
    private BaseEvent newEvent = null;

    /**
     * The public contructor for InterruptDispatchException
     *
     * @param inewEvent the event we want to dispatch instead
     */
    public InterruptDispatchException (BaseEvent inewEvent) {
        this(null, inewEvent);
    }

    /**
     * The public contructor for InterruptDispatchException
     *
     * @param s a String describing the exception
     * @param inewEvent the event we want to dispatch instead
     */
    public InterruptDispatchException (String s, BaseEvent inewEvent) {
        this(s, inewEvent, null);
    }

    /**
     * The public contructor for InterruptDispatchException
     *
     * @param s a String describing the exception
     * @param inewEvent the event we want to dispatch instead
     * @param ibaseException the root exception behind this error
     */
    public InterruptDispatchException (String s, BaseEvent inewEvent, Exception ibaseException) {
        super(s, ibaseException);
        newEvent = inewEvent;
    }
    
    /**
     * Get the new event that triggered this interrupt
     *
     * @return the event we wish to fire instead
     */
    public BaseEvent getNewEvent() {
        return newEvent;
    }
}
