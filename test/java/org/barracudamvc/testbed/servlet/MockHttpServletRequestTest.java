/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.testbed.servlet;

import java.net.MalformedURLException;
import java.net.URL;
import org.barracudamvc.util.TestUtil;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MockHttpServletRequestTest {

    public static void main(String args[]) {
        TestUtil.run(MockHttpServletRequestTest.class);
    }

    @Test
    public void test_getScheme() throws MalformedURLException {
        assertEquals("http", request("http://google.com/").getScheme());
        assertEquals("https", request("https://208.187.128.19/").getScheme());
    }

    @Test
    public void test_getServerName() throws MalformedURLException {
        assertEquals("google.com", request("http://google.com/").getServerName());
        assertEquals("208.187.128.19", request("http://208.187.128.19/").getServerName());

        assertEquals("google.com", request("http://google.com").getServerName());
        assertEquals("208.187.128.19", request("http://208.187.128.19").getServerName());
    }

    @Test
    public void test_getServerPort() throws MalformedURLException {
        assertEquals(80, request("http://google.com").getServerPort());
        assertEquals(443, request("https://google.com").getServerPort());
        assertEquals(8080, request("https://208.187.128.19:8080").getServerPort());
    }

    @Test
    public void test_getContextPath() throws MalformedURLException {
        assertEquals("", request("http://google.com/ResourceName").getContextPath());
        assertEquals("/ContextPath", request("http://google.com/ContextPath/SubPath/ResourceName?Param=1").getContextPath());
    }

    @Test
    public void test_getServletPath() throws MalformedURLException {
        assertEquals("/ServletPath/Resource", request("http://google.com/ContextPath/ServletPath/Resource").getServletPath());
        assertEquals("/ServletPath/Resource", request("http://google.com/ContextPath/ServletPath/Resource?Param=1").getServletPath());
    }

    @Test
    public void test_getQueryString() throws MalformedURLException {
        assertEquals(null, request("http://google.com/ContextPath/ServletPath/Resource").getQueryString());
        assertEquals("Param=1", request("http://google.com/ContextPath/ServletPath/Resource?Param=1").getQueryString());
    }

    @Test
    public void test_getRequestURI() throws MalformedURLException {
        assertEquals("/Resource", request("http://google.com/Resource").getRequestURI());
        assertEquals("/ContextPath/Resource", request("http://google.com/ContextPath/Resource?Param=1").getRequestURI());
        assertEquals("/ContextPath/ServletPath/Resource", request("http://google.com/ContextPath/ServletPath/Resource").getRequestURI());
    }

    private MockHttpServletRequest request(String url) throws MalformedURLException {
        return new MockHttpServletRequest(new URL(url));
    }

}
