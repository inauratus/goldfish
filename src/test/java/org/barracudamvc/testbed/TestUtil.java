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
 * $Id: TestUtil.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.testbed;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class just defines basic constants (BATCH_MODE, DEBUG_LEVEL)
 * and parses runtime parameters to set those constants.
 */
public class TestUtil {

    //other runtime parameters
    public static boolean BATCH_MODE = false;
    public static int DEBUG_LEVEL = 0;

    /**
     * This utility method is used to parse an argument string
     * to automatically adjust any global paramters
     *
     * Known paramters:
     * ---------------
     * batch - (opt) run the test in batch mode (as opposed to interactive Swing mode,
     *        which is the default)
     * debug_level - (opt) an int value which determines whether debug msgs show
     */
    public static void parseParams (String args[]) {
        //check to see runtime parameters
        if (args!=null) {
            for (int i=0; i<args.length; i++) {
                String parm = args[i].toLowerCase();
                if (parm.startsWith("batch=")) {
                    BATCH_MODE = (parm.toLowerCase().indexOf("true")>0);
                } else if (parm.startsWith("debug_level=")) {
                    try  {DEBUG_LEVEL = Integer.parseInt(parm.substring(12));}
                    catch (Exception e) {}
                }
            }
        }

        //show what we've got
        System.out.println ("Setting BATCH_MODE="+TestUtil.BATCH_MODE);
        System.out.println ("Setting DEBUG_LEVEL="+TestUtil.DEBUG_LEVEL);
        System.out.println ("");
    }

    public static String dateStringInDefaultLocaleShortForm(String theYear, String theMonth, String theDay) {
        Calendar aCalendar = Calendar.getInstance();
        // zero everything
        aCalendar.clear();
        aCalendar.set(2000, 0, 1);

        Date aZeroDate = aCalendar.getTime();
        DateFormat aDefaultDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        FieldPosition yearFieldPosn = new FieldPosition(DateFormat.YEAR_FIELD);
        FieldPosition monthFieldPosn = new FieldPosition(DateFormat.MONTH_FIELD);
        FieldPosition dayFieldPosn = new FieldPosition(DateFormat.DATE_FIELD);
        aDefaultDateFormat.format(aZeroDate, new StringBuffer(), yearFieldPosn);
        aDefaultDateFormat.format(aZeroDate, new StringBuffer(), monthFieldPosn);
        aDefaultDateFormat.format(aZeroDate, new StringBuffer(), dayFieldPosn);

        // sort the field positions into descending start index order
        ArrayList sortedPositions = new ArrayList();
        sortedPositions.add(yearFieldPosn);
        if (monthFieldPosn.getBeginIndex() > yearFieldPosn.getBeginIndex()) {
            sortedPositions.add(0, monthFieldPosn);
        } else {
            sortedPositions.add(monthFieldPosn);
        }
        if (dayFieldPosn.getBeginIndex() > ((FieldPosition)sortedPositions.get(0)).getBeginIndex()) {
            sortedPositions.add(0, dayFieldPosn);
        } else if (dayFieldPosn.getBeginIndex() > ((FieldPosition)sortedPositions.get(1)).getBeginIndex()) {
            sortedPositions.add(1, dayFieldPosn);
        } else {
            sortedPositions.add(dayFieldPosn);
        }

        // create string buffer with formatted current date in it (to make it sure is correct length)
        StringBuffer aStrBuff = new StringBuffer(aDefaultDateFormat.format(aZeroDate));
        for (int i = 0; i < sortedPositions.size(); i++) {
            FieldPosition currFieldPosn = (FieldPosition)sortedPositions.get(i);
            String currField = theYear;
            if (currFieldPosn==monthFieldPosn) {
                currField = theMonth;
            } else if (currFieldPosn==dayFieldPosn) {
                currField = theDay;
            }
            aStrBuff.replace(currFieldPosn.getBeginIndex(), currFieldPosn.getEndIndex(), currField);
        }
        return (aStrBuff.toString());
    }
}
