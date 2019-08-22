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
 * $Id: ControlEvent.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This defines a basic event, representing some kind of
 * Control function (it's basically just saying "Hey something 
 * happened, tell everyone who cares").
 *
 * Eventually, there may be several kinds of Control events,
 * but right now, HttpRequestEvent is the only kind that
 * extends it.
 */
public abstract class ControlEvent extends DefaultBaseEvent {
    /**
     * Default noargs constructor
     */
    public ControlEvent() {super();}

    /**
     * Public constructor. Automatically sets parameters associated 
     * with the event with a URL string of the form "key1=val1&key2=val2&..."
     * (the param str may be prefixed by a '?')
     */
    public ControlEvent(String urlParamStr) {
        super(urlParamStr);
    }    

    /**
     * Public constructor. Automatically sets the source parameter.
     * If you do not use this method you should manually set the
     * source before dispatching the event.     
     */
    public ControlEvent(Object source) {
        super(source);
    }
    
}
