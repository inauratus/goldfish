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
 * $Id: MaxLengthValidatorTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import java.util.Date;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

/**
 * This test verifies that MaxLengthValidator works correctly.
 */
public class MaxLengthValidatorTest extends ValidatorTestCase {

    @Test
    public void testString() {
        //create a form validator and excercise it
        MaxLengthValidator v1 = new MaxLengthValidator(-1);
        MaxLengthValidator v2 = new MaxLengthValidator(0);
        MaxLengthValidator v3 = new MaxLengthValidator(5);

        // ilc_022502.1_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid string -1 length", null);
        sm.putState("invalid string -1 length1", "");
        sm.putState("invalid string -1 length1", "   ");
        assertAllValid(v1, sm, FormType.STRING);

        sm = null;
        sm = new DefaultStateMap();
        // we've decided "" is equivalant of null
        //sm.putState("invalid string -1 length1", "");
        sm.putState("invalid string -1 length2", "foo");
        sm.putState("invalid string -1 length3", "foofoo");
        assertAllInvalid(v1, sm, FormType.STRING);

        // Validate 0 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid string 0 length1", null);
        sm.putState("valid string 0 length2", "");
        assertAllValid(v2, sm, FormType.STRING);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("Invalid string 0 length1", "f");
        sm.putState("Invalid string 0 length1", "foo");
        sm.putState("invalid string 0 length2", "foofoo");
        assertAllInvalid(v2, sm, FormType.STRING);

        // Validate 5 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid string 5 length1", null);
        sm.putState("valid string 5 length2", "");
        sm.putState("valid string 5 length3", "foo");
        sm.putState("valid string 5 length3", "foofo");
        assertAllValid(v3, sm, FormType.STRING);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid string 5 length1", "foofoo");
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
        MaxLengthValidator v = new MaxLengthValidator(5);
        Boolean bT = new Boolean(true);
        Boolean bF = new Boolean(false);

        // ilc_022502.3_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
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
        MaxLengthValidator v1 = new MaxLengthValidator(-1);
        MaxLengthValidator v2 = new MaxLengthValidator(0);
        MaxLengthValidator v3 = new MaxLengthValidator(4);
        Integer i = new Integer(1234);

        // ilc_022502.5_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid integer -1 length", null);
        assertAllValid(v1, sm, FormType.INTEGER);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid integer -1 length", i);
        assertAllInvalid(v1, sm, FormType.INTEGER);

        // Validate 0 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid integer 0 length", null);
        assertAllValid(v2, sm, FormType.INTEGER);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid integer 0 length", i);
        assertAllInvalid(v2, sm, FormType.INTEGER);

        // Validate 5 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid integer 5 length1", null);
        sm.putState("valid integer 5 length2", i);
        assertAllValid(v3, sm, FormType.INTEGER);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid integer 0 length", new Integer(12345));
        assertAllInvalid(v3, sm, FormType.INTEGER);
    }

    /**
     * Test Date types - this test ensures that if we try to validate 
     * the length on a Date it will always generate a ValidationException
     */
    @Test
    public void testDate() {
        //create a form validator and excercise it
        MaxLengthValidator v = new MaxLengthValidator(5);
        Date d = new Date();

        // ilc_022502.7_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid date 5 length", null);
        assertAllValid(v, sm, FormType.DATE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid date 5 length1", d);
        assertAllInvalid(v, sm, FormType.BOOLEAN);
        // ilc_022502.7_end

        // ilc_022502.8_start
        // use StateMap to test
        /*
         //v
         assertValid("Error validating null date", v, el, null);
         assertInvalid("Error invalidating Date", v, el, d);
         */
        // ilc_022502.8_end
    }

    /**
     * Test Long types
     */
    @Test
    public void testLong() {
        //create a form validator and excercise it
        MaxLengthValidator v1 = new MaxLengthValidator(-1);
        MaxLengthValidator v2 = new MaxLengthValidator(0);
        MaxLengthValidator v3 = new MaxLengthValidator(5);
        Long l = new Long(123);

        // ilc_022502.9_start
        // use a StateMap for testing to make sure elements go through mapping
        Long l4 = new Long(1234);
        Long l5 = new Long(12345);
        Long l6 = new Long(123456);

        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid long -1 length", null);
        assertAllValid(v1, sm, FormType.LONG);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long -1 length1", l);
        assertAllInvalid(v1, sm, FormType.LONG);

        // validate 0 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 0 length1", null);
        assertAllValid(v2, sm, FormType.LONG);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 0 length1", l);
        assertAllInvalid(v2, sm, FormType.LONG);

        // validate 5 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 5 length1", null);
        sm.putState("invalid long 5 length2", l4);
        sm.putState("invalid long 5 length3", l5);
        assertAllValid(v3, sm, FormType.LONG);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid long 5 length1", l6);
        assertAllInvalid(v3, sm, FormType.LONG);

        // ilc_022502.9_end
        // ilc_022502.10_start
        // use StateMap to test
        /*
         //v1
         assertValid("Error validating null.length()<=-1", v1, el, null);
         assertInvalid("Error invalidating l.length()<=-1", v1, el, l);
        
         //v2
         assertValid("Error validating null.length()<=0", v2, el, null);
         assertInvalid("Error invalidating l.length()<=0", v2, el, l);
        
         //v3
         assertValid("Error validating null.length()<=5", v3, el, null);
         assertValid("Error validating l.length()<=5", v3, el, l);
         */
        // ilc_022502.10_end
    }

    /**
     * Test Short types
     */
    @Test
    public void testShort() {
        //create a form validator and excercise it
        MaxLengthValidator v1 = new MaxLengthValidator(-1);
        MaxLengthValidator v2 = new MaxLengthValidator(0);
        MaxLengthValidator v3 = new MaxLengthValidator(5);
        Short s = new Short((short) 123);

        // ilc_022502.11_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid short -1 length", null);
        assertAllValid(v1, sm, FormType.SHORT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid short -1 length1", s);
        assertAllInvalid(v1, sm, FormType.SHORT);

        // validate 0 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid short 0 length1", null);
        assertAllValid(v2, sm, FormType.SHORT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid short 0 length1", s);
        assertAllInvalid(v2, sm, FormType.SHORT);

        // validate 5 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid short 5 length1", null);
        sm.putState("valid short 5 length2", s);
        assertAllValid(v3, sm, FormType.SHORT);

        // ilc_022502.11_end
        // ilc_022502.12_start
        // use StateMap to test
        //v1
        /*
         assertValid("Error validating null.length()<=-1", v1, el, null);
         assertInvalid("Error invalidating s.length()<=-1", v1, el, s);
        
         //v2
         assertValid("Error validating null.length()<=0", v2, el, null);
         assertInvalid("Error invalidating s.length()<=0", v2, el, s);
        
         //v3
         assertValid("Error validating null.length()<=5", v3, el, null);
         assertValid("Error validating s.length()<=5", v3, el, s);
         */
        // ilc_022502.12_end
    }

    /**
     * Test Double types
     */
    @Test
    public void testDouble() {
        //create a form validator and excercise it
        MaxLengthValidator v1 = new MaxLengthValidator(-1);
        MaxLengthValidator v2 = new MaxLengthValidator(0);
        MaxLengthValidator v3 = new MaxLengthValidator(5);
        Double d = new Double(123);

        // ilc_022502.13_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid double -1 length", null);
        assertAllValid(v1, sm, FormType.DOUBLE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid double -1 length1", d);
        assertAllInvalid(v1, sm, FormType.DOUBLE);

        // validate 0 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid double 0 length1", null);
        assertAllValid(v2, sm, FormType.DOUBLE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid double 0 length1", d);
        assertAllInvalid(v2, sm, FormType.DOUBLE);

        // validate 5 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid double 5 length1", null);
        sm.putState("valid double 5 length2", d);
        assertAllValid(v3, sm, FormType.DOUBLE);

        // ilc_022502.13_end
        // ilc_022502.14_start
        // use StateMap to test
        /*
         //v1
         assertValid("Error validating null.length()<=-1", v1, el, null);
         assertInvalid("Error invalidating d.length()<=-1", v1, el, d);
        
         //v2
         assertValid("Error validating null.length()<=0", v2, el, null);
         assertInvalid("Error invalidating d.length()<=0", v2, el, d);
        
         //v3
         assertValid("Error validating null.length()<=5", v3, el, null);
         assertValid("Error validating d.length()<=5", v3, el, d);
         */
        // ilc_022502.14_end
    }

    /**
     * Test Float types
     */
    @Test
    public void testFloat() {
        //create a form validator and excercise it
        MaxLengthValidator v1 = new MaxLengthValidator(-1);
        MaxLengthValidator v2 = new MaxLengthValidator(0);
        MaxLengthValidator v3 = new MaxLengthValidator(5);
        Float f = new Float(1.23);

        // ilc_022502.15_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        // Validate -1 max length
        sm.putState("valid float -1 length", null);
        assertAllValid(v1, sm, FormType.FLOAT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid float -1 length1", f);
        assertAllInvalid(v1, sm, FormType.FLOAT);

        // validate 0 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid float 0 length1", null);
        assertAllValid(v2, sm, FormType.FLOAT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("invalid float 0 length1", f);
        assertAllInvalid(v2, sm, FormType.FLOAT);

        // validate 5 max length
        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid float 5 length1", null);
        sm.putState("valid float 5 length2", f);
        assertAllValid(v3, sm, FormType.FLOAT);

        // ilc_022502.15_end
        // ilc_022502.16_start
        // use StateMap to test
        /*
         //v1
         assertValid("Error validating null.length()<=-1", v1, el, null);
         assertInvalid("Error invalidating f.length()<=-1", v1, el, f);
        
         //v2
         assertValid("Error validating null.length()<=0", v2, el, null);
         assertInvalid("Error invalidating f.length()<=0", v2, el, f);
        
         //v3
         assertValid("Error validating null.length()<=5", v3, el, null);
         assertValid("Error validating f.length()<=5", v3, el, f);
         */
        // ilc_022502.16_end
    }

}
