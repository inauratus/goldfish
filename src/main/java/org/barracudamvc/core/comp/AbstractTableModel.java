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
 * $Id: AbstractTableModel.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.List;
import org.barracudamvc.core.comp.model.ModelListener;

/**
 * This class provides the abstract implementation
 * for a Table Model.
 *
 * <p>Note: this interface is designed to be used to ways. You can
 * either implement to return a specific number of rows/cols (in
 * which case hasMoreXXX() methods should return false) OR you 
 * can implement using the hasMoreXXX() methods (in which case the
 * getColumnCount(), getRowCount() methods should return -1.
 *
 * <p>The getXXXCount() method is more like the Swing JTable interface;
 * the hasMoreXXX() method makes it easier to implement tables where
 * you don't know the total number of records when you start.
 *
 * <p>Classes implementing this interface should generally support both 
 * methods: in other words, for every row/column, invoke the getItemAt()
 * method, then while the model has more rows/columns, it should again 
 * invoke getItemAt().
 */
public abstract class AbstractTableModel implements TableModel {

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
     * Reset the model to its initial (unprocessed) state. This 
     * is a convenience method that gets invoked prior to the 
     * entire model being rendered. You only need to override this 
     * method if you want to do something (like reset internal counters)
     * before the model is queried
     */
    @Override
    public void resetModel() {
        //nop    
    }

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