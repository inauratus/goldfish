/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: DateFormTypeTest.java 
 * Created: Aug 19, 2013 8:57:49 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.forms.parsers;

import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class DateFormTypeTest extends AbstractParser {

    @Test
    public void testInvalid() {
        assertParseFail("a");
        assertParseFail("1");
        assertParseFail("1969");
        assertParseFail("M/DD/YYYY");
    }

    @Test
    public void testValid_degenerate() {
        assertParsed(null, null);
        assertParsed("", null);
    }

    @Test
    public void testUSEnglish_SillyFormats() throws Exception {
        Date parse = (Date) getParser().parse("04/20/2023 4:00 PM");
        assertEquals(parse, new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2023-04-20 16:00"));
    }

    @Test
    public void testValid_short_withSpaces() {
        assertParsedSimpleDate("1/1/1", "1/1/0001");
        assertParsedSimpleDate("    1/20/69    ", "1/20/69");
        assertParsedSimpleDate("    1/20/69", "1/20/69");
        assertParsedSimpleDate("1/20/69    ", "1/20/69");
    }

    @Test
    public void testValid_short() {
        assertParsedSimpleDate("1/20/69", "1/20/69");
        assertParsedSimpleDate("1/21/13", "1/21/13");
        assertParsedSimpleDate("8/19/13", "8/19/13");
    }

    @Test
    public void testValid_medium() {
        assertParsedSimpleDate("Jan 20, 1969", "1/20/69");
        assertParsedSimpleDate("Jan 21, 2013", "1/21/13");
        assertParsedSimpleDate("Aug 19, 2013", "8/19/13");
    }

    @Test
    public void testValid_long() {
        assertParsedSimpleDate("January 20, 1969", "1/20/69");
        assertParsedSimpleDate("January 21, 2013", "1/21/13");
        assertParsedSimpleDate("August 19, 2013", "8/19/13");
    }

    @Test
    public void testValid_full() {
        assertParsedSimpleDate("Monday, January 20, 1969", "1/20/69");
        assertParsedSimpleDate("Monday, January 21, 2013", "1/21/13");
        assertParsedSimpleDate("Monday, August 19, 2013", "8/19/13");
    }

    @Test
    public void testValid_SqlDate() {
        assertParsedSimpleDate("1969-01-20 00:00:00.0", "1/20/69");
        assertParsedSimpleDate("2013-01-21 00:00:00.0", "1/21/13");
        assertParsedSimpleDate("2013-08-19 00:00:00.0", "8/19/13");
    }

    @Test
    public void testValid_YYYY_MM_DD() {
        assertParsedSimpleDate("1969-01-20", "1/20/69");
        assertParsedSimpleDate("2013-01-21", "1/21/13");
        assertParsedSimpleDate("2013-08-19", "8/19/13");
        assertParsedSimpleDate("12-08-2018", "12/08/18");

    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return value;
    }

    @Override
    public FormType getParser() throws ParseException {
        return new DateFormType();
    }

    protected DateFormat getShortDateFormatter() {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        formatter.setLenient(false);
        return formatter;
    }

    protected Date getSimpleDate(String date) {
        try {
            return getShortDateFormatter().parse(date);
        } catch (java.text.ParseException ex) {
            return null;
        }
    }

    protected void assertParsedSimpleDate(String sourceString, String resultString) {
        assertParsed(sourceString, getSimpleDate(resultString));
    }
}
