package org.barracudamvc.core.forms;

import java.util.TreeMap;
import org.barracudamvc.plankton.data.MapStateMap;
import static org.junit.Assert.assertEquals;

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
