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
 * $Id: ClientSideRedirectException.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;


/**
 * This class defines a ClientSideRedirectException...throwing this will
 * cause the ApplicationGateway to redirect the browser to the
 * new Event.
 */
public class ClientSideRedirectException extends EventException {

    //private vars
    private String url = null;

    /**
     * The public contructor for ClientSideRedirectException
     *
     * @param newEvent - the newEvent the client browser
     *        should be redirected to
     */
//TODO - I don't think we should actually be using a BaseEvent here in the constructor,
//since by default the ApplicationGateway will only dispatch HttpRequestEvents - csc
    public ClientSideRedirectException (BaseEvent newEvent) {
//csc_010404_1        setRedirectURL(newEvent.getEventIDWithExtension());
        setRedirectURL(newEvent.getEventURL()); //csc_010404_1
    }

    /**
     * The public contructor for ClientSideRedirectException
     *
     * @param iurl - the URL the client browser should be
     *         redirected to
     */
    public ClientSideRedirectException (String iurl) {
        setRedirectURL(iurl);
    }

    /**
     * Get the new event that triggered this interrupt
     */
    public String getRedirectURL() {
        return url;
    }

    //csc_082302.2 - added
    /**
     * Set the Redirect URL (normally you don't need to 
     * do this since you simply pass the target URL into the
     * constructor. If, howver, you find the need to modify a
     * redirect after its already been created, you can do it 
     * through this method) 
     */
    public void setRedirectURL(String iurl) {
        url = iurl;
    }

    public String toString() {
        return this.getClass().getName()+"(url="+url+")";
    }
}
