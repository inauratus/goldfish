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
 * $Id: DateRangeValidatorTest.java 263 2013-10-29 20:04:37Z charleslowery $
 */
package org.barracudamvc.core.forms.validators;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ValidatorTestCase;
import static org.junit.Assert.fail;
import org.barracudamvc.plankton.DateUtil;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.testbed.TestUtil;
import org.junit.Test;

/**
 * This test verifies that DateRangeValidator works correctly.
 */
public class DateRangeValidatorTest {

    static final DateFormat DATE_FORMATTER = DateFormat.getDateInstance(DateFormat.SHORT);
    static Locale[] availableLocales;

    static {
        List<Locale> locales = new ArrayList(Arrays.asList(Locale.getAvailableLocales()));
        for (Iterator<Locale> it = locales.iterator(); it.hasNext();) {
            Locale locale = it.next();
            if (locale.getLanguage().equals("th") && locale.getCountry().equals("TH")) {
                it.remove();
            }
            if (locale.getLanguage().equals("ja") && locale.getCountry().equals("JP"))
                it.remove();
        }
        availableLocales = locales.toArray(new Locale[locales.size()]);
    }

    DateRangeValidator validator;

    @Test
    public void testStringAfterToday() {
        validator = new DateRangeValidator(new Date(), null);
        assertInvalid(2001, 1, 1);
        assertInvalid(2000, 2, 29);
        assertInvalid(1999, 12, 31);

        assertValid(2033, 1, 1);
        assertValid(2036, 2, 29);
        assertValid(2999, 12, 31);
    }

    @Test
    public void testStringBeforeToday() {
        validator = new DateRangeValidator(null, new Date());
        assertValid(2001, 1, 1);
        assertValid(2000, 2, 29);
        assertValid(1999, 12, 31);

        assertInvalid(2033, 1, 1);
        assertInvalid(2036, 2, 29);
        assertInvalid(2999, 12, 31);
    }

    public void testString_RangingWithinToday() {
        validator = new DateRangeValidator(DateUtil.getFirstDateToday().getTime(), DateUtil.getLastDateToday().getTime());

        assertInvalid(2001, 1, 1);
        assertInvalid(2000, 2, 29);
        assertInvalid(1999, 12, 31);
        assertInvalid(DateUtil.getLastDateYesterday().getTime());
        assertInvalid(DateUtil.getFirstDateTomorrow().getTime());

        assertValid(new Date());
        assertValid(DateUtil.getFirstDateToday().getTime());
        assertValid(DateUtil.getLastDateToday().getTime());
    }

    @Test
    public void testLong() {
        DateRangeValidator v = new DateRangeValidator(new Date(), null);

        // ilc_022202.3_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("LongInvalid1", new Long(0));
        sm.putState("LongInvalid2", new Long(1));
        sm.putState("LongInvalid3", new Long(-1));
        sm.putState("LongInvalid4", new Long(Long.MIN_VALUE));
        sm.putState("LongInvalid5", new Long(Long.MAX_VALUE));

        ValidatorTestCase.assertAllInvalid(v, sm, FormType.LONG, availableLocales);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("LongValid1", null);
        ValidatorTestCase.assertAllValid(v, sm, FormType.LONG, availableLocales);
        // ilc_022202.3_end
    }

    @Test
    public void testBoolean() {
        DateRangeValidator v = new DateRangeValidator(new Date(), null);
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("BooleanInvalid1", Boolean.FALSE);
        sm.putState("BooleanInvalid2", Boolean.TRUE);

        ValidatorTestCase.assertAllInvalid(v, sm, FormType.BOOLEAN, availableLocales);

        sm = new DefaultStateMap();
        sm.putState("BooleanValid1", null);
        ValidatorTestCase.assertAllValid(v, sm, FormType.BOOLEAN, availableLocales);
    }

    @Test
    public void testShort() {
        DateRangeValidator v = new DateRangeValidator(new Date(), null);
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("ShortInvalid1", new Short((short) 0));
        sm.putState("ShortInvalid2", new Short((short) 1));
        sm.putState("ShortInvalid3", new Short((short) -1));
        sm.putState("ShortInvalid4", new Short(Short.MIN_VALUE));
        sm.putState("ShortInvalid5", new Short(Short.MAX_VALUE));

        ValidatorTestCase.assertAllInvalid(v, sm, FormType.SHORT, availableLocales);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("ShortValid1", null);
        ValidatorTestCase.assertAllValid(v, sm, FormType.SHORT, availableLocales);
    }

    @Test
    public void testInteger() {
        DateRangeValidator v = new DateRangeValidator(new Date(), null);

        // ilc_022202.9_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("IntegerInvalid1", new Integer(0));
        sm.putState("IntegerInvalid2", new Integer(1));
        sm.putState("IntegerInvalid3", new Integer(-1));
        sm.putState("IntegerInvalid4", new Integer(Integer.MIN_VALUE));
        sm.putState("IntegerInvalid5", new Integer(Integer.MAX_VALUE));

        ValidatorTestCase.assertAllInvalid(v, sm, FormType.INTEGER, availableLocales);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("IntegerValid1", null);
        ValidatorTestCase.assertAllValid(v, sm, FormType.INTEGER, availableLocales);
        // ilc_022202.9_end
    }

    @Test
    public void testDate() {
        DateFormat aDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        aDateFormat.setLenient(false);
        Date now = new Date();
        DateRangeValidator afterNowValidator = new DateRangeValidator(now, null);

        // ilc_022202.11_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        try {
            sm.putState("DateAfterNowInvalid1", new Date(now.getTime() - 1));
            sm.putState("DateAfterNowInvalid2", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2001", "1", "1")));
            sm.putState("DateAfterNowInvalid3", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2000", "2", "29")));
            sm.putState("DateAfterNowInvalid4", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("1999", "12", "31")));

            ValidatorTestCase.assertAllInvalid(afterNowValidator, sm, FormType.DATE, availableLocales);

            sm = null;
            sm = new DefaultStateMap();
            sm.putState("DateAfterNowValid1", null);
            sm.putState("DateAfterNowValid2", new Date());
            sm.putState("DateAfterNowValid3", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2033", "1", "1")));
            sm.putState("DateAfterNowValid4", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2036", "2", "29")));
            sm.putState("DateAfterNowValid5", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2999", "12", "31")));

            ValidatorTestCase.assertAllValid(afterNowValidator, sm, FormType.DATE, availableLocales);

            DateRangeValidator beforeNowValidator = new DateRangeValidator(null, now);

            sm = null;
            sm = new DefaultStateMap();
            sm.putState("DateBeforeNowInValid1", new Date(now.getTime() + 1));
            sm.putState("DateBeforeNowInValid2", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2033", "1", "1")));
            sm.putState("DateBeforeNowInValid3", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2036", "2", "29")));
            sm.putState("DateBeforeNowInValid4", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2999", "12", "31")));

            ValidatorTestCase.assertAllInvalid(beforeNowValidator, sm, FormType.DATE, availableLocales);

            sm = null;
            sm = new DefaultStateMap();
            sm.putState("DateBeforeNowValid1", null);
            sm.putState("DateBeforeNowValid2", new Date(now.getTime() - 1));
            sm.putState("DateBeforeNowValid3", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2001", "1", "1")));
            sm.putState("DateBeforeNowValid4", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2000", "2", "29")));
            sm.putState("DateBeforeNowValid5", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("1999", "12", "31")));

            ValidatorTestCase.assertAllValid(beforeNowValidator, sm, FormType.DATE, availableLocales);

            DateRangeValidator todayValidator = new DateRangeValidator(DateUtil.getFirstDateToday().getTime(),
                    DateUtil.getLastDateToday().getTime());
            sm = null;
            sm = new DefaultStateMap();
            sm.putState("DateTodayInvalid1", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2001", "1", "1")));
            sm.putState("DateTodayInvalid2", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2000", "2", "29")));
            sm.putState("DateTodayInvalid3", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("1999", "12", "31")));;
            sm.putState("DateTodayInvalid4", aDateFormat.parse(TestUtil.dateStringInDefaultLocaleShortForm("2036", "2", "29")));
            sm.putState("DateTodayInvalid5", DateUtil.getLastDateYesterday().getTime());
            sm.putState("DateTodayInvalid6", DateUtil.getFirstDateTomorrow().getTime());

            ValidatorTestCase.assertAllInvalid(todayValidator, sm, FormType.DATE, availableLocales);

            sm = null;
            sm = new DefaultStateMap();
            sm.putState("DateTodayValid1", null);
            sm.putState("DateTodayValid2", new Date());
            sm.putState("DateTodayValid3", DateUtil.getFirstDateToday().getTime());
            sm.putState("DateTodayValid4", DateUtil.getLastDateToday().getTime());

            ValidatorTestCase.assertAllValid(todayValidator, sm, FormType.DATE, availableLocales);

        } catch (java.text.ParseException ex) {
            fail("testDate failed for locale:" + Locale.getDefault() + " because " + ex.getMessage());
        }
        // ilc_022202.11_end
    }

    @Test
    public void testDouble() {
        DateRangeValidator v = new DateRangeValidator(new Date(), null);

        // ilc_022202.13_start
        // use a StateMap for testing to make sure elements go through mapping
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("DoubleInvalid1", new Double(0));
        sm.putState("DoubleInvalid2", new Double(1));
        sm.putState("DoubleInvalid3", new Double(-1));
        sm.putState("DoubleInvalid4", new Double(Double.MIN_VALUE));
        sm.putState("DoubleInvalid5", new Double(Double.MAX_VALUE));

        ValidatorTestCase.assertAllInvalid(v, sm, FormType.DOUBLE, availableLocales);

        sm = null;
        sm = new DefaultStateMap();
        sm.putState("DoubleValid1", null);
        ValidatorTestCase.assertAllValid(v, sm, FormType.DOUBLE, availableLocales);
        // ilc_022202.13_end
    }

    @Test
    public void testFloat() {
        DateRangeValidator v = new DateRangeValidator(new Date(), null);
        DefaultStateMap sm = new DefaultStateMap();

        sm.putState("FloatInvalid1", new Float(0));
        sm.putState("FloatInvalid2", new Float(1));
        sm.putState("FloatInvalid3", new Float(-1));
        sm.putState("FloatInvalid4", new Float(Float.MIN_VALUE));
        sm.putState("FloatInvalid5", new Float(Float.MAX_VALUE));

        ValidatorTestCase.assertAllInvalid(v, sm, FormType.FLOAT, availableLocales);

        sm = new DefaultStateMap();
        sm.putState("FloatValid1", null);
        ValidatorTestCase.assertAllValid(v, sm, FormType.FLOAT, availableLocales);
    }

    private Locale myOriginalLocale;

    public void assertInvalid(int year, int month, int day) {
        assertValidity(String.valueOf(year), String.valueOf(month), String.valueOf(day), false);
    }

    public void assertValid(int year, int month, int day) {
        assertValidity(String.valueOf(year), String.valueOf(month), String.valueOf(day), true);
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

    public void assertInvalid(Date date) {
        assertValidity(date, false);
    }

    public void assertValid(Date date) {
        assertValidity(date, true);
    }

    public void assertValidity(Date date, boolean isValid) {
        myOriginalLocale = Locale.getDefault();
        DefaultStateMap sm = new DefaultStateMap();
        String key = date.toString();
        try {
            for (Locale locale : availableLocales) {
                Locale.setDefault(locale);
                sm.putState(key, DATE_FORMATTER.format(date));
                ValidatorTestCase.assertAll(validator, sm, FormType.STRING, isValid);
            }
        } finally {
            Locale.setDefault(myOriginalLocale);
        }
    }

}
