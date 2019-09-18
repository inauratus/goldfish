/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms;

import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import org.hamcrest.core.Is;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DefaultFormElementTest {

    @Test
    public void testSetDefaultValue_null() {
        assertSetDefaultValue(FormType.STRING, new Object[]{}, null);
    }

    @Test
    public void testSetDefaultValue_PrimitveArray() {
        assertSetDefaultValue(FormType.STRING, new Object[]{}, new Object[]{});
        assertSetDefaultValue(FormType.STRING, new Object[]{null}, new Object[]{null});

        assertSetDefaultValue(FormType.INTEGER, new Integer[]{1, 2, 3}, new Integer[]{1, 2, 3});
        assertSetDefaultValue(FormType.STRING, new String[]{"1", "2", "3"}, new String[]{"1", "2", "3"});
    }

    @Test
    public void testSetDefaultValue_Collection() {
        assertSetDefaultValue(FormType.INTEGER, new Integer[]{1, 2, 3}, asList(new Integer[]{1, 2, 3}));
    }

    @Test
    public void testGetValue_FromCollection() {
        assertGetValue(FormType.INTEGER, 1, asList(new Integer[]{1, 2, 3}));
    }

    @Test
    public void testGetValue_FromArray() {
        assertGetValue(FormType.INTEGER, 1, new Integer[]{1, 2, 3});
    }

    @Test
    public void testGetValue_FromArrayWhenPrimitive() {
        FormElement element1 = createFormElement(FormType.INTEGER);
        element1.setDefaultVal(null);
        element1.setVal(new int[]{1,2,3});
        
        Assert.assertArrayEquals(new Integer[]{1,2,3}, (Integer[])element1.getValue());
    }

    @Test
    public void testGetValue_null() {
        assertGetValue(FormType.INTEGER, null, null);
    }

    @Test
    public void testGetValue_emptyString() {
        assertGetValue(FormType.INTEGER, "", "");
    }

    @Test
    public void testValue() {
        assertValueEquals("Hello");
        assertValueEquals(new String[]{"Hello", "Hello2"});
        assertValueEquals(new ArrayList(Arrays.asList(new String[]{"Hello", "Hello2"})));
        assertValueEquals(null);
    }

    FormElement assertValueEquals(Object value) {
        FormElement data = createFormElement(FormType.STRING);
        data.setVal(value);
        Assert.assertThat(data.getValue(), Is.is(value));
        return data;
    }

    protected void assertGetValue(FormType type, Object result, Object defaultValue) {
        FormElement element1 = createFormElement(type);
        element1.setDefaultVal(defaultValue);
        Assert.assertEquals(result, element1.getVal());
    }

    protected void assertSetDefaultValue(FormType type, Object expextedResult[], Object defaultValue) {
        FormElement element1 = createFormElement(type);
        element1.setDefaultVal(defaultValue);
        assertTrue(Arrays.equals(expextedResult, (Object[]) element1.getVals()));
    }

    public FormElement createFormElement(FormType type) {
        return new DefaultFormElement("Name", type);
    }
}
