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
 * $Id: PDataTestCases.java 271 2014-08-04 14:43:21Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.Serializable;
import static org.junit.Assert.assertTrue;

/**
 * Test case for any PData. The idea is that you can test any
 * PData object by extending this test case and adding tests 
 * for the specific implementation details. All you really have 
 * to do is override teh factory PData methods.
 */
public abstract class PDataTestCases extends StateMapTestCases {

    public void testParental() {
        PData proot = null;
        PData pchild1 = null;
        PData pchild2 = null;

        //test simple set/get
        proot = getPDataInstance();
        pchild1 = getPDataInstance();
        pchild1.setParent(proot);
        assertTrue("Parental check 1 failed...basic set/get failure", pchild1.getParent() == proot);
        pchild1.setParent(null);
        assertTrue("Parental check 1a failed...set to null failure", pchild1.getParent() == null);
        pchild1.setParent(pchild1);
        assertTrue("Parental check 1b failed...allowed parent to be set to self", pchild1.getParent() != pchild1);

        //test the inheritParents attribute set/get
        proot = getPDataInstance();
        assertTrue("Parental check 2 failed...invalid default inheritParents value", proot.isInheritParents() == true);
        proot.setInheritParents(false);
        assertTrue("Parental check 2a failed...basic set/get failure", proot.isInheritParents() == false);
        proot.setInheritParents(true);
        assertTrue("Parental check  2b failed...basic set/get failure", proot.isInheritParents() == true);

        //test the getRootParent
        proot = getPDataInstance();
        pchild1 = getPDataInstance();
        pchild1.setParent(proot);
        pchild2 = getPDataInstance();
        pchild2.setParent(pchild1);
        assertTrue("Parental check 3 failed...root parent not correctly located", pchild2.getRootParent() == proot);
    }

    public void testSerializable() {
        PData pdata = this.getPDataInstance();
        assertTrue("PData instance does not implement Serializable!", pdata instanceof Serializable);
    }

    public abstract PData getPDataInstance();

    public abstract PList getPListInstance();

    public abstract PMap getPMapInstance();

}
