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
 * $Id: BSelect.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.model.Model;
import org.barracudamvc.core.comp.model.ModelListener;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLSelectRenderer;
import org.barracudamvc.core.event.ListenerFactory;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * A BSelect element is used to render a list of items, and
 * to indicate which element(s) are selected. It is typically 
 * used to manipulate &lt;select&gt; elements.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a 
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()), 
 * then you MUST BIND IT TO A VIEW before rendering, or an error will 
 * be generated.
 */
public class BSelect extends BList {

    //public vars
    protected static final Logger logger = Logger.getLogger(BSelect.class.getName());
    //private vars
    protected ListSelectionModel selectionModel = null;
    protected LocalModelListener scallback = null;
    protected List<ListenerFactory> listeners = null;
    protected Integer viewSize = null;
    protected boolean disableBackButton = false;
    protected BAction baction = null;   //csc_041403.2

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BSelect() {
    }

    /**
     * Public constructor which creates the component and
     * associates it with a ListModel
     *
     * @param imodel the specific model to back this component
     */
    public BSelect(ListModel imodel) {
        this(imodel, null, null);
    }

    /**
     * Public constructor which creates the component and
     * binds it to a specific model. The component is also
     * bound to the specified view.
     *
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param model the specific model to back this component
     * @param view the View the component should be bound to
     * @param dvc the default ViewContext (opt--its presence allows the 
     *         component to be rendered as markup in toString())
     */
    BSelect(ListModel imodel, View iview, ViewContext idvc) {
        if (imodel != null)
            setModel(imodel);
        if (iview != null)
            this.addView(iview);
        if (idvc != null)
            setDefaultViewContext(idvc);
    }

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BSelect.class, HTMLElement.class);
        installRendererFactory(rfHTML, BSelect.class, HTMLDocument.class);

    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {

        public Renderer getInstance() {
            return new HTMLSelectRenderer();
        }
    }

    //--------------- BSelect -----------------------------
    /**
     * Set the selection model that backs the list. This causes 
     * the list to register as a listener on the model, so any 
     * changes to it will be reflected in the list. If no 
     * SelectionModel is specified, a DefaultSelectionModel will
     * be used.
     *
     * @param imodel the model that backs the list
     */
    public BSelect setSelectionModel(ListSelectionModel imodel) {

        //deregister if possible
        if (selectionModel != null && scallback != null) {
            selectionModel.removeModelListener(scallback);
        }

        //set the model    
        selectionModel = imodel;

        //reregister if possible
        if (selectionModel != null) {
            if (scallback == null)
                scallback = new LocalModelListener();
            selectionModel.addModelListener(scallback);
        }
        invalidate();
        return this;
    }

    /**
     * Get the selection model that backs the list
     *
     * @return the selection model that backs the list
     */
    public ListSelectionModel getSelectionModel() {
        if (selectionModel == null)
            setSelectionModel(new DefaultListSelectionModel());
        return selectionModel;
    }

    /**
     * Convenience method to get the selected index (if the
     * component allows multiple selections, this will return 
     * the index of the first selected item). Returns a -1 if 
     * there are no selected items
     *
     * @return the index of the first selected item
     */
    public int getSelectedIndex() {
        if (model == null || model.getSize() < 1)
            return -1;
        if (selectionModel == null || selectionModel.isSelectionEmpty())
            return -1;
        for (int i = 0, max = model.getSize(); i < max; i++) {
            if (selectionModel.isSelectedIndex(i))
                return i;
        }
        return -1;
    }

    /**
     * Convenience method to set the selected index. Any other selected
     * indexes will be cleared first. Setting this value to -1
     * effectively clears all selections.
     *
     * @param i the index to be selected
     */
    public BSelect setSelectedIndex(int i) {
        if (selectionModel == null)
            getSelectionModel();
        selectionModel.setSelectionInterval(i, i);
        invalidate();
        return this;
    }

    /**
     * Convenience method to return an int array containing all
     * selected indexes. Returns a null if there are no selected items
     *
     * @return an int array for selected indexes
     */
    public int[] getSelectedIndexes() {
        if (model == null || model.getSize() < 1)
            return null;
        if (selectionModel == null || selectionModel.isSelectionEmpty())
            return null;
        int idx[] = new int[model.getSize()];
        int cntr = -1;
        for (int i = 0, max = model.getSize(); i < max; i++) {
            if (selectionModel.isSelectedIndex(i))
                idx[++cntr] = i;
        }
        int idx2[] = new int[cntr];
        System.arraycopy(idx, 0, idx2, 0, idx2.length);
        return idx2;
    }

    /**
     * Set the view size of the component (heighth in rows). If this value
     * remains null, the height will default to whatever is specified in
     * the underlying markup. If you set this value, then the size will
     * be overridden in all the views associated with this component.
     *
     * Values less than 1 will be ignored.
     *
     * @param iviewSize an integer value representing the size.
     */
    public BSelect setViewSize(Integer iviewSize) {
        if (iviewSize == null || iviewSize.intValue() < 1)
            return this;
        viewSize = iviewSize;
        invalidate();
        return this;
    }

    /**
     * Get the size of the component. May return a null if the size 
     * has not been manually specified. In this case, the renderer will
     * simply not set the size attribute in the underlying markup, leaving
     * it to default to whatever is already there.
     *
     * @return the view size
     */
    public Integer getViewSize() {
        return viewSize;
    }

    //csc_041403.2 - removed (was not being used anywhere)
    /**
     * Set disable back button (only works if your client supports
     * Javascript)
     *
     * @param disable true if we want the back button disabled 
     */
    /*
     public void setDisableBackButton(boolean idisableBackButton) {
     disableBackButton = idisableBackButton;
     }
     */
    //csc_041403.2 - removed (was not being used anywhere)
    /**
     * Get disable back button
     *
     * @return true if we want the back button disabled 
     */
    /*
     public boolean getDisableBackButton() {
     return disableBackButton;
     }
     */
    //csc_041403.2 - added
    /**
     * Specify an action for this component (rather than adding an even listener)
     *
     * @param ibaction the action to be fired when the BSelect is activated on the client
     */
    public BSelect setAction(BAction ibaction) {
        baction = ibaction;
        return this;
    }

    //csc_041403.2 - added
    /**
     * Returns the action associated with this component (if any)
     * 
     * @return the action associated with this component (if any)
     */
    public BAction getAction() {
        return baction;
    }

    //csc_041403.2 - added
    /**
     * Add an event listener to this component. 
     *
     * @param lf the event listener to be added
     * @param idisableBackButton true if the back button should be 
     *      disabled when the action occurs
     */
    public BSelect addEventListener(ListenerFactory lf, boolean idisableBackButton) {
        if (lf == null)
            return this;
        disableBackButton = idisableBackButton;
        if (listeners == null)
            listeners = new ArrayList<ListenerFactory>(5);
        listeners.add(lf);
        invalidate();
        return this;
    }

    /**
     * Add an event listener to this component. 
     *
     * @param lf the event listener to be added
     */
    public BSelect addEventListener(ListenerFactory lf) {
        if (lf == null)
            return this;
        if (listeners == null)
            listeners = new ArrayList<ListenerFactory>(5);
        listeners.add(lf);
        invalidate();
        return this;
    }

    /**
     * Remove an event listener from this component
     *
     * @param lf the event listener to be removed
     */
    public BSelect removeEventListener(ListenerFactory lf) {
        if (lf == null)
            return this;
        if (listeners == null)
            return this;
        listeners.remove(lf);
        invalidate();
        return this;
    }

    /**
     * Here in the pre-render phase we actually add
     * BAction step children for any of the listeners
     * that might have been added to this component
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void preRender(ViewContext vc, int depth) {

        //add in our BAction as a step child
        if (baction != null)
            this.addStepChild(baction, true); 

        //we want to actually add proxy components as step children
        //for any event listeners we might have to support
        if (listeners != null) {
            //run through our list of listeners
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                ListenerFactory lf = (ListenerFactory) it.next();

                BAction baComp = new BAction();
                baComp.setDisableBackButton(disableBackButton);
                baComp.addEventListener(lf);
                this.addStepChild(baComp, true);
            }
        }
    }

    /**
     * Render a specific view for the component. 
     *
     * @param view View to be rendered
     * @param vc ViewContext for the client view
     * @throws RenderException if the particular View is not supported
     * @param list a List of all the views for this component
     */
    /*
     //021102.3_csc - removed, because now its in BComponent     
     protected void renderView (View view, ViewContext vc, int depth) throws RenderException {
     if (logger.isInfoEnabled()) logger.info("rendering comp:"+this.toRef()+" view:"+view);

     //actually render the view according to known interfaces
     try {
     Renderer r = getRenderer(view);
     r.renderComponent(this, view, vc);
            
     } catch (DOMException e) {
     logger.warn("DOM Error:", e);
     throw new DOMAccessException("Error rendering component in view:"+e, e);
     }
     }
     */
    //--------------- Lifecycle ----------------------------------
    /**
     * Destroy cycle. The component should use this cycle to
     * perform any special cleanup.
     */
    public void destroyCycle() {
        //default destroy
        super.destroyCycle();

        //we set the model to null so that the component can be 
        //garbage collected. If we don't do this, the model retains
        //a reference back to the component and so the component 
        //will never be freed up...
        setSelectionModel(null);
    }

    //--------------- Utility ------------------------------------
/*
     class LocalListSelectionListener implements ListSelectionListener {
     //if the selection model changes, invalidate the component
     public void valueChanged(ListSelectionEvent e) {
     invalidate();
     }
     }
     */
    class LocalModelListener implements ModelListener {
        //get notified when one of the underlying models changes

        public void modelChanged(Model m) {
            invalidate();
        }
    }
}