/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.http.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultipartFileContentParser implements ContentParser {

    private static FileItemFactory fileItemFactory = new DiskFileItemFactory();

    @Override
    public Map<String, List<Object>> getContent(HttpServletRequest request) {
        ServletFileUpload upload = new ServletFileUpload(fileItemFactory);

        MapOfList result = new MapOfList();
        try {
            List<FileItem> items = upload.parseRequest(request);

            for (FileItem item : items) {
                if (item.isFormField()) {
                    try {
                        result.put(item.getName(), item.getString(request.getCharacterEncoding()));
                    } catch (Exception e) {
                        result.put(item.getName(), item.getString());
                    }
                } else {
                    result.put(item.getName(), item);
                }
            }

        } catch (Exception ex) {

        }
        return result;
    }

    private static class MapOfList extends HashMap<String, List<Object>> {

        public void put(String key, Object value) {
            List<Object> container = get(key);
            if (container == null) {
                container = new ArrayList<>();
                put(key, container);
            }
            container.add(value);
        }
    }
}
