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
 * $Id: ParamPersister.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.helper.state;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.barracudamvc.core.helper.servlet.BarracudaServletRequestWrapper;
import org.barracudamvc.core.util.http.SessionServices;
import org.barracudamvc.plankton.data.Param;

/**
 * <p>Utility methods used for saving servlet request param State
 * and then reconstituting them later.
 *
 * TODO - there is a potential problem with this class, in that
 * if you have multiple requests running at the same time, which
 * both need to save values in the session, they are going to stomp
 * on one another...
 */
public class ParamPersister {

    static final String PARAM_LIST = "ParamPersister.PARAM_LIST";

    /**
     * This method takes Request parameters and saves them in a
     * users session. End users should never need
     *
     * @param req the ServletRequest object
     */
    public static void persistReqParamState(HttpServletRequest req) {
        //eliminate the obvious
        if (req==null) return;

        //first see if there are any param values even associated
        //with this request
        HttpSession session = null;
        List<Param> paramList = null;

        //iterate through the list
        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements()) {
            //get the session obj and create a list to store the values in
            if (session==null) {
                session = SessionServices.getSession(req);
                paramList = new ArrayList<Param>();
                session.setAttribute(PARAM_LIST, paramList);
            }

            //now get all the values associated with a given parameter
            //and save them in the state
            String key = (String) enumeration.nextElement();
            String[] values = req.getParameterValues(key);
            for (int i=0, max=values.length; i<max; i++) {
                paramList.add(new Param(key, values[i]));
            }        
        }
    }

    /**
     * This method reconstitutes Request parameters from a users session
     *
     * @param req the wrapper ServletRequest object
     */
    public static void reconstituteReqParamState(BarracudaServletRequestWrapper req) {
        //eliminate the obvious
        if (req == null) {
            return;
        }

        //see if the user even has a session. If not, there won't be any state
        HttpSession session = SessionServices.getSession(req, false);
        if (session == null) {
            return;
        }

        //now see if the session has any param state
        List paramList = (List) session.getAttribute(PARAM_LIST);
        if (paramList != null) {
            //iterate through the list, adding them back into the req object
            Iterator it = paramList.iterator();
            while (it.hasNext()) {
                Param p = (Param) it.next();
                req.addParameter(p.getKey(), p.getValue());
            }

            //finally make sure we clear out the param state
            session.removeAttribute(PARAM_LIST);
        }
    }
}
