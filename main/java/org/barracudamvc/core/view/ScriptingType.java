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
 * $Id: ScriptingType.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.view;


/**
 * <p>This class defines all valid ScriptingTypes. 
 *
 * <p>We start by defining a series of basic interfaces to define all known scripting types
 * and specify how they relate to one another. Please note that these are hierarchical in 
 * nature. Strongly typed ScriptingType interfaces include:
 *
 * <ul>
 *   <li>ScriptingSupport<ul>
 *       <li>JavaScript<ul>
 *           <li>JavaScript1x</li><ul>
 *              <li>JavaScript10</li><ul>
 *                 <li>JavaScript11</li><ul>
 *                    <li>JavaScript12</li><ul>
 *                       <li>JavaScript13</li><ul>
 *                          <li>JavaScript14</li><ul>
 *                             <li>JavaScript15</li>
 *                            </ul>
 *                         </ul>
 *                      </ul>
 *                   </ul>
 *                </ul>
 *             </ul>
 *          </ul>
 *       </li>
 *       <li>WmlScript<ul>
 *           <li>WmlScript1x</li>
 *              <li>WmlScript10</li><ul>
 *                 <li>WmlScript11</li><ul>
 *                    <li>WmlScript12</li>
 *                   </ul>
 *                </ul>
 *             </ul>
 *         </ul>
 *       </li>
 *       <li>None</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * We can reference these interfaces to determine what kind of ScriptingType we're 
 * actually dealing with. We also define concrete scripting types to allow us to 
 * actually assign scripting type values.
 *
 * <p>Concrete scripting types include:
 * <ul>
 *    <li>ScriptingType.JAVASCRIPT_1x</li>
 *    <li>ScriptingType.JAVASCRIPT_1_0</li>
 *    <li>ScriptingType.JAVASCRIPT_1_1</li>
 *    <li>ScriptingType.JAVASCRIPT_1_2</li>
 *    <li>ScriptingType.JAVASCRIPT_1_3</li>
 *    <li>ScriptingType.JAVASCRIPT_1_4</li>
 *    <li>ScriptingType.JAVASCRIPT_1_5</li>
 *    <li>ScriptingType.WMLSCRIPT_1x</li>
 *    <li>ScriptingType.WMLSCRIPT_1_0</li>
 *    <li>ScriptingType.WMLSCRIPT_1_1</li>
 *    <li>ScriptingType.WMLSCRIPT_1_2</li>
 *    <li>ScriptingType.NONE</li>
 * </ul>
 *
 * <p> This all functions in a manner similar to the FormatType class. For more 
 * details, please refer to those Javadocs.
 */
public abstract class ScriptingType {

    //concrete instances of scripting types
    public static final ScriptingType JAVASCRIPT_1x = new JavaScript1xImpl();
    public static final ScriptingType JAVASCRIPT_1_0 = new JavaScript1_0Impl();
    public static final ScriptingType JAVASCRIPT_1_1 = new JavaScript1_1Impl();
    public static final ScriptingType JAVASCRIPT_1_2 = new JavaScript1_2Impl();
    public static final ScriptingType JAVASCRIPT_1_3 = new JavaScript1_3Impl();
    public static final ScriptingType JAVASCRIPT_1_4 = new JavaScript1_4Impl();
    public static final ScriptingType JAVASCRIPT_1_5 = new JavaScript1_5Impl();
    public static final ScriptingType WMLSCRIPT_1x = new WmlScript1xImpl();
    public static final ScriptingType WMLSCRIPT_1_0 = new WmlScript1_0Impl();
    public static final ScriptingType WMLSCRIPT_1_1 = new WmlScript1_1Impl();
    public static final ScriptingType WMLSCRIPT_1_2 = new WmlScript1_2Impl();
    public static final ScriptingType NONE = new NoneImpl();

    //base scripting types (hierarchical defs)
    public interface ScriptingSupport {};
    public interface   JavaScript extends ScriptingSupport {};
    public interface     JavaScript1x extends JavaScript {};
    public interface       JavaScript10 extends JavaScript1x {};
    public interface         JavaScript11 extends JavaScript10 {};
    public interface           JavaScript12 extends JavaScript11 {};
    public interface             JavaScript13 extends JavaScript12 {};
    public interface               JavaScript14 extends JavaScript13 {};
    public interface                 JavaScript15 extends JavaScript14 {};
    public interface   WmlScript extends ScriptingSupport {};
    public interface     WmlScript1x extends WmlScript {};
    public interface       WmlScript10 extends WmlScript1x {};
    public interface         WmlScript11 extends WmlScript10 {};
    public interface           WmlScript12 extends WmlScript11 {};
    public interface   None extends ScriptingSupport {};
    
    //private implementations (while the concrete implementations above are final
    //so they can't be changed, the specific implementations are left merely protected,
    //allowing developers to extend for custom types if needed).
    
    //scripting types
    protected static class ScriptingSupportImpl extends ScriptingType implements ScriptingSupport {
        public String toString() {
            String s = this.getClass().getName();
            int spos = s.indexOf("$");
            int epos = s.indexOf("Impl");
            return s.substring(spos+1, epos);
        }
    };
    protected static class JavaScriptImpl extends ScriptingSupportImpl implements JavaScript {};
    protected static class JavaScript1xImpl extends JavaScriptImpl implements JavaScript1x {};
    protected static class JavaScript1_0Impl extends JavaScript1xImpl implements JavaScript10 {};
    protected static class JavaScript1_1Impl extends JavaScript1_0Impl implements JavaScript11 {};
    protected static class JavaScript1_2Impl extends JavaScript1_1Impl implements JavaScript12 {};
    protected static class JavaScript1_3Impl extends JavaScript1_2Impl implements JavaScript13 {};
    protected static class JavaScript1_4Impl extends JavaScript1_3Impl implements JavaScript14 {};
    protected static class JavaScript1_5Impl extends JavaScript1_4Impl implements JavaScript15 {};
    protected static class WmlScriptImpl extends ScriptingSupportImpl implements WmlScript {};
    protected static class WmlScript1xImpl extends WmlScriptImpl implements WmlScript1x {};
    protected static class WmlScript1_0Impl extends WmlScript1xImpl implements WmlScript10 {};
    protected static class WmlScript1_1Impl extends WmlScript1_0Impl implements WmlScript11 {};
    protected static class WmlScript1_2Impl extends WmlScript1_1Impl implements WmlScript12 {};
    protected static class NoneImpl extends ScriptingSupportImpl implements None {};



    /**
     * Private constructor to prevent external instantiation
     */
    protected ScriptingType() {}

    public static void main(String args[]) {
        System.out.println ("Testing...");
        System.out.println ("JAVASCRIPT_1_3 instanceof ScriptingType.JavaScript1x:"+(ScriptingType.JAVASCRIPT_1_3 instanceof ScriptingType.JavaScript1x));
    }
}
