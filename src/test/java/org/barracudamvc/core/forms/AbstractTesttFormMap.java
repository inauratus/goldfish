/*
 * Copyright (C) 2004  Christian Cryder [christianc@granitepeaks.com]
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
 * $Id: AbstractTesttFormMap.java 256 2013-03-22 18:56:49Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

public abstract class AbstractTesttFormMap {

    @Test
    public void testBooleanValue() {
        assertBooleanSet(Boolean.TRUE, "on");
        assertBooleanSet(Boolean.TRUE, "yes");
        assertBooleanSet(Boolean.TRUE, "true");
        assertBooleanSet(Boolean.TRUE, "Y");
        assertBooleanSet(Boolean.TRUE, "y");

        assertBooleanSet(null, "the");
        assertBooleanSet(null, "quick");
        assertBooleanSet(null, "brown");
        assertBooleanSet(null, "fox");
        assertBooleanSet(null, null);

        assertBooleanSet(Boolean.FALSE, "off");
        assertBooleanSet(Boolean.FALSE, "no");
        assertBooleanSet(Boolean.FALSE, "false");
        assertBooleanSet(Boolean.FALSE, "N");
        assertBooleanSet(Boolean.FALSE, "n");
    }

    @Test
    public void testStringValue() {
        assertStringSet("Blah", "Blah");
        assertStringSet((String) null, null);
        assertStringSet((String) "    ", "    ");
        assertStringSet((String) null, "");
    }

    @Test
    public void testIntegerValue() {
        assertInteger(1, "1");
        assertInteger(1776, "1776");
        assertInteger(-1999, "-1999");
        assertInteger(null, null);
        assertInteger(null, "");
    }

    @Test
    public void testLongValue() {
        assertLong((long) 1, "1");
        assertLong((long) 1776, "1776");
        assertLong((long) -1999, "-1999");
        assertLong(null, null);
        assertLong(null, "");
    }

    @Test
    public void testDoubleValue() {
        assertDouble((double) 1, "1");
        assertDouble((double) 1776, "1776");
        assertDouble((double) -1999, "-1999");
        assertDouble(null, null);
        assertDouble(null, "");
    }

    @Test
    public void testShortValue() {
        assertShort((short) 1, "1");
        assertShort((short) 1776, "1776");
        assertShort((short) -1999, "-1999");
        assertShort(null, null);
        assertShort(null, "");
    }

    @Test
    public void testFloatValue() {
        assertFloat((float) 1.0, "1.0");
        assertFloat((float) 1.0, "1");
        assertFloat((float) 12.54, "12.54");
        assertFloat((float) -12.54, "-12.54");
        assertFloat(null, null);
        assertFloat(null, "");
    }

    @Test
    public void testBigDecimalValue() {
        assertBigDecimal("6.1234", "6.1234");
        assertBigDecimal("1", "1");
        assertBigDecimal("12.54", "12.54");
        assertBigDecimal("-12.54", "-12.54");
        assertBigDecimal(null, null);
        assertBigDecimal(null, "");
    }

    @Test
    public void testDateValue() throws Exception {
        DateFormat df = DateFormat.getDateInstance();
        Date sampleDate = df.parse(df.format(new Date()));
        assertDate(sampleDate, df.format(sampleDate));
        assertDate(null, (String) null);
    }

    @Test
    public void testDateValue_date() throws Exception {
        DateFormat df = DateFormat.getDateInstance();
        Date sampleDate = df.parse(df.format(new Date()));
        assertDate(sampleDate, sampleDate);
    }

    @Test
    public void testTimeValue() throws Exception {
        DateFormat df = DateFormat.getTimeInstance();
        Time sampleTime = new Time(df.parse(df.format(new Date())).getTime());
        assertTime(sampleTime, df.format(sampleTime));
        assertTime(null, null);
    }

    public void assertTime(Time result, String data) {
        assertSetData(result, data, FormType.TIME);
    }

    public void assertShort(Short result, String data) {
        assertSetData(result, data, FormType.SHORT);
    }

    public void assertTimestamp(Timestamp result, String data) {
        assertSetData(result, data, FormType.TIMESTAMP);
    }

    public void assertDouble(Double result, String data) {
        assertSetData(result, data, FormType.DOUBLE);
    }

    public void assertFloat(Float result, String data) {
        assertSetData(result, data, FormType.FLOAT);
    }

    public void assertBigDecimal(String result, String data) {
        assertSetData(result == null ? null : new BigDecimal(result), data, FormType.BIG_DECIMAL);
    }

    public void assertDate(Date result, String data) {
        assertSetData(result, data, FormType.DATE);
    }

    public void assertDate(Date result, Date data) {
        assertSetData(result, data, FormType.DATE);
    }

    public void assertLong(Long result, String data) {
        assertSetData(result, data, FormType.LONG);
    }

    public void assertInteger(Integer result, String data) {
        assertSetData(result, data, FormType.INTEGER);
    }

    public void assertStringSet(String[] result, List<String> data) {
        assertSetData(result, data, FormType.STRING);
    }

    public void assertStringSet(String result, String data) {
        assertSetData(result, data, FormType.STRING);
    }

    public void assertBooleanSet(Boolean result, String data) {
        assertSetData(result, data, FormType.BOOLEAN);
    }



    protected DateFormat getTimestampDateFormat(int format) {
        return DateFormat.getDateTimeInstance(format, format, Locale.getDefault());
    }

    public abstract void assertSetData(Object result, Object data, FormType formType);
}
