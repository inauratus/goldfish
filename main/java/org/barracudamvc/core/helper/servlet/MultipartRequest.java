package org.barracudamvc.core.helper.servlet;

import java.util.Enumeration;
import java.util.List;
import org.apache.commons.fileupload.FileItem;

public interface MultipartRequest {

    /**
     * Finds a matching item in uploadItems and returns it. Returns null if
     * the 'name' parameter is null or the request is not multipart.
     *
     * @param name a String specifying the name of the parameter
     * @return the matching FileItem
     */
    List<FileItem> getFileItems(String name);

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
    Enumeration getFileParameterNames();
}
