/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
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
