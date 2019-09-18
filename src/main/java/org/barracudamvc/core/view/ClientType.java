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
 * $Id: ClientType.java 265 2014-02-21 17:40:07Z alci $
 */
package org.barracudamvc.core.view;


/**
 * <p>This class defines all valid ClientTypes. 
 *
 * <p>We start by defining a series of basic interfaces to define all known client types
 * and specify how they relate to one another. Please note that these are hierarchical in 
 * nature. Strongly typed ClientType interfaces include:
 *
 * <ul>
 *   <li>GenericBrowser<ul>
 *       <li>HtmlBrowser<ul>
 *           <li>HtmlStandardBrowser<ul>
 *               <li>Html32Browser<ul>
 *                   <li>IE3x<ul>
 *                       <li>IE4x</li>
 *                     </ul>
 *                   </li>
 *                   <li>NN3x<ul>
 *                       <li>NN4x</li>
 *                     </ul>
 *                   </li>
 *                   <li>Opera4x</li>
 *                 </ul>
 *               </li>
 *               <li>Html40Browser<ul>
 *                   <li>IE5x<ul>
 *                       <li>IE6x</li>
 *                     </ul>
 *                   </li>
 *                   <li>NN6x</li>
 *                   <li>Opera5x</li>
 *                 </ul>
 *               </li>
 *             </ul>
 *           </li>
 *           <li>ChtmlBrowser</li>
 *         </ul>
 *       </li>
 *       <li>XmlBrowser<ul>
 *           <li>VxmlBrowser</li>
 *           <li>WmlBrowser</li>
 *           <li>XhtmlBrowser</li>
 *         </ul>
 *       </li>
 *       <li>Unknown</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * We can reference these interfaces to determine what kind of ClientType we're 
 * actually dealing with. We also define concrete client types to allow us to 
 * actually assign client type values.
 *
 * <p>Concrete client types include:
 * <ul>
 *    <li>ClientType.HTML_BROWSER</li>
 *    <li>ClientType.HTML_3_2_BROWSER</li>
 *    <li>ClientType.IE_3x</li>
 *    <li>ClientType.IE_4x</li>
 *    <li>ClientType.NN_3x</li>
 *    <li>ClientType.NN_4x</li>
 *    <li>ClientType.OPERA_4x</li>
 *    <li>ClientType.HTML_4_0_BROWSER</li>
 *    <li>ClientType.IE_5x</li>
 *    <li>ClientType.IE_6x</li>
 *    <li>ClientType.NN_6x</li>
 *    <li>ClientType.OPERA_5x</li>
 *    <li>ClientType.CHTML_BROWSER</li>
 *    <li>ClientType.WML_BROWSER</li>
 *    <li>ClientType.XML_BROWSER</li>
 *    <li>ClientType.UNKNOWN_BROWSER</li>
 * </ul>
 *
 * <p> This all functions in a manner similar to the FormatType class. For more 
 * details, please refer to those Javadocs.
 */
public abstract class ClientType {

    //concrete instances of client types
    public static final ClientType HTML_BROWSER = new HtmlBrowserImpl();
    public static final ClientType HTML_3_2_BROWSER = new Html32BrowserImpl();
    public static final ClientType IE_3x = new IE3xImpl();
    public static final ClientType IE_4x = new IE4xImpl();
    public static final ClientType NN_3x = new NN3xImpl();
    public static final ClientType NN_4x = new NN4xImpl();
    public static final ClientType OPERA_4x = new Opera4xImpl();
    public static final ClientType HTML_4_0_BROWSER = new Html40BrowserImpl();
    public static final ClientType IE_5x = new IE5xImpl();
    public static final ClientType IE_6x = new IE6xImpl();
    public static final ClientType IE_7x = new IE7xImpl();
    public static final ClientType IE_8x = new IE8xImpl();
    public static final ClientType IE_9x = new IE9xImpl();
    public static final ClientType IE_10x = new IE10xImpl();
    public static final ClientType IE_11x = new IE11xImpl();
    public static final ClientType NN_6x = new NN6xImpl();
    public static final ClientType OPERA_5x = new Opera5xImpl();
    public static final ClientType CHTML_BROWSER = new ChtmlBrowserImpl();
    public static final ClientType XML_BROWSER = new XmlBrowserImpl();
    public static final ClientType VXML_BROWSER = new VxmlBrowserImpl();
    public static final ClientType WML_BROWSER = new WmlBrowserImpl();
    public static final ClientType XHTML_BROWSER = new XhtmlBrowserImpl();
    public static final ClientType UNKNOWN_BROWSER = new UnknownBrowserImpl();

    //base client types (hierarchical defs)
    public interface GenericBrowser {};
    public interface   HtmlBrowser extends GenericBrowser {};
    public interface     HtmlStandardBrowser extends HtmlBrowser {};
    public interface       Html32Browser extends HtmlStandardBrowser {};
    public interface         IE3x extends Html32Browser {};
    public interface           IE4x extends IE3x {};
    public interface         NN3x extends Html32Browser {};
    public interface           NN4x extends NN3x {};
    public interface         Opera4x extends Html32Browser {};
    public interface         Html40Browser extends Html32Browser {};
    public interface           IE5x extends Html40Browser, IE4x {};
    public interface             IE6x extends Html40Browser, IE5x {};
    public interface             IE7x extends Html40Browser, IE6x {};
    public interface             IE8x extends Html40Browser, IE7x {};
    public interface             IE9x extends Html40Browser, IE8x {};
    public interface             IE10x extends Html40Browser, IE9x {};
    public interface             IE11x extends Html40Browser, IE10x {};
    public interface           NN6x extends Html40Browser, NN4x {};
    public interface           Opera5x extends Html40Browser, Opera4x {};
    public interface     ChtmlBrowser extends HtmlBrowser {};
    public interface   XmlBrowser extends GenericBrowser {};
    public interface     VxmlBrowser extends XmlBrowser {};
    public interface     WmlBrowser extends XmlBrowser {};
    public interface     XhtmlBrowser extends XmlBrowser {};
    public interface   UnknownBrowser extends GenericBrowser {};
    
//TODO: we really need to flesh out the browser info for CHTML, VXML, WML, & HXTML (both basic and standard)
    
    //private implementations (while the concrete implementations above are final
    //so they can't be changed, the specific implementations are left merely protected,
    //allowing developers to extend for custom types if needed).
    
    //client types
    protected static class GenericBrowserImpl extends ClientType implements GenericBrowser {
        public String toString() {
            String s = this.getClass().getName();
            int spos = s.indexOf("$");
            int epos = s.indexOf("Impl");
            return s.substring(spos+1, epos);
        }
    };
    protected static class HtmlBrowserImpl extends GenericBrowserImpl implements HtmlBrowser {};
    protected static class HtmlStandardBrowserImpl extends HtmlBrowserImpl implements HtmlStandardBrowser {};
    protected static class Html32BrowserImpl extends HtmlStandardBrowserImpl implements Html32Browser {};
    protected static class IE3xImpl extends Html32BrowserImpl implements IE3x {};
    protected static class IE4xImpl extends IE3xImpl implements IE4x {};
    protected static class NN3xImpl extends Html32BrowserImpl implements NN3x {};
    protected static class NN4xImpl extends NN3xImpl implements NN4x {};
    protected static class Opera4xImpl extends Html32BrowserImpl implements Opera4x {};
    protected static class Html40BrowserImpl extends Html32BrowserImpl implements Html40Browser {};
    protected static class IE5xImpl extends Html40BrowserImpl implements IE5x {};
    protected static class IE6xImpl extends Html40BrowserImpl implements IE6x {};
    protected static class IE7xImpl extends Html40BrowserImpl implements IE7x {};
    protected static class IE8xImpl extends Html40BrowserImpl implements IE8x {};
    protected static class IE9xImpl extends Html40BrowserImpl implements IE9x {};
    protected static class IE10xImpl extends Html40BrowserImpl implements IE10x {};
    protected static class IE11xImpl extends Html40BrowserImpl implements IE11x {};
    protected static class NN6xImpl extends Html40BrowserImpl implements NN6x {};
    protected static class Opera5xImpl extends Html40BrowserImpl implements Opera5x {};
    protected static class ChtmlBrowserImpl extends HtmlBrowserImpl implements ChtmlBrowser {};
    protected static class XmlBrowserImpl extends GenericBrowserImpl implements XmlBrowser {};
    protected static class VxmlBrowserImpl extends XmlBrowserImpl implements VxmlBrowser {};
    protected static class WmlBrowserImpl extends XmlBrowserImpl implements WmlBrowser {};
    protected static class XhtmlBrowserImpl extends XmlBrowserImpl implements XhtmlBrowser {};
    protected static class UnknownBrowserImpl extends GenericBrowserImpl implements UnknownBrowser {};

    /**
     * Given a target string, find the matching client type
     *
     * @param strName the str name that describes this client type
     *      (ie. "ClientType.HTML_BROWSER", etc)
     * @return the matching client type or null if there is no match
     */
    public static ClientType getInstance(String strName) {
        if (strName.indexOf("HTML_BROWSER")!=-1) return ClientType.HTML_BROWSER;
        else if (strName.indexOf("HTML_3_2_BROWSER")!=-1) return ClientType.HTML_3_2_BROWSER;
        else if (strName.indexOf("IE_3x")!=-1) return ClientType.IE_3x;
        else if (strName.indexOf("IE_4x")!=-1) return ClientType.IE_4x;
        else if (strName.indexOf("NN_3x")!=-1) return ClientType.NN_3x;
        else if (strName.indexOf("NN_4x")!=-1) return ClientType.NN_4x;
        else if (strName.indexOf("OPERA_4x")!=-1) return ClientType.OPERA_4x;
        else if (strName.indexOf("HTML_4_0_BROWSER")!=-1) return ClientType.HTML_4_0_BROWSER;
        else if (strName.indexOf("IE_5x")!=-1) return ClientType.IE_5x;
        else if (strName.indexOf("IE_6x")!=-1) return ClientType.IE_6x;
        else if (strName.indexOf("IE_7x")!=-1) return ClientType.IE_7x;
        else if (strName.indexOf("IE_8x")!=-1) return ClientType.IE_8x;
        else if (strName.indexOf("IE_9x")!=-1) return ClientType.IE_9x;
        else if (strName.indexOf("IE_10x")!=-1) return ClientType.IE_10x;
        else if (strName.indexOf("IE_11x")!=-1) return ClientType.IE_11x;
        else if (strName.indexOf("NN_6x")!=-1) return ClientType.NN_6x;
        else if (strName.indexOf("OPERA_5x")!=-1) return ClientType.OPERA_5x;
        else if (strName.indexOf("CHTML_BROWSER")!=-1) return ClientType.CHTML_BROWSER;
        else if (strName.indexOf("XML_BROWSER")!=-1) return ClientType.XML_BROWSER;
        else if (strName.indexOf("VXML_BROWSER")!=-1) return ClientType.VXML_BROWSER;
        else if (strName.indexOf("WML_BROWSER")!=-1) return ClientType.WML_BROWSER;
        else if (strName.indexOf("XHTML_BROWSER")!=-1) return ClientType.XHTML_BROWSER;
        else if (strName.indexOf("UNKNOWN_BROWSER")!=-1) return ClientType.UNKNOWN_BROWSER;
        else return null;
    }

    /**
     * Private constructor to prevent external instantiation
     */
    protected ClientType() {}

    public static void main(String args[]) {
        System.out.println ("Testing...");
        System.out.println ("IE_5x instanceof ClientType.Html32Browser:"+(ClientType.IE_5x instanceof ClientType.Html32Browser));
    }
}
