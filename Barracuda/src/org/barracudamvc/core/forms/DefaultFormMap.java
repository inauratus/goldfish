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
 * $Id: DefaultFormMap.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.io.Serializable;
import java.util.*;
import javax.servlet.ServletRequest;
import org.apache.log4j.Logger;
import org.barracudamvc.core.helper.servlet.HttpRequest;
import org.barracudamvc.core.helper.servlet.MultipartRequest;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.plankton.data.StateMap;

/**
 * <p>This class provides the default implementation of a FormMap.
 *
 * <p>A FormMap is used to provide a virtual representation of a
 * form. It can contain any number of unique FormElements, and
 * it can also be associated with FormValidators. The primary
 * function of a form map is to:
 *
 * <ul>
 *   <li>define the map (with its elements and validators)</li>
 *   <li>actually populate the map (from either a ServletRequest or a StateMap)</li>
 *   <li>validate the map (by invoking all the validators associated with the 
 *       form and all its elements)</li>
 *   <li>provide convenience methods to access the underlying values of the form 
 *       elements contained in this map</li>
 * </ul>
 *
 * The FormMap uses a pluggable FormMapper to actually control how elements 
 * are mapped.
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @author  Diez B. Roggisch <diez.roggisch@artnology.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class DefaultFormMap implements FormMap, AcceptsUnparsableElements {

    protected static final Logger localLogger = Logger.getLogger(DefaultFormMap.class.getName());
    protected Map<String, FormElement> elements = new TreeMap<>();
    protected Set<FormValidator> validators = new HashSet<>(5);
    protected StateMap statemap = new DefaultStateMap();
    protected static final Locale defaultLoc = Locale.getDefault();
    protected Locale zloc = null;
    protected Set<FormElement> withParseErrors = new HashSet<>();

    protected FormMapper formMapper = null;

    public DefaultFormMap() {
    }

    /**
     * Prefix constructor (uses PrefixFormMapper)
     *
     * @param prefix the prefix to be used by the PrefixFormMapper
     */
    public DefaultFormMap(String iprefix) {
        this.setFormMapper(new PrefixFormMapper(iprefix));
    }

    //--------------- FormMap ------------------------------------
    /**
     * This defines an element to be mapped by this form, using
     * the key from the FormElement. You would invoke this method
     * for each element in the form.
     *
     * @param element a FormElement to be mapped by this form
     * @return a reference to the newly defined FormElement
     */
    @Override
    public FormElement defineElement(FormElement element) {
        return this.defineElement(element.getKey(), element);
    }

    /**
     * This defines an element to be mapped by this form, using 
     * a manually specified key. Generally, you won't need to use 
     * this method unless you are writing some kind of framwork that
     * maps multiple elements into a for (ie. with different variations
     * on a name), all via the same fundamental key.
     *
     * @param key a manually specified key
     * @param element a FormElement to be mapped by this form
     * @return a reference to the newly defined FormElement
     */
    @Override
    public FormElement defineElement(String key, FormElement element) {
        if (key == null) {
            key = element.getKey();
        }
        if (key == null) {
            return element; //eliminate the obvious (TreeMap doesn't like calls to get(null))
        }
        elements.put(key, element);
        element.setParentForm(this);
        return element;
    }

    /**
     * Define a validator for a form element after a form element has been
     * defined. This may happen as many times as needed. This will only be done
     * if the onTrue is <tt>true</tt>
     * 
     * @param onTrue Add the validator if <tt>true</tt>
     * @param key   The key representing the form element (the element name)
     * @param validator The validator that will be added to the form element
     */
    public void defineElementValidatorIfTrue(boolean onTrue, String key, FormValidator validator) {
        if (onTrue) {
            defineElementValidator(key, validator);
        }
    }

    /**
     * Define a validator for a form element after a form element has been
     * defined. This may happen as many times as needed.
     * 
     * @param key   The key representing the form element (the element name)
     * @param validator The validator that will be added to the form element
     */
    public void defineElementValidator(String key, FormValidator validator) {
        if (key == null) {
            throw new IllegalArgumentException("The form element key may not be null");
        }
        FormElement element = elements.get(key);
        if (element == null) {
            throw new IllegalStateException("Could not bind a Key to form"
                    + " element definition. Make sure to define a form element"
                    + " before adding a validator to it.");
        }

        FormValidator existingValidator = element.getValidator();
        if (existingValidator == null) {
            element.setValidator(validator);
        } else {
            if (existingValidator instanceof And) {
                ((And) existingValidator).addValidator(validator);
            } else {
                element.setValidator(new And(existingValidator, validator));
            }
        }
    }

    /**
     * This defines a validator for the entire form. This validator
     * will be invoked prior to validating specific form element
     * validators. Calling this method multiple times will result
     * in multiple form validators being added to the form (they
     * will be invoked in the order they were added)
     *
     * @param validator a form validator to be applied to the entire form
     * @return a reference to the newly defined FormValidator
     */
    @Override
    public FormValidator defineValidator(FormValidator validator) {
        if (!validators.contains(validator)) {
            validators.add(validator);
        }
        return validator;
    }

    /**
     * This is where we actually take an incoming form (in
     * the form of a ServletRequest) and map it using the
     * definitions supplied by all the FormElements. If there
     * are multiple parameters for a given key, the values
     * will be mapped into List structures
     *
     * @param req the ServletRequest to map paramters from based on
     *        all defined FormElements
     * @return a reference to the FormMap (we do this so you can inline
     *        map/validate requests if you desire: form.map(req).validate())
     * @throws MappingException if for some reason the value cannot
     *        be mapped successfully
     */
    @Override
    public FormMap map(ServletRequest req) {
        return getFormMapper().mapForm(this, mapRequest(req));
    }

    @Override
    public FormMap map(StateMap map) {
        return map(map.getStateStore());
    }

    @Override
    @SuppressWarnings("unchecked")
    public FormMap map(Map map) {
        return getFormMapper().mapForm(this, map);
    }

    private Map mapRequest(ServletRequest request) {
        if (request instanceof HttpRequest) {
            return (Map) ((HttpRequest) request).getContentValues();
        } else {
            Enumeration<String> e = request.getParameterNames();
            Map<String, Object> map = new TreeMap<>();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                map.put(key, getValue(request, key));
            }

            if (request instanceof MultipartRequest) {
                e = ((MultipartRequest) request).getFileParameterNames();
                while (e.hasMoreElements()) {
                    String key = e.nextElement();
                    map.put(key, ((MultipartRequest) request).getFileItems(key));
                }
            }
            return map;
        }
    }

    private <DesiredType> DesiredType getValue(ServletRequest request, Object key) {
        if (key == null)
            key = "~Null~";
        String[] vals = request.getParameterValues(key.toString());
        if (vals == null)
            return null;
        else if (vals.length == 1)
            return (DesiredType) (vals[0].equals("~Null~") ? null : vals[0]);
        else {
            List<String> list = new ArrayList<>();
            for (int i = 0, max = vals.length; i < max; i++) {
                list.add(vals[i]);
            }
            return (DesiredType) list;
        }
    }

    /**
     * Map an individual element by specifiying the key and origVal. Normally, 
     * you'll just map the whole form all at once, but if for some reason you 
     * desire to manually map an element at a time, this method gives you the 
     * ability to do that.
     *
     * @param key the form map key
     * @param origVal the orig val to be mapped
     * @return a reference to the FormElement which got mapped (null if it 
     * mapper doesn't handle this key)
     * @throws MappingException if for some reason the value cannot be 
     * mapped successfully
     */
    @Override
    public FormElement mapElement(String key, Object origVal) {
        if (key == null) {
            return null; //eliminate the obvious (TreeMap doesn't like calls to get(null))
        }
        return getFormMapper().mapElement(this, key, origVal);
    }

    /**
     * Validate the entire form (both form level and elements).
     * We start by invoking form validators which apply to individual
     * form elements, then we invoke any which apply to the entire form
     *
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @return a reference to the FormMap (we do this so you can inline
     *        map/validate requests if you desire: form.map(req).validate())
     * @throws ValidationException if the form (or any element within it)
     *        is invalid
     */
    @Override
    public FormMap validate(boolean deferExceptions) throws ValidationException {
        if (localLogger.isInfoEnabled()) {
            localLogger.info("Validating FormMap (form & elements)");
        }
        DeferredValidationException dve = new DeferredValidationException();

        try {
            //validate each individual element
            try {
                validateElements(deferExceptions);
            } catch (DeferredValidationException e) {
                dve.addSubException(e);
            }

            //validate the entire form
            try {
                validateForm(deferExceptions);
            } catch (DeferredValidationException e) {
                dve.addSubException(e);
            }
        } catch (ValidationException e) {
            ErrorManager.handleError(e);
            throw e;
        }

        //now, if we have generated a ValidationExceptions,
        //rethrow it
        if (dve.hasSubExceptions()) {
            ErrorManager.handleError(dve);
            throw dve;
        }
        return this;
    }

    /**
     * Validate just the elements (not the form)
     *
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @return a reference to the FormMap (we do this so you can inline
     *        map/validate requests if you desire: form.map(req).validate())
     * @throws ValidationException if the form (or any element within it)
     *        is invalid
     */
    @Override
    public FormMap validateElements(boolean deferExceptions) throws ValidationException {
        DeferredValidationException dve = new DeferredValidationException();

        // see if we are going to require the elements to have a certain infix 
        // - basically, in the case of a UIDFormMapper, we only want to 
        // consider elemetns that were actually mapped (and thus have the :: 
        // in their key)
        String infix = null;
        if (formMapper != null && formMapper instanceof UIDFormMapper) {
            infix = UIDFormMapper.UID_TOKEN;
        }

        //validate each individual element
        Iterator it = elements.values().iterator();
        for (FormElement element : elements.values()) {
            try {
                element = (FormElement) it.next();
                if (infix != null && element.getKey().indexOf(infix) < 0) {
                    continue;
                }
                FormValidator fv = element.getValidator();
                if (fv != null) {
                    fv.validate(element, this, deferExceptions);
                }

            } catch (DeferredValidationException e) {
                dve.addSubException(e);
            } catch (ValidationException validationException) {
                dve.addSubException(validationException);
                if (!deferExceptions) {
                    throw validationException;
                }
            }
        }

        //now, if we have generated a ValidationExceptions,
        //rethrow it
        if (dve.hasSubExceptions()) {
            throw dve;
        }
        return this;
    }

    /**
     * Validate just the form (not the individual elements)
     *
     * @param deferExceptions do we want to deferValidation exceptions
     *        and attempt to validate all elements so that we can process
     *        all the exceptions at once
     * @return a reference to the FormMap (we do this so you can inline
     *        map/validate requests if you desire: form.map(req).validate())
     * @throws ValidationException if the form (or any element within it)
     *        is invalid
     */
    @Override
    public FormMap validateForm(boolean deferExceptions) throws ValidationException {
        DeferredValidationException dfve = new DeferredValidationException();

        for (FormValidator fv : validators) {
            try {
                fv.validate(null, this, deferExceptions);
            } catch (DeferredValidationException e) {
                dfve.addSubException(e);
            } catch (ValidationException ve) {
                dfve.addSubException(ve);
                if (!deferExceptions) {
                    throw ve;
                }
            }
        }

        if (dfve.hasSubExceptions()) {
            throw dfve;
        }
        return this;
    }

    /**
     * Return true if an element exists and its value is not null
     *
     * @param key the key which uniquely identifies this FormElement
     * @return true if an element exists (not null)
     */
    @Override
    public boolean exists(String key) {
        if (key == null) {
            //eliminate the obvious (TreeMap doesn't like calls to get(null))
            return false;
        }
        FormElement fel = (FormElement) elements.get(key);
        if (fel == null) {
            return false;
        }
        return (fel.getVal() != null);
    }

    /**
     * Get an element by key
     *
     * @param key the key which uniquely identifies this FormElement
     * @return the FormElement for this key (may be null)
     */
    @Override
    public FormElement getElement(String key) {
        if (key == null) {
            //eliminate the obvious (TreeMap doesn't like calls to get(null))
            return null;
        }
        return (FormElement) elements.get(key);
    }

    /**
     * return a List with containing all the defined elements in this FormMap
     *
     * @return a copy of the defined elements in this FormMap
     */
    @Override
    public Map<String, FormElement> getElements() {
        return new TreeMap<>(elements);
    }

    /**
     * Manually set the value of an element. If an element does not
     * exist for this key one will be created.
     *
     * @param key the key
     * @param val the value for the key
     */
    @Override
    public void setVal(String key, Object val) {
        //eliminate the obvious (TreeMap doesn't like calls to get(null))
        if (key == null) {
            return;
        }
        FormElement el = (FormElement) elements.get(key);
        if (el == null) {
            el = new DefaultFormElement(key);
            this.defineElement(el);
        }
        el.setVal(val);
    }

    /**
     * Get the value for a given key. This is basically a convenience
     * method. You could manually grab the FormElement using getElement
     * and then retrieve the value that way as well. Note that if the
     * particular FormElement supports multiple values, then this call
     * will only return the first value in the array; to get all the values,
     * use the getVals() function.
     *
     * @param key the key which uniquely identifies this FormElement
     * @return the value for this key (may be null)
     */
    @Override
    public Object getVal(String key) {
        return getVal(key, null);
    }

    @Override
    public Object getVal(String key, Object dflt) {
        if (key == null) {
            //eliminate the obvious (TreeMap doesn't like calls to get(null))
            return null;
        }
        FormElement el = (FormElement) elements.get(key);
        if (el == null) {
            return null;
        }
        Object val = el.getVal();
        if (val == null) {
            val = dflt;
        }
        if (val == null) {
            val = el.getDefaultVal();
        }
        return val;
    }

    /**
     * Get an array of values for a given key. This is basically a convenience
     * method. You could manually grab the FormElement using getElement
     * and then retrieve the value that way as well. You should only use this
     * method if the particular FormElement has allowMultiples = true
     *
     * @param key the key which uniquely identifies this FormElement
     * @return the value for this key (may be null)
     */
    @Override
    public Object[] getVals(String key) {
        return getVals(key, null);
    }

    @Override
    public Object[] getVals(String key, Object[] dflt) {
        if (key == null) {
            //eliminate the obvious (TreeMap doesn't like calls to get(null))
            return null;
        }
        FormElement el = (FormElement) elements.get(key);
        if (el == null) {
            return null;
        }
        Object[] vals = el.getVals();
        return (vals != null ? vals : dflt);
    }

    @Override
    public void setLocale(Locale iloc) {
        zloc = iloc;
    }

    @Override
    public Locale getLocale() {
        return (zloc != null ? zloc : defaultLoc);
    }

    @Override
    public final void setFormMapper(FormMapper imapper) {
        formMapper = imapper;
    }

    @Override
    public FormMapper getFormMapper() {
        if (formMapper == null) {
            formMapper = new DefaultFormMapper();
        }
        return formMapper;
    }

    @Override
    public void putState(Object key, Object val) {
        statemap.putState(key, val);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <DesiredType> DesiredType getState(Object key) {
        return (DesiredType) statemap.getState(key);
    }

    @Override
    public Object removeState(Object key) {
        return statemap.removeState(key);
    }

    @Override
    public Set getStateKeys() {
        return statemap.getStateKeys();
    }

    @Override
    public Map getStateStore() {
        return statemap.getStateStore();
    }

    @Override
    public void clearState() {
        statemap.clearState();
    }

    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return getState(key);
    }

    public boolean hasUnparsableValues() {
        return !withParseErrors.isEmpty();
    }

    public void addUnparsable(FormElement e) {
        withParseErrors.add(e);
    }

    public Set<FormElement> getUnparsableElements() {
        return new HashSet<>(withParseErrors);
    }

    public Map<String, FormElement> getFormElements() {
        return new HashMap<>(elements);
    }

    public Set<FormValidator> getValidators() {
        return new HashSet<>(validators);
    }
}
