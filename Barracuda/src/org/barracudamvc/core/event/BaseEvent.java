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
 * $Id: BaseEvent.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.List;
import java.util.Map;
import org.barracudamvc.plankton.data.StateMap;

/**
 * This interface defines the methods needed to implement
 * a BaseEvent
 */
public interface BaseEvent extends StateMap, Cloneable {
    public static String EVENT_ID = "$eid";             //useful constant

    /**
     * set the source for an event
     *
     * @param isource the source for this event
     */
    public void setSource(Object isource);
    
    /**
     * get the event source (may be null)
     *
     * @return the source for this event
     */
    public Object getSource();

    /**
     * get the root event source (may be null)
     *
     * @return the root source for this event
     */
    public BaseEvent getRootEvent();

    /**
     * set the event extension
     *
     * @param iext the target event extension
     */    
    public void setEventExtension(String iext);

    /**
     * get the event extension
     *
     * @return the target event extension
     */    
    public String getEventExtension();

    /**
     * Set any associated params
     */
    public void setParam(String key, String val);

    /**
     * Set any associated params
     */
    public void setParam(String key, String[] val);

    /**
     * Get any associated params
     */
    public Map<String, Object> getParams();
    
    /**
     * mark the event as handled/unhandled
     *
     * @param val true if the event is handled
     */    
    public void setHandled(boolean val);
    
    /**
     * get the handled status for the event
     *
     * @return true if the event is handled
     */    
    public boolean isHandled();

    /**
     * Add a specific listener id this event should be delivered to.
     * Events can be targeted to more than one ID.
     *
     * @param id the Listener ID the event should target
     */        
    public void addListenerID(String id);

    /**
     * Get the list of id's this event is specifically targeted for.
     * May return null if there are none.
     *
     * @return a List of ID's this event is specifically targeting
     */        
    public List<String> getListenerIDs();

    /**
     * Get the ID that identifies this event. This will typically be the
     * class name.
     *
     * @return a string that uniquely identifies this event
     */
    public String getEventID();

    /**
     * Get the ID that identifies this event, along with the event extension
     *
     * @deprecated csc010404_1; replaced by {@link #getEventURL}
     */
    public String getEventIDWithExtension();

    /**
     * Get the URL version of the event. This method will also include
     * any params associated with the event.
     *
     * @return the id and extension of this event
     */
    public String getEventURL();    //csc_010404_1

    /**
     * Get the timestamp
     *
     * @return the last time this event was touched
     */
    public long getTimestamp();

    /**
     * Update the timestamp on the event
     */
    public void touch();

    /**
     * Reset the event to it's default state
     */    
    public void reset();
}
