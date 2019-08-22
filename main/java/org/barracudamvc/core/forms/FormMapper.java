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
 * $Id: FormMapper.java 270 2014-07-15 15:36:03Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>A FormMapFilter is used to provide a control what elements
 * get mapped in a form
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @since   2.0
 */
public interface FormMapper extends Serializable {

    /**
     * Give the mapper a chance to change the key on the fly
     * This allows the mapper to not only filter what if mapped, but also influence how it is mapped (add or remove a prefix for example)
     */
    public String mangleKey(String key);

    public FormMap mapForm(FormMap fm, Map<String, Object> paramMap);

    public FormElement mapElement(FormMap fm, String key, Object origVal);

    public Map getElements();

}
