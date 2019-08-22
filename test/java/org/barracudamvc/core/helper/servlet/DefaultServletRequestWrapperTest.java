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
 * $Id: TestDefaultServletRequestWrapper.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.core.helper.servlet;

import org.barracudamvc.testbed.servlet.MockHttpServletRequest;
import org.junit.Test;

import java.util.Enumeration;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * This test verifies that we can use the ServletRequestWrapper
 * to add/remove the param values
 */
public class DefaultServletRequestWrapperTest {

    /**
     * Simple test to make sure the mockup servlet request to
     * make sure it functions as expected (ie. the necessary methods
     * do what the wrapper class will expect them to do)
     */
    @Test
    public void testMockup() {

        //vars
        MockHttpServletRequest req = null;

        //try something simple first (to make sure our mockup is working)
        //...null param string
        req = new MockHttpServletRequest((String) null);
        assertTrue("mockup check 1a failed - param string not converted correctly", req.paramList.size() == 0);
        assertTrue("mockup check 1b.1 failed - returned bad value", req.getParameter("foo1") == null);
        assertTrue("mockup check 1c.1 failed - returned non-empty enum", getSize(req.getParameterNames()) == 0);
        assertTrue("mockup check 1d.1 failed - returned non-null values", req.getParameterValues("foo1") == null);

        //...unique param values
        req = new MockHttpServletRequest("foo1=blah1&foo2=blah2&foo3=blah3");
        assertTrue("mockup check 2a failed - param string not converted correctly", req.paramList.size() == 3);
        assertTrue("mockup check 2b.1 failed - returned bad value", "blah1".equals(req.getParameter("foo1")));
        assertTrue("mockup check 2b.2 failed - returned bad value", "blah2".equals(req.getParameter("foo2")));
        assertTrue("mockup check 2b.3 failed - returned bad value", "blah3".equals(req.getParameter("foo3")));
        assertTrue("mockup check 2b.4 failed - returned bad value", req.getParameter("foo4") == null);
        assertTrue("mockup check 2c.1 failed - returned wrong enum size", getSize(req.getParameterNames()) == 3);
        assertTrue("mockup check 2d.1 failed - returned wrong length", req.getParameterValues("foo1").length == 1);
        assertTrue("mockup check 2d.2 failed - returned wrong length", req.getParameterValues("foo2").length == 1);
        assertTrue("mockup check 2d.3 failed - returned wrong length", req.getParameterValues("foo3").length == 1);
        assertTrue("mockup check 2d.4 failed - returned wrong length", req.getParameterValues("foo4") == null);

        //...duplicate param values
        req = new MockHttpServletRequest("foo1=blah1.a&foo2=blah2&foo3=blah3&foo1=blah1.b&foo1=blah1.c");
        assertTrue("mockup check 3a failed - param string not converted correctly", req.paramList.size() == 5);
        assertTrue("mockup check 3b.1 failed - returned bad value", "blah1.a".equals(req.getParameter("foo1")));
        assertTrue("mockup check 3b.2 failed - returned bad value", "blah2".equals(req.getParameter("foo2")));
        assertTrue("mockup check 3b.3 failed - returned bad value", "blah3".equals(req.getParameter("foo3")));
        assertTrue("mockup check 3b.4 failed - returned bad value", req.getParameter("foo4") == null);
        assertTrue("mockup check 3c.1 failed - returned wrong enum size", getSize(req.getParameterNames()) == 3);
        assertTrue("mockup check 3d.1 failed - returned wrong length", req.getParameterValues("foo1").length == 3);
        assertTrue("mockup check 3d.2 failed - returned wrong length", req.getParameterValues("foo2").length == 1);
        assertTrue("mockup check 3d.3 failed - returned wrong length", req.getParameterValues("foo3").length == 1);
        assertTrue("mockup check 3d.4 failed - returned wrong length", req.getParameterValues("foo4") == null);
    }

    @Test
    public void test() {
        MockHttpServletRequest req  = new MockHttpServletRequest("foo1=blah1.a&foo2=blah2&foo3=blah3&foo1=blah1.b&foo1=blah1.c");

        assertThat( req.getQueryString(), is("foo1=blah1.a&foo2=blah2&foo3=blah3&foo1=blah1.b&foo1=blah1.c"));
    }

    /**
     * Simple test to make sure the wrapper allows us to
     * add/remove parameters properly.
     */
    @Test
    public void testWrapper() {

        //vars
        MockHttpServletRequest req = null;
        DefaultServletRequestWrapper wrapper = null;

        //try something simple first (to make sure our mockup is working)
        //...null param string
        req = new MockHttpServletRequest((String) null);
        wrapper = new DefaultServletRequestWrapper(req);
        assertTrue("wrapper check 1a failed - param string not converted correctly", req.paramList.size() == 0);
        assertTrue("wrapper check 1b failed - param string not converted correctly", wrapper.paramList == null);
        assertTrue("wrapper check 1b.1 failed - returned bad value", wrapper.getParameter("foo1") == null);
        assertTrue("wrapper check 1c.1 failed - returned non-empty enum", getSize(wrapper.getParameterNames()) == 0);
        assertTrue("wrapper check 1d.1 failed - returned non-null values", wrapper.getParameterValues("foo1") == null);
        wrapper.addParameter("foo99", "blah1.w");
        wrapper.addParameter("foo99", "blah1.x");
        wrapper.addParameter("foo99", "blah1.y");
        wrapper.addParameter("foo99", "blah1.z");
        assertTrue("wrapper check 1e.1 failed - returned bad value", "blah1.w".equals(wrapper.getParameter("foo99")));
        assertTrue("wrapper check 1e.2 failed - returned wrong length", wrapper.getParameterValues("foo99").length == 4);
        wrapper.removeParameter("foo99");
        assertTrue("wrapper check 1f.1 failed - returned bad value", "blah1.x".equals(wrapper.getParameter("foo99")));
        assertTrue("wrapper check 1f.2 failed - returned wrong length", wrapper.getParameterValues("foo99").length == 3);
        wrapper.removeAllParameters("foo99");
        assertTrue("wrapper check 1g.1 failed - returned bad value", wrapper.getParameter("foo99") == null);
        assertTrue("wrapper check 1g.2 failed - returned wrong length", wrapper.getParameterValues("foo99") == null);
        assertTrue("wrapper check 1g failed - param list wrong size", wrapper.paramList != null);
        wrapper.resetParameters();
        assertTrue("wrapper check 1h failed - param list wrong size", wrapper.paramList == null);

        //...unique param values
        req = new MockHttpServletRequest("foo1=blah1&foo2=blah2&foo3=blah3");
        wrapper = new DefaultServletRequestWrapper(req);
        assertTrue("wrapper check 2a.1 failed - param string not converted correctly", req.paramList.size() == 3);
        assertTrue("wrapper check 2a.2 failed - param string not converted correctly", wrapper.paramList == null);
        assertTrue("wrapper check 2b.1 failed - returned bad value", "blah1".equals(wrapper.getParameter("foo1")));
        assertTrue("wrapper check 2b.2 failed - returned bad value", "blah2".equals(wrapper.getParameter("foo2")));
        assertTrue("wrapper check 2b.3 failed - returned bad value", "blah3".equals(wrapper.getParameter("foo3")));
        assertTrue("wrapper check 2b.4 failed - returned bad value", wrapper.getParameter("foo4") == null);
        assertTrue("wrapper check 2c.1 failed - returned wrong enum size", getSize(wrapper.getParameterNames()) == 3);
        assertTrue("wrapper check 2d.1 failed - returned wrong length", wrapper.getParameterValues("foo1").length == 1);
        assertTrue("wrapper check 2d.2 failed - returned wrong length", wrapper.getParameterValues("foo2").length == 1);
        assertTrue("wrapper check 2d.3 failed - returned wrong length", wrapper.getParameterValues("foo3").length == 1);
        assertTrue("wrapper check 2d.4 failed - returned wrong length", wrapper.getParameterValues("foo4") == null);
        wrapper.addParameter("foo99", "blah1.w");
        wrapper.addParameter("foo99", "blah1.x");
        wrapper.addParameter("foo99", "blah1.y");
        wrapper.addParameter("foo99", "blah1.z");
        assertTrue("wrapper check 2e.1 failed - returned bad value", "blah1.w".equals(wrapper.getParameter("foo99")));
        assertTrue("wrapper check 2e.2 failed - returned wrong length", wrapper.getParameterValues("foo99").length == 4);
        wrapper.removeParameter("foo99");
        assertTrue("wrapper check 2f.1 failed - returned bad value", "blah1.x".equals(wrapper.getParameter("foo99")));
        assertTrue("wrapper check 2f.2 failed - returned wrong length", wrapper.getParameterValues("foo99").length == 3);
        wrapper.removeAllParameters("foo99");
        assertTrue("wrapper check 2g.1 failed - returned bad value", wrapper.getParameter("foo99") == null);
        assertTrue("wrapper check 2g.2 failed - returned wrong length", wrapper.getParameterValues("foo99") == null);
        assertTrue("wrapper check 2g failed - param list wrong size", wrapper.paramList != null);
        wrapper.resetParameters();
        assertTrue("wrapper check 2h failed - param list wrong size", wrapper.paramList == null);

        //...duplicate param values
        req = new MockHttpServletRequest("foo1=blah1.a&foo2=blah2&foo3=blah3&foo1=blah1.b&foo1=blah1.c");
        wrapper = new DefaultServletRequestWrapper(req);
        assertTrue("wrapper check 3a.1 failed - param string not converted correctly", req.paramList.size() == 5);
        assertTrue("wrapper check 3a.2 failed - param string not converted correctly", wrapper.paramList == null);
        assertTrue("wrapper check 3b.1 failed - returned bad value", "blah1.a".equals(wrapper.getParameter("foo1")));
        assertTrue("wrapper check 3b.2 failed - returned bad value", "blah2".equals(wrapper.getParameter("foo2")));
        assertTrue("wrapper check 3b.3 failed - returned bad value", "blah3".equals(wrapper.getParameter("foo3")));
        assertTrue("wrapper check 3b.4 failed - returned bad value", wrapper.getParameter("foo4") == null);
        assertTrue("wrapper check 3c.1 failed - returned wrong enum size", getSize(wrapper.getParameterNames()) == 3);
        assertTrue("wrapper check 3d.1 failed - returned wrong length", wrapper.getParameterValues("foo1").length == 3);
        assertTrue("wrapper check 3d.2 failed - returned wrong length", wrapper.getParameterValues("foo2").length == 1);
        assertTrue("wrapper check 3d.3 failed - returned wrong length", wrapper.getParameterValues("foo3").length == 1);
        assertTrue("wrapper check 3d.4 failed - returned wrong length", wrapper.getParameterValues("foo4") == null);
        wrapper.addParameter("foo99", "blah1.w");
        wrapper.addParameter("foo99", "blah1.x");
        wrapper.addParameter("foo99", "blah1.y");
        wrapper.addParameter("foo99", "blah1.z");
        assertTrue("wrapper check 3e.1 failed - returned bad value", "blah1.w".equals(wrapper.getParameter("foo99")));
        assertTrue("wrapper check 3e.2 failed - returned wrong length", wrapper.getParameterValues("foo99").length == 4);
        wrapper.removeParameter("foo99");
        assertTrue("wrapper check 3f.1 failed - returned bad value", "blah1.x".equals(wrapper.getParameter("foo99")));
        assertTrue("wrapper check 3f.2 failed - returned wrong length", wrapper.getParameterValues("foo99").length == 3);
        wrapper.removeAllParameters("foo99");
        assertTrue("wrapper check 3g.1 failed - returned bad value", wrapper.getParameter("foo99") == null);
        assertTrue("wrapper check 3g.2 failed - returned wrong length", wrapper.getParameterValues("foo99") == null);
        assertTrue("wrapper check 3g failed - param list wrong size", wrapper.paramList != null);
        wrapper.resetParameters();
        assertTrue("wrapper check 3h failed - param list wrong size", wrapper.paramList == null);
    }

    @Test
    public void givenURL_expectParamMapFilled() {
        MockHttpServletRequest req = null;
        DefaultServletRequestWrapper wrapper = null;
        req = new MockHttpServletRequest("a=1&b=2&c=3&c=4");
        wrapper = new DefaultServletRequestWrapper(req);


        Map<String, String[]> parameterMap = wrapper.getParameterMap();
        assertThat(parameterMap.size(), is(3));
        assertThat(parameterMap.get("a"), is(new String[]{"1"}));
        assertThat(parameterMap.get("b"), is(new String[]{"2"}));
        assertThat(parameterMap.get("c"), is(new String[]{"3", "4"}));
    }

    @Test
    public void test2() {
        MockHttpServletRequest req = null;
        DefaultServletRequestWrapper wrapper = null;
        req = new MockHttpServletRequest("a=1&b=2&c=3&c=4");
        wrapper = new DefaultServletRequestWrapper(req);
        req.paramList = null;

        assertThat(wrapper.getParameter("c"), is("3"));


        req = new MockHttpServletRequest("Point=checkPermissions&Ids=12300&_=1524256475511");
        wrapper = new DefaultServletRequestWrapper(req);
        req.paramList = null;

        assertThat(wrapper.getParameter("Point"), is("checkPermissions"));
        assertThat(wrapper.getParameter("Ids"), is("12300"));
        assertThat(wrapper.getParameter("_"), is("1524256475511"));
    }

    private int getSize(Enumeration enumer) {
        int cntr = 0;
        while (enumer.hasMoreElements()) {
            cntr++;
            enumer.nextElement();
        }
        return cntr;
    }
}
