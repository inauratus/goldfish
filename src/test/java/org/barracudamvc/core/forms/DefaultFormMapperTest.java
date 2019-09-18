/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import org.barracudamvc.core.forms.parsers.ByteArrayFormType;
import org.barracudamvc.plankton.DateUtil;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.plankton.data.MapStateMap;
import org.barracudamvc.plankton.http.DummyFileItem;
import org.barracudamvc.plankton.http.MockFileItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class DefaultFormMapperTest extends AbstractTesttFormMap {

    @Before
    public void setup() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void given_file_element_parser_expect_converted_value() {
        byte[] bytes = "Hello World".getBytes();

        MockFileItem item = new MockFileItem(bytes);
        FormMap form = new DefaultFormMap();

        form.defineElement(new FileFormElement("Field", new ByteArrayFormType(), null));

        TreeMap values = new TreeMap();
        values.put("Field", item);

        form.map(new MapStateMap(values));
        assertTrue(Arrays.equals(bytes, (byte[]) form.getVal("Field")));
    }

    @Test
    public void testStirngMultipleValues() {
        String[] strings = new String[]{"1", "2", "3"};

        assertSetData(strings, new ArrayList(Arrays.asList(strings)), FormType.STRING);
    }

    @Test
    public void given_multiple_dates_null_first_expect_parsed_dates() {
        Date[] expected = new Date[]{null, DateUtil.newDate(2015, 5, 31)};

        assertSetData(expected, new ArrayList(Arrays.asList(new String[]{null, "5/31/15"})), FormType.DATE);
    }

    @Test
    public void given_single_valued_array_expect_single_value() {
        String[] strings = new String[]{"1"};

        assertSetData("1", strings, FormType.STRING);
    }

    @Test
    public void test() {
        DummyFileItem dummyFileItem = new DummyFileItem();

        assertSetData(null, dummyFileItem, FormType.STRING);
    }

    @Test
    public void testIsSet() {
        DefaultFormMap form = new DefaultFormMap();

        FormElement a = new DefaultFormElement("A", FormType.STRING);
        FormElement b = new DefaultFormElement("B", FormType.STRING);
        FormElement c = new DefaultFormElement("C", FormType.STRING);

        form.defineElement(a);
        form.defineElement(b);
        form.defineElement(c);

        TreeMap values = new TreeMap();
        values.put("A", "1");
        values.put("C", "1");
        form.map(new MapStateMap(values));

        assertTrue(a.isValueSet());
        assertFalse(b.isValueSet());
        assertTrue(c.isValueSet());
    }

    @Test
    public void testPassThrough() {
        DefaultFormMap form = new DefaultFormMap();

        FormElement a = new DefaultFormElement("A", FormType.DATE);
        form.defineElement(a);

        TreeMap values = new TreeMap();
        values.put("A", new Date());
        form.map(new MapStateMap(values));

        assertFalse(form.hasUnparsableValues());
        assertNull(a.getParseException());
    }

    @Test
    public void testMap_GetState() throws java.text.ParseException {

        final String key1 = "Integer_1";
        DefaultStateMap stateMap = new DefaultStateMap();
        stateMap.putState(key1, new Integer(1));

        Integer object1 = stateMap.getState(key1);

        assertTrue("Could not cast Integer from State", object1 != null);

        object1 = stateMap.getState(Integer.class, key1);

        assertTrue("Could not cast Integer from State", object1 != null);
    }

    @Override
    public void assertSetData(Object result, Object data, FormType formType) {
        FormMap form = new DefaultFormMap();

        form.defineElement(new DefaultFormElement("Field", formType));

        TreeMap values = new TreeMap();
        values.put("Field", data);

        form.map(new MapStateMap(values));
        assertEquals(result, form.getVal("Field"));
    }

    public void assertSetData(String[] result, List<String> datas, FormType formType) {
        FormMap form = new DefaultFormMap();

        form.defineElement(new DefaultFormElement("Field", formType));

        TreeMap values = new TreeMap();
        values.put("Field", datas);

        form.map(new MapStateMap(values));
        assertTrue(Arrays.equals(result, form.getVals("Field")));
    }

    public void assertSetData(Date[] result, List<String> datas, FormType formType) {
        FormMap form = new DefaultFormMap();

        form.defineElement(new DefaultFormElement("Field", formType));

        TreeMap values = new TreeMap();
        values.put("Field", datas);

        form.map(new MapStateMap(values));
        assertTrue(Arrays.equals(result, form.getVals("Field")));
    }
}
