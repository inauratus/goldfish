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
 * $Id: TemplateView.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.List;
import org.barracudamvc.plankton.data.StateMap;
import org.w3c.dom.Node;

/**
 * This interface defines a View for Table components to 
 * render themselves in. Similar to the java.awt.Graphics 
 * object in AWT and Swing.
 */
public interface TemplateView extends View {
    public Node getMasterTemplate();
    public void setDirIDMap(StateMap idMap);
    public StateMap getDirIDMap();
    public TemplateDirective lookupDir(String dirStr);
    public List<TemplateDirective> lookupDirsByID(String idStr);
    public void setIDAttrName(String idAttrName);
    public String getIDAttrName();
    public void setDirAttrName(String dirAttrName);
    public String getDirAttrName();
}