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
 * $Id: DefaultBaseEvent.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.plankton.data.StateMap;
import org.barracudamvc.plankton.http.HttpConverter;

/**
 * <p>This is the default implementation for the BaseEvent
 * interface. It acts as the base class in the event heirarchy.
 *
 * <p>While events are capable of carrying state information,
 * generally you want to keep them as lite as possible, storying
 * any state either in the dispatch queue or the user's session.
 *
 * <p>NOTE: As general good practice, you should try and provide
 * a no-args public constructor for all event classes. Anything
 * that implements Polymorphic or Exceptional MUST have a noargs
 * constructor, or the system will not be able to instantiate it.
 */
public abstract class DefaultBaseEvent implements BaseEvent {
    //constants

    public static boolean USE_ID_ALIASES = true;            //use id aliases (default setting)?
    //private vars
    protected Object source = null;                         //the source of the event
    protected String ext = ".event";                        //default event extension
    protected boolean handled = false;                      //has this event been handled
    protected List<String> idList = null;                           //list of IDs this event is specifically targeted towards
    protected long timestamp = -1;                          //last time this event was touched
    protected boolean useIDAliases = USE_ID_ALIASES;
    protected StateMap statemap = new DefaultStateMap();    //private property map
    protected Map<String, Object> params = null;                            //private parameter map //jbh_112202.1

    /**
     * Default noargs constructor
     */
    public DefaultBaseEvent() {
        super();
    }

    /**
     * Public constructor. Automatically sets parameters associated 
     * with the event with a URL string of the form "key1=val1&key2=val2&..."
     * (the param str may be prefixed by a '?')
     */
    public DefaultBaseEvent(String urlParamStr) {
        Map paramMap = HttpConverter.cvtURLStringToMap(urlParamStr);
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            this.setParam((String) me.getKey(), (String) me.getValue());
        }
    }

    /**
     * Public constructor. Automatically sets the source parameter.
     * If you do not use this method you should manually set the
     * source before dispatching the event.    
     */
    public DefaultBaseEvent(Object source) {
        setSource(source);
    }

    /**
     * set the source for an event
     *
     * @param isource the source for this event
     */
    @Override
    public final void setSource(Object isource) {
        source = isource;
    }

    /**
     * get the event source (may be null)
     *
     * @return the source for this event
     */
    @Override
    public Object getSource() {
        return source;
    }

    /**
     * get the root event source (may be null)
     *
     * @return the root source for this event
     */
    @Override
    public BaseEvent getRootEvent() {
        if (source != null && source instanceof BaseEvent) {
            return ((BaseEvent) source).getRootEvent();
        } else {
            return this;
        }
    }

    /**
     * set the event extension
     *
     * @param iext the target event extension
     */
    @Override
    public void setEventExtension(String iext) {
        ext = iext;
    }

    /**
     * get the event extension
     *
     * @return the target event extension
     */
    @Override
    public String getEventExtension() {
        return ext;
    }

    /**
     * determine whether or not we are using ID aliasing. This
     * can be used to override the default USE_ID_ALIASES
     * setting.
     *
     * @param val true if we want to use ID aliasing
     */
    public void setUseIDAliases(boolean val) {
        useIDAliases = val;
    }

    /**
     * see whether or not we are using ID aliasing
     *
     * @return true if we want to use ID aliasing
     */
    public boolean useIDAliases() {
        return useIDAliases;
    }

    /**
     * mark the event as handled/unhandled
     *
     * @param val true if the event is handled
     */
    @Override
    public void setHandled(boolean val) {
        handled = val;
    }

    /**
     * get the handled status for the event
     *
     * @return true if the event is handled
     */
    @Override
    public boolean isHandled() {
        return handled;
    }

    /**
     * Add a specific listener id this event should be delivered to.
     * Events can be targeted to more than one ID.
     *
     * @param id the Listener ID the event should target
     */
    @Override
    public void addListenerID(String id) {
        if (idList == null) {
            idList = new ArrayList<>();
        }
        idList.add(id);
    }

    /**
     * Get the list of id's this event is specifically targeted for.
     * May return null if there are none.
     *
     * @return a List of ID's this event is specifically targeting
     */
    @Override
    public List<String> getListenerIDs() {
        return idList;
    }

    /**
     * Get the ID that identifies this event. This will typically be the
     * class name.
     *
     * @return a string that uniquely identifies this event
     */
    @Override
    public String getEventID() {
        String idStr = this.getClass().getName();
        if (useIDAliases()) {
            int spos = idStr.lastIndexOf('.');
            if (spos > -1) {
                idStr = idStr.substring(spos + 1);
            }
        }
        return idStr;
    }

    /**
     * Get the ID that identifies this event, along with the event extension
     *
     * @deprecated csc010404_1; replaced by {@link #getEventURL}
     */
    @Override
    public String getEventIDWithExtension() {
        return (this.getEventID() + this.getEventExtension());
    }

    /**
     * Get the URL version of the event. This method will also include
     * any params associated with the event.
     *
     * @return the id and extension of this event
     */
    @Override
    public String getEventURL() {
        String url = this.getEventID() + this.getEventExtension();
        if (params != null) {
            url = url + "?" + HttpConverter.cvtMapToURLString(params);
        }
        return url;
    }

    /**
     * Get the timestamp
     *
     * @return the last time this event was touched
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Update the timestamp on the event
     */
    @Override
    public void touch() {
        timestamp = System.currentTimeMillis();
    }

    /**
     * Reset the event to it's default state
     */
    @Override
    public void reset() {
        source = null;
        ext = ".event";
        handled = false;
        idList = null;
        statemap = new DefaultStateMap();
        touch();
    }

    /**
     * Find the original event in target event's event chain (if it exists)
     *
     * @param e the target event
     * @return the original BaseEvent that caused the target event
     */
    public synchronized static BaseEvent getOriginalEvent(BaseEvent e) {
        Object source = e.getSource();
        if (source != null && source instanceof BaseEvent) {
            return getOriginalEvent((BaseEvent) source);
        } else {
            return e;
        }
    }

    /**
     * Events are generally considered equal if they are of the same class.
     * If you don't want this to be the case for some events (ie. if you
     * want to get every instance of an event through your dispatcher) then
     * you should override this method.
     *
     * @param o the object we are checking against for equality
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return (o == this || o.getClass().equals(this.getClass()));
    }

    /**
     * clone the event
     *
     * @return a copy of this event
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Get a class ID for a given class. The USE_ID_ALIASES
     * parameter is taken into account when generating the ID.
     *
     * @param cl the target class
     * @return the class ID for the target class
     */
    public static String getClassID(Class cl) {
        //eliminate the obvious
        if (cl == null) {
            return null;
        }

        String idStr = cl.getName();
        if (USE_ID_ALIASES) {
            int spos = idStr.lastIndexOf('.');
            if (spos > -1) {
                idStr = idStr.substring(spos + 1);
            }
        }
        return idStr;
    }

    /**
     * get the RootEvent that caused this event (if any). Will look at
     * the source object to see if it happens to be an instance of a
     * BaseEvent, and will recursively work deeper until it finds
     * the event which caused it all
     *
     * @param be a BaseEvent for which we wish to find the root event
     * @return the root BaseEvent in an event chain (may return null)
     */
    public synchronized static BaseEvent getRootEvent(BaseEvent be) {
        Object source = be.getSource();
        if (source != null && source instanceof BaseEvent) {
            return getRootEvent((BaseEvent) source);
        }
        return be;
    }

    //-------------------- StateMap ------------------------------
    /**
     * set a property in this StateMap
     *
     * @param key the state key object
     * @param val the state value object
     */
    @Override
    public void putState(Object key, Object val) {
        statemap.putState(key, val);
    }

    /**
     * get a property in this StateMap
     *
     * @param key the state key object
     * @return the value for the given key
     */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType extends Object> DesiredType getState(Object key) {
        return (DesiredType) statemap.getState(key);
    }

    /**
     * remove a property in this StateMap
     *
     * @param key the key object
     * @return the object which was removed
     */
    @Override
    public Object removeState(Object key) {
        return statemap.removeState(key);
    }

    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation)
     *
     * @return a Set of keys for this StateMap
     */
    @Override
    public Set getStateKeys() {
        return statemap.getStateKeys();
    }

    /**
     * get a copy of the underlying Map
     *
     * @return a copy of the underlying state Map
     */
    @Override
    public Map getStateStore() {
        return statemap.getStateStore();
    }

    //csc_052803_2 - added
    /**
     * clear all state information
     */
    @Override
    public void clearState() {
        statemap.clearState();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return (DesiredType) getState(key);
    }
    
    //jbh_112202.1_start
    //-------------------- Params --------------------------------
    /**
     * Set any associated params
     */
    @Override
    public final void setParam(String key, String val) {
        if (params == null) {
            params = new TreeMap<String, Object>();
        }
        params.put(key, val);
    }

    /**
     * Set any associated params
     */
    @Override
    public void setParam(String key, String[] val) {
        if (params == null) {
            params = new TreeMap<String, Object>();
        }
        params.put(key, val);
    }

    /**
     * Get any associated params
     */
    @Override
    public Map<String, Object> getParams() {
        return params;
    }
}
