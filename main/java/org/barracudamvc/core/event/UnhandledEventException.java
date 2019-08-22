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
 * $Id: UnhandledEventException.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;

/**
 * This class defines a UnhandledEventException...it gets
 * thrown when there there are event handlers but none of them
 * actually handle it
 */
public class UnhandledEventException extends EventException {
    EventContext context = null;

    /**
     * The public contructor for UnhandledEventException
     *
     * @param s a String describing the exception
     * @param icontext the EventContext associated with this exception
     */
    public UnhandledEventException (String s, EventContext icontext) {
        super(s);
        context = icontext;
    }

    /**
     * Get the dispatch queue associated with this exception
     *
     * @return the EventContext associated with this exception
     */    
    public EventContext getEventContext() {
        return context;
    }
}
