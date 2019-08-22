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
 * $Id: FormatType.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.view;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;

/**
 * <p>This class defines all valid FormatTypes.
 *
 * <p>We start by defining a series of basic interfaces to define all known format types
 * and specify how they relate to one another. Please note that these are hierarchical in
 * nature. Strongly typed FormatType interfaces include:
 *
 * <ul>
 *   <li>GenericData<ul>
 *     <li>AsciiData<ul>
 *         <li>Html<ul>
 *             <li>HtmlStandard<ul>
 *                 <li>Html3x<ul>
 *                     <li>Html4x</li>
 *                   </ul>
 *                 </li>
 *               </ul>
 *             </li>
 *             <li>Chtml<ul>
 *                 <li>Chtml1x<ul>
 *                     <li>Chtml2x</li>
 *                   </ul>
 *                 </li>
 *               </ul>
 *             </li>
 *           </ul>
 *         </li>
 *         <li>Xml<ul>
 *             <li>Xml1x</li>
 *             <li>Vxml<ul>
 *                 <li>Vxml1x</li>
 *               </ul>
 *             </li>
 *             <li>Wml<ul>
 *                 <li>Wml1x</li>
 *               </ul>
 *             </li>
 *             <li>Xhtml<ul>
 *                 <li>XhtmlBasic<ul>
 *                     <li>XhtmlBasic1x<ul>
 *                       <li>XhtmlBasic2x</li>
 *                       </ul>
 *                     </li>
 *                     <li>XhtmlStandard<ul>
 *                       <li>XhtmlStandard1x<ul>
 *                       <li>XhtmlStandard2x</li>
 *                       </ul>
 *                       </li>
 *                       </ul>
 *                     </li>
 *                   </ul>
 *                 </li>
 *               </ul>
 *             </li>
 *           </ul>
 *         </li>
 *       </ul>
 *     </li>
 *     <li>BinaryData<ul>
 *         <li>Rtf</li>
 *         <li>Doc</li>
 *         <li>Pdf</li>
 *         <li>Exe</li>
 *       </ul>
 *     </li>
 *     <li>Unknown<br>
 *     </li>
 *   </ul>
 *   </li>
 * </ul>
 *
 * We can reference these interfaces to determine what kind of FormatType we're
 * actually dealing with. For instance, say we have a reference to a format type 'ft':
 *
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;ft = FormatType.HTML_4_0
 *
 * <p>Note here that we assigned the format type value by referring to a
 * concrete instance of the format type (the idea here is that if you wish
 * to assign a FormatType to some object you must select a concrete type).
 *
 * <p>Concrete format types include:
 * <ul>
 *    <li>FormatType.HTML_3_0</li>
 *    <li>FormatType.HTML_3_1</li>
 *    <li>FormatType.HTML_3_2</li>
 *    <li>FormatType.HTML_4_0</li>
 *    <li>FormatType.HTML_4_1</li>
 *    <li>FormatType.CHTML_1_0</li>
 *    <li>FormatType.CHTML_2_0</li>
 *    <li>FormatType.XML_1_0</li>
 *    <li>FormatType.VXML_1_0</li>
 *    <li>FormatType.XHTML_BASIC_1_0</li>
 *    <li>FormatType.XHTML_STANDARD_1_0</li>
 *    <li>FormatType.UNKNOWN_FORMAT</li>
 * </ul>
 *
 * <p> Now lets say we want to check if 'ft' is at least compatible with HTML 3.2,
 * we can use the instanceOf() operator like this:
 *
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;if (ft instanceof FormatType.Html3x) {...}
 *
 * <p>Since FormatType.HTML_4_0 implements FormatType.Html3x the logic check
 * succeeds and our 'if' statement is executed.
 *
 * <p>In case you are wondering 'Why in the world did we go to the effort to
 * define all these interfaces and such' the reason is that we wanted to make it
 * easy to check for "classes" of formats (ie. anything that conforms to HTML 3.2
 * for instance...an HTML 4.0 browser does). The net effect of this is that you
 * can write code that targets a baseline and as new versions come out your code
 * will continue to work.
 *
 * <p>The reason we used interfaces is that as code evolves it sometimes supports
 * more than one format. XHTML is an excellent example of this. There are 2 different
 * flavors of this: XHTML Basic is a subset of XHTML. What this means is that XHTML_1_x
 * should be an instance of XHTML_Basic. However, it should also be an instance of
 * XHTML_Basic_1_x. Since Java does not allow multiple inheritance in concrete classes,
 * we had to instead use interfaces so that a given format can specify support multiple
 * formats.
 */
public abstract class FormatType {

    //concrete instances of format types
    public static final FormatType HTML_3_0 = new Html3_0Impl();
    public static final FormatType HTML_3_1 = new Html3_1Impl();
    public static final FormatType HTML_3_2 = new Html3_2Impl();
    public static final FormatType HTML_4_0 = new Html4_0Impl();
    public static final FormatType HTML_4_1 = new Html4_1Impl();
    public static final FormatType CHTML_1_0 = new Chtml1_0Impl();
    public static final FormatType CHTML_2_0 = new Chtml2_0Impl();
    public static final FormatType XML_1_0 = new Xml1_0Impl();
    public static final FormatType VXML_1_0 = new Vxml1_0Impl();
    public static final FormatType WML_1_0 = new Wml1_0Impl();
    public static final FormatType XHTML_BASIC_1_0 = new XhtmlBasic1_0Impl();
    public static final FormatType XHTML_STANDARD_1_0 = new XhtmlStandard1_0Impl();
    public static final FormatType UNKNOWN_FORMAT = new UnknownFormatImpl();

    //base format types (hierarchical defs)
    public interface GenericData {};
    public interface   AsciiData extends GenericData {};
    public interface     HtmlBasic extends AsciiData {};
    public interface       Html extends HtmlBasic {};
    public interface         Html3x extends Html {};
    public interface           Html4x extends Html3x {};
    public interface       Chtml extends HtmlBasic {};
    public interface         Chtml1x extends Chtml {};
    public interface           Chtml2x extends Chtml1x {};
    public interface     Xml extends AsciiData {};
    public interface       Xml1x extends Xml {};
    public interface       Vxml extends Xml {};
    public interface         Vxml1x extends Vxml {};
    public interface       Wml extends Xml {};
    public interface         Wml1x extends Wml {};
    public interface       Xhtml extends Xml {};
    public interface         XhtmlBasic extends Xhtml {};
    public interface           XhtmlBasic1x extends XhtmlBasic {};
    public interface             XhtmlBasic2x extends XhtmlBasic1x {};
    public interface           XhtmlStandard extends XhtmlBasic {};
    public interface             XhtmlStandard1x extends XhtmlStandard, XhtmlBasic1x {};    //TODO: doesn't this also need to implement Html4x?
    public interface               XhtmlStandard2x extends XhtmlStandard1x, XhtmlBasic2x {};
    public interface   BinaryData extends GenericData {};
    public interface     Rtf extends BinaryData {};
    public interface     Doc extends BinaryData {};
    public interface     Pdf extends BinaryData {};
    public interface     Exe extends BinaryData {};
    public interface   UnknownFormat extends GenericData {};


    //dom class
    protected Class domCl = Node.class;

    //private implementations (while the concrete implementations above are final
    //so they can't be changed, the specific implementations are left merely protected,
    //allowing developers to extend for custom types if needed).

    //format types
    protected static class GenericDataImpl extends FormatType implements GenericData {
        public String toString() {
            String s = this.getClass().getName();
            int spos = s.indexOf("$");
            int epos = s.indexOf("Impl");
            return s.substring(spos+1, epos);
        }
    };
    protected static class AsciiDataImpl extends GenericDataImpl implements AsciiData {};
    protected static class HtmlBasicImpl extends AsciiDataImpl implements HtmlBasic {
        //this specifies that all Html data types will be using the HTMLElement DOM
        //interface rather than the Node interface
        public HtmlBasicImpl() {domCl = HTMLElement.class;}
    };
    protected static class HtmlImpl extends HtmlBasicImpl implements Html {};
    protected static class Html3xImpl extends HtmlImpl implements Html3x {};
    protected static class Html3_0Impl extends Html3xImpl {};
    protected static class Html3_1Impl extends Html3_0Impl {};
    protected static class Html3_2Impl extends Html3_1Impl {};
    protected static class Html4xImpl extends Html3xImpl implements Html4x {};
    protected static class Html4_0Impl extends Html4xImpl {};
    protected static class Html4_1Impl extends Html4xImpl {};
    protected static class ChtmlImpl extends HtmlBasicImpl implements Chtml {};
    protected static class Chtml1xImpl extends ChtmlImpl implements Chtml1x {};
    protected static class Chtml1_0Impl extends Chtml1xImpl {};
    protected static class Chtml2xImpl extends Chtml1xImpl implements Chtml2x {};
    protected static class Chtml2_0Impl extends Chtml2xImpl {};
    protected static class XmlImpl extends AsciiDataImpl implements Xml {};
    protected static class Xml1xImpl extends XmlImpl implements Xml1x {};
    protected static class Xml1_0Impl extends Xml1xImpl {};
    protected static class VxmlImpl extends XmlImpl implements Vxml {};
    protected static class Vxml1xImpl extends VxmlImpl implements Vxml1x {};
    protected static class Vxml1_0Impl extends Vxml1xImpl {};
    protected static class WmlImpl extends XmlImpl implements Wml {
//TODO: we need to specify the proper WML DOM class for domCl
//        public WmlImpl() {domCl = WMLElement.class;}
    };
    protected static class Wml1xImpl extends WmlImpl implements Wml1x {};
    protected static class Wml1_0Impl extends Wml1xImpl {};
    protected static class XhtmlImpl extends XmlImpl implements Xhtml {};
    protected static class XhtmlBasicImpl extends XhtmlImpl implements XhtmlBasic {};
    protected static class XhtmlBasic1xImpl extends XhtmlBasicImpl implements XhtmlBasic1x {};
    protected static class XhtmlBasic1_0Impl extends XhtmlBasic1xImpl {};
    protected static class XhtmlStandardImpl extends XhtmlBasicImpl implements XhtmlStandard {};
    protected static class XhtmlStandard1xImpl extends XhtmlStandardImpl implements XhtmlStandard1x {};
    protected static class XhtmlStandard1_0Impl extends XhtmlStandard1xImpl {};
    protected static class BinaryDataImpl extends GenericDataImpl implements BinaryData {
//TODO: we don't have a DOM for parsing binary data
        public BinaryDataImpl() {domCl = null;}
    };
    protected static class RtfImpl extends BinaryDataImpl implements Rtf {};
    protected static class DocImpl extends BinaryDataImpl implements Doc {};
    protected static class PdfImpl extends BinaryDataImpl implements Pdf {};
    protected static class ExeImpl extends BinaryDataImpl implements Exe {};
    protected static class UnknownFormatImpl extends GenericDataImpl implements UnknownFormat {};



    /**
     * Private constructor to prevent external instantiation
     */
    protected FormatType() {}

    /**
     * Get the DOM class associated with this particular format type.
     * This is useful because it makes it possible for components to
     * look up a Renderer by getting the DOM class from the format
     * type. By default, returns a reference to Node.class. HTML formats
     * return a reference to HTMLElement.class.
     *
     * @return the DOM class associated with this particular format type.
     */
    public Class getDOMClass() {
        return domCl;
    }

    public static void main(String args[]) {
        System.out.println ("Testing...");
        System.out.println ("HTML_3_0 instanceof FormatType.GenericData:"+(FormatType.HTML_3_0 instanceof FormatType.GenericData));
    }
}
