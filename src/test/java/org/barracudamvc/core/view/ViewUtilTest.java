/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: ViewUtilTest.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.core.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import org.barracudamvc.testbed.servlet.MockHttpServletRequest;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This test verifies that the ViewUtil class can parse
 * request headers to validate client capabilities
 */
public class ViewUtilTest {

    public static final String UAS_PROPS = "user-agents.properties";

    @Test
    public void testType() {

        InputStream is = ViewUtilTest.class.getResourceAsStream(UAS_PROPS);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        try {
            while (true) {
                //read a line & make sure its valid
                String s = in.readLine();
                if (s == null)
                    break;
                s = s.trim();
                if (s.startsWith("#")) {
                    continue;
                }
                if (s.equals(""))
                    continue;

                //now figure out the uas and ctype settings
                String uas;
                String cts;
                int spos = s.indexOf("==");
                if (spos == -1)
                    throw new RuntimeException("Fatal error parsing " + UAS_PROPS + " --> " + s);
                uas = s.substring(0, spos).trim();
                if (uas.equals(""))
                    throw new RuntimeException("Invalid user-agent string in " + UAS_PROPS + " --> " + s);
                cts = s.substring(spos + 2).trim();
                if (cts == null)
                    throw new RuntimeException("Fatal error parsing client type " + UAS_PROPS + " --> " + s);
                ClientType ct = ClientType.getInstance(cts);
                if (ct == null)
                    throw new RuntimeException("Invalid client type in " + UAS_PROPS + " --> " + cts);

                assertClientTypeMatch(uas, ct);
            }
        } catch (IOException e) {
        }

    }

    /**
     * Test the client types to make sure we get what we expect for a given 
     * user agent string
     */
    public void assertClientTypeMatch(String uas, ClientType ctTarget) {
        MockHttpServletRequest req = new MockHttpServletRequest((String) null);
        req.setHeader("User-Agent", uas);
        ClientType ct = ViewUtil.getClientType(req);
        assertTrue("null value returned for user-agent='" + uas + "'", ct != null);
        assertTrue("returned '" + ct + "' instead of '" + ctTarget + "' for user-agent='" + uas, ct == ctTarget);
    }

    /**
     * Simple test to make sure the mockup servlet request to
     * make sure it functions as expected (ie. the necessary methods
     * do what the wrapper class will expect them to do)
     */
    @Test
    public void testMockup() {

        //vars
        MockHttpServletRequest req = null;
        Enumeration enumer = null;
        String k1 = "foo1";
        String k2 = "foo2";
        String s1 = "blah1";
        String s2a = "blah2.a";
        String s2b = "blah2.b";

        //try something simple first (to make sure our mockup is working)
        //...null param string
        req = new MockHttpServletRequest((String) null);
        assertTrue("mockup check 1a failed - hdr map not converted correctly", req.hdrMap.size() == 0);
        assertTrue("mockup check 1b.1 failed - returned bad value", req.getHeader(k1) == null);
        enumer = req.getHeaderNames();
        assertTrue("mockup check 1c.1 failed - returned null or non-empty enum", enumer != null && !enumer.hasMoreElements());
        enumer = req.getHeaders("foo");
        assertTrue("mockup check 1d.1 failed - returned null or non-empty enum", enumer != null && !enumer.hasMoreElements());
        //...now try with some data
        req = new MockHttpServletRequest((String) null);
        req.setHeader(k1, s1);
        req.addHeader(k2, s2a);
        req.addHeader(k2, s2b);
        assertTrue("mockup check 2a failed - hdr map not converted correctly", req.hdrMap.size() == 2);
        assertTrue("mockup check 2b.1 failed - returned bad value", "blah1".equals(req.getHeader(k1)));
        enumer = req.getHeaderNames();
        for (int i = 0; i < 2; i++) {
            Object o = enumer.nextElement();
            assertTrue("mockup check 2c.1 failed - wrong element, cntr=" + i, o == k1 || o == k2);
        }
        assertTrue("mockup check 2c.2 failed - enum claims more elements", !enumer.hasMoreElements());
        assertTrue("mockup check 2d failed - wrong value", s1.equals(req.getHeader(k1)));
        enumer = req.getHeaders(k2);
        for (int i = 0; i < 2; i++) {
            Object o = enumer.nextElement();
            assertTrue("mockup check 2e.2 failed - wrong element, cntr=" + i, o == s2a || o == s2b);
        }
        assertTrue("mockup check 2e.2 failed - enum claims more elements", !enumer.hasMoreElements());
    }
}
