/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface FileField {

    String getName();

    String getOriginalFilename();

    String getContentType();

    boolean isEmpty();

    long getSize();

    BufferedInputStream getInputStream() throws IOException;

    void transferTo(OutputStream dest) throws IOException;

}
