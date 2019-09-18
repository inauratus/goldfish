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
 * $Id: ViewEvent.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This defines a basic View event, indicating some kind of
 * response/view must be generated. By default, ViewEvents implement
 * Exceptional and therefore must be handled. The world is made up of
 * ViewEvents and eveything else.
 *
 * Eventually, there may be several kinds of View events,
 * but right now, HttpResponseEvent is the only kind that
 * extends it.
 */
public abstract class ViewEvent extends DefaultBaseEvent {
    /**
     * Default noargs constructor
     */
    public ViewEvent() {super();}

    /**
     * Public constructor. Automatically sets parameters associated 
     * with the event with a URL string of the form "key1=val1&key2=val2&..."
     * (the param str may be prefixed by a '?')
     */
    public ViewEvent(String urlParamStr) {
        super(urlParamStr);
    }    

    /**
     * Public constructor. Automatically sets the source parameter.
     * If you do not use this method you should manually set the
     * source before dispatching the event.     
     */
    public ViewEvent(Object source) {
        super(source);
    }
}
