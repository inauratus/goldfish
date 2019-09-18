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
 * $Id: EnabledHelper.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLOptGroupElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLStyleElement;
import org.w3c.dom.html.HTMLTextAreaElement;

/**
 * This class makes sure that components are rendered properly
 * for enabled or disabled states
 */
public class EnabledHelper {

    /**
     * For given elements, set their disabled attribute
     */
    public static void setEnabled(Node node, boolean enabled) {

        //..HTMLAnchorElement - clear the "href" and "target" attributes
        if (node instanceof HTMLAnchorElement) {
            Element el = (Element) node;
            if (!enabled) {
            	el.removeAttribute("href");
            	el.removeAttribute("target");
            }

        //..HTMLButtonElement - set the "disabled" attribute
        } else if (node instanceof HTMLButtonElement) {
            ((HTMLButtonElement) node).setDisabled(!enabled);

        //..HTMLInputElement - set the "disabled" attribute
        } else if (node instanceof HTMLInputElement) {
            ((HTMLInputElement) node).setDisabled(!enabled);

        //..HTMLOptGroupElement - set the "disabled" attribute
        } else if (node instanceof HTMLOptGroupElement) {
            ((HTMLOptGroupElement) node).setDisabled(!enabled);

        //..HTMLOptionElement - set the "disabled" attribute
        } else if (node instanceof HTMLOptionElement) {
            ((HTMLOptionElement) node).setDisabled(!enabled);

        //..HTMLSelectElement - set the "disabled" attribute
        } else if (node instanceof HTMLSelectElement) {
            ((HTMLSelectElement) node).setDisabled(!enabled);

        //..HTMLStyleElement - set the "disabled" attribute
        } else if (node instanceof HTMLStyleElement) {
            ((HTMLStyleElement) node).setDisabled(!enabled);

        //..HTMLTextAreaElement - set the "disabled" attribute
        } else if (node instanceof HTMLTextAreaElement) {
            ((HTMLTextAreaElement) node).setDisabled(!enabled);
        }

    }
    
}