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
 * $Id: NamingHelper.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLAppletElement;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLFrameElement;
import org.w3c.dom.html.HTMLIFrameElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLMapElement;
import org.w3c.dom.html.HTMLMetaElement;
import org.w3c.dom.html.HTMLObjectElement;
import org.w3c.dom.html.HTMLParamElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLTextAreaElement;


/**
 * Simple helper class to assist with determining an elements name. 
 * Created csc_100201.2
 */
public class NamingHelper {
    
    protected static final Logger logger = Logger.getLogger(NamingHelper.class.getName());
    
    /**
     * Simple helper method to get a name for an element.
     * If the name is not set, a unique name will be generated,
     * and the name attribute in the element will be set accordingly.
     * Only works for HTML elements that actually support the name
     * attribute (otherwise just returns "")
     *
     * @param el a target element
     * @return the String value of the name of the passed in Element
     */
    public static String getName(Element el) {
        String name = "";
        if (hasNameAttribute(el)) {
            name = el.getAttribute("name");
            if (name==null || name.equals("")) {
                name = "el_"+new Object().hashCode();
                el.setAttribute("name", name);
            }
        }
        return name;
    }
    
    /**
     * Simple helper method to set the name attribute for an
     * element.  Only sets the attribute if both parameters are
     * non-null and {@link hasNameAttribute} returns true.
     * 
     * @param el a target element
     * @param value the desired value of the name attribute
     */
    public static void setName(Element el, String value) {
    	if (value != null && hasNameAttribute(el)) {
    		el.setAttribute("name", value);
    	}
    }
    
    /**
     * Simple helper method to check if an element has the
     * "name" attribute without having to manually perform
     * a null check on the element.
     * 
     * @param el a target element
     * @return true if el has a name attribute, false if not
     */
    public static boolean hasNameAttribute(Element el) {
    	if (el != null
    	 && (el.hasAttribute("name")
    	  || el instanceof HTMLAnchorElement
          || el instanceof HTMLAppletElement
          || el instanceof HTMLButtonElement
          || el instanceof HTMLFormElement
          || el instanceof HTMLFrameElement
          || el instanceof HTMLIFrameElement
          || el instanceof HTMLInputElement
          || el instanceof HTMLMapElement
          || el instanceof HTMLMetaElement
          || el instanceof HTMLObjectElement
          || el instanceof HTMLParamElement
          || el instanceof HTMLSelectElement
          || el instanceof HTMLTextAreaElement)) {
    		return true;
    	}
    	return false;
    }
}
