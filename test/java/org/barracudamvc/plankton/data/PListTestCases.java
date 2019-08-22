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
 * $Id: PListTestCases.java 271 2014-08-04 14:43:21Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test case for any PList. The idea here is that you can test
 * any PList by simply extending this class and implementing the
 * PData factory methods.
 */
public abstract class PListTestCases extends PDataTestCases {

    public abstract void testClone();

    /**
     * Verify the equality check (the clone test may depend on this)
     */
    public void testEquals() {
        PList plist1 = null;
        PList plist2 = null;

        //test empty lists (should be equal)
        plist1 = this.getPListInstance();
        plist2 = this.getPListInstance();
        assertEquals("Empty lists equality check failed", plist1, plist2);

        //test non-empty list (same objects)
        plist1 = this.getPListInstance();
        plist2 = this.getPListInstance();
        String foo1 = "foo1";
        String foo2 = "foo2";
        String foo3 = "foo3";
        plist1 = this.getPListInstance();
        plist1.add(foo1);
        plist1.add(foo2);
        plist1.add(foo3);
        plist2 = this.getPListInstance();
        plist2.add(foo1);
        plist2.add(foo2);
        plist2.add(foo3);
        assertEquals("Non-empty lists equality check 1a failed", plist1, plist2);
        plist2.add("blah");
        assertTrue("Non-empty lists inequality check 1b failed", !(plist1.equals(plist2)));
        assertTrue("Non-empty lists inequality check 1c failed", !(plist2.equals(plist1)));

        //test non-empty list (equal objects)
        plist1 = this.getPListInstance();
        plist2 = this.getPListInstance();
        plist1.add("foo1");
        plist1.add("foo2");
        plist1.add("foo3");
        plist2 = this.getPListInstance();
        plist2.add("foo1");
        plist2.add("foo2");
        plist2.add("foo3");
        assertEquals("Non-empty lists equality check 2a failed", plist1, plist2);
        plist2.add("blah");
        assertTrue("Non-empty lists inequality check 2b failed", !(plist1.equals(plist2)));
        assertTrue("Non-empty lists inequality check 2c failed", !(plist2.equals(plist1)));

    }

    /**
     * Verify the parental inheritance aspects
     */
    public void testParentalInheritance() {
        PList proot = null;
        PList pchild1 = null;
        PList pchild2 = null;
        PList pchild3 = null;
        List tlist = null;

        //test the auto-assignment via adding as data (w/ inherit parents)
        //...basic add method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        proot.add(pchild1);
        pchild1.add(pchild2);
        assertTrue("Parental Inheritance check 1 failed...parent inheritance failed (should've but didn't)", proot.getParent() == null);
        assertTrue("Parental Inheritance check 1a failed...parent inheritance failed (should've but didn't)", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 1b failed...parent inheritance failed (should've but didn't)", pchild2.getParent() == pchild1);
        //...basic add @ index method
        pchild3 = getPListInstance();
        pchild1.add(0, pchild3);
        assertTrue("Parental Inheritance check 1c failed...parent inheritance failed (should've but didn't)", pchild3.getParent() == pchild1);
        //...basic add all method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        tlist = new ArrayList();
        tlist.add(pchild1);
        tlist.add(pchild2);
        proot.addAll(tlist);
        assertTrue("Parental Inheritance check 1d failed...parent inheritance failed (should've but didn't)", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 1e failed...parent inheritance failed (should've but didn't)", pchild2.getParent() == proot);
        //...basic add all @ index method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        tlist = new ArrayList();
        tlist.add(pchild1);
        tlist.add(pchild2);
        proot.addAll(0, tlist);
        assertTrue("Parental Inheritance check 1f failed...parent inheritance failed (should've but didn't)", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 1g failed...parent inheritance failed (should've but didn't)", pchild2.getParent() == proot);
        //...set @ index method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        proot.add(pchild1);
        proot.set(0, pchild2);
        assertTrue("Parental Inheritance check 1h failed...child1 retained parental setting", pchild1.getParent() == null);
        assertTrue("Parental Inheritance check 1i failed...child2 didn't inherit parent", pchild2.getParent() == proot);
        //...clear method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        proot.add(pchild1);
        proot.add(pchild2);
        proot.clear();
        assertTrue("Parental Inheritance check 1j failed...child1 retained parental setting", pchild1.getParent() == null);
        assertTrue("Parental Inheritance check 1k failed...child2 retained parental setting", pchild2.getParent() == null);
        //...remove by index method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        proot.add(pchild1);
        proot.remove(0);
        assertTrue("Parental Inheritance check 1m failed...child retained parental setting", pchild1.getParent() == null);
        //...remove by object method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        proot.add(pchild1);
        proot.remove(pchild1);
        assertTrue("Parental Inheritance check 1n failed...child retained parental setting", pchild1.getParent() == null);
        //...removeAll method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        pchild3 = getPListInstance();
        proot.add(pchild1);
        proot.add(pchild2);
        proot.add(pchild3);
        tlist = new ArrayList();
        tlist.add(pchild2);
        tlist.add(pchild3);
        proot.removeAll(tlist);
        assertTrue("Parental Inheritance check 1o failed...child1 did not retain parental setting", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 1p failed...child2 retained parental setting", pchild2.getParent() == null);
        assertTrue("Parental Inheritance check 1q failed...child3 retained parental setting", pchild3.getParent() == null);
        //...retainAll method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        pchild3 = getPListInstance();
        pchild1.add("pchild 1");
        pchild2.add("pchild 2");
        pchild3.add("pchild 3");
        proot.add(pchild1);
        proot.add(pchild2);
        proot.add(pchild3);
        tlist = new ArrayList();
        tlist.add(pchild2);
        tlist.add(pchild3);
        proot.retainAll(tlist);
        assertTrue("Parental Inheritance check 1r failed...child1 retained parental setting", pchild1.getParent() == null);
        assertTrue("Parental Inheritance check 1s failed...child2 did not retain parental setting", pchild2.getParent() == proot);
        assertTrue("Parental Inheritance check 1t failed...child3 did not retain parental setting", pchild3.getParent() == proot);

        //test the auto-assignment via adding as data (w/out inherit parents)
        //...basic add method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild1.setInheritParents(false);
        pchild2 = getPListInstance();
        pchild2.setInheritParents(false);
        proot.add(pchild1);
        pchild1.add(pchild2);
        assertTrue("Parental Inheritance check 2 failed...parent inheritance failed (shouldn't have but did)", proot.getParent() == null);
        assertTrue("Parental Inheritance check 2a failed...parent inheritance failed (shouldn't have but did)", pchild1.getParent() != proot);
        assertTrue("Parental Inheritance check 2b failed...parent inheritance failed (shouldn't have but did)", pchild2.getParent() != pchild1);
        //...basic add @ index method
        pchild3 = getPListInstance();
        pchild3.setInheritParents(false);
        pchild1.add(0, pchild3);
        assertTrue("Parental Inheritance check 2c failed...parent inheritance failed (shouldn't have but did)", pchild3.getParent() != pchild1);
        //...basic add all method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild1.setInheritParents(false);
        pchild2 = getPListInstance();
        pchild2.setInheritParents(false);
        tlist = new ArrayList();
        tlist.add(pchild1);
        tlist.add(pchild2);
        proot.addAll(tlist);
        assertTrue("Parental Inheritance check 2d failed...parent inheritance failed (shouldn't have but did)", pchild1.getParent() != proot);
        assertTrue("Parental Inheritance check 2e failed...parent inheritance failed (shouldn't have but did)", pchild2.getParent() != proot);
        //...basic add all @ index method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild1.setInheritParents(false);
        pchild2 = getPListInstance();
        pchild2.setInheritParents(false);
        tlist = new ArrayList();
        tlist.add(pchild1);
        tlist.add(pchild2);
        proot.addAll(0, tlist);
        assertTrue("Parental Inheritance check 2f failed...parent inheritance failed (shouldn't have but did)", pchild1.getParent() != proot);
        assertTrue("Parental Inheritance check 2g failed...parent inheritance failed (shouldn't have but did)", pchild2.getParent() != proot);
        //...set @ index method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        proot.add(pchild1);
        pchild1.setInheritParents(false);    //note, by setting this false _after_ the item has been added the parental value won't be touched (it'll still be pointing to root)
        pchild2.setInheritParents(false);
        proot.set(0, pchild2);
        assertTrue("Parental Inheritance check 2h failed...child1's parental value was improperly cleared", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 2i failed...child2's parental value was improperly set", pchild2.getParent() == null);
        //...clear method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        proot.add(pchild1);
        proot.add(pchild2);
        pchild1.setInheritParents(false);    //note, by setting this false _after_ the item has been added the parental value won't be touched (it'll still be pointing to root)
        pchild2.setInheritParents(false);    //ditto
        proot.clear();
        assertTrue("Parental Inheritance check 2j failed...child1's parental value was improperly cleared", pchild1.getParent() == proot);
        assertTrue("Parental Inheritance check 2k failed...child2's parental value was improperly cleared", pchild2.getParent() == proot);
        //...remove by index method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        proot.add(pchild1);
        pchild1.setInheritParents(false);    //note, by setting this false _after_ the item has been added the parental value won't be touched (it'll still be pointing to root)
        proot.remove(0);
        assertTrue("Parental Inheritance check 2m failed...child's parental value was improperly cleared", pchild1.getParent() == proot);
        //...remove by object method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        proot.add(pchild1);
        pchild1.setInheritParents(false);    //note, by setting this false _after_ the item has been added the parental value won't be touched (it'll still be pointing to root)
        proot.remove(pchild1);
        assertTrue("Parental Inheritance check 2n failed...child's parental value was improperly cleared", pchild1.getParent() == proot);
        //...removeAll method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        pchild3 = getPListInstance();
        proot.add(pchild1);
        proot.add(pchild2);
        proot.add(pchild3);
        pchild1.setInheritParents(false);
        pchild2.setInheritParents(false);
        pchild3.setInheritParents(false);
        tlist = new ArrayList();
        tlist.add(pchild2);
        tlist.add(pchild3);
        proot.removeAll(tlist);
        assertTrue("Parental Inheritance check 2p failed...child2 did not retain parental setting", pchild2.getParent() == proot);
        assertTrue("Parental Inheritance check 2q failed...child3 did not retain parental setting", pchild3.getParent() == proot);
        //...retainAll method
        proot = getPListInstance();
        pchild1 = getPListInstance();
        pchild2 = getPListInstance();
        pchild3 = getPListInstance();
        proot.add(pchild1);
        proot.add(pchild2);
        proot.add(pchild3);
        tlist = new ArrayList();
        tlist.add(pchild2);
        tlist.add(pchild3);
        proot.retainAll(tlist);
        assertTrue("Parental Inheritance check 2r failed...child1 did not retain parental setting", pchild1.getParent() == proot);
    }

    /**
     * Test basic data manipulation
     */
    public void testListManipulation() {
        PList proot = null;
        List tlist = null;
        Object[] els = new Object[]{"Foo", new Integer(99), null};

        //test the single add/remove methods
        //...put it in
        proot = getPListInstance();
        for (int i = 0; i < els.length; i++) {
            proot.add(els[i]);
            assertTrue("Add data check 1a, idx[" + i + "] - data not added", proot.contains(els[i]));
            assertTrue("Add data check 1b, idx[" + i + "] - incorrect size", proot.size() == i + 1);
            assertTrue("Add data check 1c, idx[" + i + "] - incorrect index", proot.indexOf(els[i]) == i);
            assertTrue("Add data check 1d, idx[" + i + "] - incorrect last index", proot.lastIndexOf(els[i]) == i);
            assertTrue("Add data check 1e, idx[" + i + "] - data not retrieved", proot.get(i) == els[i]);
            assertTrue("Add data check 1f, idx[" + i + "] - list claims to be empty", !proot.isEmpty());
        }
        //...take it back out
        for (int i = els.length - 1; i >= 0; i--) {
            proot.remove(els[i]);
            assertTrue("Remove data check 1a, idx[" + i + "] - data not removed", !proot.contains(els[i]));
            assertTrue("Remove data check 1b, idx[" + i + "] - incorrect size", proot.size() == i);
            assertTrue("Remove data check 1c, idx[" + i + "] - incorrect index", proot.indexOf(els[i]) == -1);
            assertTrue("Remove data check 1d, idx[" + i + "] - incorrect last index", proot.lastIndexOf(els[i]) == -1);
            try {
                proot.get(i);
                fail("Remove data check 1e, idx[" + i + "] - exception not generated");
            } catch (IndexOutOfBoundsException e) {
            }
        }
        assertTrue("Remove data check 1f - list not empty", proot.isEmpty());

        //test the Collection add/remove methods
        tlist = new ArrayList();
        for (int i = 0; i < els.length; i++) {
            tlist.add(els[i]);
        }
        //...put it in
        proot = getPListInstance();
        proot.addAll(tlist);
        assertTrue("Add data check 2a - incorrect size", proot.size() == els.length);
        assertTrue("Add data check 2b - doesn't contain all items", proot.containsAll(tlist));
        for (int i = 0; i < els.length; i++) {
            assertTrue("Add data check 2c, idx[" + i + "] - data not added", proot.contains(els[i]));
            assertTrue("Add data check 2d, idx[" + i + "] - incorrect index", proot.indexOf(els[i]) == i);
            assertTrue("Add data check 2e, idx[" + i + "] - incorrect last index", proot.lastIndexOf(els[i]) == i);
            assertTrue("Add data check 2f, idx[" + i + "] - data not retrieved", proot.get(i) == els[i]);
            assertTrue("Add data check 2f, idx[" + i + "] - list claims to be empty", !proot.isEmpty());
        }
        //...take it back out
        proot.removeAll(tlist);
        assertTrue("Remove data check 2a - incorrect size", proot.size() == 0);
        assertTrue("Remove data check 2b - still contain all items", !proot.containsAll(tlist));
        for (int i = els.length - 1; i >= 0; i--) {
            proot.remove(els[i]);
            assertTrue("Remove data check 2c, idx[" + i + "] - data not removed", !proot.contains(els[i]));
            assertTrue("Remove data check 2d, idx[" + i + "] - incorrect index", proot.indexOf(els[i]) == -1);
            assertTrue("Remove data check 2e, idx[" + i + "] - incorrect last index", proot.lastIndexOf(els[i]) == -1);
            try {
                proot.get(i);
                fail("Remove data check 2f, idx[" + i + "] - exception not generated");
            } catch (IndexOutOfBoundsException e) {
            }
        }
        assertTrue("Remove data check 2f - list not empty", proot.isEmpty());
        //...put it in at a given index
        proot = getPListInstance();
        proot.add("some other item");
        proot.addAll(0, tlist);
        assertTrue("Add data check 3a - incorrect size", proot.size() == els.length + 1);
        assertTrue("Add data check 3b - doesn't contain all items", proot.containsAll(tlist));
        for (int i = 0; i < els.length; i++) {
            assertTrue("Add data check 3c, idx[" + i + "] - data not added", proot.contains(els[i]));
            assertTrue("Add data check 3d, idx[" + i + "] - incorrect index", proot.indexOf(els[i]) == i);
            assertTrue("Add data check 3e, idx[" + i + "] - incorrect last index", proot.lastIndexOf(els[i]) == i);
            assertTrue("Add data check 3f, idx[" + i + "] - data not retrieved", proot.get(i) == els[i]);
        }
        //...test retention
        proot.retainAll(tlist);
        assertTrue("Remove data check 3a - incorrect size", proot.size() == els.length);

        //misc tests
        //...test the set method
        proot = getPListInstance();
        proot.add("Foo");
        proot.set(0, "Blah");
        assertTrue("Set method failed", proot.get(0).equals("Blah"));
        //...test subList method
        proot = getPListInstance();
        proot.add("Foo");
        tlist = new ArrayList();
        for (int i = 0; i < els.length; i++) {
            tlist.add(els[i]);
        }
        proot.addAll(tlist);
        proot.add("Blah");
        List sublist = proot.subList(1, els.length + 1);
        assertTrue("subList method failed", sublist.size() == els.length);
        //...test the clear method
        proot = getPListInstance();
        for (int i = 0; i < els.length; i++) {
            proot.add(els[i]);
        }
        assertTrue("Clear check 4a failed - data not added", proot.size() == els.length);
        proot.clear();
        assertTrue("Clear check 4b failed - data not removed", proot.size() == 0);
        //...toArray
        //(todo)        

    }

}
