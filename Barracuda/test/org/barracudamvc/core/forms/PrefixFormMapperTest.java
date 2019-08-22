/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: TestPrefixFormMapper.java 
 * Created: Aug 14, 2013 4:49:15 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms;

import java.util.TreeMap;
import org.barracudamvc.plankton.data.MapStateMap;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class PrefixFormMapperTest extends AbstractTesttFormMap {

    public static final String PREFIX = "__pr";

    @Override
    public void assertSetData(Object result, Object data, FormType formType) {
        FormMap form = new DefaultFormMap();
        form.setFormMapper(new PrefixFormMapper(PREFIX));

        String field = "Field";

        form.defineElement(new DefaultFormElement(field, formType));

        TreeMap values = new TreeMap();
        values.put(PREFIX + field, data);

        form.map(new MapStateMap(values));
        assertEquals(result, form.getVal(field));
    }
}
