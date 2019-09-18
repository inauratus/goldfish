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
 * $Id: BList.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.model.Model;
import org.barracudamvc.core.comp.model.ModelListener;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLListRenderer;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * A BList component is used to render list data into a DOM 
 * template. It can be bound to a wide range of markup elements
 * (see HTMLListRenderer for details). 
 *
 * <p>In the case of BList, you will ALMOST ALWAYS need to manually
 * bind it to a View, unless you happen to be returning it from a model
 * (in which case this will be done for you automatically by cloning
 * the node which contained the directive)
 */
public class BList extends BComponent implements BCompoundComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BList.class.getName());
    
    //private vars
    protected ListModel model = null;
    private LocalModelListener callback = null;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BList() {}
    
    /**
     * Public constructor which creates the component and
     * binds it to a specific view
     *
     * @param imodel the specific model to back this component
     */
    public BList(ListModel imodel) {
        this(imodel, null); 
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
     */
    BList(ListModel imodel, View iview) {
        if (imodel!=null) setModel(imodel);
        if (iview!=null) this.addView(iview);    
    }
    



    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BList.class, HTMLElement.class);
        installRendererFactory(rfHTML, BList.class, HTMLDocument.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLListRenderer();}
    }



    //--------------- BList -------------------------------
    /**
     * Set the model that backs the list. This causes the list to register
     * as a listener on the model, so any changes to it will be reflected in 
     * the list.
     *
     * @param imodel the model that backs the list
     */
    public BList setModel(ListModel imodel) {

        //deregister if possible
        if (model!=null && callback!=null) {
            model.removeModelListener(callback);
        }
    
        //set the model    
        model = imodel;
        invalidate();
        
        //reregister if possible
        if (model!=null) {
            if (callback==null) callback = new LocalModelListener();
            model.addModelListener(callback);
        }
        
        //Q: how does this affect garbage collection? In other words, how do we
        //ensure that when we're done with this object it will get gc'd since the
        //model will still have a reference to it? This seems especially problematic
        //if we had multiple components sharing a model and one of them goes away.
        //
        //A: (I think) In the components destroyCycle, make sure the list model
        //gets set to null
        return this;
    }
    
    /**
     * Get the model that backs the list
     *
     * @return the model that backs the list
     */
    public ListModel getModel() {
        if (model==null) setModel(new DefaultListModel());
        return model;    
    }
    
    /**
     * A convenience method that constructs a ListModel from an array of Objects 
     * and then applies setModel to it.
     *
     * @param list an array of Objects containing the items to display 
     *         in the list
     */
    public BList setListData(Object[] list) {
        DefaultListModel lm = new DefaultListModel();
        if (list!=null) for (int i=0,max=list.length; i<max; i++) {
            lm.add(list[i]);
        }
        setModel(lm);    
        return this;
    }
    
    /**
     * A convenience method that constructs a ListModel from an Iterator 
     * and then applies setModel to it.
     *
     * @param it an iterator of objects to display in the list
     */
    public BList setListData(Iterator it) {
        DefaultListModel lm = new DefaultListModel();
        while (it.hasNext()) {
            lm.add(it.next());
        }
        setModel(lm);    
        return this;
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
        setModel(null);        
    }




    //--------------- Utility ------------------------------------
/*    
    class LocalListDataListener implements ListDataListener {
        //for right now, just invalidate everything. In the future,
        //we could get a lot smarter and keep track of the ranges
        //changed and then only redraw those. This would greatly 
        //speed rendering, because we'd only need to redraw the parts
        //of the DOM that changed...
        public void intervalAdded(ListDataEvent e) {invalidate();}
        public void intervalRemoved(ListDataEvent e) {invalidate();}
        public void contentsChanged(ListDataEvent e) {invalidate();}
    }
*/    
    class LocalModelListener implements ModelListener {
        //get notified when one of the underlying models changes
        public void modelChanged(Model m) {
            invalidate();
        }
    }
}
