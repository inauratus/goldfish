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
 * $Id: TestDateValidator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.forms.validators;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.testbed.TestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

public class DateValidatorTest {

    static Locale[] availableLocales;
    DateValidator validator;

    static {
        List<Locale> locales = new ArrayList(Arrays.asList(Locale.getAvailableLocales()));
        for (Iterator<Locale> it = locales.iterator(); it.hasNext();) {
            Locale locale = it.next();
            if (locale.getLanguage().equals("th") && locale.getCountry().equals("TH")) {
                it.remove();
            }
        }
        availableLocales = locales.toArray(new Locale[locales.size()]);
    }

    @Before
    public void setup() {
        validator = new DateValidator();
    }

    @Test
    public void testString() {
        assertInvalid("2000", "1", "32");
        assertInvalid("2001", "2", "29");
        assertInvalid("2000", "01", "32");
        assertInvalid("2001", "02", "29");
        assertInvalid("2002", "01", "32");
        assertInvalid("2013", "02", "29");

        assertValid("2001", "1", "1");
        assertValid("2005", "1", "1");
        assertValid("2001", "12", "1");

        assertValid("1999", "12", "31");
        assertValid("2010", "12", "31");
        assertValid("2001", "01", "01");
        assertValid("2005", "01", "01");
        assertValid("2001", "12", "01");

    }

    private Locale myOriginalLocale;

    public void assertInvalid(String year, String month, String day) {
        assertValidity(year, month, day, false);
    }

    public void assertValid(String year, String month, String day) {
        assertValidity(year, month, day, true);
    }

    public void assertValidity(String year, String month, String day, boolean isValid) {
        myOriginalLocale = Locale.getDefault();
        DefaultStateMap sm = new DefaultStateMap();
        String key = "" + year + "-" + month + "day";
        try {
            for (Locale locale : availableLocales) {
                Locale.setDefault(locale);
                sm.putState(key, TestUtil.dateStringInDefaultLocaleShortForm(year, month, day));
                ValidatorTestCase.assertAll(validator, sm, FormType.STRING, isValid);
            }
        } finally {
            Locale.setDefault(myOriginalLocale);
        }
    }
}
