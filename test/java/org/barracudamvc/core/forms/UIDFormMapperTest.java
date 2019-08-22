/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: TestUIDFormMapper.java 
 * Created: Aug 14, 2013 5:10:03 PM
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
public class UIDFormMapperTest extends AbstractTesttFormMap {

    @Override
    public void assertSetData(Object result, Object data, FormType formType) {
        FormMap form = new DefaultFormMap();
        form.setFormMapper(new UIDFormMapper());
        form.defineElement(new DefaultFormElement("field", formType));

        String field = "field" + UIDFormMapper.UID_TOKEN;
        for (int i = 0; i < 5; i++) {
            TreeMap values = new TreeMap();
            values.put(field + i, data);
            form.map(new MapStateMap(values));
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(result, form.getVal(field + i));
        }
    }
}
