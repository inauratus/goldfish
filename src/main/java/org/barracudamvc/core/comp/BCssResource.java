package org.barracudamvc.core.comp;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLCssResourceRenderer;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;


/**
 * BCssResource is used to make sure a CSS is loaded.
 */
public class BCssResource extends BComponent {
	
    //public vars
    protected static final Logger logger = Logger.getLogger(BCssResource.class.getName());

    //private vars
    protected String hRef = null;
    
    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BCssResource() {}

    /**
     * Public constructor which creates the component. When rendered,
     * it will make sure that the href CSS is available to the client.
     *
     * <p>You should generally only use this constructor when returning
     * BCssResource from a Model, as the model components will automatically
     * bind the component to a view for you. If you use this constructor
     * in some other situation, you should manually bind the component
     * to the proper view.
     *
     * @param href the href CSS that backs this component
     */
    public BCssResource(String hRef) {
        this(hRef, null);
    }

    /**
     * Public constructor which creates the component and
     * binds it to a view. When rendered, it will make sure
     * that the href CSS is available to the client.
     *
     * <p>Null values may be passed in for any parameters,
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually
     * rendering the component
     *
     * @param href the href CSS that backs this component
     * @param view the View the component should be bound to. Notice this component renders in header anyway !
     */
    BCssResource(String hRef, View view) {
        if (hRef!=null) this.setHRef(hRef);
        if (view!=null) this.addView(view);
    }
    
    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BCssResource.class, HTMLElement.class);
        installRendererFactory(rfHTML, BCssResource.class, HTMLDocument.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLCssResourceRenderer();}
    }

    //--------------- BComponent ---------------------------------
    /**
     * Set the href for this particular component
     *
     * @param ihref the href CSS that backs this component
     */
    public BCssResource setHRef(String ihref) {
    	hRef = ihref;
        invalidate();
        return this;
    }

    /**
     * Get the href for this particular component
     *
     * @return the href for this particular component
     */
    public String getHRef() {
        return hRef;
    }
}
