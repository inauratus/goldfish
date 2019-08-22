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
package org.barracudamvc.plankton.l10n;

import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.http.SessionServices;

/**
 * Simple locale utilities. This class makes it easy to determine the target
 * locale from an event context or a servlet request. You can also set the locale
 * and ask the class to save the information for you (in a cookie and/or the
 * session) so that the locale info will persist across muliple requests
 */
public class Locales {

    public static String LANGAUGE_KEY = "$loc_lang";
    public static String COUNTRY_KEY = "$loc_cntry";
    public static String VARIANT_KEY = "$loc_var";

    public static final int NONE = 0;
    public static final int SESSION = 1;
    public static final int COOKIE = 2;
    public static final int COOKIES_AND_SESSION = 3;

    public static int PERSIST_DEFAULT = SESSION;

    protected static final Logger logger = Logger.getLogger(Locales.class.getName());
    private static final String LOCALE = Localize.class.getName() + ".Locale";
    private static final String COOKIE_PART_SEPARATOR = "%3B";

    /**
     * Get the client locale from a ServletRequest using the default
     * param keys and persist option
     *
     * @param req  the HttpServletRequest from which we'd like to determine Locale
     * @param resp the HttpServletResponse (needed if we want to save Locale in a cookie
     *             otherwise it may be null)
     * @return the target client Locale
     */
    public static Locale getClientLocale(HttpServletRequest req, HttpServletResponse resp) {
        return getClientLocale(req, resp, LANGAUGE_KEY, COUNTRY_KEY, VARIANT_KEY, PERSIST_DEFAULT);
    }

    /**
     * This method attempts to get the client locale.
     *
     * <p>First we look to see if we can determine the locale by the req's
     * form parameters (language, country, variant). If not, we look in
     * the session to see if we can get the information from there. If
     * that fails, we see if we can get the information from a client
     * cookie (where the value is a comma delimited string containing
     * language, country, and variant. If we still haven't got the locale
     * information, we try and retrieve it from the servlet request, and
     * if that fails, we use the default locale.
     *
     * <p>In each of these cases we save (depending on persistOption) the
     * locale info in both the session and in a client cookie (so as to
     * speed lookups on subsequent requests).
     *
     * @param req           the HttpServletRequest from which we'd like to determine Locale
     * @param resp          the HttpServletResponse (needed if we want to save Locale in a cookie
     *                      otherwise it may be null)
     * @param languageKey   the key to be used to look in the request for a
     *                      language paramter
     * @param countryKey    the key to be used to look in the request for a
     *                      country paramter
     * @param variantKey    the key to be used to look in the request for a
     *                      variant paramter
     * @param persistOption how we'd like to persist the Locale (by default, it will be stored
     *                      in the SESSION)
     * @return the target client Locale
     */
    public static Locale getClientLocale(HttpServletRequest req, HttpServletResponse resp, String languageKey, String countryKey, String variantKey, int persistOption) {
        Locale locale = findLocaleInRequest(req, languageKey, countryKey, variantKey);

        if (locale == null)
            locale = Locale.getDefault();

        saveClientLocale(req, resp, locale, persistOption);

        return locale;
    }

    private static Locale findLocaleInRequest(HttpServletRequest req, String languageKey, String countryKey, String variantKey) {
        if (req != null) {
            Locale locale = findLocaleInRequestParams(req, languageKey, countryKey, variantKey);
            if (locale != null)
                return locale;
            locale = findLocateInSession(req);
            if (locale != null)
                return locale;
            locale = getLocaleFromCookie(req);
            if (locale != null)
                return locale;
            return req.getLocale();
        }
        return null;
    }

    private static Locale findLocateInSession(HttpServletRequest req) {
        HttpSession session = SessionServices.getSession(req);
        if (session != null) {
            try {
                return (Locale) session.getAttribute(LOCALE);
            } catch (IllegalStateException ex) {
                // no-op; this will happen when the session is invalidated
            }
        }
        return null;
    }

    private static Locale getLocaleFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(LOCALE)) {
                    return readLocaleFromCookie(cookie);
                }
            }
        }
        return null;
    }

    private static Locale findLocaleInRequestParams(HttpServletRequest req, String languageKey, String countryKey, String variantKey) {
        //first look for manual form parameters; if we find it here
        //be sure to store the locale both as a cookie and in the session
        String language = req.getParameter(languageKey);
        String country = req.getParameter(countryKey);
        String variant = req.getParameter(variantKey);
        if (language != null) {
            return getLocale(language, country, variant);
        }
        return null;
    }

    private static Locale getLocale(String language, String country, String variant) {
        Locale locale = null;
        if (language != null) {
            if (country == null)
                country = "";
            if (variant != null)
                locale = new Locale(language, country, variant);
            else
                locale = new Locale(language, country);
        }
        return locale;
    }


    private static Locale readLocaleFromCookie(Cookie cookie) {
        String value = cookie.getValue();
        String language = null;
        String country = null;
        String variant = null;
        StringTokenizer st = new StringTokenizer(value, COOKIE_PART_SEPARATOR);
        if (st.hasMoreTokens())
            language = st.nextToken();
        if (st.hasMoreTokens())
            country = st.nextToken();
        if (st.hasMoreTokens())
            variant = st.nextToken();

        return getLocale(language, country, variant);
    }

    private static void writeLocaleInSession(HttpServletRequest req, Locale locale) {
        if (req == null)
            return;
        try {
            HttpSession session = SessionServices.getSession(req);
            if (locale != null)
                session.setAttribute(LOCALE, locale);
            else
                session.removeAttribute(LOCALE);
        } catch (IllegalStateException ex) {
            // no-op; nothing to do in this case
        }
    }

    private static void writeLocaleInCookie(HttpServletResponse resp, Locale locale) {
        if (resp == null) return;
        String value = null;
        if (locale != null)
            value = locale.getLanguage()
                    + COOKIE_PART_SEPARATOR
                    + locale.getCountry()
                    + COOKIE_PART_SEPARATOR
                    + locale.getVariant();
        Cookie cookie = new Cookie(LOCALE, value);
        cookie.setMaxAge(locale != null ? Integer.MAX_VALUE : 0);
        resp.addCookie(cookie);
    }

    /**
     * Here we actually force a locale to be saved using the default
     * persist option. It will be stored in a cookie and/or the session.
     *
     * @param req  the HttpServletRequest (needed to get the HttpSession)
     * @param resp the HttpServletResponse (needed if we want to save Locale
     *             in a cookie otherwise it may be null)
     * @param loc  the target client locale we'd like to set
     */
    public static void saveClientLocale(HttpServletRequest req, HttpServletResponse resp, Locale loc) {
        saveClientLocale(req, resp, loc, PERSIST_DEFAULT);
    }

    /**
     * Here we actually force a locale to be saved. It will be stored in
     * a cookie and/or the session.
     *
     * @param req           the HttpServletRequest (needed to get the HttpSession)
     * @param resp          the HttpServletResponse (needed if we want to save Locale
     *                      in a cookie otherwise it may be null)
     * @param loc           the target client locale we'd like to set
     * @param persistOption the specific persistOption to be used
     */
    public static void saveClientLocale(HttpServletRequest req, HttpServletResponse resp, Locale loc, int persistOption) {
        if (persistOption == SESSION || persistOption == COOKIES_AND_SESSION)
            writeLocaleInSession(req, loc);

        if (persistOption == COOKIE || persistOption == COOKIES_AND_SESSION)
            writeLocaleInCookie(resp, loc);
    }

    /**
     * Release a client locale (this effectively removes it from whereever
     * it might have been persisted)
     *
     * @param req  the HttpServletRequest (needed to get the HttpSession)
     * @param resp the HttpServletResponse (needed if we want to clear Locale
     *             from a cookie otherwise it may be null)
     */
    public static void releaseClientLocale(HttpServletRequest req, HttpServletResponse resp) {
        releaseClientLocale(req, resp, PERSIST_DEFAULT);
    }

    /**
     * Release a client locale (this effectively removes it from whereever
     * it might have been persisted)
     *
     * @param req           the HttpServletRequest (needed to get the HttpSession)
     * @param resp          the HttpServletResponse (needed if we want to clear Locale
     *                      from a cookie otherwise it may be null)
     * @param persistOption the specific persistOption to be used (this tells us
     *                      where the locale info needs to be removed from)
     */
    public static void releaseClientLocale(HttpServletRequest req, HttpServletResponse resp, int persistOption) {
        saveClientLocale(req, resp, null, persistOption);
    }

    /**
     * This utility function will run through a list of Locales
     * and return the index of the locale that matches most closely.
     * If none of them match whatsoever, return the defaultIndex
     *
     * @param targetLocale the target locale
     * @param locales      the array of locales to search
     * @param defaultIndex the default index (if no match is found)
     * @return the index of the closest matching locale
     */
    public static int findClosestLocale(Locale targetLocale, Locale[] locales, int defaultIndex) {
        int match2 = -1;
        int match1 = -1;

        for (int i = 0, max = locales.length; i < max; i++) {
            //if we find a direct match (language, country, variant) return immediately
            if (targetLocale.equals(locales[i])) return i;

            //if we can match on 2, save a reference to the index
            if (targetLocale.getLanguage().equals(locales[i].getLanguage()) &&
                    targetLocale.getCountry().equals(locales[i].getCountry()))
                match2 = i;

            //if we just match on language, save a reference to the index
            if (targetLocale.getLanguage().equals(locales[i].getLanguage()))
                match1 = i;
        }
        if (match2 != -1)
            return match2;
        else if (match1 != -1)
            return match1;
        else
            return defaultIndex;
    }
}
