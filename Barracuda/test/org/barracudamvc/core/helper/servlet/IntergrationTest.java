/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */

package org.barracudamvc.core.helper.servlet;

import java.io.ByteArrayInputStream;
import java.util.Map;
import org.barracudamvc.testbed.servlet.MockHttpServletRequest;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class IntergrationTest {

    @Test
    public void test(){
        
        ServletWrapperFactory factory = new ServletWrapperFactory();
        MockHttpServletRequest rawRequest = new MockHttpServletRequest();
        rawRequest.setMethod("LIST");
        rawRequest.setInputStream(new ByteArrayInputStream("key=value".getBytes()));
        rawRequest.setContentType("application/x-www-form-urlencoded; charset=UTF-8");

        CustomRequestWrapper wrappedRequest = (CustomRequestWrapper) factory.create(rawRequest);
        
        Map values = wrappedRequest.getContentValues();
        Assert.assertThat(values.size(), Is.is(1));
        Assert.assertThat((Iterable<String>)values.get("key"), hasItem("value"));
        
    }
    
}
