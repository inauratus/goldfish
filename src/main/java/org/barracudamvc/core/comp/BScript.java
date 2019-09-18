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
 * $Id: BScript.java 233 2010-06-30 15:08:26Z alci $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLScriptRenderer;
import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;


//csc_102201.1 - created
/**
 * BScript is used to attach a javascript command to a DOM element
 * attribute.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()),
 * then you MUST BIND IT TO A VIEW before rendering, or an error will
 * be generated.
 */
public class BScript extends BComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BScript.class.getName());

    //how should the command be inserted when inline
    public static final int REPLACE = 0;
    public static final int PREPEND = 1;
    public static final int APPEND = 2;
    
    /**
     * rendering mode : inline (whitin the tag), in header (after the css and script resources) or in body (appended to body children)
     */
    public enum RenderMode {HEADER, BODY, INLINE};

    //target actions
    //...std events
    public static final String ON_CLICK = "onclick";
    public static final String ON_DBL_CLICK = "ondblclick";
    public static final String ON_MOUSE_DOWN = "onmousedown";
    public static final String ON_MOUSE_UP = "onmouseup";
    public static final String ON_MOUSE_OVER = "onmouseover";
    public static final String ON_MOUSE_MOVE = "onmousemove";
    public static final String ON_MOUSE_OUT = "onmouseout";
    public static final String ON_KEY_PRESS = "onkeypress";
    public static final String ON_KEY_DOWN = "onkeydown";
    public static final String ON_KEY_UP = "onkeyup";
    //..<body> specific
    public static final String ON_LOAD = "onload";
    public static final String ON_UNLOAD = "onunload";
    //..<a>,<area>,<label>,<input>,<select>,<textarea>,<button> specific
    public static final String ON_FOCUS = "onfocus";
    public static final String ON_BLUR = "onblur";
    //..<input>,<textarea> specific
    public static final String ON_SELECT = "onselect";
    //..<input>,<select> specific
    public static final String ON_CHANGE = "onchange";
    //..<form> specific
    public static final String ON_SUBMIT = "onsubmit";
    public static final String ON_RESET = "onreset";

    //private vars
    protected String jscmd = null;
    protected String jsattr = null;
    protected int mode = REPLACE;
    protected List<String> resources = null;
    protected List<String> cssList = null;
    protected RenderMode renderMode = RenderMode.HEADER;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BScript() {}
    
    /**
     * Public constructor which create the component with the following characteristics :
     * - rendering mode : HEADER
     * @param cmd the javascript that will be rendered
     */
    public BScript(String cmd) {
    	this();
    	this.jscmd = cmd;
    	this.renderMode = RenderMode.HEADER;
    }

    /**
     * Public constructor which creates the component. When rendered,
     * it will take the specified command and REPLACE it in the target
     * scripting attribute
     *
     * <p>You should generally only use this constructor when returning
     * BScript from a Model, as the model components will automatically
     * bind the component to a view for you. If you use this constructor
     * in some other situation, you should manually bind the component
     * to the proper view.
     *
     * @param jsattr the target script attribute
     * @param jscmd the script command
     */
    public BScript(String jsattr, String jscmd) {
        this(jsattr, jscmd, REPLACE);
    }

    /**
     * Public constructor which creates the component. When rendered,
     * it will take the specified command and render it in the target
     * scripting attribute, using the specified replacement mode.
     *
     * @param jsattr the target script attribute
     * @param jscmd the script command
     * @param mode the cmd replacement mode (REPLACE, PREPEND, or APPEND)
     */
    public BScript(String jsattr, String jscmd, int mode) {
        this (jsattr, jscmd, mode, null);
    }

    /**
     * Public constructor which creates the component. When rendered,
     * it will take the specified command and render it in the target
     * scripting attribute, using the specified replacement mode.
     *
     * @param jsattr the target script attribute
     * @param jscmd the script command
     * @param mode the cmd replacement mode (REPLACE, PREPEND, or APPEND)
     * @param resource any necessary resource scripts
     */
    public BScript(String jsattr, String jscmd, int mode, String resource) {
        if (jsattr!=null) setJSAttr(jsattr);
        if (jscmd!=null) setCmd(jscmd);
        setMode(mode);
        if (resource!=null) addResource(resource);
        this.setRenderMode(RenderMode.INLINE);
    }

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BScript.class, HTMLElement.class);
        installRendererFactory(rfHTML, BScript.class, HTMLDocument.class);
/*
        WMLRendererFactory rfWML = new WMLRendererFactory();
        installRendererFactory(rfWML, BScript.class, HTMLElement.class);
        installRendererFactory(rfWML, BScript.class, HTMLDocument.class);
*/
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLScriptRenderer();}
    }

    @Override
	protected void renderView (View view, ViewContext vc, int depth) throws RenderException {
        //021102.3_csc_start - this method didn't used to do anything, which meant that
        //if you tried to use a plain-jane BComponent to control visibility it would never
        //actually get rendered. Dumb. THis should make it work now...
        if (logger.isInfoEnabled()) logger.info("rendering view: "+view);

        //actually render the view according to known interfaces
        try {
            Renderer r = getRenderer(view);
            r.renderComponent(this, view, vc);
            
        } catch (DOMException e) {
            logger.warn("DOM Error:", e);
            throw new DOMAccessException("Error rendering component in view:"+e, e);
        }
        //021102.3_csc_end
    }
    
    
    protected void preRender(ViewContext vc, int depth) {
    	//add in BScriptResource components that are needed
	    if (getResources() != null) {
	       for (String scr : getResources()) {
	            BScriptResource bsr = new BScriptResource(scr);
	            //bsr.setView(new DefaultView(node));
	            this.addStepChild(bsr, true);
	        }
	    }
	    
	  //add in BCssResource components that are needed
		List<String> cssList = this.getCssList();
		if (cssList!=null) {
			BCssResource bcr;
			for (String scr : cssList){
				bcr = new BCssResource(scr);
				//bcr.setView(new DefaultView(node));
				this.addStepChild(bcr, true);
			}
		}
	}


    //--------------- BComponent ---------------------------------
    /**
     * Specify the JavaScript attr the command should be bound to
     *
     * @param ijsattr the JavaScript attr the command should be bound to
     */
    public BScript setJSAttr(String ijsattr) {
        jsattr = ijsattr;
        invalidate();
        return this;
    }

    /**
     * Get the JavaScript attr the command is be bound to
     *
     * @return the JavaScript attr the command is be bound to
     */
    public String getJSAttr() {
        return jsattr;
    }

    /**
     * Set the JavaScript command for this particular component
     *
     * @param ijscmd the JavaScript command that backs this component
     */
    public BScript setCmd(String ijscmd) {
        jscmd = ijscmd;
        invalidate();
        return this;
    }

    /**
     * Get the JavaScript command for this particular component
     *
     * @return the JavaScript command for this particular component
     */
    public String getCmd() {
        return jscmd;
    }

    /**
     * Set the mode for the action (defaults to REPLACE). In most cases,
     * you will simply REPLACE whatever the current cmd is in the template.
     * Sometimes, however, you may want to keep what's already there. In such
     * a situation, you would specify either APPEND (tack it on to what's
     * already there) or PREPEND (stick it in front of what's there)
     *
     * @param imode the cmd replacement mode
     */
    public BScript setMode(int imode) {
        if (imode!=PREPEND && imode!=APPEND) imode = REPLACE;
        mode = imode;
        invalidate();
        return this;
    }

    /**
     * Get the replacement mode for this script
     *
     * @return the replacement mode for this script
     */
    public int getMode() {
        return mode;
    }

    /**
     * Add a script resource (resources specified will automatically
     * create BScriptResource delegates at render time)
     *
     * @param resource a script resource
     */
    public BScript addResource(String resource) {
        if (resources==null) resources = new ArrayList<String>();
        resources.add(resource);
        invalidate();
        return this;
    }
    
	/**
     * Convinience methos to add a BScriptResource child
     * @param resource a script resource
     */
    public BScript addResource(BScriptResource resource) {
    	 if (resources==null) resources = new ArrayList<String>();
    	resources.addAll(resource.getSources());
        invalidate();
        return this;
    }

    /**
     * Remove a script resource
     *
     * @param resource a script resource
     */
    public BScript removeResource(String resource) {
        if (resources==null) return this;
        resources.remove(resource);
        invalidate();
        return this;
    }

    /**
     * Get a list of script resource
     *
     * @return a list of script resources
     */
    public List<String> getResources() {
        return resources;
    }
    
    
    /**
     * Add a css resource (resources specified will automatically
     * create BCssResource delegates at render time)
     *
     * @param css a css resource
     */
    public BScript addCss(String css) {
        if (cssList==null) cssList = new ArrayList<String>();
        cssList.add(css);
        invalidate();
        return this;
    }
    
    /**
     * Remove a css resource
     *
     * @param resource a script resource
     */
    public BScript removeCss(String css) {
        if (cssList==null) return this;
        cssList.remove(css);
        invalidate();
        return this;
    }
    
    /**
     * Get a list of css resource
     *
     * @return a list of script resources
     */
    public List<String> getCssList() {
        return cssList;
    }
    
    /**
     * Set the rendering mode for this component.
     * @param rendering HEADER, BODY, INLINE
     * @return this
     */
    public BScript setRenderMode(RenderMode rendering) {
    	if (rendering == null) {
    		logger.warn("setRenderMode call with null argument. Probably a programming bug.");
    	}
    	else {
    		this.renderMode = rendering;
    	}
    	return this;
    }
    
    public RenderMode getRenderMode() {
    	return this.renderMode;
    }

    /**
     * Get a String representation of the component
     */
    public String toString() {
        return jsattr+"=\""+jscmd+"\"";
    }

}