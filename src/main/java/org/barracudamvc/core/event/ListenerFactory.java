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
 * $Id: ListenerFactory.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This interface defines the methods needed to be an 
 * ListenerFactory. 
 * 
 * Note that a factory is responsible for ensuring threadsafety
 * within the underlying listener, so the factory should either 
 * return a new instance of the listener OR keep one static 
 * synchronized instance and return that instead.
 */
public interface ListenerFactory {

    /**
     * Get an instance of the underlying BaseEventListener
     *
     * @return get an instance of the BaseEventListener
     */
    public BaseEventListener getInstance();
    
    /**
     * Return true if you want to always be notified, even when an event has 
     * been handled by someone else. Return false (default) when you only want 
     * to notified if the event hasn't been handled already. 
     *
     * Typically, only logging type listeners would return true.
     *
     * @return return true to be notified of the event even if it's already 
     *        been handled
     */
    public boolean notifyAlways();

    /**
     * Get the Listener ID associated with this class of listener. This will
     * generally either be the class name of the listener that the factory 
     * creates
     *
     * @return the listener ID that describes this factory
     */
    public String getListenerID();

}
