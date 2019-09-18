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
 * $Id: FormUtil.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.sql.*;
import java.text.*;
import java.util.Date;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.*;
import org.barracudamvc.core.forms.parsers.FormElementParser;
import org.barracudamvc.plankton.*;
import org.barracudamvc.plankton.data.*;

/**
 * <p>Simple Form related utilities
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 */
public class FormUtil {

    //public constants
    protected static final Logger logger = Logger.getLogger(FormUtil.class.getName());

    /**
     * Given a BComponent and a reference to a FormMap, this method will automatically
     * repopulate the form value into the component. The way it works is fairly simply: 
     * you create the appropriate component and name it - the component name will be used
     * to locate the appropriate form element and repopulate the component based on the 
     * form element value.
     *
     * @param fm the backing form map
     * @param bcomp the component to be repopulated
     * @since csc_110304_1
     */
    public static void repopulate(FormMap fm, BComponent bcomp) {
        //get the component name - this will be used to actually name the html input element,
        //so we can use it to look up the item in the form
// fro_070907_1_start
        repopulate(fm, bcomp, bcomp.getName());
    }

    
    /**
     * Instead of using directly the BComponent name as key, you can specify your own key.
     * 
     *
     * @param fm the backing form map
     * @param bcomp the component to be repopulated
     * @since fro_070907_1
     */
    public static void repopulate(FormMap fm, BComponent bcomp, String key) {

        if (key == null) {
            return;
        }

        //form repopulation (if we have a BInput or BToggleButton, and we can match it 
        //to a form element, and the component doesn't yet have a value, but the form 
        //element does, then set it to what's in the form element)
        FormElement el = fm.getElement(key);
        if (el == null) {
            return;
        }

        //now lets get some info about the form element we're repopulating from
//        FormType type = el.getType();
        Object val = el.getVal();
        Object oval = el.getOrigVal();
        if (val == null || val.toString().trim().equals("")) {
            val = oval;
        }

        //finally, let's repopulate according to the type of the component
        if (bcomp instanceof ExtendedComponent) {
            ((ExtendedComponent) bcomp).repopulate(fm, key);
        } else if (bcomp instanceof BToggleButton) {
            ((BToggleButton) bcomp).setSelected(Boolean.TRUE.equals(val));

        } else if ((val != null) && (bcomp instanceof BInput) && (((BInput) bcomp).getValue() == null)) {
            ((BInput) bcomp).setValue(formatForOutput(val, el));

//csc_010804_1_start
//this is actually wrong - assume the value in the form map is the _value_ not the index. This 
//means that we need to iterate through the model and find a value that matches - THAT's the index
        } else if ((val != null) && (bcomp instanceof BSelect) && (((BSelect) bcomp).getSelectedIndex() == -1)) {
            ListModel lm = ((BSelect) bcomp).getModel();
            if (lm != null) {
                for (int i = 0, max = lm.getSize(); i < max; i++) {
                    Object item = lm.getItemAt(i);
                    boolean match = (item instanceof ItemMap && CollectionsUtil.checkEquals(val, ((ItemMap) item).getKey()));
                    if (!match) {
                        match = (item instanceof ItemMap && CollectionsUtil.checkEquals(val, ((ItemMap) item).getValue()));
                    }
                    if (!match) {
                        match = CollectionsUtil.checkEquals(val, item);
                    }
                    if (match) {
                        ((BSelect) bcomp).setSelectedIndex(i);
                        break;
                    }
                }
            }

//csc_010804_1_end
        }
    }
//  fro_070907_1_end
    
    
    /**
     * Convenience method to format form values in a friendly manner (based on
     * their data type) that will allow for later reparsing
     */
    public static String formatForOutput(Object val, FormElement el) {

        //see if we can format it using a formatter
        Format formatter = el.getFormat();
        if (formatter != null) {
            try {
                return formatter.format(val);               //try to format using formatter
            } catch (IllegalArgumentException e) {
                if (val != null && val.toString().trim().trim().length() > 0) {
                    return "" + val;                          //if its NOT null and can't be formatted according to type, just return as a string
                } else {
                    try {
                        return formatter.format("");        //if we get an err because the value was null try to format again, but with an empty string
                    } catch (IllegalArgumentException e1) {
                        return "";                          //if that failed too then just return blanks (rather than 'null')
                    }
                }
            }
        }

        //if not, let's try and do it ourselves    
        if (val == null) {
            return "";
        }
        String tval = "" + val;
        FormElementParser type = el.getType();
        try {
            if (FormType.TIMESTAMP == type) {
                tval = DateUtil.getTimestampStr((Timestamp) val);
            } else if (FormType.DATE == type) {
                tval = DateUtil.getDateStr((Date) val);
            } else if (FormType.TIME == type) {
                tval = DateUtil.getShortTimeStr((Time) val);
            }
        } catch (ClassCastException e) {
        } catch (IllegalArgumentException e) {
        }
        return tval;
    }
    
    /**
     * Convenience method to have the ErrorManager flag a component for errors (if they exist).
     *
     * @param bcomp the component to be flagged
     * @see ErrorManager#apply(BComponent)
     * @since csc_110304_1
     */
    public static void flagErrors(BComponent bcomp) {
        ErrorManager eman = ErrorManager.getInstance();
        if (eman != null) {
            eman.apply(bcomp);
        }
    }



    /**
     * <p>This method allows you to assert that a String value falls within a given
     * min/max length range. If min or max is -1, that particular aspect will not
     * be evaluated.
     *
     * <p>Example usage: <br>
     *    String user = assertMinMax((String) map.get(USER), "User Name", 5, 30);
     *
     * <p>This would retrieve a username from a map, validate it to ensure that its
     * between 5 and 30 characters in length, and then return the adjusted value (if it
     * was null in the map, it will come back as "", which is convenient for further
     * custom evaluation)
     *
     * <p>Todo:
     * <ul>
     *   <li>The error message generated by this method is not localized yet
     *   </li>
     * </ul>
     *
     * @param field the field to be evaluated
     * @param fieldDescr a description of the field to be included in any error messages
     * @param min the minimum length of the String, or -1 if there is no min
     * @param max the maximum length of the String, or -1 if there is no max
     * @return the field value (non-null, adjusted and trimmed for further custom evaluation)
     * @throws a ValidationException if the field is not valid
     */
    public static String assertMinMax(String field, String fieldDescr, int min, int max) throws ValidationException {
        if (field == null) {
            field = "";
        } else {
            field = field.trim();
        }
        if (min != -1) {
            assertTrue(fieldDescr + " must be at least " + min + " chars in length -- Please re-enter!", field.length() >= min);
        }
        if (max != -1) {
            assertTrue(fieldDescr + " may not exceed " + max + " chars in length -- Please re-enter!", field.length() <= max);
        }
        return field;
    }

    /**
     * This function simply evaluates a given boolean expression and throws a ValidationException
     * using the specified error message if its not valid
     *
     * @param errmsg the error message to be used if the expression is invalid
     * @param expression the expression to be evaluated
     * @throws ValidationException if the expression is not true
     */
    public static void assertTrue(String errmsg, boolean expression) throws ValidationException {
        if (!expression) {
            throw new ValidationException(null, errmsg);
        }
    }


}
