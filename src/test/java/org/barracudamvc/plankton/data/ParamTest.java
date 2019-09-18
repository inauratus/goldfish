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
 * $Id: TestParam.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.data;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ParamTest {

    @Test
    public void testParam() {
        Param p = null;
        Param p2 = null;
        String key1 = "foo1";
        String key1a = "foo1";
        String key2 = "foo2";
        String value1 = "blah1";
        String value1a = "blah1";
        String value2 = "blah2";

        p = new Param();
        assertTrue("param obj check 1a.1 failed - p.key!=null", p.key == null);
        assertTrue("param obj check 1a.2 failed - p.getKey()!=null", p.getKey() == null);
        assertTrue("param obj check 1b.1 failed - p.value!=null", p.value == null);
        assertTrue("param obj check 1b.2 failed - p.getvalue()!=null", p.getValue() == null);
        p = new Param(key1, value1);
        assertTrue("param obj check 2a.1 failed - p.key!=key", p.key == key1);
        assertTrue("param obj check 2a.2 failed - p.getKey()!=key", p.getKey() == key1);
        assertTrue("param obj check 2b.1 failed - p.value!=value", p.value == value1);
        assertTrue("param obj check 2b.2 failed - p.getvalue()!=value", p.getValue() == value1);

        //equality (shouldn't happen until both key and value match)
        p2 = new Param(key2, value2);
        assertTrue("param obj check 3a.1 failed - equality check failed", !(p.equals(p2)));
        assertTrue("param obj check 3a.2 failed - equality check failed", !(p2.equals(p)));
        p2.setKey(key1);
        assertTrue("param obj check 3a.3 failed - equality check failed", !(p.equals(p2)));
        assertTrue("param obj check 3a.4 failed - equality check failed", !(p2.equals(p)));
        p2.setKey(key2);
        p2.setValue(value1);
        assertTrue("param obj check 3a.5 failed - equality check failed", !(p.equals(p2)));
        assertTrue("param obj check 3a.6 failed - equality check failed", !(p2.equals(p)));
        p2.setKey(key1);
        assertTrue("param obj check 3a.7 failed - equality check failed", (p.equals(p2)));
        assertTrue("param obj check 3a.8 failed - equality check failed", (p2.equals(p)));
        p2.setKey(key1a);
        p2.setValue(value1a);
        assertTrue("param obj check 3a.9 failed - equality check failed", (p.equals(p2)));
        assertTrue("param obj check 3a.10 failed - equality check failed", (p2.equals(p)));
    }
}
