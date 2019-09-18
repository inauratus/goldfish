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
 * $Id: Locales.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.l10n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.barracudamvc.core.event.ControlEventContext;
import org.barracudamvc.core.event.EventContext;
import org.barracudamvc.core.event.ViewEventContext;


/**
 * Simple locale utilities. This class makes it easy to determine the target
 * locale from an event context or a servlet request. You can also set the locale
 * and ask the class to save the information for you (in a cookie and/or the
 * session) so that the locale info will persist across muliple requests
 */
public class Locales extends org.barracudamvc.plankton.l10n.Locales {

    protected static final Logger logger = Logger.getLogger(Locales.class.getName());

    /**
     * Get the client locale from an EventContext using the default
     * param keys and persist option. This will probably be the method
     * you use most frequently.
     *
     * @param ec the EventContext from which we'd like to determine Locale
     * @return the target client Locale
     */
    public static Locale getClientLocale(EventContext ec) {
        return getClientLocale(ec, LANGAUGE_KEY, COUNTRY_KEY, VARIANT_KEY, PERSIST_DEFAULT);
    }

    /**
     * Get the client locale from an EventContext
     *
     * @param ec the EventContext from which we'd like to determine Locale
     * @param languageKey the key to be used to look in the request for a
     *        language paramter
     * @param countryKey the key to be used to look in the request for a
     *        country paramter
     * @param variantKey the key to be used to look in the request for a
     *        variant paramter
     * @param persistOption how we'd like to persist the Locale (by default, it will be stored
     *        in the SESSION)
     * @return the target client Locale
     */
    public static Locale getClientLocale(EventContext ec, String languageKey, String countryKey, String variantKey, int persistOption) {
        HttpServletRequest req = (HttpServletRequest) ec.getState(ControlEventContext.HTTP_SERVLET_REQUEST);
        HttpServletResponse resp = (HttpServletResponse) ec.getState(ViewEventContext.HTTP_SERVLET_RESPONSE);
        return getClientLocale(req, resp, languageKey, countryKey, variantKey, persistOption);
    }

    /**
     * Save the client locale using an EventContext using the default
     * persist option
     *
     * @param ec the EventContext in which we'd like to set Locale
     * @param loc the target client locale we'd like to set
     */
    public static void saveClientLocale(EventContext ec, Locale loc) {
        saveClientLocale(ec, loc, PERSIST_DEFAULT);
    }

    /**
     * Save the client locale using an EventContext
     *
     * @param ec the EventContext in which we'd like to set Locale
     * @param loc the target client locale we'd like to set
     * @param persistOption the specific persistOption to be used
     */
    public static void saveClientLocale(EventContext ec, Locale loc, int persistOption) {
        HttpServletRequest req = (HttpServletRequest) ec.getState(ControlEventContext.HTTP_SERVLET_REQUEST);
        HttpServletResponse resp = (HttpServletResponse) ec.getState(ViewEventContext.HTTP_SERVLET_RESPONSE);
        saveClientLocale(req, resp, loc, persistOption);
    }

    /**
     * Release the client locale using an EventContext (this effectively
     * removes it from whereever it might have been persisted). This means that
     * on the next request the locale will be determined from scratch again.
     *
     * @param ec the EventContext in which we'd like to set Locale (this tells us
     *        where the locale info needs to be removed from)
     */
    public static void releaseClientLocale(EventContext ec) {
        releaseClientLocale(ec, PERSIST_DEFAULT);
    }

    /**
     * Release the client locale using an EventContext (this effectively
     * removes it from whereever it might have been persisted). This means that
     * on the next request the locale will be determined from scratch again.
     *
     * @param ec the EventContext in which we'd like to set Locale
     * @param persistOption the specific persistOption to be used (this tells us
     *        where the locale info needs to be removed from)
     */
    public static void releaseClientLocale(EventContext ec, int persistOption) {
        HttpServletRequest req = (HttpServletRequest) ec.getState(ControlEventContext.HTTP_SERVLET_REQUEST);
        HttpServletResponse resp = (HttpServletResponse) ec.getState(ViewEventContext.HTTP_SERVLET_RESPONSE);
        releaseClientLocale(req, resp);
    }
}
