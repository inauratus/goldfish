/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

public class RangeValidatorTest {

    @Test
    public void testInteger() {
        RangeValidator validator = new RangeValidator(1, 10);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("inrange1", 1);
        sm.putState("inrange2", 2);
        sm.putState("inrange3", 3);
        sm.putState("inrange4", 4);
        sm.putState("inrange5", 5);
        sm.putState("inrange6", 10);
        ValidatorTestCase.assertAllValid(validator, sm, FormType.INTEGER);

        sm = new DefaultStateMap();
        sm.putState("inrange-1", -1);
        sm.putState("inrange0", 0);
        sm.putState("inrange11", 11);
       sm.putState("inrange12", 12);

        ValidatorTestCase.assertAllInvalid(validator, sm, FormType.INTEGER);
    }

//    @Test
//    public void testLong() {
//        RangeValidator validator = new RangeValidator(1, 10);
//
//        DefaultStateMap sm = new DefaultStateMap();
//        sm.putState("inrange", 1);
//        sm.putState("inrange", 2);
//        sm.putState("inrange", 3);
//        sm.putState("inrange", 4);
//        sm.putState("inrange", 5);
//        sm.putState("inrange", 10);
//        ValidatorTestCase.assertAllValid(validator, sm, FormType.LONG);
//    }
}
