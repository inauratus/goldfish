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
 * $Id: TemplateDirective.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class defines a TemplateDirective. A TemplateDirective is
 * created from a string (usually in the HTML class attribute) and
 * takes the form
 *     <strong><emph>Dir::&lt;command&gt;.&lt;model&gt;.&lt;key&gt;.&lt;props&gt;...</emph></strong>
 * where:
 * <ul>
 *    <li>&lt;command&gt; = the name of the command (custom directives are allowed)</li>
 *    <li>&lt;model&gt; = the name of the model</li>
 *    <li>&lt;key&gt; = the name of the key within the model</li>
 *    <li>&lt;data&gt; = any additional data (in any format) that may be associated with the directive</li>
 * </ul>
 * <p>As an example, the following are valid directives:
 * <ul>
 *    <li><strong><emph>Dir::Get_Data.UserData.FirstName</emph></strong></li>
 *    <li><strong><emph>Dir::Get_Data.UserData.LastName</emph></strong></li>
 *    <li><strong><emph>Dir::Get_Data.Iterate_Start.UserData..max=50&min=25&foo=bar</emph></strong></li>
 *    <li><strong><emph>Dir::Get_Data.Iterate_End</emph></strong></li>
 *    <li><strong><emph>Dir::Custom_Directive.UserData..do=something</emph></strong></li>
 * </ul>
 */
public class TemplateDirective {

    //directive prefix
    public final static String DIR_PREFIX = "Dir::";
    //supported directives commands
    public final static String ITERATE_START = "Iterate_Start";
    public final static String ITERATE_NEXT = "Iterate_Next";
    public final static String ITERATE_END = "Iterate_End";
    public final static String GET_DATA = "Get_Data";
    public final static String SET_ATTR = "Set_Attr";
    public final static String DISCARD = "Discard";
    //supported directives commands (specific to BlockIterator)
    public final static String BLOCK_ITERATE = "Block_Iterate";
    public final static String BLOCK_ITERATE_START = "Block_Iterate_Start"; //implies discard of node which contains this dir
    public final static String BLOCK_ITERATE_END = "Block_Iterate_End";     //implies discard of node which contains this dir
    //public constants (this value is stored in the lor if the template helper encounters a block iterator directive)
    public static final String HAS_BLOCK_ITERATOR = TemplateView.class.getName() + ".HasBlockIterator";              //(Boolean) - true if we encountered a BlockIterator directive while processing
    protected String cmd = null;
    protected String modelName = null;
    protected String keyName = null;
    protected String keyData = null;
    public static final TemplateDirective EMPTY_DIRECTIVE = new TemplateDirective("<Empty>", "<Empty>", "<Empty>", "<Empty>");

    protected TemplateDirective() {
    }

    //csc_030703.1 - added to make it easier to programatically create directives,
    //witout having to parse a String
    public TemplateDirective(String icmd, String imodelName, String ikeyName, String ikeyData) {
        cmd = icmd;
        modelName = imodelName;
        keyName = ikeyName;
        keyData = ikeyData;
    }

    public String getCommand() {
        return cmd;
    }

    public String getModelName() {
        return modelName;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getKeyData() {
        return keyData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("Dir::").append(cmd);
        if (modelName != null) {
            sb.append(".").append(modelName);
            if (keyName != null) {
                sb.append(".").append(keyName);
                if (keyData != null) {
                    sb.append(".").append(keyData);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof TemplateDirective)) {
            return false;
        }
        TemplateDirective td = (TemplateDirective) o;
        if (cmd != null && td.getCommand() != null && !cmd.equals(td.getCommand())) {
            return false;
        }
        if (modelName != null && td.getModelName() != null && !modelName.equals(td.getModelName())) {
            return false;
        }
        if (keyName != null && td.getKeyName() != null && !keyName.equals(td.getKeyName())) {
            return false;
        }
        if (keyData != null && td.getKeyData() != null && !keyData.equals(td.getKeyData())) {
            return false;
        }
        return true;
    }

    /**
     * Given a space delimited string of directive commands, convert
     * each segment into a TemplateDirective and return all the 
     * valid directives in a List. Invalid directives and block iterator specific
     * directives are silently ignored.
     *
     * @param sourceStr a space delimited string of directive commands
     * @return a List of TemplateDirectives
     */
    public static synchronized List<TemplateDirective> getAllInstances(String sourceStr) {
        List<TemplateDirective> directives = new ArrayList<TemplateDirective>();
        StringTokenizer st = new StringTokenizer(sourceStr, " ");
        while (st.hasMoreTokens()) {
            try {
                directives.add(getInstance_private(st.nextToken()));
            } catch (InvalidDirectiveException e) {
            }
        }
        return directives;
    }

    /**
     * Given a string of the form  
     * <strong><emph>Dir::&lt;command&gt;.&lt;model&gt;.&lt;key&gt;.&lt;props&gt;...</emph></strong>
     * convert it into a TemplateDirective and return that object.
     *
     * @param sourceStr a space delimited string of directive commands
     * @return the corresponding TemplateDirectives
     * @throws InvalidDirectiveException if the String cannot be converted
     */
    public static TemplateDirective getInstance(String sourceStr) throws InvalidDirectiveException {
        return getInstance_private(sourceStr);
    }

    private static TemplateDirective getInstance_private(String sourceStr) throws InvalidDirectiveException {
        //make sure it starts with Dir::
        if (sourceStr == null || !(sourceStr.startsWith("Dir::"))) {
            throw new InvalidDirectiveException("Invalid Directive:" + sourceStr + "...Directive must start with 'Dir::'");
        }

        //create a new TemplateDirective
        TemplateDirective td = new TemplateDirective();

        //create a String tokenizer and parse on periods    
        int pos0 = 5;
        int pos1 = -1;
        for (int cntr = 0; cntr < 4; cntr++) {
            pos1 = sourceStr.indexOf(".", pos0);
            if (pos1 < 0) {
                pos1 = sourceStr.length();
            }
            if (cntr == 0 && pos0 > -1 && pos1 > -1 && pos0 < pos1) {
                td.cmd = sourceStr.substring(pos0, pos1);
            } else if (cntr == 1 && pos0 > -1 && pos1 > -1 && pos0 < pos1) {
                td.modelName = sourceStr.substring(pos0, pos1);
            } else if (cntr == 2 && pos0 > -1 && pos1 > -1 && pos0 < pos1) {
                td.keyName = sourceStr.substring(pos0, pos1);
            } else if (cntr == 3 && pos0 > -1 && pos0 < sourceStr.length()) {
                td.keyData = sourceStr.substring(pos0);
            }
            pos0 = pos1 + 1;
        }

        return td;
    }
}