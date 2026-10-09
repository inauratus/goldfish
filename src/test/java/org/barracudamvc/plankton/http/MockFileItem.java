package org.barracudamvc.plankton.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MockFileItem extends DummyFileItem {

    private final byte[] raw;

    public MockFileItem(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(raw);
    }

    @Override
    public long getSize() {
        return raw.length;
    }
}
