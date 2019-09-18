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
 * Christian Cryder, Diez B. Roggisch
 *
 * $Id: FormElement.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.io.Serializable;
import java.text.*;
import java.util.Set;
import org.barracudamvc.core.forms.parsers.FormElementParser;

/**
 * <p>This interfaces defines the methods a class needs to implement
 * to act as a FormElement.
 *
 * <p>A FormElement defines how an element in a FormMap should
 * be mapped to a first class java object. There are several
 * key pieces of information required:
 *
 * <ul>
 *         <li>key - used to retrieve the target value from the incoming
 *            data source (either a ServletRequest or a StateMap)</li>
 *         <li>type - the type of data object the incoming value should be
 *            mapped to, as defined by the FormType class (String, Boolean,
 *            Integer, Date, Long, Short, Double, Float)</li>
 *         <li>default val - the default val to be used if the key value is
 *            null in the incoming data source (may be null)</li>
 *         <li>validator - any validators associated with this particular
 *            form elmenent (may be null)</li>
 *         <li>allow multiples - the servlet interface allows multiple params
 *            with the same key. This value indicates whether or not that is
 *            acceptable for a key. If so, then when multiple values are
 *            encountered they element will actually contain a List of values
 *            of the proper type</li>
 * </ul>
 *
 * <p>Once we have this information, we basically have enough data to map
 * a String value in the data source to a first class Java object in the
 * element.
 *
 * <p>Note that you can optionally specify a Format object, which will be used
 * by FormUtil.repopulate() and FormUtil.formatForOutput() to render the form
 * value in a manner that will be fit for human consumption (as well as subsequent
 * form reparsing).
 *
 * <p>Also note that all of the setters have been modified to return a reference
 * to the FormElement. This makes it possible to chain your calls on a single line, 
 * like this:<br><br>
 *
 *           new DefaultFormElement(...).setName("foo").setDefaultVal("blah")
 */
public interface FormElement extends Comparable, Serializable {

    public FormElement setKey(String key);

    public String getKey();

    public FormElement setName(String name);

    public String getName();

    public FormElement setType(FormElementParser type);

    public FormElementParser getType();

    public FormElement setDefaultVal(Object defaultVal);

    public Object getDefaultVal();

    public FormElement setAllowMultiples(boolean val);

    public boolean allowMultiples();

    public FormElement setValidator(FormValidator validator);

    public FormValidator getValidator();

    public FormElement setOrigVal(Object iorigVal);

    public Object getOrigVal();

    public FormElement setVal(Object ival);

    public <DesiredType extends Object> DesiredType getValue();
    
    public Object getVal();

    public Object getVal(Object dflt);

    public Object[] getVals();

    public Object[] getVals(Object[] dflt);

    public FormElement setParseException(ParseException pe);

    public ParseException getParseException();

    public FormElement setFormat(Format iformat);

    public Format getFormat();

    public FormElement setParentForm(FormMap iparent);

    public FormMap getParentForm();

    public boolean isValueSet();

    /**
     * Set a value in this element that is not correct.
     * 
     * @param value The value that was not correct or did not meet the
     * requirements of the field.
     * 
     * @return The form element that was applied to. 
     */
    public FormElement setErroneousValue(Object value);

    /**
     * Retrieve the values that were submitted to this form element that were
     * in error or did not conform to the requirements.
     * @return a set of the values that did not match the requirements.
     */
    public Set<Object> getErroneousValues();

    /**
     * Remove all invalid values for this element. Making all values that 
     * may have been invalid valid.
     * @return The form element that was applied to. 
     */
    public FormElement clearErroneousValues();

    public void setValueSet(boolean isPresent);
    
    public boolean hasValue();
}
