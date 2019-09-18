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
 * $Id: PArrayListTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test case for any PList
 */
public class PArrayListTest extends PListTestCases {

    @Test
    public void testClone() {

        //test an empty list
        PArrayList plist1 = (PArrayList) this.getPListInstance();
        PArrayList plist2 = (PArrayList) plist1.clone();
        assertTrue("Cloned obj==source obj at 1", plist1 != plist2);
        assertEquals("Clone check 1a failed", plist1, plist2);
        assertEquals("Clone check 1b failed", plist2, plist1);

        //test a list with some items
        plist1 = (PArrayList) this.getPListInstance();
        plist1.add("foo1");
        plist1.add("foo2");
        plist1.add("foo3");
        plist1.add(new Integer(99));
        plist1.add(null);
        plist2 = (PArrayList) plist1.clone();
        assertTrue("Cloned obj==source obj at 2", plist1 != plist2);
        assertEquals("Clone check 2a failed", plist1, plist2);
        assertEquals("Clone check 2b failed", plist2, plist1);

        //test a list with some PData items
        plist1 = (PArrayList) this.getPListInstance();
        plist1.add("foo1");
        PList plTmp = this.getPListInstance();
        plTmp.add("blah 1");
        plTmp.add("blah 2");
        plist1.add(plTmp);
        plist2 = (PArrayList) plist1.clone();
        assertTrue("Cloned obj==source obj at 3", plist1 != plist2);
        assertEquals("Clone check 3a failed", plist1, plist2);
        assertEquals("Clone check 3b failed", plist2, plist1);
    }

    //-------------------- Abstract methods ----------------------
    public StateMap getStateMap() {
        return getPListInstance();
    }

    public PData getPDataInstance() {
        return getPListInstance();
    }

    public PList getPListInstance() {
        return new PArrayList();
    }

    public PMap getPMapInstance() {
        return new PHashMap();
    }
}
