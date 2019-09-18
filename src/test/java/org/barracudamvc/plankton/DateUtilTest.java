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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DateUtilTest {

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
