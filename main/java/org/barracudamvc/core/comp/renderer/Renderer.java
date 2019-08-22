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
 * $Id: Renderer.java 159 2007-02-04 17:17:18Z alci $
 */
package org.barracudamvc.core.comp.renderer;

import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.InvalidNodeException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface defines the methods needed to implement a Renderer.
 */
public interface Renderer {

    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException;
    public void addDefaultView(BComponent comp, Node node);
    public Node addChildToParent(Node parent, Node child) throws InvalidNodeException;
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException;
    
}