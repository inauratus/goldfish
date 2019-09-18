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
 * $Id: BTemplateGateway.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.helper;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BTemplate;
import org.barracudamvc.core.comp.DefaultTemplateView;
import org.barracudamvc.core.comp.TemplateModel;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.util.dom.DefaultDOMLoader;
import org.w3c.dom.Document;

/**
 * <p>A custom implementation of the component gateway tailored for
 * template components. All you have to do is instantiate the class
 * with the name of the template and a reference to the model; it takes 
 * care of everything else.
 */
public abstract class BTemplateGateway extends ComponentGateway {

    public abstract TemplateModel getTemplateModel();
    
    public abstract Class getTemplateClass();
    
    public Document handleDefault (BComponent root, ViewContext vc, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Document page = DefaultDOMLoader.getGlobalInstance().getDOM(getTemplateClass(), vc.getViewCapabilities().getClientLocale());  
        BTemplate templateComp = new BTemplate(getTemplateModel());
        templateComp.setView(new DefaultTemplateView(page.getDocumentElement()));
        root.addChild(templateComp);
        return page;
    }


}
