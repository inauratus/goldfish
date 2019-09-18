/*
 * 
 */
package org.barracudamvc.core.comp;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.model.Model;
import org.barracudamvc.core.comp.model.ModelListener;
import org.barracudamvc.core.comp.model.TreeModel;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLTreeRenderer;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * A BTree component is used to render tree data into a DOM 
 * template. It will render into UL markup (see HTMLTreeRenderer for details). 
 */
public class BTree extends BComponent implements BCompoundComponent {

	private static final String TREEVIEW_CLASS = "bmvc_treeview";
    //public vars
    protected static final Logger logger = Logger.getLogger(BTree.class.getName());
    
    //private vars
    protected TreeModel model = null;
    private LocalModelListener callback = null;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BTree() {
    	this.setAttr("class", TREEVIEW_CLASS);
    }
    
    /**
     * Public constructor which creates the component and
     * binds it to a specific view
     *
     * @param imodel the specific model to back this component
     */
    public BTree(TreeModel imodel) {
        this(imodel, TREEVIEW_CLASS);
    }
    
    public BTree(TreeModel imodel, String classe) {
        this(imodel, (View)null);
        this.setAttr("class", classe);
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
    BTree(TreeModel imodel, View iview) {
        if (imodel!=null) setModel(imodel);
        if (iview!=null) this.addView(iview);    
    }
    



    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BTree.class, HTMLElement.class);
        installRendererFactory(rfHTML, BTree.class, HTMLDocument.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLTreeRenderer();}
    }



    //--------------- BTree -------------------------------
    /**
     * Set the model that backs the tree. This causes the tree to register
     * as a listener on the model, so any changes to it will be reflected in 
     * the tree.
     *
     * @param imodel the model that backs the tree
     */
    public BTree setModel(TreeModel imodel) {

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
    public TreeModel getModel() {
        //if (model==null) setModel(new DefaultTreeModel());
        return model;    
    }


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

    
    class LocalModelListener implements ModelListener {
        //get notified when one of the underlying models changes
        public void modelChanged(Model m) {
            invalidate();
        }
    }
}
