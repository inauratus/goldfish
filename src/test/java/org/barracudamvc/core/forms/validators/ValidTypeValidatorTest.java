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
 * $Id: ValidTypeValidatorTest.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import java.text.DateFormat;
import java.util.Iterator;
import org.barracudamvc.core.forms.DefaultFormElement;
import org.barracudamvc.core.forms.DefaultFormMap;
import org.barracudamvc.core.forms.DefaultFormValidator;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.plankton.data.StateMap;
import org.barracudamvc.testbed.TestUtil;
import org.junit.Test;

/**
 * This test verifies that ValidTypeValidator works correctly.
 */
public class ValidTypeValidatorTest extends ValidatorTestCase {

    /**
     * Test String types
     */
    @Test
    public void testString() {
        //start by building a statemap
        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("val_s1", "foo");
        sm.putState("val_s2", "null");
        sm.putState("val_s3", new Integer(123));

        ValidTypeValidator v = new ValidTypeValidator();
        this.assertAllValid(v, sm, FormType.STRING);
    }

    /**
     * Test Boolean types - this test ensures that if we pass in a 
     * null FormElement or try to validate the length on a Boolean
     * it will always generate a ValidationException
     */
    @Test
    public void testBoolean() {
        //start by building a statemap
        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("val_b1", "yes");
        sm.putState("val_b2", "no");
        sm.putState("val_b3", "true");
        sm.putState("val_b4", "false");
        sm.putState("val_b5", "on");
        sm.putState("val_b6", "off");

        ValidTypeValidator v = new ValidTypeValidator();

        this.assertAllValid(v, sm, FormType.BOOLEAN);

        sm = null;
        sm = new DefaultStateMap();

        sm.putState("inval_b7", "boogle");
        sm.putState("inval_b8", "blah");
        sm.putState("inval_b9", "foo");
        sm.putState("inval_b10", "bar");
        this.assertAllInvalid(v, sm, FormType.BOOLEAN);
    }

    /**
     * Test Integer types
     */
    @Test
    public void testInteger() {
        //start by building a statemap
        DefaultStateMap sm = new DefaultStateMap();
        sm.putState("val_i1", "123");
        sm.putState("val_i2", "123.0");
        sm.putState("val_i4", new Integer(123));
        sm.putState("val_i5", new Float(123));
        sm.putState("val_i6", new Double(123));
        sm.putState("val_i8", new Short((short) 123));

        // ilc_022702.3_start
        // use the assertAllValid/Invalid methods
        ValidTypeValidator v = new ValidTypeValidator();

        this.assertAllValid(v, sm, FormType.INTEGER);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("inval_i1", "3.141592654");
        sm.putState("inval_i2", "13b");
        sm.putState("inval_i3", new Long(2147483648L));
        this.assertAllInvalid(v, sm, FormType.INTEGER);
    }

    /**
     * Test Date types - this test ensures that if we try to validate 
     * the length on a Date it will always generate a ValidationException
     */
    @Test
    public void testDate() {
        //ilc_022702.4_start
        DefaultStateMap sm = new DefaultStateMap();
        ValidTypeValidator v = new ValidTypeValidator();
        DateFormat aDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        aDateFormat.setLenient(false);

        try {
            sm.putState("Valid Date1", TestUtil.dateStringInDefaultLocaleShortForm("2001", "1", "1"));
            sm.putState("Valid Date2", TestUtil.dateStringInDefaultLocaleShortForm("2000", "2", "29"));
            sm.putState("Valid Date3", TestUtil.dateStringInDefaultLocaleShortForm("2010", "12", "31"));
            sm.putState("Valid Date4", TestUtil.dateStringInDefaultLocaleShortForm("2010", "12", "31"));
            sm.putState("Valid Date5", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2001", "1", "1")));
            sm.putState("Valid Date6", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2000", "2", "29")));
            sm.putState("Valid Date5", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2010", "12", "31")));
        } catch (java.text.ParseException ex) {
            fail("testDate failed because " + ex.getMessage());
        }

        this.assertAllValid(v, sm, FormType.DATE);
    }

    /**
     * Test Long types
     */
    @Test
    public void testLong() {
        DefaultStateMap sm = new DefaultStateMap();
        ValidTypeValidator v = new ValidTypeValidator();

        sm.putState("Valid Long1", "123");
        sm.putState("Valid Long2", "1234.0");
        sm.putState("Valid Long3", new Integer(123));
        sm.putState("Valid Long4", new Float(123));
        sm.putState("Valid Long5", new Double(123));
        sm.putState("Valid Long6", new Short((short) 123));
        this.assertAllValid(v, sm, FormType.LONG);

        sm = null;
        sm = new DefaultStateMap();

        sm.putState("Invalid Long1", "foo");
        sm.putState("invalid Long2", "1234.4");
        sm.putState("invalid Long4", new Float(123.23));
        sm.putState("invalid Long5", new Double(123.44));
        this.assertAllInvalid(v, sm, FormType.LONG);
    }

    /**
     * Test Short types
     */
    @Test
    public void testShort() {
        //ilc_022702.6_start
        DefaultStateMap sm = new DefaultStateMap();
        ValidTypeValidator v = new ValidTypeValidator();

        sm.putState("Valid Short1", "123");
        sm.putState("Valid Short2", "123.0");
        sm.putState("Valid Short3", new Integer(123));
        sm.putState("Valid Short4", new Float(123));
        sm.putState("Valid Short5", new Double(123));
        sm.putState("Valid Short6", new Short((short) 123));
        this.assertAllValid(v, sm, FormType.SHORT);

        sm = null;
        sm = new DefaultStateMap();

        sm.putState("Invalid Short1", "foo");
        sm.putState("invalid Short2", "1234.4");
        sm.putState("invalid Short4", new Float(123.23));
        sm.putState("invalid Short5", new Double(123.44));
        sm.putState("invalid Short6", new Integer(123456));
        this.assertAllInvalid(v, sm, FormType.SHORT);

        //ilc_022702.6_end
    }

    /**
     * Test Double types
     */
    @Test
    public void testDouble() {
        DefaultStateMap sm = new DefaultStateMap();
        ValidTypeValidator v = new ValidTypeValidator();

        sm.putState("Valid Double1", "123");
        sm.putState("Valid Double2", "123.0");
        sm.putState("Valid Double3", new Integer(123));
        sm.putState("Valid Double4", new Float(123));
        sm.putState("Valid Double5", new Double(123));
        sm.putState("Valid Double6", new Short((short) 123));
        this.assertAllValid(v, sm, FormType.DOUBLE);

        sm = null;
        sm = new DefaultStateMap();

        sm.putState("Invalid Double1", "foo");
        this.assertAllInvalid(v, sm, FormType.DOUBLE);
    }

    /**
     * Test Float types
     */
    @Test
    public void testFloat() {
        //ilc_022702.8_start
        DefaultStateMap sm = new DefaultStateMap();
        ValidTypeValidator v = new ValidTypeValidator();

        sm.putState("Valid Float1", "123");
        sm.putState("Valid Float2", "123.0");
        sm.putState("Valid Float3", new Integer(123));
        sm.putState("Valid Float4", new Float(123));
        sm.putState("Valid Float5", new Double(123));
        sm.putState("Valid Float6", new Short((short) 123));
        this.assertAllValid(v, sm, FormType.FLOAT);

        sm = null;
        sm = new DefaultStateMap();

        sm.putState("Invalid Float1", "foo");
        this.assertAllInvalid(v, sm, FormType.FLOAT);

        //ilc_022702.8_end
    }

    protected void checkIt(StateMap sm, FormType ft) {
        //now lets create a form map
        DefaultFormMap fm = new DefaultFormMap();
        Iterator it = sm.getStateKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fm.defineElement(new DefaultFormElement(key, ft, null, new ValidTypeValidator()));
        }

        //mow map the StateMap into the FormMap        
        fm.map(sm);

        //now get all the form elements
        it = sm.getStateKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            DefaultFormElement el = (DefaultFormElement) fm.getElement(key);
            DefaultFormValidator v = (DefaultFormValidator) el.getValidator();
            // ilc_022202.2_start
            // make sure we're validating the origVal
            //if (key.startsWith("val")) assertValid("Error validating element for key:"+key, v, el, null);
            if (key.startsWith("val"))
                assertValid("Error validating element for key:" + key + " value: " + el.getOrigVal(), v, el, el.getOrigVal());
            // else assertInvalid("Error invalidating element for key:"+key, v, el, null);
            else
                assertInvalid("Error invalidating element for key:" + key + " value: " + el.getOrigVal(), v, el, el.getOrigVal());
            // ilc_022102.1_end
        }
    }

}
