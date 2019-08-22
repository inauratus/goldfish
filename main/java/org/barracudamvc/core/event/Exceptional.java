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
 * $Id: Exceptional.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This marker interface indicates an Event must be handled, and if
 * it's not, the parent event should be dispatched. This is 
 * kind of the opposite meaning of the Polymorphic interface.
 *
 * Events should never indicate both Exceptional and Polymorphic
 * (and can't because both of these interfaces define 
 * describeEventChainingStrategy())
 */
public interface Exceptional {
    /**
     * Describe the event chaining stategy. This method really
     * just serves to ensure that objects cannot implement BOTH
     * Polymorphic and Exceptional (it's got to be one or the other)
     *
     * @return string describing the event chain strategy
     */
    public String describeEventChainingStrategy();
}
