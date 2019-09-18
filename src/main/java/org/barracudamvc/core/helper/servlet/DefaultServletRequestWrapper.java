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
 * $Id: DefaultServletRequestWrapper.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.helper.servlet;

import org.apache.log4j.Logger;
import org.barracudamvc.core.helper.state.ParamPersister;
import org.barracudamvc.core.http.content.ContentParser;
import org.barracudamvc.core.http.content.NoActionContentParser;
import org.barracudamvc.plankton.data.Param;
import org.barracudamvc.plankton.io.parser.URLEncoded.URLEncodedParser;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.util.*;

//csc_010404_1 - revamped to extend from servlet package's HttpServletRequestWrapper

/**
 * <p>This class acts as a thin wrapper around a ServletRequest. Most calls
 * are simply passed through to the underlying request object. This object
 * does however, expose a method which allows you to set parameters in
 * the request object. This was necessary for cases where we needed to
 * be able to do a POST, save the parameters somewhere, and then do a GET
 * and reconstitute the parameters from that.
 * <p>
 * <p>When you instantiate this object, it will automatically check the
 * clients session to see if there are any parameter state information that
 * needs to be reconstituted into the current request.
 */
public class DefaultServletRequestWrapper extends HttpServletRequestWrapper implements BarracudaServletRequestWrapper {

    protected static final Logger logger = Logger.getLogger(DefaultServletRequestWrapper.class.getName());
    public static String DEFAULT_ENCODING = System.getProperty("file.encoding");
    private static final Enumeration<String> EMPTY_ENUMERATION = new LocalEnumerator(Collections.<Param>emptyList());

    HttpServletRequest req = null;
    List<Param> paramList = null;
    String method = null;

    /**
     * Create a DefaultServletRequestWrapper around some other
     * HttpServletRequest impl. The wrapper adds the ability to
     * add/remove parameter values.
     *
     * @param ireq the underlying HttpServletRequest
     */
    public DefaultServletRequestWrapper(HttpServletRequest ireq) {
        this(ireq, new NoActionContentParser());
    }

    public DefaultServletRequestWrapper(HttpServletRequest ireq, ContentParser parser) {
        super(ireq);
        setRequest(ireq);
        init(parser);
        setMethod(ireq.getParameter("_method"));
    }

    private void init(ContentParser parser) {
        Map<String, List<Object>> result = parser.getContent((HttpServletRequest) getRequest());
        for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
            for (Object value : entry.getValue()) {
                addParameter(entry.getKey(), value);
            }
        }
    }

    @Override
    public void setMethod(String imethod) {
        method = imethod;
    }

    /**
     * Get the underlying request method.
     */
    @Override
    public String getMethod() {
        return (method != null ? method : req.getMethod());
    }

    /**
     * Set a given parameter (note that this is backed by a hashmap,
     * so the structure is slightly different than that of the
     * underlying ServletRequest which allows multiple paramters
     * with the same name). This means that if you attempt to
     * set a parameter whose key already exists you will effectively
     * overwrite the existing value.
     *
     * @param name  the key name for the parameter
     * @param value the value associated with the given key
     */
    @Override
    public void addParameter(String name, Object value) {
        if (name == null) {
            return;
        }
        if (paramList == null) {
            setupParamList();
        }
        paramList.add(new Param(name, value));
    }

    private String _parseParameter(HttpServletRequest req, String keyName) {
        String s = req.getParameter(keyName);
        if (s != null) {
            return s;
        }

        // Still no parameter found, check the queryString
        String queryString = req.getQueryString();
        if (queryString == null) {
            return null;
        }

        try {
            queryString = java.net.URLDecoder.decode(queryString, DEFAULT_ENCODING);
        } catch (Exception e) {
            logger.warn("failed to decode queryString, but allowing to continue", e);
        }

        //need "=" to know it is a parameter name as opposed to a value
        // also need '&' to make difference between id and aaa_id for example
        // if is first param, will be checked in different place
        int startPos = queryString.indexOf("&" + keyName + "=");

        if (startPos == -1) {
            // not found  inside parameter list, try find as first
            if (queryString.startsWith(keyName + "=")) {
                startPos = 0;
            }
        } else {
            // skip '&'
            startPos++;
        }

        if (startPos != -1) {
            startPos = startPos + keyName.length() + 1;
            int endPos = queryString.indexOf("&", startPos);

            if (endPos == -1) {
                s = queryString.substring(startPos);
            } else {
                s = queryString.substring(startPos, endPos);
            }
        }
        return s;
    }

    /**
     * Returns the value of a request parameter as a String, or
     * null if the parameter does not exist.
     *
     * @param name the key name for the parameter
     * @return the parameter value associated with a key name
     */
    @Override
    public String getParameter(String name) {
        //eliminate the obvious
        if (name == null) {
            return null;
        }

        //if paramList exists, get the value from there
        if (paramList != null) {
            for (Param param : paramList) {
                if (param.getKey().equals(name)) {
                    return param.getValue() == null ? null : param.getValue().toString();
                }
            }
            return null;
        } else {
            return _parseParameter(req, name);
        }
    }

    /**
     * Returns an Enumeration of String objects containing the
     * names of the parameters contained in this request.
     *
     * @return an Enumeration of all the parameter names
     */
    @Override
    public Enumeration<String> getParameterNames() {
        if (paramList != null) {
            return new LocalEnumerator(paramList);
        } else {
            try {
                Enumeration<String> parameterNames = req.getParameterNames();
                if (parameterNames == null || (!parameterNames.hasMoreElements() && req.getQueryString() != null && req.getQueryString().length() > 0)) {
                    logger.fatal("Servlet Container was not able parse the Parameters Names and return an empty enumeration [" + req.getQueryString() + "]");
                    return handleImpossibleTomcat();
                }
                return parameterNames;
            } catch (Exception npe) {
                logger.fatal("Servlet Container was not able parse the Parameters Names and returned an exception instead [" + req.getQueryString() + "]");
                logger.fatal("Unexpected Exception in getParameterNames(): ", npe);

                return handleImpossibleTomcat();
            }
        }
    }

    private Enumeration<String> handleImpossibleTomcat() {
        if (req.getQueryString() == null) {
            return EMPTY_ENUMERATION;
        }

        Map<String, List<String>> parse = new URLEncodedParser().parse(new ByteArrayInputStream(req.getQueryString().getBytes()));

        paramList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : parse.entrySet())
            for (Object o : entry.getValue())
                paramList.add(new Param(entry.getKey(), o));

        return new LocalEnumerator(paramList);
    }

    /**
     * Returns a java.util.Map of the parameters of this request.
     * Request parameters are extra information sent with the request.
     * For HTTP servlets, parameters are contained in the query string
     * or posted form data.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String[]> getParameterMap() {
        //if paramList exists, get the value from there
        if (paramList == null) {
            return req.getParameterMap();
        } else {
            Map<String, List<Object>> paramMap = new HashMap<>(paramList.size());

            //populate the paramMap with key/val pairs
            for (Param param : paramList) {
                String key = param.getKey();
                List<Object> valList = (List) paramMap.get(key);
                if (valList == null) {
                    valList = new ArrayList(10);
                    paramMap.put(key, valList);
                }
                valList.add(param.getValue());
            }
            //now run back through the paramMap and convert all the 
            //List values into String[] (to conform with servlet spec)
            Map<String, String[]> result = new HashMap<>(paramList.size());
            for (Map.Entry<String, List<Object>> entry : paramMap.entrySet()) {
                String key = entry.getKey();
                List<Object> values = entry.getValue();
                String[] valuesArray = new String[values.size()];
                result.put(key, values.toArray(valuesArray));
            }

            return result;
        }
    }

    /**
     * Returns an array of String objects containing all of the
     * values the given request parameter has, or null if the
     * parameter does not exist.
     *
     * @param name the key name for the parameter
     * @return an array of Strings for the given key name
     */
    @Override
    public String[] getParameterValues(String name) {
        //eliminate the obvious
        if (name == null) {
            return null;
        }

        //if paramList is not null, build the array from there
        if (paramList == null) {
            return req.getParameterValues(name);
        } else {
            List<Object> valueList = new ArrayList<>(paramList.size());
            for (Param param : paramList) {
                if (param.getKey().equals(name)) {
                    valueList.add(param.getValue());
                }
            }
            if (valueList.isEmpty()) {
                return null;
            } else {
                return valueList.toArray(new String[valueList.size()]);
            }
        }
    }

    /**
     * Remove the first parameter whose key matches the specified name
     *
     * @param name the key name for the parameter
     */
    @Override
    public void removeParameter(String name) {
        //eliminate the obvious
        if (name == null) {
            return;
        }

        //make sure the paramList is initialized
        if (paramList == null) {
            setupParamList();
        }

        //finally remove the first occurence of the parameter
        for (int i = 0, max = paramList.size(); i < max; i++) {
            Param param = (Param) paramList.get(i);
            if (param.getKey().equals(name)) {
                paramList.remove(i);
                break;
            }
        }

    }

    /**
     * Remove all parameters for a specified name
     *
     * @param name the key name for the parameter
     */
    @Override
    public void removeAllParameters(String name) {
        //eliminate the obvious
        if (name == null) {
            return;
        }

        //make sure the paramList is initialized
        if (paramList == null) {
            setupParamList();
        }

        //finally remove the all occurences of the parameter
        for (int i = paramList.size() - 1; i >= 0; i--) {
            Param param = (Param) paramList.get(i);
            if (param.getKey().equals(name)) {
                paramList.remove(i);
            }
        }
    }

    /**
     * Reset the parameter values to their original state
     * (ie. the actual values in the request)
     */
    @Override
    public void resetParameters() {
        paramList = null;
    }

    /**
     * Get the underlying servlet request. The only reason you
     * should ever have to do this is if you are trying to forward
     * a request. Some containers check to make sure that the
     * request object being forwarded is an instance of their own
     * implementation...
     *
     * @return the underlying servlet request object
     */
    public HttpServletRequest getCoreRequest() {
        return req;
    }

    @Override
    public void setRequest(ServletRequest ireq) {
        super.setRequest(ireq);

        if (req != ireq && ireq instanceof HttpServletRequest) {
            //set the reference to the req
            req = (HttpServletRequest) ireq;

            ParamPersister.reconstituteReqParamState(this);
        }
    }

    private void setupParamList() {
        //eliminate the obvious (only initialize once!)
        if (paramList != null) {
            return;
        }

        //create the param list
        paramList = new ArrayList<>(10);

        //now copy in all param values from the underlying servlet
        //request. From this point on then, the param values will
        //be maintained in the paramList
        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements()) {
            //get the key
            String key = (String) enumeration.nextElement();

            //find all values associated with the key
            String[] vals = req.getParameterValues(key);
            for (int i = 0, max = vals.length; i < max; i++) {
                paramList.add(new Param(key, vals[i]));
            }
        }
    }

    /**
     * This inner class implements Enumaration. It will effectively
     * enumerate over all of the parameter key names.
     */
    static class LocalEnumerator implements Enumeration<String> {

        List<String> keyList = null;
        Iterator<String> it = null;

        public LocalEnumerator(List<Param> iparamList) {
            keyList = new ArrayList<>(iparamList.size());

            for (Param param : iparamList) {
                if (!keyList.contains(param.getKey())) {
                    keyList.add(param.getKey());
                }
            }
            it = keyList.iterator();
        }

        @Override
        public boolean hasMoreElements() {
            return (it.hasNext());
        }

        @Override
        public String nextElement() {
            return it.next();
        }
    }


    public List<Param> getInternalParamList() {
        if (paramList != null) {
            return new ArrayList<>(paramList);
        }
        return null;
    }

    public String getRedirectMethod() {
        return method;
    }
}
