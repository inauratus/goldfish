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
 * $Id: HTMLComponentRenderer.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.InvalidNodeException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.DOMComponentRenderer;
import org.barracudamvc.plankton.Classes;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLLIElement;
import org.w3c.dom.html.HTMLOListElement;
import org.w3c.dom.html.HTMLOptGroupElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableColElement;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;
import org.w3c.dom.html.HTMLUListElement;

/**
 * This interface defines the methods needed to implement a Renderer.
 */
public class HTMLComponentRenderer extends DOMComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLComponentRenderer.class.getName());

    /**
     *
     */
    public Node addChildToParent(Node parent, Node child) throws InvalidNodeException {
        //eliminate the obvious
        if (parent == null || child == null)
            throw new InvalidNodeException("Invalid node: cannot add child:" + child + " to parent:" + parent);

        boolean childIsEmptyText = false;
        if (child instanceof Text) {
            String txt = ((Text) child).getData().trim();
            if (txt.length() == 0)
                childIsEmptyText = true;
        }

        //make any adjustments specific to the markup
        if (parent instanceof HTMLElement) {
            //...<TR>
            if (parent instanceof HTMLTableRowElement) {
                if (!(child instanceof HTMLTableCellElement)
                        && !childIsEmptyText) {
                    child = addChildToParent(parent.getOwnerDocument().createElement("TD"), child);
                }
                //...<THEAD>,<TBODY>,<TFOOT>
            } else if (parent instanceof HTMLTableSectionElement) {
                if (!(child instanceof HTMLTableRowElement)
                        && !childIsEmptyText) {
                    child = addChildToParent(parent.getOwnerDocument().createElement("TR"), child);
                }
                //...<TABLE>
            } else if (parent instanceof HTMLTableElement) {
                if (!(child instanceof HTMLTableCaptionElement)
                        && !(child instanceof HTMLTableCellElement)
                        && !(child instanceof HTMLTableColElement)
                        && !(child instanceof HTMLTableRowElement)
                        && !(child instanceof HTMLTableSectionElement)
                        && !childIsEmptyText) {
                    child = addChildToParent(parent.getOwnerDocument().createElement("TR"), child);
                }
                //...<OL>,<UL>    
            } else if ((parent instanceof HTMLOListElement)
                    || (parent instanceof HTMLUListElement)) {
                if (!(child instanceof HTMLLIElement)
                        && !childIsEmptyText) {
                    child = addChildToParent(parent.getOwnerDocument().createElement("LI"), child);
                }
                //...<OPTGROUP>
            } else if (parent instanceof HTMLOptGroupElement) {
                if (!(child instanceof HTMLOptionElement)
                        && !childIsEmptyText) {
                    child = addChildToParent(parent.getOwnerDocument().createElement("OPTION"), child);
                }
                //...<SELECT>
            } else if (parent instanceof HTMLSelectElement) {
                if (!(child instanceof HTMLOptGroupElement)
                        && !(child instanceof HTMLOptionElement)
                        && !childIsEmptyText) {
                    child = addChildToParent(parent.getOwnerDocument().createElement("OPTION"), child);
                }
                //...<COLGROUP>    
            } else if (parent instanceof HTMLTableColElement) {
                if (!(child instanceof HTMLTableColElement)
                        && !childIsEmptyText) {
                    throw new InvalidNodeException("Child:" + child + " cannot be added to parent:" + parent);
                }
            } else {
                String parentTag = ((Element) parent).getTagName().toUpperCase();
                String childTag = null;
                if (child instanceof Element)
                    childTag = ((Element) child).getTagName().toUpperCase();

                //...<DL>
                if (parentTag.equals("DL")) {
                    if (!childTag.equals("DD")
                            && !childTag.equals("DT")
                            && !childIsEmptyText) {
                        child = addChildToParent(parent.getOwnerDocument().createElement("DD"), child);
                    }
                }
            }
        }

        //now add the child in
        if (child != null)
            parent.appendChild(child);

        //return the parent
        return parent;
    }

    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        super.renderComponent(comp, view, vc);

        Element el = (Element) view.getNode();
        String name = comp.getName();
        if (el != null && name != null) {
            NamingHelper.setName(el, name);
        }

        EnabledHelper.setEnabled(el, comp.isEnabled());
    }
}
