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
 * $Id: HttpRequester.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

import org.barracudamvc.plankton.data.Base64;

/**
 * This class encapsulates access to/from a URL via both POST and GET methods. 
 * To use, simply set the URL, the method (POST/GET), and the params. If you're
 * using get, the params are optional (they can be included as part of the URL). 
 * Also note that you can pass a username and password if you need to do basic 
 * authentication. This class also now supports cookies, thanks to Shawn Wilson 
 * [shawnw@atmreports.com] - look at the sample code down in the main method for
 * an example of how to use it (basically, you just use the requestor to access a
 * URL, thereby getting the cookie, and then you re-use the requestor to access
 * any other URLs which depend on that cookie).
 *
 * Refer to the source for this class (main method) to see an example of
 * how you would use this class for both POST and GET methods:
 */
public class HttpRequester {

    public static final String POST = "POST";
    public static final String GET = "GET";
    protected URL url = null;
    protected String method = GET;
    protected Map props = null;
    protected Map<String, String> hdrs = new TreeMap<String, String>();
    protected HttpOutputWriter outputWriter = null;
    protected String user = null;
    protected String password = null;
    protected boolean authenticate = false;
    protected boolean acceptCookies = true; //saw_121102.2
    protected boolean manageCookies = true; //saw_022004_1
    protected List<Cookie> cookies = null;          //saw_121102.2
    protected Map<Cookie, Date> cookieTimes = null;       //saw_022004_1
    protected OutputStream outStream = null;
    protected InputStream inStream = null;
    protected BufferedReader in = null;
    protected URLConnection conn = null;    //csc_010404_1
    protected String userAgent = null;
    protected Map<String,List<String>> requestProperties;
    protected Map<String,List<String>> headerFields;

    /**
     * Set the Request. This is a convenience method to encapsulate
     * calls to setUrl, setMethod, and setParams all in one fell swoop.
     *
     * @param iurl the URL we wish to access
     * @param imethod the method we wish to use (either GET or POST)
     * @param iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     * @throws MalformedURLException
     */
    public void setRequest(String iurl, String imethod, Map iprops) throws MalformedURLException {
        setRequest(iurl, imethod, iprops, null);
    }

    /**
     * Set the Request. This is a convenience method to encapsulate
     * calls to setUrl, setMethod, and setParams all in one fell swoop.
     *
     * @param iurl the URL we wish to access
     * @param imethod the method we wish to use (either GET or POST)
     * @param iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     * @throws MalformedURLException
     */
    public void setRequest(URL iurl, String imethod, Map iprops) throws MalformedURLException {
        setRequest(iurl, imethod, iprops, null);
    }

    /**
     * Set the Request. This is a convenience method to encapsulate
     * calls to setUrl, setMethod, and setParams all in one fell swoop.
     *
     * @param iurl the URL we wish to access
     * @param imethod the method we wish to use (either GET or POST)
     * @param iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     * @param ioutputWriter the HttpOutputWriter we wish to write to
     * @throws MalformedURLException
     */
    public void setRequest(String iurl, String imethod, Map iprops, HttpOutputWriter ioutputWriter) throws MalformedURLException {
        setRequest(iurl, imethod, iprops, null, null, null);
    }

    /**
     * Set the Request. This is a convenience method to encapsulate
     * calls to setUrl, setMethod, and setParams all in one fell swoop.
     *
     * @param iurl the URL we wish to access
     * @param imethod the method we wish to use (either GET or POST)
     * @param iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     * @param ioutputWriter the HttpOutputWriter we wish to write to
     * @throws MalformedURLException
     */
    public void setRequest(URL iurl, String imethod, Map iprops, HttpOutputWriter ioutputWriter) throws MalformedURLException {
        setRequest(iurl, imethod, iprops, null, null, null);
    }

    /**
     * Set the Request. This is a convenience method to encapsulate
     * calls to setUrl, setMethod, and setParams all in one fell swoop.
     *
     * @param iurl the URL we wish to access
     * @param imethod the method we wish to use (either GET or POST)
     * @param iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     * @param iuser the user named required to connect
     * @param ipwd the password named required to connect
     * @param ioutputWriter the HttpOutputWriter we wish to write to
     * @throws MalformedURLException
     */
    public void setRequest(String iurl, String imethod, Map iprops, String iuser, String ipwd, HttpOutputWriter ioutputWriter) throws MalformedURLException {
        if (iurl != null) {
            setUrl(iurl);
        }
        if (imethod != null) {
            setMethod(imethod);
        }
        if (iprops != null) {
            setParams(iprops);
        }
        if (iuser != null) {
            setUser(iuser);
        }
        if (ipwd != null) {
            setPassword(ipwd);
        }
        if (ioutputWriter != null) {
            setOutputWriter(ioutputWriter);
        }
    }

    /**
     * Set the Request. This is a convenience method to encapsulate
     * calls to setUrl, setMethod, and setParams all in one fell swoop.
     *
     * @param iurl the URL we wish to access
     * @param imethod the method we wish to use (either GET or POST)
     * @param iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     * @param iuser the user named required to connect
     * @param ipwd the password named required to connect
     * @param ioutputWriter the HttpOutputWriter we wish to write to
     * @throws MalformedURLException
     */
    public void setRequest(URL iurl, String imethod, Map iprops, String iuser, String ipwd, HttpOutputWriter ioutputWriter) throws MalformedURLException {
        if (iurl != null) {
            setUrl(iurl);
        }
        if (imethod != null) {
            setMethod(imethod);
        }
        if (iprops != null) {
            setParams(iprops);
        }
        if (iuser != null) {
            setUser(iuser);
        }
        if (ipwd != null) {
            setPassword(ipwd);
        }
        if (ioutputWriter != null) {
            setOutputWriter(ioutputWriter);
        }
    }

    /**
     * Set the URL we wish to access
     *
     * @param iurl the URL we wish to access
     * @throws MalformedURLException
     */
    public void setUrl(String iurl) throws MalformedURLException {
        //if we're setting it back to null, otherwise, create the url 
        //which represents the servlet which will do the generation
        if (iurl == null) {
            url = null;
        } else {
            setUrl(new URL(iurl));
        }
    }

    /**
     * Set the URL we wish to access
     *
     * @param iurl the URL we wish to access
     */
    public void setUrl(URL iurl) {
        url = iurl;
    }

    /**
     * Get the URL for the HttpRequest object
     *
     * @return the URL behind this request
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Set the method we wish to use. Valid values are either GET
     * or POST. Default is GET.
     *
     * @param imethod the method we wish to use (either GET or POST)
     */
    public void setMethod(String imethod) {
        if (imethod.toUpperCase().equals(POST)) {
            method = POST;
        } else {
            method = GET;
        }
    }

    /**
     * Get the method we're using for this HttpRequest object
     *
     * @return the method we're using (either GET or POST)
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the parmeters we wish to pass to the URL as name-value pairs.
     * If you are using the POST method, it will look for properties in
     * here. If you are using the get method, you can manually pass the
     * properties as part of the URL string, and just ignore this method.
     *
     * @param  iprops the Map contains our key-value URL parameter pairs.
     *       If the value is a Set, the resulting URL will contain a key-value
     *        mapping for each entry in the Set.
     */
    public void setParams(Map iprops) {
        props = iprops;
    }

    /**
     * Return the HashMap object for this HttpRequest. If the map is null (ie.
     * because you are using the GET method), we attempt to look for the
     * properties in the actual URL string and build a HashMap from that.
     *
     * @return a Map containing all the parameters for this HttpRequest
     */
    public Map getParams() {
        //if someone asks for the param map and its null, try
        //and build it based on the actual URL string
        if (props == null) {
            //avoid the obvious errs
            if (url == null) {
                return null;
            }

            //build the HashMap
            props = HttpConverter.cvtURLStringToMap(url.toString(), "&");
        }

        //return the HashMap
        return props;
    }

    /**
     * Return a read-only list of headers this client is sending to the server
     * (will not include cookies)
     */
    public Map getHeaders() {
        return new HashMap<String, String>(hdrs);
    }

    /**
     * Clear any headers this client knows about.
     */
    public void clearHeaders() {
        hdrs.clear();
    }

    /**
     * Add a header
     */
    public void addHeader(String key, String value) {
        hdrs.put(key, value);
    }

    /**
     * remove a header
     */
    public String removeHeader(String key) {
        return (String) hdrs.remove(key);
    }

    /**
     * Set the user (if we need to authenticate in order to make the connection)
     *
     * @param iuser the user name
     */
    public void setUser(String iuser) {
        user = iuser;
        authenticate = (user != null);
    }

    /**
     * Get the user name
     *
     * @return the user name
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the password (if we need to authenticate in order to make the connection)
     *
     * @param ipwd the password
     */
    public void setPassword(String ipwd) {
        password = ipwd;
        authenticate = (password != null);
    }

    /**
     * Get the password
     *
     * @return the password
     */
    protected String getPassword() {
        return password;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getUserAgent() {
        return this.userAgent;
    }
    
    //saw_121102.2_start
    /**
     * Set whether or not to accept cookies from the server.
     * The default is <code>true</code>.
     *
     * Setting this value to <code>false</code> after cookies
     * have already been obtained does not clear the current
     * cookies, it simply will not accept any new cookies.
     *
     * @see #clearCookies()
     */
    public void setAcceptCookies(boolean accept) {
        this.acceptCookies = accept;
    }

    /**
     * Determine whether or not we are accepting cookies.
     */
    public boolean getAcceptCookies() {
        return acceptCookies;
    }

    /**
     * Return a read-only list of cookies this client is sending to the server
     *
     * @return a list of {@link Cookie cookies}, or <code>null</code> if
     * the server has not set any cookies in the client
     *
     * @see Cookie
     */
    public List getCookies() {
        return new ArrayList<Cookie>(cookies);
    }

    /**
     * Clear any cookies this client knows about.
     */
    public void clearCookies() {
        cookies = null;
    }
    //saw_121102.2_end

    /**
     * @since   saw_022004_1
     */
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
        cookieTimes.put(cookie, new Date());
    }

    /**
     * @since   saw_022004_1
     */
    public boolean removeCookie(Cookie cookie) {
        cookieTimes.remove(cookie);
        return cookies.remove(cookie);
    }

    /**
     * Set whether to automatically manage cookies or not.
     * The default is <tt>true</tt>.
     *
     * When enabled, cookies will be retained between connections and sent to the
     * server if they match (domain, path, etc.). If cookies have an expiration
     * date they will be automatically removed once they expire. Note that in
     * order for this to work properly with 302 (moved) responses you must set
     * HttpURLConnection.setFollowRedirects(false) and have your code manually
     * follow the redirects. Otherwise the redirects are handled by
     * HttpURLConnection and the cookies are not sent to the redirect URL.
     *
     * If disabled, the cookie list is cleared after the request headers are
     * sent to the server.
     *
     * @since   saw_022004_1
     */
    public void setManageCookies(boolean manage) {
        this.manageCookies = manage;
    }

    /**
     * Find out whether we are managing cookies or not
     *
     * @since   saw_022004_1
     */
    public boolean getManageCookies() {
        return manageCookies;
    }

    /**
     * Set the output writer to be used for posting data
     *
     * @param  ioutputWriter the HttpOutputWriter
     */
    public void setOutputWriter(HttpOutputWriter ioutputWriter) {
        outputWriter = ioutputWriter;
    }

    /**
     * Return the output writer. If none is set, the default will be used.
     *
     * @return the HttpOutputWriter
     */
    public HttpOutputWriter getOutputWriter() {
        //if someone asks for the output writer and its null,
        //return the default
        if (outputWriter == null) {
            return new DefaultOutputWriter();
        } else {
            return outputWriter;
        }
    }

    //csc_010404_1 - added
    /**
     * Return the connection. Only set after connect has been called
     *
     * @return the URLConnection
     */
    public URLConnection getURLConnection() {
        return conn;
    }

    /**
     * Connect to the URL
     *
     * @throws ConnectException
     * @throws IOException
     */
    public void connect() throws ConnectException, IOException {
        //pre-launch checks
        if (url == null) {
            throw new ConnectException("Invalid URL. URL can not be NULL");
        }
        if (method != POST && method != GET) {
            throw new ConnectException("Invalid Method. Method must be either POST or GET");
        }



        // set any params
        if (GET == method) {
            //first see if we have a param structure...if so, build a URL String.
            if (props != null) {
                //figure out what the current URL is and strip off any parameters
                String newUrl = getUrl().toString();
                int pos = newUrl.indexOf("?");
                if (pos > 0) {
                    newUrl = newUrl.substring(0, pos);
                }

                //now run through the map and build a new url string
                setUrl(newUrl + "?" + HttpConverter.cvtMapToURLString(props, "&"));
                newUrl = getUrl().toString();
                if (newUrl.endsWith("?")) {
                    setUrl(newUrl.substring(0, newUrl.length() - 1));
                }
                //now set the url and set the params object back to null so we don't
                //need to rebuild the URL string again
                setParams(null);
            }
        }

        
        // open the connection
        conn = url.openConnection();
        
        

        // set connection parameters
        conn.setUseCaches(false);
        conn.setDoInput(true); //always want input
        conn.setDoOutput(POST == method);

        //Set up an authorization header with our credentials (this chunk of
        //code stolen from org.apache.catalina.ant.AbstractCatalinaTask; 
        //thanks to Craig R. McClanahan [craigmcc@apache.org] for pointing
        //me to this example)
        if (authenticate) {
            String input = user + ":" + password;
            String output = new String(Base64.encode(input.getBytes()));
            conn.setRequestProperty("Authorization", "Basic " + output);
        }
        
        if (getUserAgent() != null) {
            conn.setRequestProperty("User-Agent", getUserAgent());
        }

        //saw_121102.2_start
        //do we have cookies to send?
        if (cookies != null) {
            //NOTE: this implementation of cookie support is very basic and
            //does not completely follow spec. Specifically, cookie expiration
            //is not checked and a single repeated cookie is not ordered by
            //the path specifications as the spec requires.

            StringBuffer sb = new StringBuffer();
            String sep = "";
            int maxVersion = 0;
            boolean haveCookies = false;

            for (Iterator it = cookies.iterator(); it.hasNext();) {
                Cookie cookie = (Cookie) it.next();
                Date created = (Date) cookieTimes.get(cookie);
                boolean transmit = false;

                // not managing so always transmit
                if (!manageCookies) {
                    transmit = true;

                    // check domain
                } else if (cookie.getDomain() == null || url.getHost().toLowerCase().endsWith(cookie.getDomain().toLowerCase())) {
                    //check path
                    if (cookie.getPath() == null || url.getPath().startsWith(cookie.getPath())) {

                        // verify security
                        if (!cookie.getSecure() || url.getProtocol().equalsIgnoreCase("https")) {

                            // verify age
                            int maxAge = cookie.getMaxAge();
                            if (maxAge < 0 || System.currentTimeMillis() - created.getTime() <= maxAge * 1000) {
                                transmit = true;
                            } else {
                                it.remove();
                            }
                        }
                    }
                }

                if (transmit) {
                    haveCookies = true;

                    sb.append(sep).append(cookie.getName()).append('=').append(cookie.getValue());
                    if (cookie.getVersion() > 0) {
                        if (cookie.getVersion() > maxVersion) {
                            maxVersion = cookie.getVersion();
                        }
                        if (cookie.getPath() != null) {
                            sb.append(";$Path=").append(cookie.getPath());
                        }
                        if (cookie.getDomain() != null) {
                            sb.append(";$Domain=").append(cookie.getDomain());
                        }
                    }
                    sep = ";";
                }
            }

            if (haveCookies) {
                String cookieStr;
                if (maxVersion == 0) {
                    cookieStr = sb.toString();
                } else {
                    cookieStr = "$Version=" + maxVersion + sb.toString();
                }

                conn.setRequestProperty("Cookie", cookieStr);
            }
        }

        if (hdrs != null) {
            Iterator it = hdrs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                conn.addRequestProperty("" + me.getKey(), "" + me.getValue());
            }
        }
        requestProperties = conn.getRequestProperties();
        
        // open the stream(s)... this will cause the connection to be established
        if (POST == method) {
            outStream = conn.getOutputStream();
            getOutputWriter().writeOutput(outStream);
        }
        inStream = conn.getInputStream();
        in = new BufferedReader(new InputStreamReader(inStream));
        

        List<String> scookies = new ArrayList<String>();


        // here's a workaround that should handle it
        boolean foundNull = false;
        for (int i = 0; true; i++) {
            String key = conn.getHeaderFieldKey(i);
            if (key == null) {
                if (foundNull) {
                    break;
                }
                foundNull = true;
            } else if ("set-cookie".equals(key.toLowerCase())) {
                scookies.add(conn.getHeaderField(i));
            }
        }

        // dbr_032601.end
        if (acceptCookies && scookies != null) {
            if (cookies == null || !manageCookies) {
                cookies = new ArrayList<Cookie>(scookies.size());
                cookieTimes = new HashMap<Cookie, Date>();
            }
            Date time = new Date();

            for (Iterator it = scookies.iterator(); it.hasNext();) {
                String cookieStr = (String) it.next();
                try {
                    Cookie cookie = HttpServices.parseCookie(cookieStr);

                    if (manageCookies) {
                        // the Cookie object doesn't provide an equals() implementation,
                        // so we need to manually search for a match to overwrite
                        String key = cookie.getDomain() + ":" + cookie.getName();

                        for (Iterator it2 = cookies.iterator(); it2.hasNext();) {
                            Cookie cookie2 = (Cookie) it2.next();
                            String key2 = cookie2.getDomain() + ":" + cookie2.getName();

                            if (key2.equalsIgnoreCase(key)) {
                                it2.remove();
                                cookieTimes.remove(cookie2);
                                break;
                            }
                        }
                    }

                    cookies.add(cookie);
                    cookieTimes.put(cookie, time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        //saw_121102.2_end
    }
    
    @SuppressWarnings("unchecked")
    public String printRequestProperies() {
        StringBuilder propertyBuilder = new StringBuilder();
        propertyBuilder
                .append("URL: ")
                .append(conn.getURL())
                .append('\n');
                
        
        if(props != null) {
            propertyBuilder.append("Header Fields ------------------------------\n");
            for(Map.Entry<String, String> entries : ((Map<String, String>)props).entrySet()) {
                propertyBuilder
                        .append(entries.getKey())
                        .append(": ")
                        .append(entries.getValue())
                        .append('\n');
            }
            propertyBuilder.append("/Header Fields -----------------------------\n");
        }
        if(headerFields != null) {
            propertyBuilder.append("Header Fields ------------------------------\n");
            for(Map.Entry<String, List<String>> entries : headerFields.entrySet()) {
                propertyBuilder.append(entries.getKey()).append(":");
                for(String value : entries.getValue()) {
                    propertyBuilder.append(" \"").append(value).append("\" ");
                }
                propertyBuilder.append('\n');
            }
            propertyBuilder.append("/Header Fields -----------------------------\n");
        }
        
        if(requestProperties != null) {
            propertyBuilder.append("Request Properties -------------------------\n");
            for(Map.Entry<String, List<String>> entries : requestProperties.entrySet()) {
                propertyBuilder.append(entries.getKey()).append(":");
                for(String value : entries.getValue()) {
                    propertyBuilder.append(" \"").append(value).append("\" ");
                }
                propertyBuilder.append('\n');
            }
            propertyBuilder.append("/Request Properties ------------------------\n");
        }
        return propertyBuilder.toString();
    }

    /**
     * Read responses from the URL
     *
     * @return a String representation of what we got back
     * @throws ConnectException
     * @throws IOException
     */
    public String readLine() throws ConnectException, IOException {
        //pre-launch checks
        if (in == null) {
            throw new ConnectException("Connection is not active");
        }

        //get the line
        String inputLine = in.readLine();

        //if its null, auto disconnect
        if (inputLine == null) {
            disconnect();
        }

        //now return the String
        return inputLine;
    }

    /**
     * Get the underlying output stream
     *
     * @return the output stream
     * @throws ConnectException
     */
    public OutputStream getOutputStream() throws ConnectException {
        //pre-launch checks
        if (outStream == null) {
            throw new ConnectException("Connection is not active");
        }

        //return the input stream
        return outStream;
    }

    /**
     * Get the underlying input stream
     *
     * @return the input stream
     * @throws ConnectException
     */
    public InputStream getInputStream() throws ConnectException {
        //pre-launch checks
        if (inStream == null) {
            throw new ConnectException("Connection is not active");
        }

        //return the input stream
        return inStream;
    }

    /**
     * Disconnect from the URL. You really only need to call this
     * if you terminate the readLine process on your end. if
     * readLine() encounters a null value, it assumes input is
     * complete and automatically calls this method.
     */
    public void disconnect() {
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException ioe) {
            }
        }
        outStream = null;
        if (in != null) {
            try {
                in.close();
            } catch (IOException ioe) {
            }
        }
        in = null;
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException ioe) {
            }
        }
        inStream = null;
    }

    /**
     * This inner class provides the default mechanism to write to an output stream
     */
    class DefaultOutputWriter implements HttpOutputWriter {

        @Override
        public void writeOutput(OutputStream outputStream) throws IOException {
            PrintWriter out = new PrintWriter(outputStream);
            try {
                String paramStr = HttpConverter.cvtMapToURLString(props, "&");
                if (paramStr != null && paramStr.trim().length() > 0) {
                    out.print(paramStr);
                }
            } finally {
                out.close();
                outputStream.close();
            }
        }
    }

    public static void main(String[] args) {
        //sample GET
        try {
            HttpRequester hr = new HttpRequester();
            String urlStr = "http://localhost:8080/manager/list";   //connect to Tomcat manager app
            String paramStr = null;
            Map props = null;
            if (paramStr != null) {
                HttpConverter.cvtURLStringToMap(paramStr, "&");
            }
            hr.setRequest(urlStr, HttpRequester.GET, props, "admin", "123123", null);
            hr.connect();
            String inputLine;
            while ((inputLine = hr.readLine()) != null) {
                System.out.println(inputLine);
            }
            hr.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            HttpRequester hr = new HttpRequester();
            hr.setAcceptCookies(true);
            hr.setManageCookies(true);
            hr.setRequest("http://www.psycinfo.com/cookie/set-cookie.cfm", HttpRequester.GET, null);
            hr.connect();
            String inputLine;
            while ((inputLine = hr.readLine()) != null) {
                System.out.println(inputLine);
            }
            hr.disconnect();

            hr.setRequest("http://www.psycinfo.com/cookie/check-cookie.cfm", HttpRequester.GET, null);
            hr.connect();
            while ((inputLine = hr.readLine()) != null) {
                System.out.println(inputLine);
            }
            hr.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
