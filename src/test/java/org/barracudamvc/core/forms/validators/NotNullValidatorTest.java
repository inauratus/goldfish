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
 * $Id: NotNullValidatorTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import java.util.Date;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.junit.Test;

public class NotNullValidatorTest extends ValidatorTestCase {

    @Test
    public void testString() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();

        // ilc_022602.1_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        sm.putState("invalid not null2", "");
        sm.putState("invalid not null3", "   ");
        assertAllInvalid(v, sm, FormType.STRING);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null", "foofoo");
        assertAllValid(v, sm, FormType.STRING);
    }

    /**
     * Test Boolean types - this test ensures that if we pass in a 
     * null FormElement or try to validate the length on a Boolean
     * it will always generate a ValidationException
     */
    @Test
    public void testBoolean() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();

        // ilc_022602.3_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.BOOLEAN);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", new Boolean(true));
        sm.putState("valid not null2", new Boolean(false));
        assertAllValid(v, sm, FormType.BOOLEAN);

    }

    /**
     * Test Integer types
     */
    @Test
    public void testInteger() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();
        Integer i1 = new Integer(123);

        // ilc_022602.5_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.INTEGER);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", i1);
        assertAllValid(v, sm, FormType.INTEGER);

    }

    @Test
    public void testDate() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();
        Date d = new Date();

        // ilc_022602.7_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.DATE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", d);
        assertAllValid(v, sm, FormType.DATE);
    }

    /**
     * Test Long types
     */
    @Test
    public void testLong() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();
        Long l1 = new Long(123);

        // ilc_022602.9_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.LONG);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", l1);
        assertAllValid(v, sm, FormType.LONG);

    }

    @Test
    public void testShort() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();
        Short s1 = new Short((short) 123);

        // ilc_022602.11_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.SHORT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", s1);
        assertAllValid(v, sm, FormType.SHORT);
    }

    @Test
    public void testDouble() {
        //create a form validator and excercise it
        NotNullValidator v = new NotNullValidator();
        Double d1 = new Double(123);

        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.DOUBLE);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", d1);
        assertAllValid(v, sm, FormType.DOUBLE);

    }

    @Test
    public void testFloat() {
        NotNullValidator v = new NotNullValidator();
        Float f1 = new Float(123);

        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("invalid not null1", null);
        assertAllInvalid(v, sm, FormType.FLOAT);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("valid not null1", f1);
        assertAllValid(v, sm, FormType.FLOAT);
    }
}
