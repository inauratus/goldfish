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
 * $Id: DateUtil.java 271 2014-08-04 14:43:21Z charleslowery $
 */
package org.barracudamvc.plankton;

// import java specifics
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * class holding some useful date utilities
 */
public class DateUtil {

//csc_080904_1_start
    private static DateFormat dfShort = DateFormat.getDateInstance(DateFormat.SHORT);
    private static DateFormat dfMedium = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static DateFormat dfLong = DateFormat.getDateInstance(DateFormat.LONG);
    private static DateFormat dfFull = DateFormat.getDateInstance(DateFormat.FULL);
    private static DateFormat dfTimestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private static DateFormat dfShortTime = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static DateFormat dfTime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
//csc_080904_1_end

    //===========================================================================
    // CONSTRUCTORS
    //===========================================================================
    //===========================================================================
    // QUERY METHODS
    //===========================================================================
    //===========================================================================
    // MUTATOR METHODS
    //===========================================================================
//csc_080904_1_start
    //===========================================================================
    // Formatting Methods
    //===========================================================================
    /**
     * Easily format a millis value as a date (default = SHORT)
     * @since csc_080904_1
     */
    public static String getDateStr(long millis) {
        return getShortDateStr(new Date(millis));
    }

    /**
     * Easily format a date (default = SHORT).
     * @since csc_080904_1
     */
    public static String getDateStr(Date date) {
        return getShortDateStr(date);
    }

    /**
     * Easily format a date (SHORT). SHORT is completely numeric, such as 12.13.52 or 3:30pm
     * @since csc_080904_1
     */
    public static synchronized String getShortDateStr(Date date) {
        if (date == null)
            return null;
        return dfShort.format(date);
    }

    /**
     * Easily format a date (MEDIUM). MEDIUM is longer, such as Jan 12, 1952
     * @since csc_080904_1
     */
    public static synchronized String getMedDateStr(Date date) {
        if (date == null)
            return null;
        return dfMedium.format(date);
    }

    /**
     * Easily format a date (LONG). LONG is longer, such as January 12, 1952 or 3:30:32pm
     * @since csc_080904_1
     */
    public static synchronized String getLongDateStr(Date date) {
        if (date == null)
            return null;
        return dfLong.format(date);
    }

    /**
     * Easily format a date (FULL). FULL is pretty completely specified, such as Tuesday, April 12, 1952 AD or 3:30:42pm PST.
     * @since csc_080904_1
     */
    public static synchronized String getFullDateStr(Date date) {
        if (date == null)
            return null;
        return dfFull.format(date);
    }

    /**
     * Easily format the current time as a timestamp (default)
     * @since csc_080904_1
     */
    public static String getTimestampStr() {
        return getTimestampStr(System.currentTimeMillis());
    }

    /**
     * Easily format a millis value as a timestamp (default)
     * @since csc_080904_1
     */
    public static String getTimestampStr(long millis) {
        return getTimestampStr(new Date(millis));
    }

    /**
     * Easily format a date as a timestamp (SHORT date, MEDIUM time)
     * @since csc_080904_1
     */
    public static synchronized String getTimestampStr(Date timestamp) {
        if (timestamp == null)
            return null;
        return dfTimestamp.format(timestamp);
    }

    /**
     * Easily format the current time as a time string (default)
     * @since csc_080904_1
     */
    public static String getTimeStr() {
        return getTimeStr(System.currentTimeMillis());
    }

    /**
     * Easily format a millis value as a time string (default)
     * @since csc_080904_1
     */
    public static String getTimeStr(long millis) {
        return getTimeStr(new Date(millis));
    }

    /**
     * Easily format a date as a time string (MEDIUM time)
     * @since csc_080904_1
     */
    public static synchronized String getTimeStr(Date time) {
        if (time == null)
            return null;
        return dfTime.format(time);
    }

    /**
     * Easily format the current time as a short time string (default)
     * @since csc_102904_1
     */
    public static String getShortTimeStr() {
        return getShortTimeStr(System.currentTimeMillis());
    }

    /**
     * Easily format a millis value as a short time string (default)
     * @since csc_102904_1
     */
    public static String getShortTimeStr(long millis) {
        return getShortTimeStr(new Date(millis));
    }

    /**
     * Easily format a date as a short time string (SHORT time)
     */
    public static synchronized String getShortTimeStr(Date time) {
        if (time == null)
            return null;
        return dfShortTime.format(time);
    }

    /**
     * get a Calendar representing an elapsed amt of time
     * @since csc_080904_1
     */
    public static Calendar getElapsed(long elapsed) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setTimeInMillis(cal.getTimeInMillis() + elapsed);
        return cal;
    }

    /**
     * get a String describing an elapsed amt of time
     * @since csc_080904_1
     */
    public static String getElapsedStr(long elapsed) {
        Calendar cal = getElapsed(elapsed);
        int hrs = cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);
        int secs = cal.get(Calendar.SECOND);
        return (hrs > 0 ? hrs + " hrs, " : "") + ((hrs > 0 || mins > 0) ? mins + " mins, " : "") + secs + " secs" + " (" + elapsed + " millis)";
    }

    /**
     * Set up a calendar for the specified date.
     *
     * This differs from {@link Calendar#setTime(int,int,int)} in that actual
     * human dates are used (e.g. month starts at 1, not 0).
     *
     * @author shawn@lannocc.com
     */
    public static Calendar newCalendar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, day);
        return cal;
    }

    /**
     * Calls {@link newCalendar(int,int,int)} and returns the associated Date.
     *
     * @author shawn@lannocc.com
     */
    public static Date newDate(int year, int month, int day) {
        return newCalendar(year, month, day).getTime();
    }

    /**
     * get the number of days between two dates (inclusive of the latter date - so
     * the number of days between Monday of this week and Monday of next week will be 7).
     * If either date is null, it will be defaulted to today. If d1<d2, the result will be 
     * the positive # of days between the two. If d1>d2 the result will be a negative number 
     * representing the # of days between the two.
     *
     * NOTE: there's probably a simpler way of doing this, but its not just as straightforward 
     * as (int) (((d1.getTime()-d2.getTime())/86400000)+1)...the problem is, you want to know
     * _how many dates_ actually fall within this range. If someone has a better suggestion than
     * looping, feel free to speak up
     *
     * @since csc_122804_1
     */
    public static int getNumberOfDaysBetween(Date d1, Date d2) {
        if (d1 == null)
            d1 = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);

        if (d2 == null)
            d2 = new Date();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);

        int incr = ((d1.compareTo(d2) <= 0) ? 1 : -1);

        int diff = 0;
        while ((cal1.get(Calendar.DATE) != cal2.get(Calendar.DATE))
                || (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH))
                || (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR))) {
            diff += incr;
            cal1.add(Calendar.DATE, incr);
        }
        return diff;
    }

    //===========================================================================
    // HELPER METHODS
    //===========================================================================
    public static Calendar getFirstDateToday() {
        return DateUtil.getFirstDate(0);
    }

    public static Calendar getLastDateToday() {
        return DateUtil.getLastDate(0);
    }

    public static Calendar getFirstDateTomorrow() {
        return DateUtil.getFirstDate(1);
    }

    public static Calendar getLastDateTomorrow() {
        return DateUtil.getLastDate(1);
    }

    public static Calendar getFirstDateYesterday() {
        return DateUtil.getFirstDate(-1);
    }

    public static Calendar getLastDateYesterday() {
        return DateUtil.getLastDate(-1);
    }

    public static Calendar getFirstDate(int theOffsetInDays) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.add(Calendar.DATE, theOffsetInDays);
        return date;
    }

    public static Calendar getLastDate(int theOffsetInDays) {
        Calendar date = DateUtil.getFirstDate(theOffsetInDays + 1);
        date.add(Calendar.MILLISECOND, -1);
        return date;
    }

    //===========================================================================
    // DATA MEMBERS
    //===========================================================================
    //===========================================================================
    // STATIC DATA MEMBERS
    //===========================================================================
    // setup the constant for number of seconds in a day
    public static final long SECONDS_IN_DAY = 24 * 60 * 60;

    // setup the constant for number of milliseconds in a day
    public static final long MILLISECONDS_IN_DAY = SECONDS_IN_DAY * 1000;

}
