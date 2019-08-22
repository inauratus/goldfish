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
 * $Id: MockHttpServletRequest.java 265 2014-02-21 17:40:07Z alci $
 */
package org.barracudamvc.testbed.servlet;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.barracudamvc.plankton.data.Param;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.URL;
import java.security.Principal;
import java.util.*;

/**
 * <p>This class provides a mockup implementation for the HttpServletRequest.
 * Note that not all the methods are implemented...you may need to extend/override
 * to get the behaviour you expect. If the behaviour is common to all
 * servlet requests, add it back in here. Note also that all properties of
 * this class are public, allowing you to access them directly (makes assertions
 * easier) 
 */
public class MockHttpServletRequest implements HttpServletRequest {

    public String paramStr = null;
    public List<Param> paramList = new ArrayList<Param>();
    public Map<String, List<String>> hdrMap = new HashMap<String, List<String>>();
    public Map<String, Object> atttributes = new HashMap<String, Object>();
    private MockServletInputStream is;
    private String method = "GET";
    private int contentLength = -1;
    private URL requestURL;

    public MockHttpServletRequest(URL url) {
        this(null, url);
    }

    /**
     * Noargs constructor
     */
    public MockHttpServletRequest() {
        this(null, null);
    }

    public MockHttpServletRequest(String paramStr) {
        this(paramStr, null);
    }

    /**
     * Create a request with a given param String
     */
    public MockHttpServletRequest(String paramStr, URL requestURL) {
        if (paramStr != null) {
            setParamStr(paramStr);
        }
        if (requestURL == null) {
            try {
                requestURL = new URL("http://example.com/example" + (paramStr == null ? "" : "?"  + paramStr));
            } catch (Exception ex) {
                System.out.println("A");
            }
        }

        this.requestURL = requestURL;
    }

    //-------------------- MockHttpServletRequest ----------------
    public final void setParamStr(String paramStr) {
        //clear the paramList
        paramList.clear();
        if (paramStr == null) {
            return;
        }

        //convert the incoming parameters into param vals and store them in the list
        String delimiter = "&";
        StringTokenizer st = new StringTokenizer(paramStr, delimiter);
        while (st.hasMoreTokens()) {
            String segment = st.nextToken();
            StringTokenizer stsub = new StringTokenizer(segment, "=");
            String key = (String) stsub.nextElement();
            String value = (String) stsub.nextElement();
            paramList.add(new Param(key, value));
        }
    }

    public void setHeader(String hdrName, String hdrValue) {
        List hdrs = new ArrayList(1);
        hdrs.add(hdrValue);
        hdrMap.put(hdrName, hdrs);
    }

    public void addHeader(String hdrName, String hdrValue) {
        List<String> hdrs = hdrMap.get(hdrName);
        if (hdrs == null) {
            setHeader(hdrName, hdrValue);
        } else {
            hdrs.add(hdrValue);
        }
    }

    //-------------------- HttpServletRequest --------------------
    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public String getContextPath() {
        String path = requestURL.getPath();
        if (!path.startsWith("/")) {
            return "";
        }
        path = path.substring(1);

        int firstIndex = path.indexOf('/');
        if (firstIndex == -1) {
            return "";
        } else {
            return "/" + path.substring(0, firstIndex);
        }
    }

    @Override
    public Cookie[] getCookies() {
        return null;
    }

    @Override
    public long getDateHeader(String name) {
        return -1;
    }

    /**
     * Get a header
     */
    @Override
    public String getHeader(String hdrName) {
        List hdrs = (List) hdrMap.get(hdrName);
        if (hdrs == null) {
            return null;
        } else {
            return (String) hdrs.get(0);
        }
    }

    /**
     * Get a list of header names
     */
    @Override
    public Enumeration getHeaderNames() {
        Iterator it = hdrMap.keySet().iterator();
        List keys = new ArrayList();
        while (it.hasNext()) {
            keys.add(it.next());
        }
        return new LocalEnumerator(keys);
    }

    /**
     * get a list of header values for a given name
     */
    @Override
    public Enumeration getHeaders(String hdrName) {
        return new LocalEnumerator((List) hdrMap.get(hdrName));
    }

    @Override
    public int getIntHeader(String name) {
        return -1;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getQueryString() {
        return requestURL.getQuery();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return requestURL.getPath();
    }

    @Override
    public String getServletPath() {
        return requestURL.getPath().substring(getContextPath().length());
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    //-------------------- ServletRequest ------------------------
    @Override
    public Object getAttribute(String name) {
        return atttributes.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return new LocalEnumerator(atttributes.keySet());
    }

    @Override
    public void setCharacterEncoding(String s) {
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public String getContentType() {
        List<String> contentType = hdrMap.get("Content-Type");

        if (contentType == null || contentType.isEmpty()) {
            return null;
        }
        return contentType.get(0);
    }

    public void initInputStream() throws UnsupportedEncodingException, IOException {

        is = new MockServletInputStream(setupForMultipart(paramList));
    }

    public void setInputStream(InputStream stream) {
        is = new MockServletInputStream(stream);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return is;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public Map getParameterMap() {
        HashMap<String, String[]> params = new HashMap<String, String[]>();
        for (Param p : paramList) {
            params.put(p.getKey(), getParameterValues(p.getKey()));
        }
        return params;
    }

    @Override
    public StringBuffer getRequestURL() {
        return requestURL == null ? null : new StringBuffer(requestURL.toString());
    }

    public void setRequestURL(URL url) {
        requestURL = url;
    }

    @Override
    public String getParameter(String name) {
        if(paramList == null)
            return null;

        for (Param param : paramList) {
            if (param.getKey().equals(name)) {
                return param.getValue() == null ? null : param.getValue().toString();
            }
        }
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return new LocalEnumerator(paramList);
    }


    @Override
    public String[] getParameterValues(String name) {
        List<String> valueList = new ArrayList<>(paramList.size());
        for (Param param : paramList) {
            if (param.getKey().equals(name)) {
                valueList.add(param.getValue() == null ? null : param.getValue().toString());
            }
        }

        int idx = -1;
        String[] valueArr = new String[valueList.size()];
        for (String value : valueList) {
            valueArr[++idx] = value;
        }
        if (valueArr.length == 0) {
            return null;
        } else {
            return valueArr;
        }
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getScheme() {
        return requestURL.getProtocol();
    }

    @Override
    public String getServerName() {
        return requestURL.getHost();
    }

    @Override
    public int getServerPort() {
        int port = requestURL.getPort();
        if (port == -1) {
            port = getDefaultPort(getScheme());
        }
        return port;
    }

    private int getDefaultPort(String scheme) {
        int port;
        if (scheme.equals("http")) {
            port = 80;
        } else if (scheme.equals("https")) {
            port = 443;
        } else {
            port = -1;
        }
        return port;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public void removeAttribute(String name) {
    }

    @Override
    public void setAttribute(String name, Object o) {
        atttributes.put(name, o);
    }

    //@Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean authenticate(HttpServletResponse hsr) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void login(String string, String string1) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Part getPart(String string) throws IOException, IllegalStateException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AsyncContext startAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setContentType(String type) {
        addHeader("Content-Type", type);
    }



    public class MockServletInputStream extends ServletInputStream {

        InputStream inputStream;

        public MockServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

    //-------------------- Utilities -----------------------------
    /**
     * This inner class implements Enumaration. It will effectively
     * enumerate over all of the parameter key names.
     */
    class LocalEnumerator implements Enumeration {

        List keyList = null;
        Iterator it = null;

        public LocalEnumerator(Collection iparamList) {
            if (iparamList != null) {
                keyList = new ArrayList(iparamList.size());
                it = iparamList.iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    if (o instanceof Param) {
                        Param param = (Param) o;
                        if (!keyList.contains(param.getKey())) {
                            keyList.add(param.getKey());
                        }
                    } else {
                        keyList.add(o);
                    }
                }
                it = keyList.iterator();
            }
        }

        @Override
        public boolean hasMoreElements() {
            return (it != null && it.hasNext());
        }

        @Override
        public Object nextElement() {
            return it.next();
        }

        @Override
        public String toString() {
            return super.toString() + " {" + keyList.size() + " items}";
        }
    }

    /**
     * Converts the param list into a input stream encoded for an HTTP Request
     * 
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    private InputStream setupForMultipart(List<Param> params) throws UnsupportedEncodingException, IOException {
        // Just a reminder as we may need to test files as well in the
        // future
        //FileBody bin = new FileBody(new File(PATH));
        // new MultipartRequestEntity() ;
        MultipartEntity reqEntity = new MultipartEntity();

        for (Param param : params) {
            Object value = param.getValue();
            String result = value == null ? null : value.toString();
            reqEntity.addPart(param.getKey(), new StringBody(result));
        }
        this.setHeader("Content-Type", reqEntity.getContentType().getValue());
        ByteArrayOutputStream ug = new ByteArrayOutputStream();
        reqEntity.writeTo(ug);
        contentLength = ug.size();
        return new ByteArrayInputStream(ug.toByteArray());
    }
}
