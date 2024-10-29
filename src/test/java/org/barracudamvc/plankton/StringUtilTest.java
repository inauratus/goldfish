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
 * $Id: StringUtilTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test the StringUtil class
 */
public class StringUtilTest {

    @Test
    public void testReplace() {

        assertReplaced(null, null, "Blah", "Blah");
        assertReplaced(null, "a", "Blah", null);
        assertReplaced("Foo Blah", null, "_", "Foo Blah");
        assertReplaced("Foo Blah", " ", "_", "Foo_Blah");
        assertReplaced("Foo Blah Blah", " ", "_", "Foo_Blah_Blah");
        assertReplaced("Foo  Blah", " ", "_", "Foo__Blah");
        assertReplaced("~Some Text", "~", null, "Some Text");
        assertReplaced("Some Text~", "~", null, "Some Text");
        assertReplaced("~Some~Text~", "~", null, "SomeText");
        assertReplaced("~~~", "~", null, null);
        assertReplaced("~~~", "~", "", "");
        assertReplaced("", " ", null, "");

    }

    public void assertReplaced(String sourceStr, String oldPattern, String newPattern, String expectedResult) {
        String actualResult = StringUtil.replace(sourceStr, oldPattern, newPattern);
        assertTrue("Replace err on source:'" + sourceStr + "' ('" + oldPattern + "'-->'" + newPattern + "') got:'" + actualResult + "' expected:'" + expectedResult + "'", actualResult == expectedResult || (actualResult != null && actualResult.equals(expectedResult)));
    }

    @Test
    public void sanitizeRemovesScript() {
        assertThat(StringUtil.sanitize(null), nullValue());
        assertThat(StringUtil.sanitize(""), is(""));
        assertThat(StringUtil.sanitize("<SCRIPT>alert('hello world')</SCRIPT>"), is("alert('hello world')"));
        assertThat(StringUtil.sanitize("<script>alert('hello world')</script>"), is("alert('hello world')"));
        assertThat(StringUtil.sanitize("<div><script>alert('hello world')</script></div>"), is("<div>alert('hello world')</div>"));
    }


}
