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
 * $Id: PHashMapTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test case for PHashMap
 */
public class PHashMapTest {

    /**
     * Verify the clone (we have to test this here instead
     * the clone method is not part of the Map interface...we need
     * a concrete implementation in order to be able to reference it)
     */
    @Test
    public void testClone() {
        PHashMap pmap1 = null;
        PHashMap pmap2 = null;

        //test an empty list
        pmap1 = (PHashMap) this.getPMapInstance();
        pmap2 = (PHashMap) pmap1.clone();
        assertTrue("Cloned obj==source obj at 1", pmap1 != pmap2);
        assertEquals("Clone check 1a failed", pmap1, pmap2);
        assertEquals("Clone check 1b failed", pmap2, pmap1);

        //test a list with some items
        pmap1 = (PHashMap) this.getPMapInstance();
        pmap1.put("key1", "foo1");
        pmap1.put("key2", "foo2");
        pmap1.put("key3", "foo3");
        pmap1.put("key4", new Integer(99));
        pmap1.put("key5", null);
        pmap2 = (PHashMap) pmap1.clone();
        assertTrue("Cloned obj==source obj at 2", pmap1 != pmap2);
        assertEquals("Clone check 2a failed", pmap1, pmap2);
        assertEquals("Clone check 2b failed", pmap2, pmap1);

        //test a list with some PData items
        pmap1 = (PHashMap) this.getPMapInstance();
        pmap1.put("key1", "foo1");
        PMap pmTmp = this.getPMapInstance();
        pmTmp.put("key2a", "blah 1");
        pmTmp.put("key2b", "blah 2");
        pmap1.put("key2", pmTmp);
        pmap2 = (PHashMap) pmap1.clone();
        assertTrue("Cloned obj==source obj at 3", pmap1 != pmap2);
        assertEquals("Clone check 3a failed", pmap1, pmap2);
        assertEquals("Clone check 3b failed", pmap2, pmap1);
    }

    //-------------------- Abstract methods ----------------------
    public StateMap getStateMap() {
        return getPMapInstance();
    }

    public PData getPDataInstance() {
        return getPMapInstance();
    }

    public PList getPListInstance() {
        return new PArrayList();
    }

    public PMap getPMapInstance() {
        return new PHashMap();
    }
}
