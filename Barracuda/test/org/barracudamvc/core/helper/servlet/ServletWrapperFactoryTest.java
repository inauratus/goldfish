/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.helper.servlet;

import org.barracudamvc.testbed.servlet.MockHttpServletRequest;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class ServletWrapperFactoryTest {

    ServletWrapperFactory factory = new ServletWrapperFactory();

    @Test
    public void test_combine_given_empty_maps_expect_empty_result() {
        Map<String, String[]> source = Collections.emptyMap();
        Map<String, List<Object>> toAdd = Collections.emptyMap();

        Map<String, List<Object>> combined = factory.combine(source, toAdd);
        Assert.assertThat(combined.size(), Is.is(0));
    }
    
    
        @Test
    public void test_combine_given_values_maps_expect_empty_result() {
        Map<String, String[]> source = new HashMap<>();
        Map<String, List<Object>> toAdd = new HashMap<>();
        source.put("key", new String[]{"one", "two"});
        toAdd.put("key", Arrays.asList(new Object[]{"three"}));
        toAdd.put("key2", Arrays.asList(new Object[]{"three"}));

        Map combined = factory.combine(source, toAdd);
        assertThat(combined.size(), Is.is(2));
        assertThat((Iterable<String>)combined.get("key"), hasItems("one", "two", "three"));
        assertThat((Iterable<String>)combined.get("key2"), hasItems("three"));
    }


    @Test
    public void given_post_or_get_expect_default_wrapper() {
        assertMethodProducesType("POST", DefaultServletRequestWrapper.class);
        assertMethodProducesType("GET", DefaultServletRequestWrapper.class);
    }

    @Test
    public void given_custom_request_method_expect_custom_wrapper() {
        assertMethodProducesType("LIST", CustomRequestWrapper.class);
        assertMethodProducesType("THUMBS", CustomRequestWrapper.class);
        assertMethodProducesType("ACTION", CustomRequestWrapper.class);
        assertMethodProducesType("go", CustomRequestWrapper.class);
    }

    @Test
    public void test() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("SPECIAL");
        request.setParamStr("a=b");
        request.setContentType("application/x-www-form-urlencoded");
        request.setInputStream(new ByteArrayInputStream("b=c".getBytes()));

        HttpServletRequestWrapper requestWrapper = factory.create(request);
        Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
        assertThat(parameterMap.size(), is(2));

    }

    void assertMethodProducesType(String method, Class type) {
        MockHttpServletRequest requst = new MockHttpServletRequest();
        requst.setMethod(method);
        Assert.assertThat(factory.create(requst), IsInstanceOf.instanceOf(type));
    }

}
