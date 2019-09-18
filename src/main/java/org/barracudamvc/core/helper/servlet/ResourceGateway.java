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
 * $Id: ResourceGateway.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.helper.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.barracudamvc.plankton.data.ReferenceFactory;
import org.barracudamvc.plankton.http.ContextServices;
import static org.barracudamvc.plankton.io.StreamUtils.readIntoByteArray;

/**
 * <p>The purpose of this servlet is to look for a static resource
 * on the classpath and return it to the client
 */
public class ResourceGateway extends HttpServlet {

    protected static final Logger logger = Logger.getLogger(ResourceGateway.class.getName());
    public static String EXT_RESOURCE_ID = "xlib";
    public static String RESOURCE_NOT_FOUND = "ResourceNotFound";
    protected static final long startup = System.currentTimeMillis();

    public static final NoCacheResourceProvider NO_CACHE = new NoCacheResourceProvider();
    public static final CachingResourceProvider CACHE = new CachingResourceProvider();
    public static ResourceProvider RESOURCE_PROVIDER = CACHE;

    public void init() throws ServletException {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }

    /**
     * <p>Handle the default HttpRequest.
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resourceName = EXT_RESOURCE_ID + req.getPathInfo();
        Object resource = RESOURCE_PROVIDER.getResource(getServletContext(), resourceName);
        if (resource instanceof LocalResource) {

            //saw_031604_1 begin
            // if the client sends an "If-Modified-Since" then we should compare it against the
            // startup time and send a 304 (Not Modified) response if it hasn't changed
            long lastTime = req.getDateHeader("If-Modified-Since");

            // divide by 1000 because milliseconds aren't transmitted via HTTP
            if (lastTime / 1000 >= startup / 1000) {
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            resp.setHeader("Cache-Control", "public");
            resp.setDateHeader("Last-Modified", startup);

            LocalResource lr = (LocalResource) resource;
            resp.setContentType(lr.contentType);

            OutputStream out = resp.getOutputStream();
            out.write(lr.contentData);
            resp.flushBuffer();
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found: " + resourceName);
        }
    }

    /**
     * Used to create a SoftReference to a specified resource
     */
    static class LocalReferenceFactory implements ReferenceFactory {

        String resourceName = null;
        ServletContext context;

        public LocalReferenceFactory(ServletContext context, String iresourceName) {
            resourceName = iresourceName;
            this.context = context;
        }

        @Override
        public Reference getObjectReference() {
            Object result;
            try {
                BufferedInputStream in = new BufferedInputStream(getResourceStream());
                byte[] dstbytes = readIntoByteArray(in);
                in.close();

                result = new LocalResource(determineMimeType(), dstbytes);
            } catch (IOException e) {
                if (logger.isDebugEnabled())
                    logger.debug("Failure: " + e);
                result = RESOURCE_NOT_FOUND;
            }
            return new SoftReference<Object>(result);
        }

        private String determineMimeType() {
            String contentType = context.getMimeType(resourceName);
            if (contentType == null)
                contentType = "text/plain";
            return contentType;
        }

        private InputStream getResourceStream() throws IOException {
            // The URL is used here instead of going directly against the Class Loader for the resource
            // as the ClassLoader is allowed to cache resources. Dependent on your class loader 
            // you will receive caching
            ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
            URL resURL = ctxLoader.getResource(resourceName);
            if (resURL == null) {
                throw new IOException("Resource Not Found " + resourceName);
            }
            URLConnection resConn = resURL.openConnection();

            resConn.setUseCaches(false);
            return resConn.getInputStream();
        }
    }

    /**
     * The actual resource that gets cached
     */
    static class LocalResource {

        public final String contentType;
        public final byte[] contentData;

        public LocalResource(String contentType, byte[] contentData) {
            this.contentType = contentType;
            this.contentData = contentData;
        }
    }

    public interface ResourceProvider {

        public Object getResource(ServletContext context, String name);
    }

    public static class CachingResourceProvider implements ResourceProvider {

        @Override
        public Object getResource(ServletContext context, String name) {
            return ContextServices.getObjectFromCache(context, name, new LocalReferenceFactory(context, name));
        }
    }

    public static class NoCacheResourceProvider implements ResourceProvider {

        @Override
        public Object getResource(ServletContext context, String name) {
            return new LocalReferenceFactory(context, name).getObjectReference().get();
        }
    }

    public static void setProvider(ResourceProvider provider) {
        RESOURCE_PROVIDER = provider;
    }
}
