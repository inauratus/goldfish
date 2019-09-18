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
 * $Id: BTemplateViewHandler.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.event.helper;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BTemplate;
import org.barracudamvc.core.comp.DefaultTemplateView;
import org.barracudamvc.core.comp.TemplateModel;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.event.EventException;
import org.barracudamvc.core.util.dom.DefaultDOMLoader;
import org.w3c.dom.Document;

/**
 * <p>A custom implementation of the default view handler tailored for
 * template components. All you have to do is instantiate the class
 * with the name of the template and a reference to the model; it takes
 * care of everything else.
 */
public abstract class BTemplateViewHandler extends DefaultViewHandler {

    private static final Logger logger = Logger.getLogger(BTemplateViewHandler.class.getName());
    protected BTemplate templateComp = null;

    //-------------------- DefaultViewHandler --------------------
    /**
     * Return an instance of the template model; you can either return
     * a single instance of a TemplateModel, -OR- you can return a
     * List of TemplateModels
     */
    public abstract Object getTemplateModels();

    /**
     * Return an instance of the template class (must implement Document)
     */
    public abstract Class<?> getTemplateClass();

    /**
     * Provide a handle to the underlying BTemplate component
     */
    public BTemplate getBTemplate() {
        return templateComp;
    }

    /**
     * Generate the view
     */
    @Override
    public Document handleViewEvent(BComponent root) throws EventException, ServletException, IOException { //csc_030603.1
        ViewContext vc = this.getViewContext();

        //load the localized DOM template
        Class<?> cl = getTemplateClass();
        Document page = DefaultDOMLoader.getGlobalInstance().getDOM(cl, vc.getViewCapabilities().getClientLocale());

        //create a template component and bind it to the views
        templateComp = new BTemplate();
        Object o = getTemplateModels();
        if (o instanceof TemplateModel) {
            templateComp.addModel((TemplateModel) o);
        } else if (o instanceof List) {
            templateComp.addModels((List<?>) o);
        } else if (o == null) {
        } else {
            throw new RuntimeException("Fatal err: Model must return either TemplateModel or List");
        }
        templateComp.setView(new DefaultTemplateView(page.getDocumentElement()));
        root.addChild(templateComp);

        // Set ongoingTemplate on the view context, for BScript deffered rendering
        vc.setOngoingBTemplate(templateComp);

        //return the page
        return page;
    }

    @Override
    public void postCompRender(BComponent root) {
        //
    }

    @Override
    public void preCompRender(BComponent root) {
        //(--n/a--)
    }
}
