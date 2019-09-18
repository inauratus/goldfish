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
 * $Id: View.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.comp;

import org.w3c.dom.Node;

/**
 * This interface defines a View for components to 
 * render themselves in. Similar to the java.awt.Graphics 
 * object in AWT and Swing.
 */
public interface View extends Cloneable {

    public void setName(String name);
    public String getName();
    public void setNode(Node node) throws InvalidViewException;
    public Node getNode();
    public Object clone();
}