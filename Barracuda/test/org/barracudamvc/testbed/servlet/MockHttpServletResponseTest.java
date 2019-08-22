/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.testbed.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import org.barracudamvc.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class MockHttpServletResponseTest  {

    public static void main(String args[]) {
        TestUtil.run(MockHttpServletResponseTest.class);
    }

    @Test
    public void testPrintWriter() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        final String message = "This should be in there";
        PrintWriter writer = response.getWriter();

        writer.write(message);
        byte[] bytes = response.getContentsAsBtyeArray();
        Assert.assertEquals(message, new String(bytes, Charset.defaultCharset().name()));
    }

}
