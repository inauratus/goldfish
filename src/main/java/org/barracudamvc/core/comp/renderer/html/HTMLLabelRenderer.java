/*
 * HTMLLabelRenderer.java
 *
 * Created on 16 novembre 2004, 17:26
 * Copyright (c) 2004 mecadu.org
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
 */
package org.barracudamvc.core.comp.renderer.html;

import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BLabel;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.plankton.StringUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLLabelElement;

public class HTMLLabelRenderer extends HTMLComponentRenderer {

    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {
        if (vc.getTemplateNode() instanceof HTMLLabelElement) {
            return super.createDefaultNode(doc, comp, vc);
        } else {
            return doc.createElement("LABEL");
        }
    }

    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        if (!(comp instanceof BLabel))
            throw new NoSuitableRendererException("This renderer can only render BLabel components");

        super.renderComponent(comp, view, vc);

        final Node node = view.getNode();
        if (!(node instanceof HTMLLabelElement))
            throw new NoSuitableRendererException("Node does not implement HTMLLabelElement and cannot be rendered: " + node);

        merge((HTMLLabelElement) node, (BLabel) comp);
    }

    private static void merge(final HTMLLabelElement source, final BLabel label) throws DOMException {
        final Node newNode = source.getOwnerDocument().createTextNode(StringUtil.sanitize(label.getText()));
        final Node firstChild = source.getFirstChild();
        if (firstChild instanceof Text) {
            source.replaceChild(newNode, firstChild);
        } else {
            source.insertBefore(newNode, firstChild);
        }
        source.setHtmlFor(label.getForId());
    }
}
