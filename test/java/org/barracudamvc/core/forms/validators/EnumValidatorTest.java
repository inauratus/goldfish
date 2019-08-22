/*
 * Copyright (C) 2007  Franck Routier
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
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

public class EnumValidatorTest {

    public enum TestEnum {

        ONE, TWO, THREE;
    };

    @Test
    public void testString() {
        EnumValidator<TestEnum> v = new EnumValidator<TestEnum>(TestEnum.class);

        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("Invalid, enum is case sensitive", "One");
        sm.putState("Invalid, not in enum", "FOUR");
        ValidatorTestCase.assertAllInvalid(v, sm, FormType.STRING);

        sm = new DefaultStateMap();
        sm.putState("valid ONE", "ONE");
        sm.putState("valid TWO", "TWO");
        sm.putState("valid THREE", "THREE");
        ValidatorTestCase.assertAllValid(v, sm, FormType.STRING);
    }

}
