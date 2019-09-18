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
 * $Id: AbstractTemplateModel.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.List;
import org.barracudamvc.core.comp.model.ModelListener;

/**
 * This class provides the abstract implementation
 * for a Template Model.
 */
public abstract class AbstractTemplateModel implements TemplateModel {

    public static final String UNDEFINED = "Undefined";
    protected ViewContext viewContext = null;
    protected List<ModelListener> listeners = new ArrayList<ModelListener>();

    //--------------- AbstractTemplateModel ----------------------
    /**
     * Add a listener to the template that's notified each time a change
     * to the data model occurs.
     *
     * @param ml the TemplateModelListener
     */
    @Override
    public void addModelListener(ModelListener ml) {
        listeners.add(ml);
    }

    /**
     * Remove a listener
     *
     * @param ml the TemplateModelListener
     */
    @Override
    public void removeModelListener(ModelListener ml) {
        listeners.remove(ml);
    }

    /**
     * Forwards the given notification event to all
     * <code>TemplateModelListeners</code> that registered
     * themselves as listeners for this template model.
     */
    public void fireModelChanged() {
        for (ModelListener listener : listeners) {
            listener.modelChanged(this);
        }
    }

    /**
     * process any directives. return false to indicate a the node
     * containing this directive should be skipped.
     */
    @Override
    public boolean processDirective(TemplateDirective td) {
        return true;    //by default, allow all directives
    }

    //csc_030603.2 - added
    /**
     * get an item for a given template directive. Implement this
     * method if you want access to the full directive, not just the
     * String key. Note that if you implement this method, AND you
     * want to use the getItem(String key) method, then your implementation
     * of this method must be sure to call super.getItem() in order for
     * for the convenience method to get called.
     */
    @Override
    public Object getItem(TemplateDirective td) {
        return getItem(td.getKeyName());
    }

    /**
     * Convenience method to get an item based on the key name (extracted
     * from the TemplateDirective).
     */
    public Object getItem(String key) {
        return getName() + "." + key + " " + UNDEFINED;
    }

    //--------------- Contextual ---------------------------------
    /**
     * Specify the ViewContext. This method will generally be called
     * by the class that is using the model to actually render the data
     * in a view. The context will be specified prior to a render pass,
     * and the context will be reset to null after the render pass. 
     *
     * @param ivc the current ViewContext
     */
    @Override
    public void setViewContext(ViewContext ivc) {
        viewContext = ivc;
    }

    /**
     * Get the current ViewContext
     *
     * @return the current ViewContext
     */
    @Override
    public ViewContext getViewContext() {
        return viewContext;
    }
}