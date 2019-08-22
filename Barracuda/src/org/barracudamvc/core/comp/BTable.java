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
 * $Id: BTable.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.model.Model;
import org.barracudamvc.core.comp.model.ModelListener;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLTableRenderer;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * A BTable is used to put data into a table format within a DOM. In
 * practice, this has proven to be one of the least used Barracuda 
 * components, since it it usually a lot easier (and more flexible)
 * simply to use a BTemplate.
 *
 * <p>In the case of BTable, you will ALMOST ALWAYS need to manually
 * bind it to a View, unless you happen to be returning it from a model
 * (in which case this will be done for you automatically)
 */
public class BTable extends BComponent implements BCompoundComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BTable.class.getName());
    
    //private vars
    protected TableModel model = null;
    protected TableModel headerModel = null;
    protected TableModel footerModel = null;
    private LocalModelListener callback = null;
    private LocalModelListener headerCallback = null;
    private LocalModelListener footerCallback = null;
    protected Node templateNode = null;
    protected BText caption = null;        //ndc_101202.1

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BTable() {
        this (null);
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
     * @param imodel the specific model to back this component
     */
    public BTable(TableModel imodel) {
        this (null, imodel, null, null);
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
     * @param headerModel the specific header model to back this component
     * @param bodyModel the main data model that backs this component
     * @param footerModel the specific footer model to back this component
     * @param view the View the component should be bound to
     */
    BTable(TableModel iheaderModel, TableModel ibodyModel, TableModel ifooterModel, TableView view) {
        if (iheaderModel!=null) setHeaderModel(iheaderModel);
        if (ibodyModel!=null) setModel(ibodyModel);
        if (ifooterModel!=null) setFooterModel(ifooterModel);
        if (view!=null) this.addView(view);    
    }
    




    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BTable.class, HTMLElement.class);
        installRendererFactory(rfHTML, BTable.class, HTMLDocument.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLTableRenderer();}
    }




    //--------------- BTable ------------------------------
    /**
     * Set the model that backs the table. This causes the table to register
     * as a listener on the model, so any changes to it will be reflected in 
     * the table.
     *
     * @param imodel the model that backs the table
     */
    public BTable setModel(TableModel imodel) {

        //deregister if possible
        if (model!=null && callback!=null) {
//            model.removeTableModelListener(callback);
            model.removeModelListener(callback);
        }
    
        //set the model    
        model = imodel;
        invalidate();
        
        //reregister if possible
        if (model!=null) {
            if (callback==null) callback = new LocalModelListener();
//            model.addTableModelListener(callback);
            model.addModelListener(callback);
        }
        return this;
    }
    
    /**
     * Get the model that backs the table
     *
     * @return the model that backs the table
     */
    public TableModel getModel() {
        return model;    
    }
    
    /**
     * Set the header model that backs the table. This causes 
     * the table to register as a listener on the model, so 
     * any changes to it will be reflected in the table header.
     *
     * @param imodel the headermodel that backs the table
     */
    public BTable setHeaderModel(TableModel imodel) {

        //deregister if possible
        if (headerModel!=null && headerCallback!=null) {
//            headerModel.removeTableModelListener(headerCallback);
            headerModel.removeModelListener(headerCallback);
        }
    
        //set the model    
        headerModel = imodel;
        invalidate();
        
        //reregister if possible
        if (headerModel!=null) {
            if (headerCallback==null) headerCallback = new LocalModelListener();
//            headerModel.addTableModelListener(headerCallback);
            headerModel.addModelListener(headerCallback);
        }
        return this;
    }
    
    /**
     * Get the header model that backs the table
     *
     * @return the header model that backs the table
     */
    public TableModel getHeaderModel() {
        return headerModel;    
    }
    
    /**
     * Set the footer model that backs the table. This causes 
     * the table to register as a listener on the model, so 
     * any changes to it will be reflected in the table footer.
     *
     * @param imodel the footer model that backs the table
     */
    public BTable setFooterModel(TableModel imodel) {

        //deregister if possible
        if (footerModel!=null && footerCallback!=null) {
//            footerModel.removeTableModelListener(footerCallback);
            footerModel.removeModelListener(footerCallback);
        }
    
        //set the model    
        footerModel = imodel;
        invalidate();
        
        //reregister if possible
        if (footerModel!=null) {
            if (footerCallback==null) footerCallback = new LocalModelListener();
//            footerModel.addTableModelListener(footerCallback);
            footerModel.addModelListener(footerCallback);
        }
        return this;
    }
    
    /**
     * Get the footer model that backs the table
     *
     * @return the footer model that backs the table
     */
    public TableModel getFooterModel() {
        return footerModel;    
    }
    
    //ndc_101202.1 - added
    /**
     * Set the caption that backs the table
     *
     * @param icaption A BText to represent to Caption Element.
     */
    public BTable setCaption(BText icaption) {
        this.caption = icaption;
        return this;
    }

    //ndc_101202.1 - added
    /**
     * Get the caption that backs the table
     *
     * @return the caption to be rendered
     */
    public BText getCaption() {
        return this.caption;
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
        if (logger.isInfoEnabled()) logger.info("rendering view: "+view);

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
        setModel(null);        
        setHeaderModel(null);        
        setFooterModel(null);        
    }




    //--------------- Utility ------------------------------------
/*    
    class LocalTableModelListener implements TableModelListener {
        //for right now, just invalidate everything. In the future,
        //we could get a lot smarter and keep track of the ranges
        //changed and then only redraw those. This would greatly 
        //speed rendering, because we'd only need to redraw the parts
        //of the DOM that changed...
        public void tableChanged(TableModelEvent e) {
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
