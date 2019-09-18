/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: MockServletOutputStream.java 
 * Created: Nov 1, 2013 9:04:59 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.testbed.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
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
