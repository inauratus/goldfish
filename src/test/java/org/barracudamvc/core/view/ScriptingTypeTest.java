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
 * $Id: ScriptingTypeTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.view;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This test verifies that the hierarchical organization of 
 * ScriptingType classes. We do this by testing for what it should be 
 * an instance of and what it shouldn't. The idea is that the ScriptingType
 * extension hierarchy is very important and we don't want it getting 
 * changed accidentally. This test should preclude that.
 */
public class ScriptingTypeTest {

    @Test
    public void testTypes() {
        //add the tests
        //...JAVASCRIPT_1x
        assertIsInstance(ScriptingType.JAVASCRIPT_1x, ScriptingType.JavaScript1x.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1x, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1x, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1x, ScriptingType.None.class);
        //...JAVASCRIPT_1_0
        assertIsInstance(ScriptingType.JAVASCRIPT_1_0, ScriptingType.JavaScript10.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_0, ScriptingType.JavaScript1x.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_0, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_0, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_0, ScriptingType.None.class);
        //...JAVASCRIPT_1_1
        assertIsInstance(ScriptingType.JAVASCRIPT_1_1, ScriptingType.JavaScript11.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_1, ScriptingType.JavaScript10.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_1, ScriptingType.JavaScript1x.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_1, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_1, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_1, ScriptingType.None.class);
        //...JAVASCRIPT_1_2
        assertIsInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.JavaScript12.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.JavaScript11.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.JavaScript10.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.JavaScript1x.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_2, ScriptingType.None.class);
        //...JAVASCRIPT_1_3
        assertIsInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.JavaScript13.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.JavaScript12.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.JavaScript11.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.JavaScript10.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.JavaScript1x.class);
        assertIsInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.JAVASCRIPT_1_3, ScriptingType.None.class);
        //...WMLSCRIPT_1x
        assertIsInstance(ScriptingType.WMLSCRIPT_1x, ScriptingType.WmlScript1x.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1x, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1x, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1x, ScriptingType.None.class);
        //...WMLSCRIPT_1_0
        assertIsInstance(ScriptingType.WMLSCRIPT_1_0, ScriptingType.WmlScript10.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_0, ScriptingType.WmlScript1x.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_0, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1_0, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1_0, ScriptingType.None.class);
        //...WMLSCRIPT_1_1
        assertIsInstance(ScriptingType.WMLSCRIPT_1_1, ScriptingType.WmlScript11.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_1, ScriptingType.WmlScript10.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_1, ScriptingType.WmlScript1x.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_1, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1_1, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1_1, ScriptingType.None.class);
        //...WMLSCRIPT_1_2
        assertIsInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.WmlScript12.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.WmlScript11.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.WmlScript10.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.WmlScript1x.class);
        assertIsInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.WmlScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.WMLSCRIPT_1_2, ScriptingType.None.class);
        //...NONE
        assertIsInstance(ScriptingType.NONE, ScriptingType.None.class);
        assertNotInstance(ScriptingType.NONE, ScriptingType.JavaScript.class);
        assertNotInstance(ScriptingType.NONE, ScriptingType.WmlScript.class);

    }

    public void assertIsInstance(ScriptingType st, Class targetCl) {
        assertTrue(st + " not an instanceof " + targetCl, targetCl.isAssignableFrom(st.getClass()));
    }

    public void assertNotInstance(ScriptingType st, Class targetCl) {
        assertTrue(st + " is an instanceof " + targetCl + " (and it shouldn't be!)", !targetCl.isAssignableFrom(st.getClass()));
    }
}
