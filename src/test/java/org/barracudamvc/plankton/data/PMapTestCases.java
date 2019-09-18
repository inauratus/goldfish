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
 * $Id: PMapTestCases.java 271 2014-08-04 14:43:21Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for any PMap. The idea here is that you can test
 * any PMap by simply extending this class and implementing the
 * PData factory methods.
 */
public abstract class PMapTestCases extends PDataTestCases {

    public abstract void testClone();

    /**
     * Verify the equality check (the clone test may depend on this)
     */
    public void testEquals() {
        PMap pmap1 = null;
        PMap pmap2 = null;

        //test empty maps (should be equal)
        pmap1 = this.getPMapInstance();
        pmap2 = this.getPMapInstance();
        assertEquals("Empty lists equality check failed", pmap1, pmap2);

        //test non-empty list (same objects)
        pmap1 = this.getPMapInstance();
        pmap2 = this.getPMapInstance();
        String foo1 = "foo1";
        String foo2 = "foo2";
        String foo3 = "foo3";
        pmap1 = this.getPMapInstance();
        pmap1.put("key1", foo1);
        pmap1.put("key2", foo2);
        pmap1.put("key3", foo3);
        pmap2 = this.getPMapInstance();
        pmap2.put("key1", foo1);
        pmap2.put("key2", foo2);
        pmap2.put("key3", foo3);
        assertEquals("Non-empty lists equality check 1a failed", pmap1, pmap2);
        pmap2.put("key4", "blah");
        assertTrue("Non-empty lists inequality check 1b failed", !(pmap1.equals(pmap2)));
        assertTrue("Non-empty lists inequality check 1c failed", !(pmap2.equals(pmap1)));

        //test non-empty list (equal objects)
        pmap1 = this.getPMapInstance();
        pmap2 = this.getPMapInstance();
        pmap1.put("key1", "foo1");
        pmap1.put("key2", "foo2");
        pmap1.put("key3", "foo3");
        pmap2 = this.getPMapInstance();
        pmap2.put("key1", "foo1");
        pmap2.put("key2", "foo2");
        pmap2.put("key3", "foo3");
        assertEquals("Non-empty lists equality check 2a failed", pmap1, pmap2);
        pmap2.put("key4", "blah");
        assertTrue("Non-empty lists inequality check 2b failed", !(pmap1.equals(pmap2)));
        assertTrue("Non-empty lists inequality check 2c failed", !(pmap2.equals(pmap1)));

    }

    /**
     * Verify the parental inheritance aspects
     */
    public void testParentalInheritance() {
        PMap proot = null;
        PMap pchild1 = null;
        PMap pchild2 = null;
        Map tmap = null;

        //test the auto-assignment via adding as data (w/ inherit parents)
        //...basic add method
        proot = getPMapInstance();
        pchild1 = getPMapInstance();
        pchild2 = getPMapInstance();
        proot.put("k1", pchild1);
        pchild1.put("k2", pchild2);
        assertTrue("Parental Inheritance check 1 failed...parent inheritance failed (should've but didn't)", proot.getParent() == null);
        assertTrue("Parental Inheritance check 1a failed...parent inheritance failed (should've but didn't)", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 1b failed...parent inheritance failed (should've but didn't)", pchild2.getParent() == pchild1);
        //...putAll method
        proot = getPMapInstance();
        pchild1 = getPMapInstance();
        pchild2 = getPMapInstance();
        tmap = new HashMap();
        tmap.put("c1", pchild1);
        tmap.put("c2", pchild2);
        proot.putAll(tmap);
        assertTrue("Parental Inheritance check 1d failed...parent inheritance failed (should've but didn't)", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 1e failed...parent inheritance failed (should've but didn't)", pchild2.getParent() == proot);
        //...remove method
        proot = getPMapInstance();
        pchild1 = getPMapInstance();
        proot.put("key1", pchild1);
        proot.remove("key1");
        assertTrue("Parental Inheritance check 1n failed...child retained parental setting", pchild1.getParent() == null);

        //test the auto-assignment via adding as data (w/out inherit parents)
        //...basic add method
        proot = getPMapInstance();
        pchild1 = getPMapInstance();
        pchild1.setInheritParents(false);
        pchild2 = getPMapInstance();
        pchild2.setInheritParents(false);
        proot.put("k1", pchild1);
        pchild1.put("k2", pchild2);
        assertTrue("Parental Inheritance check 2 failed...parent inheritance failed (shouldn't have but did)", proot.getParent() == null);
        assertTrue("Parental Inheritance check 2a failed...parent inheritance failed (shouldn't have but did)", pchild1.getParent() != proot);
        assertTrue("Parental Inheritance check 2b failed...parent inheritance failed (shouldn't have but did)", pchild2.getParent() != pchild1);
        //...putAll method
        proot = getPMapInstance();
        pchild1 = getPMapInstance();
        pchild1.setInheritParents(false);
        pchild2 = getPMapInstance();
        pchild2.setInheritParents(false);
        tmap = new HashMap();
        tmap.put("c1", pchild1);
        tmap.put("c2", pchild2);
        proot.putAll(tmap);
        assertTrue("Parental Inheritance check 2d failed...parent inheritance failed (shouldn't have but did)", pchild1.getParent() != proot);
        assertTrue("Parental Inheritance check 2e failed...parent inheritance failed (shouldn't have but did)", pchild2.getParent() != proot);
        //...remove method
        proot = getPMapInstance();
        pchild1 = getPMapInstance();
        proot.put("key1", pchild1);
        pchild1.setInheritParents(false);    //note, by setting this false _after_ the item has been added the parental value won't be touched (it'll still be pointing to root)
        proot.remove(pchild1);
        assertTrue("Parental Inheritance check 2n failed...child's parental value was improperly cleared", pchild1.getParent() == proot);
    }

    /**
     * Test basic data manipulation
     */
    public void testMapManipulation() {
        PMap proot = null;
        Map tmap = null;
        Object[] keys = new Object[]{"key1", "key2", "key3"};
        Object[] vals = new Object[]{"Foo", new Integer(99), null};

        //test the single put/remove methods
        //...put it in
        proot = getPMapInstance();
        for (int i = 0; i < vals.length; i++) {
            proot.put(keys[i], vals[i]);
            assertTrue("Add data check 1a, idx[" + i + "] - key missing", proot.containsKey(keys[i]));
            assertTrue("Add data check 1b, idx[" + i + "] - value missing", proot.containsValue(vals[i]));
            assertTrue("Add data check 1c, idx[" + i + "] - incorrect size", proot.size() == i + 1);
            assertTrue("Add data check 1d, idx[" + i + "] - data not retrieved", proot.get(keys[i]) == vals[i]);
            assertTrue("Add data check 1e, idx[" + i + "] - map claims to be empty", !proot.isEmpty());
        }
        //...take it back out
        for (int i = vals.length - 1; i >= 0; i--) {
            proot.remove(keys[i]);
            assertTrue("Remove data check 1a, idx[" + i + "] - key not removed", !proot.containsKey(keys[i]));
            assertTrue("Remove data check 1b, idx[" + i + "] - value not removed", !proot.containsValue(vals[i]));
            assertTrue("Remove data check 1c, idx[" + i + "] - incorrect size", proot.size() == i);
            assertTrue("Remove data check 1d, idx[" + i + "] - data not removed", proot.get(keys[i]) == null);
        }
        assertTrue("Remove data check 1e - map not empty", proot.isEmpty());

        //test the Map add/remove methods
        tmap = new HashMap();
        for (int i = 0; i < vals.length; i++) {
            tmap.put(keys[i], vals[i]);
        }
        //...put it in
        proot = getPMapInstance();
        proot.putAll(tmap);
        assertTrue("Add data check 2a - incorrect size", proot.size() == vals.length);
        for (int i = 0; i < vals.length; i++) {
            assertTrue("Add data check 2a, idx[" + i + "] - key missing", proot.containsKey(keys[i]));
            assertTrue("Add data check 2b, idx[" + i + "] - value missing", proot.containsValue(vals[i]));
            assertTrue("Add data check 2c, idx[" + i + "] - data not retrieved", proot.get(keys[i]) == vals[i]);
            assertTrue("Add data check 2d, idx[" + i + "] - map claims to be empty", !proot.isEmpty());
        }

        //misc tests
        //...test the clear method
        proot = getPMapInstance();
        for (int i = 0; i < vals.length; i++) {
            proot.put(keys[i], vals[i]);
        }
        assertTrue("Clear check 3a failed - data not added", proot.size() == vals.length);
        proot.clear();
        assertTrue("Clear check 3b failed - data not removed", proot.size() == 0);
    }
}
