package org.barracudamvc.testbed.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

public class MockServletOutputStream extends ServletOutputStream {
    private ByteArrayOutputStream outputstream = new ByteArrayOutputStream();

    public MockServletOutputStream() {
    }

    @Override
    public void write(int b) throws IOException {
        outputstream.write(b);
    }
   
    
    public ByteArrayOutputStream getStream() {
        return outputstream;
    }
}
