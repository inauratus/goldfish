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
 * $Id: TestMinLengthValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import java.util.Date;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

public class MinLengthValidatorTest extends ValidatorTestCase {

    @Test
    public void testString() {
        //create a form validator and excercise it
        MinLengthValidator v1 = new MinLengthValidator(-1);
        MinLengthValidator v2 = new MinLengthValidator(0);
        MinLengthValidator v3 = new MinLengthValidator(5);

        // ilc_022602.1_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        // we've decided "" is equivalant of null
        sm.putState("valid string -1 length", null);
        sm.putState("valid string -1 length1", "");
        sm.putState("valid string -1 length1", "   ");
        assertAllValid(v1, sm, FormType.STRING);

        // Validate 0 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid string 0 length1", null);
        sm.putState("valid string 0 length2", "");
        assertAllValid(v2, sm, FormType.STRING);

        // Validate 5 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid string 5 length1", null);
        sm.putState("valid string 5 length2", "");
        sm.putState("valid string 5 length3", "12345");
        sm.putState("valid string 5 length4", "123456");
        assertAllValid(v3, sm, FormType.STRING);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid string 5 length1", "1234");
        assertAllInvalid(v3, sm, FormType.STRING);

    }

    /**
     * Test Boolean types - this test ensures that if we pass in a
     * null FormElement or try to validate the length on a Boolean
     * it will always generate a ValidationException
     */
    @Test
    public void testBoolean() {
        //create a form validator and excercise it
        MinLengthValidator v = new MinLengthValidator(5);
        Boolean bT = new Boolean(true);
        Boolean bF = new Boolean(false);

        // ilc_022602.3_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid bool 5 length", null);
        assertAllValid(v, sm, FormType.BOOLEAN);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid bool 5 length1", bT);
        sm.putState("invalid bool 5 length1", bF);
        assertAllInvalid(v, sm, FormType.BOOLEAN);

    }

    /**
     * Test Integer types
     */
    @Test
    public void testInteger() {
        //create a form validator and excercise it
        MinLengthValidator v1 = new MinLengthValidator(-1);
        MinLengthValidator v2 = new MinLengthValidator(0);
        MinLengthValidator v3 = new MinLengthValidator(4);
        Integer i = new Integer(1234);

        // ilc_022602.5_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid integer -1 length", null);
        assertAllValid(v1, sm, FormType.INTEGER);

        // Validate 0 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid integer 0 length", null);
        assertAllValid(v2, sm, FormType.INTEGER);

        // Validate 5 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid integer 5 length1", null);
        sm.putState("valid integer 5 length2", i);
        assertAllValid(v3, sm, FormType.INTEGER);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid integer 0 length", new Integer(123));
        assertAllInvalid(v3, sm, FormType.INTEGER);
    }

    /**
     * Test Date types - this test ensures that if we try to validate
     * the length on a Date it will always generate a ValidationException
     */
    @Test
    public void testDate() {
        //create a form validator and excercise it
        MinLengthValidator v = new MinLengthValidator(5);
        Date d = new Date();

        // ilc_022602.7_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid date 5 length", null);
        assertAllValid(v, sm, FormType.DATE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid date 5 length1", d);
        assertAllInvalid(v, sm, FormType.BOOLEAN);
    }

    /**
     * Test Long types
     */
    @Test
    public void testLong() {
        //create a form validator and excercise it
        MinLengthValidator v1 = new MinLengthValidator(-1);
        MinLengthValidator v2 = new MinLengthValidator(0);
        MinLengthValidator v3 = new MinLengthValidator(5);
        Long l = new Long(123);

        // ilc_022602.9_start
        // use a StateMap for testing to make sure elements go through mapping
        Long l4 = new Long(1234);
        Long l5 = new Long(12345);
        Long l6 = new Long(123456);

        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid long -1 length1", null);
        sm.putState("valid long -1 length2", l);
        sm.putState("valid long -1 length3", l4);
        sm.putState("valid long -1 length4", l5);
        sm.putState("valid long -1 length4", l6);
        assertAllValid(v1, sm, FormType.LONG);

        // validate 0 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 0 length1", null);
        sm.putState("invalid long 0 length2", l);
        sm.putState("invalid long 0 length3", l4);
        sm.putState("invalid long 0 length4", l5);
        sm.putState("invalid long 0 length5", l6);
        assertAllValid(v2, sm, FormType.LONG);

        // validate 5 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 5 length1", null);
        sm.putState("invalid long 5 length2", l5);
        sm.putState("invalid long 5 length3", l6);
        assertAllValid(v3, sm, FormType.LONG);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 5 length1", l4);
        assertAllInvalid(v3, sm, FormType.LONG);
    }

    /**
     * Test Short types
     */
    @Test
    public void testShort() {
        //create a form validator and excercise it
        MinLengthValidator v1 = new MinLengthValidator(-1);
        MinLengthValidator v2 = new MinLengthValidator(0);
        Short s = new Short((short) 123);

        // ilc_022602.11_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid short -1 length", null);
        sm.putState("valid short -1 length2", s);
        assertAllValid(v1, sm, FormType.SHORT);

        // validate 0 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid short 0 length1", null);
        sm.putState("valid short 0 length2", s);
        assertAllValid(v2, sm, FormType.SHORT);

        // validate 5 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid short 5 length1", null);
        sm.putState("valid short 5 length2", s);
        assertAllValid(v1, sm, FormType.SHORT);
    }

    /**
     * Test Double types
     */
    @Test
    public void testDouble() {
        //create a form validator and excercise it
        MinLengthValidator v1 = new MinLengthValidator(-1);
        MinLengthValidator v2 = new MinLengthValidator(0);
        MinLengthValidator v3 = new MinLengthValidator(5);
        Double d = new Double(123);

        // ilc_022602.13_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid double -1 length", null);
        sm.putState("valid double -1 length2", d);
        assertAllValid(v1, sm, FormType.DOUBLE);

        // validate 0 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid double 0 length1", null);
        sm.putState("valid double 0 length2", d);
        assertAllValid(v2, sm, FormType.DOUBLE);

        // validate 5 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid double 5 length1", null);
        assertAllValid(v1, sm, FormType.DOUBLE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid double 5 length2", d);
        assertAllValid(v3, sm, FormType.DOUBLE);
    }

    /**
     * Test Float types
     */
    @Test
    public void testFloat() {
        //create a form validator and excercise it
        MinLengthValidator v1 = new MinLengthValidator(-1);
        MinLengthValidator v2 = new MinLengthValidator(0);
        MinLengthValidator v3 = new MinLengthValidator(5);
        Float f = new Float(1.23);
        Float f2 = new Float(1.233);

        // ilc_022602.15_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 min length
        sm.putState("valid float -1 length", null);
        sm.putState("valid float -1 length2", f);
        sm.putState("valid float -1 length3", f2);
        assertAllValid(v1, sm, FormType.FLOAT);

        // validate 0 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid float 0 length1", null);
        sm.putState("valid float 0 length2", f);
        sm.putState("valid float 0 length3", f2);
        assertAllValid(v2, sm, FormType.FLOAT);

        // validate 5 min length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid float 5 length1", null);
        sm.putState("valid float 5 length2", f2);
        assertAllValid(v3, sm, FormType.FLOAT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid float 5 length1", f);
        assertAllInvalid(v3, sm, FormType.FLOAT);
    }

}
