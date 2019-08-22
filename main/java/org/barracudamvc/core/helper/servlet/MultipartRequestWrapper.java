/*
 * Copyright (C) 2003 Stefan Armbruster
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
 * $Id: MultipartRequestWrapper.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.helper.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * Handles both simple and multipart requests transparently
 *
 * <p>For handling multipart stuff, Jakarta Commons Fileupload is used.
 * Otherwise this wrapper's behavior is identical to the superclass.
 * getFileItem* methods are provided to get direct access to file
 * items in a multipart request and an isMultipart() method is provided for
 * querying whether the current request has multipart data in it or not. The
 * only drawback to using this wrapper is the slight overhead involved in doing
 * multipart checks when the request isn't multipart. The performance deficit
 * should, however, be neglegible.</p>
 *
 * @author  Stefan Armbruster (sam, 2003-11-15)
 * @author  Shawn A. Wilson
 * @author  Jacob Kjome
 */
public class MultipartRequestWrapper extends DefaultServletRequestWrapper implements MultipartRequest, HttpRequest {

    private static final Logger logger = Logger.getLogger(MultipartRequestWrapper.class);
    /** maximum allowed size of uploads */
    /** TODO - remove this as a field after determining it won't hurt anyone. 
     * Of course, it wouldn't be an issue if fields were private in the first place!
     */
    protected long maxSize = -1;
    /** indicates if the current request is multipart */
    protected boolean isMultipart;
    /** contains a list of {@link FileItem file items} */
    protected List<FileItem> uploadItemList;
    /**
     * Part of HTTP content type header.
     */
    public static final String MULTIPART = "multipart/";

    /**
     * Checks if request is multipart. If so, initializes upload items.
     * 
     * @param req the request
     * @param max the maximum allowed size of uploads, -1 signifies no limit
     * @param fileItemFactory some FileItemFactory implementation, defaults to DiskFileItemFactory if null 
     */
    public MultipartRequestWrapper(HttpServletRequest req, long max, FileItemFactory fileItemFactory) {
        super(req);
        this.uploadItemList = new ArrayList<FileItem>();
        this.maxSize = max;
        this.isMultipart = isMultipartContent(req);
        if (logger.isDebugEnabled()) {
            logger.debug("request is multipart: " + isMultipart);
        }
        if (this.isMultipart) {
            initUploadItemList(req, fileItemFactory != null ? fileItemFactory : new DiskFileItemFactory());
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return req.getInputStream();
    }

    /** 
     * @param req the request
     * @param max Maximum allowed size of uploads
     * @see #this(HttpServletRequest, long, FileItemFactory)
     */
    public MultipartRequestWrapper(HttpServletRequest req, long max) {
        this(req, max, null);
    }

    /**
     * Simple constructor, accepts any file size and uses the default
     * FileItemFactory
     * 
     * @param req
     * @see #this(HttpServletRequest, long, FileItemFactory)
     */
    public MultipartRequestWrapper(HttpServletRequest req) {
        this(req, -1, null);
    }

    /**
     * Parses the multipart request for upload items and stores them as
     * {@link FileItem file items} in a list
     * 
     * @param req the request
     * @param fileItemFactory the FileItemFactory
     * @see ServletFileUpload
     */
    @SuppressWarnings("unchecked")
    private void initUploadItemList(HttpServletRequest req, FileItemFactory fileItemFactory) {
        ServletFileUpload upload = new ServletFileUpload(fileItemFactory);

        if (maxSize != -1) {
            upload.setSizeMax(maxSize);
        }
        try {
            uploadItemList = upload.parseRequest(req);
        } catch (FileUploadException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Same behavior as ServletRequest.getParameter, but extended to handle
     * parameters when request is multipart. Only non-file parameters are
     * applicable here.
     * 
     * @param name a String specifying the name of the parameter
     * @return a String representing the single value of the parameter
     * @see javax.servlet.ServletRequest#getParameter(String)
     */
    @Override
    public String getParameter(String name) {
        if (name == null) {
            return null;
        }
        String paramString;
        if (isMultipart()) {
            paramString = getUploadItemValueByName(name);
            if (paramString == null) {
                return super.getParameter(name);
            } else {
                return paramString;
            }
        }
        return super.getParameter(name);
    }

    /**
     * Same behavior as ServletRequest.getParameterNames, but extended to handle
     * parameter names when request is multipart. Only parameter names representing
     * non-file parameters are applicable here.
     * 
     * @return an Enumeration of String objects, each String containing the 
     *		name of a request parameter; or an empty Enumeration if the request 
     *		has no parameters
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @Override
    public Enumeration getParameterNames() {
        if (isMultipart()) {
            HashSet<String> allNames = new HashSet<String>();
            for (FileItem item : uploadItemList) {
                String fieldName = item.getFieldName();

                if (item.isFormField()) {
                    allNames.add(fieldName);
                }
            }
            return Collections.enumeration(allNames);
        }

        return super.getParameterNames();
    }

    /**
     * Akin to getParameterNames(), except that only parameter names
     * representing file parameters are applicable here. If the request is not
     * multipart or it is but there are no file fields to speak of, an empty
     * Enumeration will be returned.
     * 
     * @return an Enumeration of String objects, each String containing the 
     * 		name of a file request parameter; or an empty Enumeration if the 
     *		request has no file parameters
     * @see #getParameterNames
     */
    @Override
    public Enumeration getFileParameterNames() {
        List<String> allNames = new ArrayList<String>();
        if (isMultipart()) {
            for (FileItem item : uploadItemList) {
                if (!item.isFormField()) {
                    allNames.add(item.getFieldName());
                }
            }
        }
        return Collections.<String>enumeration(allNames);
    }

    /**
     * Same behavior as ServletRequest.getParameterValues, but extended to 
     * handle parameters when request is multipart. Only non-file parameters 
     * are applicable here.
     * 
     * @param name a String containing the name of the parameter whose value is 
     * requested
     * @return an array of String objects containing the parameter's values
     * @see javax.servlet.ServletRequest#getParameterValues(String)
     */
    @Override
    public String[] getParameterValues(String name) {
        if (name == null) {
            return null;
        }

        if (isMultipart()) {
            String[] paramString;
            paramString = getUploadItemValuesByName(name);
            if (paramString == null) {
                return super.getParameterValues(name);
            } else {
                return paramString;
            }
        }
        return super.getParameterValues(name);
    }

    /**
     * Same behavior as ServletRequest.getParameterMap, but extended to handle
     * parameters when request is multipart. Only non-file parameters are
     * applicable here.
     *
     * @return an immutable java.util.Map containing parameter names as keys 
     * 		and parameter values as map values. The keys in the parameter map 
     *		are of type String. The values in the parameter map are of type 
     *		String array
     * @see javax.servlet.ServletRequest#getParameterMap
     */
    @Override
    public Map getParameterMap() {
        if (isMultipart()) {
            Map<String, String[]> map = new HashMap<String, String[]>();
            for (Enumeration enumer = this.getParameterNames(); enumer.hasMoreElements();) {
                String name = (String) enumer.nextElement();
                map.put(name, this.getParameterValues(name));
            }
            return Collections.unmodifiableMap(map);
        }

        return super.getParameterMap();
    }

    /**
     * Finds a matching item in uploadItems and returns its value as a string.
     * Returns null if the 'name' parameter is null, the named file item doesn't
     * exist, the file item does exist but it isn't a simple form field (in which
     * case the value is not provided as parameter data, only as file data), or
     * the request is not multipart.
     * 
     * @param name a String specifying the name of the parameter
     * @return the result of FileItem.getString(), first trying the encoding 
     *     of the request and falling back to the system default if that fails
     * @see #getFileItem(String)
     * @see FileItem#getString(String)
     */
    protected String getUploadItemValueByName(String name) {
        if (name == null) {
            return null;
        }

        List<FileItem> items = getFileItems(name);
        FileItem item = items.isEmpty() ? null : items.get(0);

        if (item == null || !item.isFormField()) {
            return null;
        }

        try {
            return item.getString(this.getCharacterEncoding());
        } catch (Exception e) {
            return item.getString();
        }
    }

    /**
     * Finds a matching item in uploadItems and returns it. Returns null if
     * the 'name' parameter is null or the request is not multipart.
     * 
     * @param name a String specifying the name of the parameter
     * @return the matching FileItem
     */
    @Override
    public List<FileItem> getFileItems(String name) {
        if (name == null) {
            return null;
        }
        if (!isMultipart()) {
            return null;
        }

        ArrayList<FileItem> items = new ArrayList<>();
        for (FileItem item : uploadItemList) {
            if (name.equals(item.getFieldName())) {
                items.add(item);
            }
        }

        return items;
    }

    public FileItem getFileItem(String name) {
        List<FileItem> items = getFileItems(name);
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.get(0);
    }

    /**
     * Specifies whether the current request is multipart
     * 
     * @return true if multipart, false if not
     */
    public boolean isMultipart() {
        return isMultipart;
    }

    protected List<FileItem> getUploadItemList() {
        return uploadItemList;
    }

    /**
     * Find all FileItems matching the name provided and return there
     * values.
     * @param name a String specifying the name of the field
     * @return the String values of the File Items.
     */
    protected String[] getUploadItemValuesByName(String name) {
        if (name == null) {
            return null;
        }

        ArrayList<FileItem> items = getFileItemsByName(name);
        ArrayList<String> values = new ArrayList<String>();

        for (FileItem item : items) {
            if (item == null || !item.isFormField()) {
                continue;
            }
            try {
                values.add(item.getString(this.getCharacterEncoding()));
            } catch (Exception e) {
                values.add(item.getString());
            }
        }

        if (values.isEmpty()) {
            return null;
        } else {
            return values.toArray(new String[values.size()]);
        }
    }

    /**
     * Find all FileItems matching the name provided. Returns null if
     * the 'name' parameter is null or the request is not multipart.
     * @param name a String specifying the name of the field
     * @return the FileItem with field names that match the provided name
     exactly.
     */
    public ArrayList<FileItem> getFileItemsByName(String name) {
        if (name == null) {
            return null;
        }

        if (!isMultipart()) {
            return null;
        }

        ArrayList<FileItem> items = new ArrayList<FileItem>();

        for (FileItem fileItem : getUploadItemList()) {
            if (fileItem == null) {
                continue;
            }
            if (name.equals(fileItem.getFieldName())) {
                items.add(fileItem);
            }
        }

        return items;
    }

    /**
     * Utility method that determines whether the request contains multipart
     * content.
     *
     * @param request The servlet request to be evaluated. Must be non-null.
     *
     * @return <code>true</code> if the request is multipart;
     *         <code>false</code> otherwise.
     */
    public static boolean isMultipartContent(HttpServletRequest request) {
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.toLowerCase().startsWith(MULTIPART);
    }

    @Override
    public Map<String, List<Object>> getContentValues() {
        if (req instanceof HttpRequest) {
            return ((HttpRequest) req).getContentValues();
        } else {
            Enumeration<String> e = getParameterNames();
            Map<String, List<Object>> map = new TreeMap<>();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                map.put(key, getValue(this, key));
            }

            e = this.getFileParameterNames();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                map.put(key, (List) getFileItems(key));
            }
            return map;
        }
    }

    private List<Object> getValue(ServletRequest request, Object key) {
        if (key == null)
            key = "~Null~";
        String[] vals = request.getParameterValues(key.toString());
        if (vals == null)
            return null;
        else if (vals.length == 1)
            return (List) Arrays.asList(transformValue(vals[0]));
        else {

            return (List) Arrays.asList(vals);
        }
    }

    private Object transformValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value.equals("~Null~")) {
            return null;
        } else {
            return value;
        }

    }
}
