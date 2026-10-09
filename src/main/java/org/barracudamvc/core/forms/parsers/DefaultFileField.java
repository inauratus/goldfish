package org.barracudamvc.core.forms.parsers;

import org.apache.commons.fileupload.FileItem;
import org.barracudamvc.plankton.io.StreamUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DefaultFileField implements FileField {

    public static int DEFAULT_BUFFER_SIZE = 128;
    private final FileItem item;
    private final long size;
    private final BufferedInputStream stream;

    public DefaultFileField(FileItem item) {
        this.item = item;
        this.size = item.getSize();
        try {
            this.stream = new BufferedInputStream(item.getInputStream(), DEFAULT_BUFFER_SIZE);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String getName() {
        return item.getFieldName();
    }

    @Override
    public String getOriginalFilename() {
        return item.getName();
    }

    @Override
    public String getContentType() {
        return item.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public BufferedInputStream getInputStream() throws IOException {
        return stream;
    }

    @Override
    public void transferTo(OutputStream dest) throws IOException {
        StreamUtils.copy(getInputStream(), dest);
    }

    protected final FileItem getFileItem() {
        return item;
    }

}
