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
