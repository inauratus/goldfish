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
 * $Id: DateUtilTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DateUtilTest {

    @Test
    public void testGetShortDateStr() throws ParseException {
        assertThat(DateUtil.getShortDateStr(date("2023-12-25 16:32:11")), is("12/25/23"));
        assertThat(DateUtil.getShortDateStr(date("2023-01-01 16:32:11")), is("1/1/23"));
    }

    @Test
    public void testGetMedDateStr() throws ParseException {
        assertThat(DateUtil.getMedDateStr(date("2023-12-25 16:32:11")), is("Dec 25, 2023"));
        assertThat(DateUtil.getMedDateStr(date("2023-01-01 16:32:11")), is("Jan 1, 2023"));
    }

    @Test
    public void testGetLongDateStr() throws ParseException {
        assertThat(DateUtil.getLongDateStr(date("2023-12-25 16:32:11")), is("December 25, 2023"));
        assertThat(DateUtil.getLongDateStr(date("2023-01-01 16:32:11")), is("January 1, 2023"));
    }
    @Test
    public void testGetFullDateStr() throws ParseException {
        assertThat(DateUtil.getFullDateStr(date("2023-12-25 16:32:11")), is("Monday, December 25, 2023"));
        assertThat(DateUtil.getFullDateStr(date("2023-01-01 16:32:11")), is("Sunday, January 1, 2023"));
    }

    @Test
    public void testGetTimestampStr() throws ParseException {
        assertThat(DateUtil.getTimestampStr(date("2023-12-25 16:32:11")), is("12/25/23 4:32:11 PM"));
        assertThat(DateUtil.getTimestampStr(date("2023-01-01 16:32:11")), is("1/1/23 4:32:11 PM"));
        assertThat(DateUtil.getTimestampStr(date("2023-01-01 01:02:01")), is("1/1/23 1:02:01 AM"));
    }

    @Test
    public void testGetTimeStr() throws ParseException {
        assertThat(DateUtil.getTimeStr(date("2023-12-25 16:32:11")), is("4:32:11 PM"));
        assertThat(DateUtil.getTimeStr(date("2023-12-25 23:55:11")), is("11:55:11 PM"));
        assertThat(DateUtil.getTimeStr(date("2023-01-01 01:02:01")), is("1:02:01 AM"));
        assertThat(DateUtil.getTimeStr(date("2023-01-01 00:00:00")), is("12:00:00 AM"));
    }

    @Test
    public void testGetShortTimeStr() throws ParseException {
        assertThat(DateUtil.getShortTimeStr(date("2023-12-25 16:32:11")), is("4:32 PM"));
        assertThat(DateUtil.getShortTimeStr(date("2023-12-25 23:55:11")), is("11:55 PM"));
        assertThat(DateUtil.getShortTimeStr(date("2023-01-01 01:02:01")), is("1:02 AM"));
        assertThat(DateUtil.getShortTimeStr(date("2023-01-01 00:00:00")), is("12:00 AM"));
    }

    private static Date date(String source) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(source);
    }

    @Test
    public void testDateDiff() throws ParseException {
        assertValidDateDiff("1-3-2005", "12-28-2004", -6);
        assertValidDateDiff("12-28-2004", "1-3-2005", 6);
    }

    public void assertValidDateDiff(String d1, String d2, int expectedDiff) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        int actualDiff = DateUtil.getNumberOfDaysBetween(sdf.parse(d1), sdf.parse(d2));
        assertTrue("err diffing days:'" + d1 + "', '" + d2 + "' got:'" + actualDiff + "' expected:'" + expectedDiff + "'", actualDiff == expectedDiff);
    }
}
