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
 * $Id: ValidatorTestCase.java 271 2014-08-04 14:43:21Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.Iterator;
import java.util.Locale;
import junit.framework.TestCase;
import org.barracudamvc.plankton.data.StateMap;

/**
 * This test verifies that BLink works correctly.
 */
public abstract class ValidatorTestCase extends TestCase {

    //to fully test a Validator, we need to verify its behavior against
    //all supported types. Consequently, validator tests should implement
    //these methods
    public abstract void testString();

    public abstract void testBoolean();

    public abstract void testInteger();

    public abstract void testDate();

    public abstract void testLong();

    public abstract void testShort();

    public abstract void testDouble();

    public abstract void testFloat();

    public void assertAllValid(DefaultFormValidator v, DefaultFormElement el, Object[] objs) {
        for (int i = 0; i < objs.length; i++) {
            assertValid(objs[i] + " should be valid", v, el, objs[i]);
        }

    }

    public void assertAllInvalid(DefaultFormValidator v, DefaultFormElement el, Object[] objs) {
        for (int i = 0; i < objs.length; i++) {
            assertInvalid(objs[i] + " should be invalid", v, el, objs[i]);
        }

    }

    // send all the validation through a formmap.
    public static void assertAllInvalid(FormValidator fv, StateMap sm, FormType ft) {
        assertAll(fv, sm, ft, false);
    }

    public static void assertAllInvalid(FormValidator fv, StateMap sm, FormType ft, Locale... locales) {
        assertAll(fv, sm, ft, false, locales);
    }

    public static void assertAllValid(FormValidator fv, StateMap sm, FormType ft) {
        assertAll(fv, sm, ft, true);
    }

    public static void assertAllValid(FormValidator fv, StateMap sm, FormType ft, Locale... locales) {
        assertAll(fv, sm, ft, true, locales);
    }

    private static Locale myOriginalLocale;

    public static void assertAll(FormValidator fv, StateMap sm, FormType ft, boolean assertValid, Locale... locales) {
        myOriginalLocale = Locale.getDefault();
        try {
            for (Locale locale : locales) {
                Locale.setDefault(locale);
                assertAll(fv, sm, ft, assertValid);
            }
        } finally {
            Locale.setDefault(myOriginalLocale);
        }
    }

    public static void assertAll(FormValidator fv, StateMap sm, FormType ft, boolean assertValid) {
        DefaultFormMap fm = new DefaultFormMap();
        Iterator it = sm.getStateKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            fm.defineElement(new DefaultFormElement(key, ft, null, fv));
        }

        //mow map the StateMap into the FormMap
        fm.map(sm);

        it = sm.getStateKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            DefaultFormElement el = (DefaultFormElement) fm.getElement(key);
            DefaultFormValidator v = (DefaultFormValidator) el.getValidator();
            if (assertValid)
                assertValid("Error validating element for locale:" + Locale.getDefault() + " key:" + key + " value: " + el.getOrigVal(), v, el, el.getOrigVal());
            else
                assertInvalid("Error invalidating element for locale:" + Locale.getDefault() + " key:" + key + " value: " + el.getOrigVal(), v, el, el.getOrigVal());
        }

    }

    public static void assertValid(String msg, DefaultFormValidator v, DefaultFormElement el, Object o) {
        try {
            v.validateFormElement(o, el, false);
            //good, there was no error
        } catch (ValidationException ve) {
            fail(msg + " - " + ve);
        } catch(NullPointerException ex) {
            fail("NPE- validator: " + v + " ");
        }
    }

    /**
     * Validate a validator against invalid cases (we expect errors)
     */
    public static void assertInvalid(String msg, DefaultFormValidator v, DefaultFormElement el, Object o) {
        try {
            v.validateFormElement(o, el, false);
            fail(msg);
        } catch (ValidationException ve) {
            //error is expected
        }
    }
}
