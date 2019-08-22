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
 * $Id: DefaultFormElement.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.text.DateFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.barracudamvc.core.forms.parsers.FormElementParser;
import org.barracudamvc.core.util.ArrayUtils;

/**
 * <p>A FormElement defines how an element in a FormMap should
 * be mapped to a first class java object. There are several
 * key pieces of information required:
 *
 * <ul>
 *         <li>key - used to retrieve the target value from the incoming
 *            data source (either a ServletRequest or a StateMap)</li>
 *         <li>name - a displayable name for this element (defaults to key)</li>
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
 * <p>Note that the element keeps track of the original value as well, so
 * that if for some reason you ever need to get to it that information is
 * available.
 *
 * <p>Also not that we provide convenience methods which automatically cast
 * for you (Note however: this type of thing is not type safe, meaning that
 * you can get a ClassCastException if you do a getXXXVal() when the
 * underlying object is not of type XXX.
 */
public class DefaultFormElement implements FormElement {

    //public constants
    protected static final Logger logger = Logger.getLogger(DefaultFormElement.class.getName());
    //non-public vars
    /**
     * @clientCardinality 1 
     */
    protected FormMap parent = null;
    protected String key = null;
    protected String name = null;
    protected FormElementParser type = null;
    protected Object defaultVal = null;
    protected ParseException pe = null;
    /**
     * @clientCardinality 1 
     */
    protected FormValidator validator = null;
    protected boolean allowMultiples = false;
    protected Object origVal = null;
    protected Object val = null;
    protected Format format = null;
    protected boolean isValueSet = false;
    protected HashSet<Object> erroneousValues = new HashSet<Object>();

    /**
     * Public noargs constructor. Form key defaults to null.
     */
    public DefaultFormElement() {
        this((String) null);
    }

    /**
     * Public constructor. Form type defaults to FormType.STRING
     *
     * @param ikey the key name in the data source
     */
    public DefaultFormElement(String ikey) {
        this(ikey, FormType.STRING);
    }

    /**
     * Public constructor. Default value defaults to Null.
     *
     * @param ikey the key name in the data source
     * @param itype the FormType for the element
     */
    public DefaultFormElement(String ikey, FormElementParser itype) {
        this(ikey, itype, null);
    }

    /**
     * Public constructor. Validator defaults to null.
     *
     * @param ikey the key name in the data source
     * @param itype the FormType for the element
     * @param idefaultVal the default value to be used if the key is
     *        not found in the data source or the value for the key is null
     */
    public DefaultFormElement(String ikey, FormElementParser itype, Object idefaultVal) {
        this(ikey, itype, idefaultVal, null);
    }

    /**
     * Public constructor. AllowMultiples defaults to false.
     *
     * @param ikey the key name in the data source
     * @param itype the FormType for the element
     * @param idefaultVal the default value to be used if the key is
     *        not found in the data source or the value for the key is null
     * @param ivalidator the FormValidator associated with this element
     */
    public DefaultFormElement(String ikey, FormElementParser itype, Object idefaultVal, FormValidator ivalidator) {
        this(ikey, itype, idefaultVal, ivalidator, false);
    }

    /**
     * Public constructor, Name defaults to Key
     *
     * @param ikey the key name in the data source
     * @param itype the FormType for the element
     * @param idefaultVal the default value to be used if the key is
     *        not found in the data source or the value for the key is null
     * @param ivalidator the FormValidator associated with this element
     * @param iallowMultiples true if there may be multiple values in the datasource
     *        for this particular key name
     */
    public DefaultFormElement(String ikey, FormElementParser itype, Object idefaultVal, FormValidator ivalidator, boolean iallowMultiples) {
        this(ikey, ikey, itype, idefaultVal, ivalidator, iallowMultiples);
    }

    /**
     * Public constructor
     *
     * @param ikey the key name in the data source
     * @param iname the name for the element
     * @param itype the FormType for the element
     * @param idefaultVal the default value to be used if the key is
     *        not found in the data source or the value for the key is null
     * @param ivalidator the FormValidator associated with this element
     * @param iallowMultiples true if there may be multiple values in the datasource
     *        for this particular key name
     */
    public DefaultFormElement(String ikey, String iname, FormElementParser itype, Object idefaultVal, FormValidator ivalidator, boolean iallowMultiples) {
        setKey(ikey);
        setName(iname);
        setType(itype);
        setDefaultVal(idefaultVal);
        setValidator(ivalidator);
        setAllowMultiples(iallowMultiples);
    }

    /**
     * Public constructor
     *
     * @param feSource the form element on which to base this form element
     */
    public DefaultFormElement(FormElement feSource) {
        this(feSource.getKey(), feSource.getName(), feSource.getType(), feSource.getDefaultVal(), feSource.getValidator(), feSource.allowMultiples());
    }

    //--------------- FormElement --------------------------------
    /**
     * Set the key value for this form element
     *
     * @param ikey the key value for this form element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setKey(String ikey) {
        key = ikey;
        return this;
    }

    /**
     * Get the key value for this form element
     *
     * @return the key for this form element
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Set the name of this form element
     *
     * @param iname the name of this form element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setName(String iname) {
        if (!key.equals(iname)) {
            name = iname;   // we only actually set it if its different from the key
        }
        return this;
    }

    /**
     * Get the name of this form element
     *
     * @return the name of this form element
     */
    @Override
    public String getName() {
        // if the name is null, use the key as the name
        return (name != null ? name : key);
    }

    /**
     * Set the FormType for this form element
     *
     * @param itype the FormType for this form element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setType(FormElementParser itype) {
        if (itype == null) {
            itype = FormType.STRING;
        }
        type = itype;
        return this;
    }

    /**
     * Get the FormType for this for element
     *
     * @return the FormType for this form element
     */
    @Override
    public FormElementParser getType() {
        return type;
    }

    /**
     * Set the default value for this form element
     *
     * @param idefaultVal the FormType for this form element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setDefaultVal(Object idefaultVal) {
        /**
         * TODO if the type of the value does not match the 
         * value type of this element this method should generate an
         * exception.
         * e.g. if the user passed in new String[]{1} to an element with the FormType of IntegerFormType
         * a parse cast exception should be thrown.
         * Types of Integer[] for FIntegerFormType are already handled
         */
        defaultVal = idefaultVal;
        return this;
    }

    /**
     * Get the default value for this form element
     *
     * @return the default value for this form element (may be null)
     */
    @Override
    public Object getDefaultVal() {
        return defaultVal;
    }

    /**
     * Set whether or not this element allows multiple values
     *
     * @param val true if the element allows multiples
     * @return a reference to the current form element
     */
    @Override
    public FormElement setAllowMultiples(boolean val) {
        allowMultiples = val;
        return this;
    }

    /**
     * Does this element allow multiple values
     *
     * @return true if this element allows multiple values
     */
    @Override
    public boolean allowMultiples() {
        return allowMultiples;
    }

    /**
     * Set the FormValidator for this form element
     *
     * @param ivalidator the FormValidator for this form element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setValidator(FormValidator ivalidator) {
        validator = ivalidator;
        return this;
    }

    /**
     * Get the default FormValidator for this form element
     *
     * @return the validator for this form element (may be null)
     */
    @Override
    public FormValidator getValidator() {
        return validator;
    }

    /**
     * Set the original value for this element
     *
     * @param iorigVal the original value
     * @return a reference to the current form element
     */
    @Override
    public FormElement setOrigVal(Object iorigVal) {
        origVal = iorigVal;
        return this;
    }

    /**
     * Get the original value for this element
     *
     * @return the original value for this form element (may be null)
     */
    @Override
    public Object getOrigVal() {
        return origVal;
    }

    /**
     * Set the value for this element
     *
     * @param ival the value for this element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setVal(Object ival) {
        /**
         * TODO if the type of the value does not match the 
         * value type of this element this method should generate an
         * exception.
         * e.g. if the user passed in new String[]{1} to an element with the FormType of IntegerFormType
         * a parse cast exception should be thrown.
         * Types of Integer[] for FIntegerFormType are already handled
         */
        val = ival;
        return this;
    }

    /**
     * Get the value for this element. If the underlying object
     * is actually an array (ie. allowMultiples = true), then
     * you should really be calling getVals() to get the whole
     * object array; if you call this method, you will just get 
     * the first element of the array.
     *
     * @return the value for this form element (may be null)
     */
    @Override
    public Object getVal() {
        return getVal(null);
    }

    @Override
    public Object getVal(Object dflt) {
        Object tval = val;
        if (tval == null) {
            tval = dflt;
        }
        if (tval == null) {
            tval = defaultVal;
        }
        if (tval != null && tval.getClass().isArray()) {
            if (tval instanceof Object[]) {
                tval = ((Object[]) tval)[0];
            } else {
                tval = ArrayUtils.toObjectArray(tval)[0];
            }

        } else if (tval instanceof Collection) {
            if (((Collection) tval).size() > 0) {
                tval = ((Collection) tval).iterator().next();
            }
        }

        return tval;
    }

    /**
     * Get all the values for this element. There will always be an array returned.
     * if the value for this FormElement is null, an empty array is returned. This
     * method only really makes sense if allowMultiples = true
     *
     * @return the array of values for this form element
     */
    @Override
    public Object[] getVals() {
        return getVals(null);
    }

    @Override
    public Object[] getVals(Object[] dflt) {
        if (val == null && dflt != null) {
            return dflt;
        }
        Object valCopy = val;
        if (valCopy == null) {
            valCopy = defaultVal;
        }
        if (valCopy == null) {
            if (type != null) {
                return type.getTypeArray(0);
            } else {
                return new Object[]{};
            }
        }
        if (valCopy instanceof Collection) {
            valCopy = ((Collection) valCopy).toArray();
        } else if (valCopy.getClass().isArray()) {
            // do nothing as we are already an array
        } else {
            valCopy = new Object[]{valCopy};
        }
        Object[] vals = (Object[]) valCopy;
        Object[] typedVals = null;
        if (type != null) {
            typedVals = type.getTypeArray(vals.length);
        }
        if (typedVals == null) {
            typedVals = new Object[vals.length];
        }
        System.arraycopy(vals, 0, typedVals, 0, vals.length);
        return typedVals;
    }

    /**
     * Set the parse exception associated with this element
     *
     * @return any parse exceptions associated with the element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setParseException(ParseException ipe) {
        pe = ipe;
        return this;
    }

    /**
     * Get any parse exceptions associated with the element (ie. that
     * might have occurred when the element was mapped)
     *
     * @return any parse exceptions associated with the element
     */
    @Override
    public ParseException getParseException() {
        return pe;
    }

    /**
     * Set the Format object for this element (null indicates default formatting)
     *
     * @param iformat the Format for this form element
     * @return a reference to the current form element
     */
    @Override
    public FormElement setFormat(Format iformat) {
        format = iformat;
        return this;
    }

    /**
     * Set the Format object for this element (null indicates default formatting)
     *
     * @return the Format for this form element
     */
    @Override
    public Format getFormat() {
        if (format == null) {
            if (FormType.TIMESTAMP == type) {
                format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            } else if (FormType.DATE == type) {
                format = DateFormat.getDateInstance(DateFormat.SHORT);
            } else if (FormType.TIME == type) {
                format = DateFormat.getTimeInstance(DateFormat.SHORT);
            }
        }
        return format;
    }

    /**
     * Set the parent form map
     *
     * @param iparent the parent map object
     * @return a reference to the current form element
     */
    @Override
    public FormElement setParentForm(FormMap iparent) {
        parent = iparent;
        return this;
    }

    /**
     * Get the parent form map
     */
    @Override
    public FormMap getParentForm() {
        return parent;
    }

    /**
     * Compares with another object to determine ordering
     */
    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        FormElement fe = (FormElement) o;
        return this.getKey().compareTo(fe.getKey());
    }

    /**
     * Get a string representation of this element
     *
     * @return the String describing this element
     */
    @Override
    public String toString() {
        return "Key:" + key + " Name:" + getName()
                + " Val:" + val + " Type:" + (type != null ? type.getFormClass().getName() : "null")
                + " Orig:" + origVal + " Dflt:" + defaultVal
                + " Mult:" + allowMultiples
                + " Validator:" + (validator != null ? "@" + Integer.toHexString(validator.hashCode()) : "null");
    }

    @Override
    public FormElement setErroneousValue(Object value) {
        erroneousValues.add(value);
        return this;
    }

    @Override
    public Set<Object> getErroneousValues() {
        return new HashSet<Object>(erroneousValues);
    }

    @Override
    public FormElement clearErroneousValues() {
        erroneousValues.clear();
        return this;
    }

    @Override
    public boolean isValueSet() {
        return isValueSet;
    }

    public void setValueSet(boolean isPresent) {
        this.isValueSet = isPresent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType> DesiredType getValue() {
        if (val instanceof Collection) {
            return (DesiredType) new ArrayList((Collection) val);
        } else if (val == null) {
            return null;
        } else if (val.getClass().isArray() && type == null) {
            return null;
        } else if (val.getClass().isArray()) {
            Object[] value = ArrayUtils.toObjectArray(val);
            Object[] dest = type.getTypeArray(value.length);
            System.arraycopy(value, 0, dest, 0, value.length);
            return (DesiredType) dest;
        }

        return (DesiredType) val;
    }

    @Override
    public boolean hasValue() {
        return val != null;
    }
}
