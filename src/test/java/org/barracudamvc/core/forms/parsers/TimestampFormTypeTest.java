/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

public class TimestampFormTypeTest extends AbstractParser {

    @Test
    public void testValid_shortWithSpaces() {
        assertParsedShortFormat("       8/19/13 10:48 AM        ", "8/19/13 10:48 AM");
    }

    @Test
    public void testValid_short() {
        assertParsedShortFormat("8/19/13 10:48 AM", "8/19/13 10:48 AM");
        assertParsedShortFormat("8/19/13 0:00 AM", "8/19/13 0:00 AM");
        assertParsedShortFormat("8/19/13 0:00 PM", "8/19/13 0:00 PM");
        assertParsedShortFormat("8/19/1969 0:00 PM", "8/19/69 0:00 PM");
    }

    @Test
    public void testValid_medium() {
        assertParsedMediumFormat("Aug 19, 2013 10:48:53 AM", "Aug 19, 2013 10:48:53 AM");
        assertParsedMediumFormat("Aug 19, 2013 00:00:00 AM", "Aug 19, 2013 00:00:00 AM");
        assertParsedMediumFormat("Aug 19, 2013 00:00:00 PM", "Aug 19, 2013 00:00:00 PM");
        assertParsedMediumFormat("Jan 19, 2013 10:48:53 AM", "Jan 19, 2013 10:48:53 AM");
        assertParsedMediumFormat("Aug 19, 69 10:48:53 AM", "Aug 19, 69 10:48:53 AM");
        assertParsedMediumFormat("Aug 19, 13 10:48:53 AM", "Aug 19, 0013 10:48:53 AM");
    }

    @Test
    public void testValid_long() {
        assertParseLongFormat("January 20, 1969 10:57:07 AM MDT", "January 20, 1969 10:57:07 AM MDT");
        assertParseLongFormat("January 20, 1969 10:57:07 AM MST", "January 20, 1969 10:57:07 AM MST");
        assertParseLongFormat("January 20, 1969 10:57:07 AM EST", "January 20, 1969 10:57:07 AM EST");
        assertParseLongFormat("January 20, 69 10:57:07 AM EST", "January 20, 0069 10:57:07 AM EST");
        assertParseLongFormat("January 20, 69 00:00:00 AM EST", "January 20, 0069 00:00:00 AM EST");
    }

    @Test
    public void testValid_full() {
        assertParseFullFormat("Monday, August 19, 2013 10:57:56 AM MDT", "Monday, August 19, 2013 10:57:56 AM MDT");
        assertParseFullFormat("Monday, August 19, 13 10:57:56 AM MDT", "Monday, August 19, 0013 10:57:56 AM MDT");
    }

    @Test
    public void testValid_SqlDate() {
        assertParsedSimpleDate("1969-01-20 00:00:00.0", "1/20/69");
        assertParsedSimpleDate("2013-01-21 00:00:00.0", "1/21/13");
        assertParsedSimpleDate("2013-08-19 00:00:00.0", "8/19/13");
    }

    public void assertParsedShortFormat(String toParse, String resultString) {
        assertParsed(toParse, parseTimestamp(resultString, "M/dd/yy hh:mm a"));
    }

    public void assertParsedMediumFormat(String toParse, String resultString) {
        assertParsed(toParse, parseTimestamp(resultString, "MMM dd, yyyy hh:mm:ss a"));
    }

    public void assertParseLongFormat(String toParse, String resultString) {
        assertParsed(toParse, parseTimestamp(resultString, "MMM dd, yyyy hh:mm:ss a zz"));
    }

    public void assertParseFullFormat(String toParse, String resultString) {
        assertParsed(toParse, parseTimestamp(resultString, "E, MMM dd, yyyy hh:mm:ss a zz"));
    }

    protected DateFormat getShortDateFormatter() {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        formatter.setLenient(false);
        return formatter;
    }

    protected void assertParsedSimpleDate(String sourceString, String resultString) {
        assertParsed(sourceString, new Timestamp(getSimpleDate(resultString).getTime()));
    }

    protected Date getSimpleDate(String date) {
        try {
            return getShortDateFormatter().parse(date);
        } catch (java.text.ParseException ex) {
            return null;
        }
    }

    protected Timestamp parseTimestamp(String timestamp, int format) {
        try {
            return new Timestamp(getTimestampDateFormat(format).parse(timestamp).getTime());
        } catch (java.text.ParseException ex) {
            return null;
        }
    }

    protected Timestamp parseTimestamp(String timestamp, String format) {
        try {
            return new Timestamp(new SimpleDateFormat(format).parse(timestamp).getTime());
        } catch (java.text.ParseException ex) {
            return null;
        }
    }

    protected DateFormat getTimestampDateFormat(int format) {
        return DateFormat.getDateTimeInstance(format, format, Locale.getDefault());
    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return value;
    }

    @Override
    public FormType getParser() throws ParseException {
        return new TimestampFormType();
    }
}
