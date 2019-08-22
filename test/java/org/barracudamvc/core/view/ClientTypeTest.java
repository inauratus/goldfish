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
 * $Id: ClientTypeTest.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.view;


import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This test verifies that the hierachical organization of 
 * ClientType classes. We do this by testing for what it should be 
 * an instanceof and what it shouldn't. The idea is that the ClientType
 * extension hierarchy is very important and we don't want it getting 
 * changed accidentally. This test should preclude that.
 */
public class ClientTypeTest {

    @Test
    public void testTypes() {
        assertTypeIsInstance(ClientType.HTML_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.HTML_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_BROWSER, ClientType.HtmlStandardBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_BROWSER, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_BROWSER, ClientType.UnknownBrowser.class);
        //...HTML_3_2_BROWSER
        assertTypeIsInstance(ClientType.HTML_3_2_BROWSER, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.HTML_3_2_BROWSER, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.HTML_3_2_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.HTML_3_2_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.Html40Browser.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_3_2_BROWSER, ClientType.UnknownBrowser.class);
        //...IE_3x
        assertTypeIsInstance(ClientType.IE_3x, ClientType.IE3x.class);
        assertTypeIsInstance(ClientType.IE_3x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.IE_3x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.IE_3x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.IE_3x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.IE4x.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.Html40Browser.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_3x, ClientType.UnknownBrowser.class);
        //...IE_4x
        assertTypeIsInstance(ClientType.IE_4x, ClientType.IE4x.class);
        assertTypeIsInstance(ClientType.IE_4x, ClientType.IE3x.class);
        assertTypeIsInstance(ClientType.IE_4x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.IE_4x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.IE_4x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.IE_4x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_4x, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.IE_4x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.IE_4x, ClientType.Html40Browser.class);
        assertTypeIsNotInstance(ClientType.IE_4x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_4x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_4x, ClientType.UnknownBrowser.class);
        //...NN_3x
        assertTypeIsInstance(ClientType.NN_3x, ClientType.NN3x.class);
        assertTypeIsInstance(ClientType.NN_3x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.NN_3x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.NN_3x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.NN_3x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.NN4x.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.Html40Browser.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_3x, ClientType.UnknownBrowser.class);
        //...NN_4x
        assertTypeIsInstance(ClientType.NN_4x, ClientType.NN4x.class);
        assertTypeIsInstance(ClientType.NN_4x, ClientType.NN3x.class);
        assertTypeIsInstance(ClientType.NN_4x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.NN_4x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.NN_4x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.NN_4x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_4x, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.NN_4x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.NN_4x, ClientType.Html40Browser.class);
        assertTypeIsNotInstance(ClientType.NN_4x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_4x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_4x, ClientType.UnknownBrowser.class);
        //...OPERA_4x
        assertTypeIsInstance(ClientType.OPERA_4x, ClientType.Opera4x.class);
        assertTypeIsInstance(ClientType.OPERA_4x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.OPERA_4x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.OPERA_4x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.OPERA_4x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.Opera5x.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.Html40Browser.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.OPERA_4x, ClientType.UnknownBrowser.class);
        //...HTML_4_0_BROWSER
        assertTypeIsInstance(ClientType.HTML_4_0_BROWSER, ClientType.Html40Browser.class);
        assertTypeIsInstance(ClientType.HTML_4_0_BROWSER, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.HTML_4_0_BROWSER, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.HTML_4_0_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.HTML_4_0_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.IE5x.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.NN6x.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.Opera5x.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.HTML_4_0_BROWSER, ClientType.UnknownBrowser.class);
        //...IE_5x
        assertTypeIsInstance(ClientType.IE_5x, ClientType.IE5x.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.IE4x.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.IE3x.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.Html40Browser.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.IE_5x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_5x, ClientType.IE6x.class);
        assertTypeIsNotInstance(ClientType.IE_5x, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.IE_5x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.IE_5x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_5x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_5x, ClientType.UnknownBrowser.class);
        //...IE_6x
        assertTypeIsInstance(ClientType.IE_6x, ClientType.IE6x.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.IE5x.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.IE4x.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.IE3x.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.Html40Browser.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.IE_6x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_6x, ClientType.NN3x.class);
        assertTypeIsNotInstance(ClientType.IE_6x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.IE_6x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_6x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.IE_6x, ClientType.UnknownBrowser.class);
        //...NN_6x
        assertTypeIsInstance(ClientType.NN_6x, ClientType.NN6x.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.NN4x.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.NN3x.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.Html40Browser.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.NN_6x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_6x, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.NN_6x, ClientType.Opera4x.class);
        assertTypeIsNotInstance(ClientType.NN_6x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_6x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.NN_6x, ClientType.UnknownBrowser.class);
        //...OPERA_5x
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.Opera5x.class);
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.Opera4x.class);
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.Html40Browser.class);
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.Html32Browser.class);
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.HtmlStandardBrowser.class);
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.OPERA_5x, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.OPERA_5x, ClientType.IE3x.class);
        assertTypeIsNotInstance(ClientType.OPERA_5x, ClientType.NN4x.class);
        assertTypeIsNotInstance(ClientType.OPERA_5x, ClientType.ChtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.OPERA_5x, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.OPERA_5x, ClientType.UnknownBrowser.class);
        //...CHTML_BROWSER
        assertTypeIsInstance(ClientType.CHTML_BROWSER, ClientType.ChtmlBrowser.class);
        assertTypeIsInstance(ClientType.CHTML_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsInstance(ClientType.CHTML_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.CHTML_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsNotInstance(ClientType.CHTML_BROWSER, ClientType.UnknownBrowser.class);
        //...XML_BROWSER
        assertTypeIsInstance(ClientType.XML_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsInstance(ClientType.XML_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.XML_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.XML_BROWSER, ClientType.UnknownBrowser.class);
        //...VXML_BROWSER
        assertTypeIsInstance(ClientType.VXML_BROWSER, ClientType.VxmlBrowser.class);
        assertTypeIsInstance(ClientType.VXML_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsInstance(ClientType.VXML_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.VXML_BROWSER, ClientType.WmlBrowser.class);
        assertTypeIsNotInstance(ClientType.VXML_BROWSER, ClientType.XhtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.VXML_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.VXML_BROWSER, ClientType.UnknownBrowser.class);
        //...WML_BROWSER
        assertTypeIsInstance(ClientType.WML_BROWSER, ClientType.WmlBrowser.class);
        assertTypeIsInstance(ClientType.WML_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsInstance(ClientType.WML_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.WML_BROWSER, ClientType.VxmlBrowser.class);
        assertTypeIsNotInstance(ClientType.WML_BROWSER, ClientType.XhtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.WML_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.WML_BROWSER, ClientType.UnknownBrowser.class);
        //...HXTML_BROWSER
        assertTypeIsInstance(ClientType.XHTML_BROWSER, ClientType.XhtmlBrowser.class);
        assertTypeIsInstance(ClientType.XHTML_BROWSER, ClientType.XmlBrowser.class);
        assertTypeIsInstance(ClientType.XHTML_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.XHTML_BROWSER, ClientType.VxmlBrowser.class);
        assertTypeIsNotInstance(ClientType.XHTML_BROWSER, ClientType.WmlBrowser.class);
        assertTypeIsNotInstance(ClientType.XHTML_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.XHTML_BROWSER, ClientType.UnknownBrowser.class);
        //...UNKNOWN_BROWSER
        assertTypeIsInstance(ClientType.UNKNOWN_BROWSER, ClientType.UnknownBrowser.class);
        assertTypeIsInstance(ClientType.UNKNOWN_BROWSER, ClientType.GenericBrowser.class);
        assertTypeIsNotInstance(ClientType.UNKNOWN_BROWSER, ClientType.HtmlBrowser.class);
        assertTypeIsNotInstance(ClientType.UNKNOWN_BROWSER, ClientType.XmlBrowser.class);

    }

    public static void assertTypeIsInstance(ClientType ct, Class targetCl) {

        assertTrue(ct + " not an instanceof " + targetCl, targetCl.isAssignableFrom(ct.getClass()));
    }

    /**
     * Make sure that the class being tested is NOT an instance of the 
     * target class
     */
    public static void assertTypeIsNotInstance(ClientType ct, Class targetCl) {

        assertTrue(ct + " is an instanceof " + targetCl + " (and it shouldn't be!)", !targetCl.isAssignableFrom(ct.getClass()));
    }
}
