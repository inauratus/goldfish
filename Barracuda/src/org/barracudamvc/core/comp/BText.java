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
 * $Id: BText.java 194 2007-11-22 20:33:08Z alci $
 */
package org.barracudamvc.core.comp;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLTextRenderer;
import org.barracudamvc.core.comp.renderer.xml.XMLTextRenderer;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;


/**
 * BText is used for rendering text into a DOM. You can bind it to just
 * about any type of node, and it will do its best to put the text in 
 * the proper place.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a 
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()), 
 * then you MUST BIND IT TO A VIEW before rendering, or an error will 
 * be generated.
 */
public class BText extends BComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BText.class.getName());
    
    //private vars
    protected String text = null;
    protected boolean allowMarkupInText = false;    //csc_092701.1
    protected boolean insertBefore = false;    //fro_112207

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BText() {}
    
    /**
     * Public constructor which creates the component and sets 
     * the text. 
     *
     * <p>You should generally only use this constructor when returning
     * BText from a Model, as the model components will automatically
     * bind the component to a view for you. If you use this constructor
     * in some other situation, you should manually bind the component
     * to the proper view.
     *
     * @param text the text string that backs this component
     */
    public BText(String text) {
        this(text, null);
    }
    
    /**
     * Public constructor which creates the component and
     * binds it to a view, and sets the text
     *
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param text the text string that backs this component
     * @param view the View the component should be bound to
     */
    BText(String text, View view) {
        if (text!=null) this.setText(text);
        if (view!=null) this.addView(view);
    }


    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BText.class, HTMLElement.class);
        installRendererFactory(rfHTML, BText.class, HTMLDocument.class);
/*        
        WMLRendererFactory rfWML = new WMLRendererFactory();
        installRendererFactory(rfWML, BText.class, HTMLElement.class);
        installRendererFactory(rfWML, BText.class, HTMLDocument.class);
*/        
        XMLRendererFactory rfXML = new XMLRendererFactory();
        installRendererFactory(rfXML, BText.class, Node.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLTextRenderer();}
    }

    /**
     * XML RendererFactory
     */
    static class XMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new XMLTextRenderer();}
    }


    //--------------- BComponent ---------------------------------
    /**
     * Set the text for this particular component
     *
     * @param itext the text representation of this component
     */
    public BText setText(String itext) {
        text = itext;
        invalidate();
        return this;
    }
    
    /**
     * Get the text for this particular component
     *
     * @return the text for this particular component
     */
    public String getText() {
        return text;    
    }
    
    /**
     * Do we wish to allow markup in this text (defaults to false)
     *
     * @param val true if we wish to allow markup in the text
     */
    public BText setAllowMarkupInText(boolean val) {
        allowMarkupInText = val;
        invalidate();
        return this;
    }
    
    /**
     * See if we allow markup in the text
     *
     * @return true if we wish to allow markup in the text
     */
    public boolean allowMarkupInText() {
        return allowMarkupInText;    
    }
    
    // fro_112207_begin
    /**
     * Should BText be rendered at the beginning of the node
     */
    public BText setInsertBefore(boolean val) {
    	insertBefore = val;
    	invalidate();
    	return this;
    }
    public boolean insertBefore() {
    	return insertBefore;
    }
    // fro_112207_end
    
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
    /**
     * Get a String representation of the component
     */
    public String toString() {
        return text;
    }

}