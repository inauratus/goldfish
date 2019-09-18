/*
 * HTMLImageRenderer.java
 *
 * Created on 9 dec 2006
 * Copyright (c) 2006 mecadu.org
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

package org.barracudamvc.core.comp;

import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLFieldSetRenderer;
import org.w3c.dom.html.HTMLElement;

public class BFieldSet extends BComponent {
    
    private String legend = null;

    // --------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BFieldSet() {
    }
    
    public BFieldSet(String iLegend) {
    	this.legend = iLegend;
    }
    
    public String getLegend() {
    	return this.legend;
    }


    // --------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BFieldSet.class, HTMLElement.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {
            return new HTMLFieldSetRenderer();
        }
    }

}