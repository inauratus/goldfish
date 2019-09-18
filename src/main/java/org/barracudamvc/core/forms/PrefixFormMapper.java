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
 * $Id: PrefixFormMapper.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.forms;

import java.util.*;

/**
 * <p>A PrefixFormMapFilter allows you to specify a prefix (ie. "addr1_") and
 * only those keys which start with that prefix will actually get mapped. When 
 * they are mapped, they will be mapped without the prefix. 
 *
 * @author  Christian Cryder [christianc@granitepeaks.com]
 * @since   2.0
 */
public class PrefixFormMapper extends DefaultFormMapper {

    protected String prefix = "";

    public PrefixFormMapper(String iprefix) {
        prefix = iprefix;
        iterateOverParams = true;   //force mapping to be driven by param keys, not element keys
    }

    /**
     * Map an individual element
     */
    public FormElement mapElement(FormMap fm, String key, Object origVal) {
        if (localLogger.isInfoEnabled()) localLogger.info("FormMapper "+this+".mapElement() - start"); 

        //if someone tries to map an element based on the shortened key, add on the prefix (because the
        //getElementForMapping() method is going to expect that prefix to be there...) 
        String prefixedKey = key;
        if (!key.startsWith(prefix)) prefixedKey = prefix+key;
        
        //default behavior is that an individual element will only be mapped if its already defined
        String realKey = prefixedKey.substring(prefix.length());
        FormElement el = fm.getElement(realKey);
        if (el==null) return null;
        
        //we delegate to the mapForm() function here to make sure this method of mapping 
        //also makes use of the filter stuff, as well as handling Object[] vals, etc
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put(prefixedKey, origVal);
        mapForm(fm, map);
        if (localLogger.isInfoEnabled()) localLogger.info("FormMapper "+this+".mapElement() - finished"); 
        return el;
    }

    public void setPrefix(String iprefix) {
        prefix = iprefix;
    }

    public String getPrefix() {
        return prefix;
    }

    //------------------------------------------------------------
    //protected methods for custom mappers to override
    //------------------------------------------------------------
    protected FormElement getElementForMapping(FormMap fm, String paramKey) {
        if (!paramKey.startsWith(prefix)) return null;
        String realKey = paramKey.substring(prefix.length());
        return fm.getElement(realKey);
    }


}
