/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.forms.parsers;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Locale;
import org.barracudamvc.core.forms.FormType;
import org.barracudamvc.core.forms.ParseException;
import org.junit.Test;

public class TimeFormTypeTest extends AbstractParser {

    @Test
    public void testValid_short() throws java.text.ParseException {
        assertParseShortTime("00:31 PM", "00:31 PM");
        assertParseShortTime("2:31 PM", "2:31 PM");
        assertParseShortTime("24:00 PM", "24:00 PM");
        assertParseShortTime("22:31 PM", "22:31 PM");
    }

    @Test
    public void testValid_medium() {
        assertParseMediumTime("2:32:29 PM", "2:32:29 PM");
        assertParseMediumTime("00:00:00 AM", "00:00:00 AM");
        assertParseMediumTime("24:00:00 PM", "24:00:00 PM");
    }

    @Test
    public void testValid_long() {
        assertParseLongTime("2:32:45 PM MDT", "2:32:45 PM MDT");
        assertParseLongTime("00:00:00 AM MDT", "00:00:00 AM MDT");
        assertParseLongTime("24:00:00 PM MDT", "24:00:00 PM MDT");
    }

    @Test
    public void testValid_full() {
        assertParseFullTime("2:33:09 PM MDT", " 2:33:09 PM MDT");
        assertParseLongTime("00:00:00 AM MDT", "00:00:00 AM MDT");
        assertParseLongTime("24:00:00 PM MDT", "24:00:00 PM MDT");
    }

    @Override
    public Object convertType(Object value) throws ParseException {
        return value;
    }

    public void assertParseShortTime(String toParse, String resultString) {
        assertParsed(toParse, parseTime(resultString, DateFormat.SHORT));
    }

    public void assertParseMediumTime(String toParse, String resultString) {
        assertParsed(toParse, parseTime(resultString, DateFormat.MEDIUM));
    }

    public void assertParseLongTime(String toParse, String resultString) {
        assertParsed(toParse, parseTime(resultString, DateFormat.LONG));
    }

    public void assertParseFullTime(String toParse, String resultString) {
        assertParsed(toParse, parseTime(resultString, DateFormat.FULL));
    }

    protected Time parseTime(String timestamp, int format) {
        try {
            DateFormat formatter = DateFormat.getTimeInstance(format, Locale.getDefault());
            return new Time(formatter.parse(timestamp).getTime());
        } catch (java.text.ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public FormType getParser() throws ParseException {
        return new TimeFormType();
    }
}
