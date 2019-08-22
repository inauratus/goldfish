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
 * $Id: StateMapTestCases.java 271 2014-08-04 14:43:21Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertTrue;

/**
 * Test case for any StateMap. The idea is that you can test any
 * StateMap object by extending this test case and adding tests 
 * for the specific implementation details. All you really have 
 * to do is override teh factory StateMap methods.
 */
public abstract class StateMapTestCases {

    public void testStateMap() {
        StateMap map = getStateMap();
        String key1 = "foo1";
        String key2 = "foo2";
        String key3 = "foo3";
        String s1 = "blah1";
        Integer i1 = new Integer(99);
        String s2 = null;

        //simple tests
        map.putState(key1, s1);
        map.putState(key2, i1);
        map.putState(key3, s2);
        assertTrue("Error 1a set/get state", map.getState(key1) == s1);
        assertTrue("Error 1b set/get state", map.getState(key2) == i1);
        assertTrue("Error 1c set/get state", map.getState(key3) == s2);
        Set keys = map.getStateKeys();
        assertTrue("Error 2a - Key list length is wrong", keys.size() == 3);
        assertTrue("Error 2b - Key missing", keys.contains(key1));
        assertTrue("Error 2c - Key missing", keys.contains(key2));
        assertTrue("Error 2d - Key missing", keys.contains(key3));
        Map values = map.getStateStore();
        assertTrue("Error 3a - Value list length is wrong", values.size() == 3);
        assertTrue("Error 3b - Value missing", values.get(key1) == s1);
        assertTrue("Error 3c - Value missing", values.get(key2) == i1);
        assertTrue("Error 3d - Value missing", values.get(key3) == s2);
        map.removeState(key1);
        map.removeState(key2);
        map.removeState(key3);
        assertTrue("Error 4a remove state", map.getState(key1) == null);
        assertTrue("Error 4b remove state", map.getState(key2) == null);
        assertTrue("Error 4c remove state", map.getState(key3) == null);
    }

    //-------------------- Abstract methods ----------------------
    public abstract StateMap getStateMap();

}
