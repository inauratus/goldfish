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
 * $Id: EqualsValidatorTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import java.util.Date;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

public class EqualsValidatorTest extends ValidatorTestCase {

    @Test
    public void testString() {
        String s1 = "foo";
        EqualsValidator v1 = new EqualsValidator(s1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("valid equals1", null);
        sm.putState("valid equals2", "foo");
        assertAllValid(v1, sm, FormType.STRING);

        sm = new DefaultStateMap();
        sm.putState("invalid equals1", "foo2");
        assertAllInvalid(v1, sm, FormType.STRING);

        sm = new DefaultStateMap();
        sm.putState("valid equal null", null);
        assertAllValid(v2, sm, FormType.STRING);

        sm = new DefaultStateMap();
        sm.putState("invalid equal null", "foo");
        assertAllInvalid(v2, sm, FormType.STRING);

    }

    @Test
    public void testBoolean() {
        Boolean bT = new Boolean(true);
        Boolean bF = new Boolean(false);
        EqualsValidator v1 = new EqualsValidator(bT);
        EqualsValidator v2 = new EqualsValidator(bF);
        EqualsValidator v3 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("valid boolean equals true 1", null);
        sm.putState("valid boolean equals true 2", bT);
        sm.putState("valid boolean equals true 3", "true");
        sm.putState("valid boolean equals true 4", "yes");
        assertAllValid(v1, sm, FormType.BOOLEAN);

        sm = new DefaultStateMap();
        sm.putState("invalid boolean equals true 1", bF);
        sm.putState("invalid boolean equals true 2", "false");
        sm.putState("invalid boolean equals true 3", "no");
        assertAllInvalid(v1, sm, FormType.BOOLEAN);

        sm = new DefaultStateMap();
        sm.putState("valid boolean equals false 1", null);
        sm.putState("valid boolean equals false 2", bF);
        sm.putState("valid boolean equals false 3", "false");
        sm.putState("valid boolean equals false 4", "no");
        assertAllValid(v2, sm, FormType.BOOLEAN);

        sm = new DefaultStateMap();
        sm.putState("invalid boolean equals false 1", bT);
        sm.putState("invalid boolean equals false 2", "true");
        sm.putState("invalid boolean equals false 2", "yes");
        assertAllInvalid(v2, sm, FormType.BOOLEAN);

        sm = new DefaultStateMap();
        sm.putState("valid boolean equals null 1", null);
        assertAllValid(v3, sm, FormType.BOOLEAN);

        sm = new DefaultStateMap();
        sm.putState("invalid boolean equals null 1", bT);
        sm.putState("invalid boolean equals null 2", bF);
        sm.putState("invalid boolean equals null 3", "true");
        sm.putState("invalid boolean equals null 4", "false");
        sm.putState("invalid boolean equals null 5", "no");
        sm.putState("invalid boolean equals null 6", "yes");
        assertAllInvalid(v3, sm, FormType.BOOLEAN);
    }

    @Test
    public void testInteger() {
        Integer i1 = new Integer(1000);
        EqualsValidator v1 = new EqualsValidator(i1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("valid integer equals 1000 1", null);
        sm.putState("valid integer equals 1000 2", new Integer(1000));
        assertAllValid(v1, sm, FormType.INTEGER);

        sm = new DefaultStateMap();
        sm.putState("invalid integer equals 1000", new Integer(2000));
        assertAllInvalid(v1, sm, FormType.INTEGER);

        sm = new DefaultStateMap();
        sm.putState("valid integer equals null", null);
        assertAllValid(v2, sm, FormType.INTEGER);

        sm = new DefaultStateMap();
        sm.putState("invalid integer equals null", i1);
        assertAllInvalid(v2, sm, FormType.INTEGER);
    }

    @Test
    public void testDate() {
        Date d1 = new Date();
        Date d2 = new Date(d1.getTime());
        Date d3 = new Date(65000);
        EqualsValidator v1 = new EqualsValidator(d1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("1 valid date equals " + d1, null);
        sm.putState("2 valid date equals " + d1, d1);
        sm.putState("2 valid date equals " + d1, d2);
        assertAllValid(v1, sm, FormType.DATE);

        sm = new DefaultStateMap();
        sm.putState("1 invalid date equals " + d1, d3);
        assertAllInvalid(v1, sm, FormType.DATE);

        sm = new DefaultStateMap();
        sm.putState("1 valid date equals null", null);
        assertAllValid(v2, sm, FormType.DATE);

        sm = new DefaultStateMap();
        sm.putState("1 invalid date equals null", d3);
        assertAllInvalid(v2, sm, FormType.DATE);
    }

    @Test
    public void testLong() {
        Long l1 = new Long(1000);
        EqualsValidator v1 = new EqualsValidator(l1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("1 valid long equals " + l1, null);
        sm.putState("2 valid long equals " + l1, new Long(1000));
        sm.putState("3 valid long equals " + l1, "1000");
        assertAllValid(v1, sm, FormType.LONG);

        sm = new DefaultStateMap();
        sm.putState("1 invalid long equals " + l1, new Long(2000));
        sm.putState("1 invalid long equals " + l1, "2000");
        assertAllInvalid(v1, sm, FormType.LONG);

        sm = new DefaultStateMap();
        sm.putState("1 valid long equals null", null);
        sm.putState("1 valid long equals null", "");
        sm.putState("1 valid long equals null", "  ");
        assertAllValid(v2, sm, FormType.LONG);

        sm = new DefaultStateMap();
        sm.putState("1 invalid long equals null", l1);
        assertAllInvalid(v2, sm, FormType.LONG);
    }

    @Test
    public void testShort() {
        Short s1 = new Short((short) 1000);
        EqualsValidator v1 = new EqualsValidator(s1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("1 valid short equals " + s1, null);
        sm.putState("2 valid short equals " + s1, new Short((short) 1000));
        sm.putState("3 valid short equals " + s1, "1000");
        assertAllValid(v1, sm, FormType.SHORT);

        sm = new DefaultStateMap();
        sm.putState("1 invalid short equals " + s1, new Short((short) 2000));
        sm.putState("2 invalid short equals " + s1, "2000");
        assertAllInvalid(v1, sm, FormType.SHORT);

        sm = new DefaultStateMap();
        sm.putState("1 valid short equals null", null);
        sm.putState("2 valid short equals null", "");
        sm.putState("3 valid short equals null", "  ");
        assertAllValid(v2, sm, FormType.SHORT);

        sm = new DefaultStateMap();
        sm.putState("1 invalid short equals null", s1);
        assertAllInvalid(v2, sm, FormType.SHORT);
    }

    @Test
    public void testDouble() {
        Double d1 = new Double(1000);
        EqualsValidator v1 = new EqualsValidator(d1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("1 valid double equals " + d1, null);
        sm.putState("2 valid double equals " + d1, new Double(1000));
        sm.putState("3 valid double equals " + d1, "1000");
        assertAllValid(v1, sm, FormType.DOUBLE);

        sm = new DefaultStateMap();
        sm.putState("1 invalid double equals " + d1, new Double(2000));
        sm.putState("2 invalid double equals " + d1, "2000");
        assertAllInvalid(v1, sm, FormType.DOUBLE);

        sm = new DefaultStateMap();
        sm.putState("1 valid double equals null", null);
        sm.putState("2 valid double equals null", "");
        sm.putState("3 valid double equals null", "  ");
        assertAllValid(v2, sm, FormType.DOUBLE);

        sm = new DefaultStateMap();
        sm.putState("1 invalid double equals null", d1);
        assertAllInvalid(v2, sm, FormType.DOUBLE);
    }

    @Test
    public void testFloat() {
        Float f1 = new Float(1000);
        EqualsValidator v1 = new EqualsValidator(f1);
        EqualsValidator v2 = new EqualsValidator(null);

        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("1 valid float equals " + f1, null);
        sm.putState("2 valid float equals " + f1, new Float(1000));
        sm.putState("3 valid float equals " + f1, "1000");
        assertAllValid(v1, sm, FormType.FLOAT);

        sm = new DefaultStateMap();
        sm.putState("1 invalid float equals " + f1, new Float(2000));
        sm.putState("2 invalid float equals " + f1, "2000");
        assertAllInvalid(v1, sm, FormType.FLOAT);

        sm = new DefaultStateMap();
        sm.putState("1 valid float equals null", null);
        sm.putState("2 valid float equals null", "");
        sm.putState("3 valid float equals null", "  ");
        assertAllValid(v2, sm, FormType.FLOAT);

        sm = new DefaultStateMap();
        sm.putState("1 invalid float equals null", f1);
        assertAllInvalid(v2, sm, FormType.FLOAT);
    }
}
