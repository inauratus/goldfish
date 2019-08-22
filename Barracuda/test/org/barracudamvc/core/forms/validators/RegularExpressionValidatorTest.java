/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
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
 * $Id: TestEqualsValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import java.util.regex.Pattern;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

public class RegularExpressionValidatorTest {


    @Test
    public void testString() {
        //create a form validator and excercise it
        String s1 = "^[A-E]*$";
        RegularExpressionValidator v1 = new RegularExpressionValidator(Pattern.compile(s1));

        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("valid regexp1", null);
        sm.putState("valid regexp2", "AAAED");
        ValidatorTestCase.assertAllValid(v1, sm, FormType.STRING);

        sm = new DefaultStateMap();
        sm.putState("invalid regexp1", "DOES NOT MATCH");
        ValidatorTestCase.assertAllInvalid(v1, sm, FormType.STRING);
    }

}
