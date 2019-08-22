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
 * $Id: HTMLTemplateRenderer.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BTemplate;
import org.barracudamvc.core.comp.InvalidViewException;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.TemplateView;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.TemplateHelper;

/**
 * This class handles the default rendering of a template into an HTML view.
 */
public class HTMLTemplateRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLTemplateRenderer.class.getName());
    
    protected TemplateHelper th = null;
    
    /**
     *
     */
    public HTMLTemplateRenderer() {
        this(null);    
    }
    
    /**
     *
     */
    public HTMLTemplateRenderer(TemplateHelper ith) {
        setTemplateHelper(ith);
    }
    
    /**
     *
     */
    public void setTemplateHelper(TemplateHelper ith) {
        th = ith;
    }
    
    /**
     *
     */
    public TemplateHelper getTemplateHelper() {
        if (th==null) th = new TemplateHelper(this);
        return th;
    }
    
    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a template component
        if (!(comp instanceof BTemplate)) throw new NoSuitableRendererException("This renderer can only render BTemplate components");

        //make sure the View implements TemplateView
        if (!(view instanceof TemplateView)) throw new InvalidViewException ("Component is bound to an unsupported View:"+view);

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);
        
        //now delegate the actual rendering to the TemplateHelper
        getTemplateHelper().render((BTemplate) comp, (TemplateView) view, vc);
    }
    
}