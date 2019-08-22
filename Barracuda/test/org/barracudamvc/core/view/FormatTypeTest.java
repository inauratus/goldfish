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
 * $Id: FormatTypeTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.view;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This test verifies that the hierachical organization of 
 * FormatType classes. We do this by testing for what it should be 
 * an instanceof and what it shouldn't. The idea is that the FormatType
 * extension hierarchy is very important and we don't want it getting 
 * changed accidentally. This test should preclude that.
 */
public class FormatTypeTest {

    @Test
    public void testTypes() {
        //add the tests
        //...HTML_3_0
        assertInstance(FormatType.HTML_3_0, FormatType.Html3x.class);
        assertInstance(FormatType.HTML_3_0, FormatType.Html.class);
        assertInstance(FormatType.HTML_3_0, FormatType.HtmlBasic.class);
        assertInstance(FormatType.HTML_3_0, FormatType.AsciiData.class);
        assertInstance(FormatType.HTML_3_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.HTML_3_0, FormatType.Html4x.class);
        assertNotInstance(FormatType.HTML_3_0, FormatType.Chtml.class);
        assertNotInstance(FormatType.HTML_3_0, FormatType.Xml.class);
        assertNotInstance(FormatType.HTML_3_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.HTML_3_0, FormatType.UnknownFormat.class);
        //...HTML_3_1
        assertInstance(FormatType.HTML_3_1, FormatType.Html3x.class);
        assertInstance(FormatType.HTML_3_1, FormatType.Html.class);
        assertInstance(FormatType.HTML_3_1, FormatType.HtmlBasic.class);
        assertInstance(FormatType.HTML_3_1, FormatType.AsciiData.class);
        assertInstance(FormatType.HTML_3_1, FormatType.GenericData.class);
        assertNotInstance(FormatType.HTML_3_1, FormatType.Html4x.class);
        assertNotInstance(FormatType.HTML_3_1, FormatType.Chtml.class);
        assertNotInstance(FormatType.HTML_3_1, FormatType.Xml.class);
        assertNotInstance(FormatType.HTML_3_1, FormatType.BinaryData.class);
        assertNotInstance(FormatType.HTML_3_1, FormatType.UnknownFormat.class);
        //...HTML_3_2
        assertInstance(FormatType.HTML_3_2, FormatType.Html3x.class);
        assertInstance(FormatType.HTML_3_2, FormatType.Html.class);
        assertInstance(FormatType.HTML_3_2, FormatType.HtmlBasic.class);
        assertInstance(FormatType.HTML_3_2, FormatType.AsciiData.class);
        assertInstance(FormatType.HTML_3_2, FormatType.GenericData.class);
        assertNotInstance(FormatType.HTML_3_2, FormatType.Html4x.class);
        assertNotInstance(FormatType.HTML_3_2, FormatType.Chtml.class);
        assertNotInstance(FormatType.HTML_3_2, FormatType.Xml.class);
        assertNotInstance(FormatType.HTML_3_2, FormatType.BinaryData.class);
        assertNotInstance(FormatType.HTML_3_2, FormatType.UnknownFormat.class);
        //...HTML_4_0
        assertInstance(FormatType.HTML_4_0, FormatType.Html4x.class);
        assertInstance(FormatType.HTML_4_0, FormatType.Html3x.class);
        assertInstance(FormatType.HTML_4_0, FormatType.Html.class);
        assertInstance(FormatType.HTML_4_0, FormatType.HtmlBasic.class);
        assertInstance(FormatType.HTML_4_0, FormatType.AsciiData.class);
        assertInstance(FormatType.HTML_4_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.HTML_4_0, FormatType.Chtml.class);
        assertNotInstance(FormatType.HTML_4_0, FormatType.Xml.class);
        assertNotInstance(FormatType.HTML_4_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.HTML_4_0, FormatType.UnknownFormat.class);
        //...HTML_4_1
        assertInstance(FormatType.HTML_4_1, FormatType.Html4x.class);
        assertInstance(FormatType.HTML_4_1, FormatType.Html3x.class);
        assertInstance(FormatType.HTML_4_1, FormatType.Html.class);
        assertInstance(FormatType.HTML_4_1, FormatType.HtmlBasic.class);
        assertInstance(FormatType.HTML_4_1, FormatType.AsciiData.class);
        assertInstance(FormatType.HTML_4_1, FormatType.GenericData.class);
        assertNotInstance(FormatType.HTML_4_1, FormatType.Chtml.class);
        assertNotInstance(FormatType.HTML_4_1, FormatType.Xml.class);
        assertNotInstance(FormatType.HTML_4_1, FormatType.BinaryData.class);
        assertNotInstance(FormatType.HTML_4_1, FormatType.UnknownFormat.class);
        //...CHTML_1_0
        assertInstance(FormatType.CHTML_1_0, FormatType.Chtml1x.class);
        assertInstance(FormatType.CHTML_1_0, FormatType.Chtml.class);
        assertInstance(FormatType.CHTML_1_0, FormatType.HtmlBasic.class);
        assertInstance(FormatType.CHTML_1_0, FormatType.AsciiData.class);
        assertInstance(FormatType.CHTML_1_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.CHTML_1_0, FormatType.Chtml2x.class);
        assertNotInstance(FormatType.CHTML_1_0, FormatType.Html.class);
        assertNotInstance(FormatType.CHTML_1_0, FormatType.Xml.class);
        assertNotInstance(FormatType.CHTML_1_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.CHTML_1_0, FormatType.UnknownFormat.class);
        //...CHTML_2_0
        assertInstance(FormatType.CHTML_2_0, FormatType.Chtml2x.class);
        assertInstance(FormatType.CHTML_2_0, FormatType.Chtml1x.class);
        assertInstance(FormatType.CHTML_2_0, FormatType.Chtml.class);
        assertInstance(FormatType.CHTML_2_0, FormatType.HtmlBasic.class);
        assertInstance(FormatType.CHTML_2_0, FormatType.AsciiData.class);
        assertInstance(FormatType.CHTML_2_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.CHTML_2_0, FormatType.Html.class);
        assertNotInstance(FormatType.CHTML_2_0, FormatType.Xml.class);
        assertNotInstance(FormatType.CHTML_2_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.CHTML_2_0, FormatType.UnknownFormat.class);
        //...XML_1_0
        assertInstance(FormatType.XML_1_0, FormatType.Xml1x.class);
        assertInstance(FormatType.XML_1_0, FormatType.Xml.class);
        assertInstance(FormatType.XML_1_0, FormatType.AsciiData.class);
        assertInstance(FormatType.XML_1_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.XML_1_0, FormatType.Vxml.class);
        assertNotInstance(FormatType.XML_1_0, FormatType.Wml.class);
        assertNotInstance(FormatType.XML_1_0, FormatType.Xhtml.class);
        assertNotInstance(FormatType.XML_1_0, FormatType.HtmlBasic.class);
        assertNotInstance(FormatType.XML_1_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.XML_1_0, FormatType.UnknownFormat.class);
        //...VXML_1_0
        assertInstance(FormatType.VXML_1_0, FormatType.Vxml1x.class);
        assertInstance(FormatType.VXML_1_0, FormatType.Vxml.class);
        assertInstance(FormatType.VXML_1_0, FormatType.Xml.class);
        assertInstance(FormatType.VXML_1_0, FormatType.AsciiData.class);
        assertInstance(FormatType.VXML_1_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.VXML_1_0, FormatType.Xml1x.class);
        assertNotInstance(FormatType.VXML_1_0, FormatType.Wml.class);
        assertNotInstance(FormatType.VXML_1_0, FormatType.Xhtml.class);
        assertNotInstance(FormatType.VXML_1_0, FormatType.HtmlBasic.class);
        assertNotInstance(FormatType.VXML_1_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.VXML_1_0, FormatType.UnknownFormat.class);
        //...WML_1_0
        assertInstance(FormatType.WML_1_0, FormatType.Wml1x.class);
        assertInstance(FormatType.WML_1_0, FormatType.Wml.class);
        assertInstance(FormatType.WML_1_0, FormatType.Xml.class);
        assertInstance(FormatType.WML_1_0, FormatType.AsciiData.class);
        assertInstance(FormatType.WML_1_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.WML_1_0, FormatType.Xml1x.class);
        assertNotInstance(FormatType.WML_1_0, FormatType.Vxml.class);
        assertNotInstance(FormatType.WML_1_0, FormatType.Xhtml.class);
        assertNotInstance(FormatType.WML_1_0, FormatType.HtmlBasic.class);
        assertNotInstance(FormatType.WML_1_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.WML_1_0, FormatType.UnknownFormat.class);
        //...XHTML_BASIC_1_0
        assertInstance(FormatType.XHTML_BASIC_1_0, FormatType.XhtmlBasic1x.class);
        assertInstance(FormatType.XHTML_BASIC_1_0, FormatType.XhtmlBasic.class);
        assertInstance(FormatType.XHTML_BASIC_1_0, FormatType.Xhtml.class);
        assertInstance(FormatType.XHTML_BASIC_1_0, FormatType.Xml.class);
        assertInstance(FormatType.XHTML_BASIC_1_0, FormatType.AsciiData.class);
        assertInstance(FormatType.XHTML_BASIC_1_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.XhtmlBasic2x.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.XhtmlStandard.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.Xml1x.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.Vxml.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.Wml.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.HtmlBasic.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.XHTML_BASIC_1_0, FormatType.UnknownFormat.class);
        //...XHTML_STANDARD_1_0
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.XhtmlStandard1x.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.XhtmlStandard.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.XhtmlBasic1x.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.XhtmlBasic.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.Xhtml.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.Xml.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.AsciiData.class);
        assertInstance(FormatType.XHTML_STANDARD_1_0, FormatType.GenericData.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.XhtmlStandard2x.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.XhtmlBasic2x.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.Xml1x.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.Vxml.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.Wml.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.HtmlBasic.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.BinaryData.class);
        assertNotInstance(FormatType.XHTML_STANDARD_1_0, FormatType.UnknownFormat.class);
        //...UNKNOWN_FORMAT
        assertInstance(FormatType.UNKNOWN_FORMAT, FormatType.UnknownFormat.class);
        assertInstance(FormatType.UNKNOWN_FORMAT, FormatType.GenericData.class);
        assertNotInstance(FormatType.UNKNOWN_FORMAT, FormatType.AsciiData.class);
        assertNotInstance(FormatType.UNKNOWN_FORMAT, FormatType.BinaryData.class);

    }

    public static void assertInstance(FormatType ft, Class targetCl) {

        assertTrue(ft + " not an instanceof " + targetCl, targetCl.isAssignableFrom(ft.getClass()));
    }

    public static void assertNotInstance(FormatType ft, Class targetCl) {
        assertTrue(ft + " is an instanceof " + targetCl + " (and it shouldn't be!)", !targetCl.isAssignableFrom(ft.getClass()));
    }
}
