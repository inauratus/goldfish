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
 * $Id: DefaultFormMapper.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.barracudamvc.core.forms.exception.InvalidParserException;
import org.barracudamvc.core.forms.parsers.DefaultFileField;
import org.barracudamvc.core.forms.parsers.FileElementParser;
import org.barracudamvc.core.forms.parsers.FormElementParser;
import org.barracudamvc.core.forms.parsers.StringElementParser;

/**
 * <p>A FormMapFilter is used to provide a control what elements
 * get mapped in a form
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @since   2.0
 */
public class DefaultFormMapper implements FormMapper {

    //public constants
    protected static final Logger localLogger = Logger.getLogger(DefaultFormMapper.class.getName());
    //working vars
    protected Map<String, FormElement> mappedElements = new TreeMap<String, FormElement>();
    protected boolean iterateOverParams = false;

    //--------------- FormMapper ---------------------------------
    /**
     * Map a whole form
     */
    /**
     * Map an individual element
     */
    @Override
    public FormElement mapElement(FormMap fm, String key, Object origVal) {

        //default behavior is that an individual element will only be mapped if its already defined
        FormElement el = fm.getElement(key);
        if (el == null)
            return null;

        //we delegate to the mapForm() function here to make sure this method of mapping 
        //also makes use of the filter stuff, as well as handling Object[] vals, etc
        boolean orig = iterateOverParams;
        iterateOverParams = true;
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put(key, origVal);
        mapForm(fm, map);
        iterateOverParams = orig;
        return el;
    }

    @Override
    public FormMap mapForm(FormMap fm, Map<String, Object> paramMap) {
        this.preMap();
        if (iterateOverParams) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                storeValueFieldElement(fm, key, value, true);
            }
        } else {
            for (Map.Entry<String, FormElement> entry : fm.getElements().entrySet()) {
                String key = mangleKey(entry.getKey());
                Object value = paramMap.get(key);
                storeValueFieldElement(fm, key, value, paramMap.containsKey(key));
            }
        }
        this.postMap();
        return fm;
    }

    protected void storeValueFieldElement(FormMap fm, String key, Object o, boolean isPresent) {
        //get the filter associated with the key
        FormElement el = getElementForMapping(fm, key);
        if (el == null)
            return;
        populate(el, o, fm, isPresent);
        //place the element back into the form map (this is important,
        //since the filter may return a different element in certain
        //situations)
        fm.defineElement(el);
        mappedElements.put(el.getKey(), el);
    }

    /**
     * Actually map a value to a form element. Returns the mapped value.
     */
    protected Object parseValue(FormMap map, FormElement element, Object origVal) {
        Locale locale = map.getLocale();
        FormElementParser type = element.getType();
        element.setParseException(null);

        try {
            if (type instanceof FormType) {
                return ((FormType) type).parse(origVal, locale);
            }
            if (type instanceof StringElementParser) {
                return ((StringElementParser) type).parse((String) origVal, locale);
            }
            return null;
        } catch (ParseException e) {
            if (map instanceof AcceptsUnparsableElements) {
                ((AcceptsUnparsableElements) map).addUnparsable(element);
            }
            element.setParseException(e);
            return null;
        }
    }

    /**
     * Returns a map containing all elements mapped by this mapper (will return an empty
     * map if the form has not been mapped yet)
     */
    @Override
    public Map getElements() {
        return mappedElements;
    }

    /**
     * This method defines what the FormMap identifies as null-value. If this
     * doesn't suit your needs, you can simply overload it.
     *
     * @param val The value from request or statemap
     * @return if the value is considered to be null
     */
    protected boolean isNull(Object val) {
        return ((val == null) || (val.toString().trim().length() < 1));
    }

    protected void populate(FormElement el, Object o, FormMap map, boolean isPresent) {
        el.setValueSet(isPresent);

        if (o instanceof List) {
            o = ((List) o).toArray();
        }

        if (o instanceof Object[]) {
            populateArrayItem(map, el, (Object[]) o);
        } else {
            if (isPresent) {
                el.setVal(parseItem(map, el, o));
            } else {
                el.setVal(null);
            }
            el.setOrigVal(o);
        }

        if (!el.hasValue())  {
            el.setVal(el.getDefaultVal());
        }
    }

    void populateArrayItem(FormMap map, FormElement el, Object[] values) {
        if (values.length == 1) {
            el.setOrigVal(values[0]);
            el.setVal(parseItem(map, el, values[0]));
        } else {
            ArrayList<Object> orginal = new ArrayList<>(values.length);
            ArrayList<Object> parsed = new ArrayList<>(values.length);
            for (Object value : values) {
                orginal.add(value);
                parsed.add(parseItem(map, el, value));
            }
            el.setVal(parsed);
            el.setOrigVal(orginal);
        }
    }

    protected Object parseItem(FormMap map, FormElement el, Object origVal) {
        if (origVal instanceof FileItem) {
            return parseFile(map, el, (FileItem) origVal);
        } else {
            return parseValue(map, el, origVal);
        }
    }

    public Object parseFile(FormMap map, FormElement element, FileItem origVal) {
        Locale locale = map.getLocale();
        FormElementParser type = element.getType();
        element.setParseException(null);

        try {
            if (type instanceof FileElementParser) {
                return ((FileElementParser) type).parse(new DefaultFileField(origVal), locale);
            } else {
                throw new InvalidParserException();
            }
        } catch (ParseException e) {
            if (map instanceof AcceptsUnparsableElements) {
                ((AcceptsUnparsableElements) map).addUnparsable(element);
            }
            element.setParseException(e);
            return null;
        }
    }

    @Override
    public String mangleKey(String key) {
        return key;
    }

    //------------------------------------------------------------
    //protected methods for custom mappers to override
    //------------------------------------------------------------
    /**
     * Invoked before mapping begins
     */
    protected void preMap() {
        //nop
    }

    /**
     * This method is used by the mapping process to look up FormElements. It
     * basically provides a way for the filter to determine whether or not
     * mapping should occur. You can override this method of control what gets
     * mapped and what doesn't. Based on the target key, the mapper either passes 
     * back the appropriate FormElement to map the key to, or it returns null to
     * indicate the element should not be mapped.
     */
    protected FormElement getElementForMapping(FormMap fm, String paramKey) {
        return fm.getElement(paramKey);
    }

    /**
     * Invoked after mapping is completed
     */
    protected void postMap() {
        //nop
    }

}
