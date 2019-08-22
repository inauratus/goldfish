/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.helper.servlet;

import java.util.Enumeration;
import org.barracudamvc.testbed.servlet.MockHttpServletRequest;
import org.barracudamvc.util.TestUtil;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MultipartRequestWrapperTest {

    public static void main(String args[]) {
        TestUtil.run(MultipartRequestWrapperTest.class);
    }

    @Test
    public void testWrapper() {

        MockHttpServletRequest req = new MockHttpServletRequest("foo1=blah1.a&foo2=blah2&foo3=blah3&foo1=blah1.b&foo1=blah1.c");
        req.setMethod("POST");
        req.addHeader("Content-Type", "multipart/form-data");
        try {
            req.initInputStream();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

        DefaultServletRequestWrapper wrapper = new MultipartRequestWrapper(req, 10000);
        assertTrue("wrapper check 3a.1 failed - param string not converted correctly", req.paramList.size() == 5);
        assertTrue("wrapper check 3a.2 failed - param string not converted correctly", wrapper.paramList == null);
        assertTrue("wrapper check 3b.1 failed - returned bad value [" + wrapper.getParameter("foo1") + "]", "blah1.a".equals(wrapper.getParameter("foo1")));
        assertTrue("wrapper check 3b.2 failed - returned bad value", "blah2".equals(wrapper.getParameter("foo2")));
        assertTrue("wrapper check 3b.3 failed - returned bad value", "blah3".equals(wrapper.getParameter("foo3")));
        assertTrue("wrapper check 3b.4 failed - returned bad value", wrapper.getParameter("foo4") == null);
        assertTrue("wrapper check 3c.1 failed - returned wrong enum size found [" + getSize(wrapper.getParameterNames()) + "]", getSize(wrapper.getParameterNames()) == 3);
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

    private int getSize(Enumeration enumer) {
        int cntr = 0;
        while (enumer.hasMoreElements()) {
            cntr++;
            enumer.nextElement();
        }
        return cntr;
    }
}
