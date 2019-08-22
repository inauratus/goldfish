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
 * $Id: FormMap.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.*;

import javax.servlet.ServletRequest;

import org.barracudamvc.plankton.data.StateMap;

/**
 * <p>A FormMap is used to provide a virtual representation of a
 * form. It can contain any number of unique FormElements, and
 * it can also be associated with FormValidators. The primary
 * function of a form map is to:
 *
 * <ul>
 *         <li>define the map (with its elements and validators)</li>
 *         <li>actually populate the map (from either a ServletRequest
 *            or a StateMap)</li>
 *         <li>validate the map (by invoking all the validators associated
 *            with the form and all its elements)</li>
 *        <li>provide convenience methods to access the underlying values
 *            of the form elements contained in this map</li>
 * </ul>
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @author  Diez B. Roggisch <diez.roggisch@artnology.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0
 */
public interface FormMap extends StateMap {

    public FormElement defineElement(FormElement element);

    public FormElement defineElement(String key, FormElement element);

    public FormValidator defineValidator(FormValidator validator);

    public FormMap map(ServletRequest req);

    public FormMap map(StateMap map);

    public FormMap map(Map map);

    public FormElement mapElement(String key, Object origVal);

    public FormMap validate(boolean deferExceptions) throws ValidationException;

    public FormMap validateElements(boolean deferExceptions) throws ValidationException;

    public FormMap validateForm(boolean deferExceptions) throws ValidationException;

    public boolean exists(String key);

    public FormElement getElement(String key);

    public Map<String, FormElement> getElements();

    public void setVal(String key, Object val);

    public Object getVal(String key);

    public Object getVal(String key, Object dflt);

    public Object[] getVals(String key);

    public Object[] getVals(String key, Object[] dflt);

    public void setFormMapper(FormMapper mapper);

    public FormMapper getFormMapper();

    public void setLocale(Locale loc);

    public Locale getLocale();

    /**
     * Returns true if a one of more of the elements had values that were not parsable
     * @return <tt>true</tt> if one of more of the elements had values that were not parsable
     */
    public boolean hasUnparsableValues();
}
