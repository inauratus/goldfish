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
 * $Id: BScriptResource.java 203 2008-07-21 10:01:29Z alci $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLScriptResourceRenderer;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;


//csc_102201.1 - created
/**
 * BScriptResource is used to make sure a client side script is available for
 * use by client scripting code.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()),
 * then you MUST BIND IT TO A VIEW before rendering, or an error will
 * be generated.
 */
public class BScriptResource extends BComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BScriptResource.class.getName());

    public static final String JS_CLIENT_SERVER_HTTP_LIB = "/org/barracudamvc/core/scripts/ClientServerHTTPLib.js";
    public static final String JS_FORM_CONTROL = "/org/barracudamvc/core/scripts/FormControl.js";
    public static final String JS_SCRIPTING_CHECK = "/org/barracudamvc/core/scripts/ScriptingCheck.js";

    //private vars
    // fro_071508_1
    protected List<String> sources = new ArrayList<String>();
    //protected String src = null;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BScriptResource() {}

    /**
     * Public constructor which creates the component. When rendered,
     * it will make sure that the src script is available to the client.
     *
     * <p>You should generally only use this constructor when returning
     * BScriptResource from a Model, as the model components will automatically
     * bind the component to a view for you. If you use this constructor
     * in some other situation, you should manually bind the component
     * to the proper view.
     *
     * @param src the src script that backs this component
     */
    public BScriptResource(String src) {
        this(src, null);
    }

    /**
     * Public constructor which creates the component and
     * binds it to a view. When rendered, it will make sure
     * that the src script is available to the client.
     *
     * <p>Null values may be passed in for any parameters,
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually
     * rendering the component
     *
     * @param src the src script that backs this component
     * @param view the View the component should be bound to
     */
    BScriptResource(String src, View view) {
        if (src!=null) this.setSrc(src);
        if (view!=null) this.addView(view);
    }


    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BScriptResource.class, HTMLElement.class);
        installRendererFactory(rfHTML, BScriptResource.class, HTMLDocument.class);
/*
        WMLRendererFactory rfWML = new WMLRendererFactory();
        installRendererFactory(rfWML, BScriptResource.class, HTMLElement.class);
        installRendererFactory(rfWML, BScriptResource.class, HTMLDocument.class);
*/
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLScriptResourceRenderer();}
    }



    //--------------- BComponent ---------------------------------
    /**
     * Set the src for this particular component
     *
     * @param isrc the src script that backs this component
     */
     // fro_071508_1_begin
    public BScriptResource setSrc(String isrc) {
        sources.add(isrc);
        invalidate();
        return this;
    }

    /**
     * Get the src for this particular component
     *
     * @return the src for this particular component
     */
    public List<String> getSources() {
        return sources;
    }

    /**
     * Add a single dependency for this resource
     */
    public void dependsOn(String src) {
    	sources.add(0, src);
	}
    
    /**
     * Add a resource dependency for this resource
     * Every src in this resource will be rendered before
     * the component's own src. 
     */
    public BScriptResource dependsOn(BScriptResource res) {
        sources.addAll(0, res.getSources());
        invalidate();
        return this;
    }
	// fro_071508_1_end

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
        //fro_071508_1
        return sources.toString();
    }

}