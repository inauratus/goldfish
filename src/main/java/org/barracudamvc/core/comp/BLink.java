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
 * $Id: BLink.java 150 2006-09-28 01:59:49Z jkjome $
 */
package org.barracudamvc.core.comp;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLLinkRenderer;
import org.barracudamvc.core.event.ControlEvent;
import org.w3c.dom.html.HTMLElement;


/**
 * BLink is used to manipulate any element in a DOM template that
 * is capable of generating a URL request. In the case of HTML, this 
 * would typically include &lt;a&gt;, &lt;input&gt;, and &lt;button&gt; 
 * elements.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a 
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()), 
 * then you MUST BIND IT TO A VIEW before rendering, or an error will 
 * be generated. 
 *
 * <p>Because BLink may often be used for inling, it includes constructors 
 * that conveniently allow you to specify the a ViewContext (ie. instead of
 * an actual View)
 */
public class BLink extends BAction {

    //public vars
    protected static final Logger logger = Logger.getLogger(BLink.class.getName());
    
    //private vars
    protected String text = null;
    protected String target = null;
    protected boolean allowMarkupInText = false;    //csc_092701.1

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BLink() {}
    
    /**
     * Public constructor which creates the component and sets the text, 
     * and target values. This link will fire the default action event (unless
     * you manually specify an action). 
     *
     * @param itext the text string that backs this component
     */
    public BLink(String itext) {
        if (itext!=null) setText(itext);
    }

    /**
     * Public constructor which creates the component and sets the text 
     * and action values. 
     *
     * @param itext the text string that backs this component
     * @param iactionUrl the action url to be fired (opt--if null, the default
     *      action specified in the template will be fired)
     */
    public BLink(String itext, String iactionUrl) {
        this(itext, iactionUrl, null);
    }

    /**
     * Public constructor which creates the component and sets the text 
     * and action values. This constructor takes a ViewContext object which 
     * will be used to create a default view for the component (ie. use this
     * constructor for inling a BLink)
     *
     * @param itext the text string that backs this component
     * @param iactionUrl the action url to be fired (opt--if null, the default
     *      action specified in the template will be fired)
     * @param idvc the default ViewContext (opt--its presence allows the 
     *         component to be rendered as markup in toString())
     */
    public BLink(String itext, String iactionUrl, ViewContext idvc) {
        if (idvc!=null) setDefaultViewContext(idvc);
        if (itext!=null) setText(itext);
        if (iactionUrl!=null) setAction(iactionUrl);
    }

    /**
     * Public constructor which creates the component and sets the text 
     * and action values. 
     *
     * @param itext the text string that backs this component
     * @param iactionEvent the action event to be fired (opt--if null, the default
     *        ActionEvent will be fired)
     */
    public BLink(String itext, ControlEvent iactionEvent) {
        this(itext, iactionEvent, null);
    }
    
    /**
     * Public constructor which creates the component and sets the text 
     * and action values. This constructor takes a ViewContext object which 
     * will be used to create a default view for the component (ie. use this
     * constructor for inling a BLink)
     *
     * @param itext the text string that backs this component
     * @param iactionEvent the action event to be fired (opt--if null, the default
     *        ActionEvent will be fired)
     * @param idvc the default ViewContext (opt--its presence allows the 
     *         component to be rendered as markup in toString())
     */
    public BLink(String itext, ControlEvent iactionEvent, ViewContext idvc) {
        if (idvc!=null) setDefaultViewContext(idvc);
        if (itext!=null) setText(itext);
        if (iactionEvent!=null) setAction(iactionEvent);
    }



    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BLink.class, HTMLElement.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLLinkRenderer();}
    }



    //--------------- BLink --------------------------------------
    /**
     * Set the text for this particular component
     *
     * @param itext the text representation of this component
     */
    public BLink setText(String itext) {
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
     * Set the target for this particular component
     *
     * @param itarget the ext representation of the target
     */
    public BLink setTarget(String itarget) {
        target = itarget;
        invalidate();
        return this;
    }
    
    /**
     * Get the target for this particular component
     *
     * @return the target for this particular component
     */
    public String getTarget() {
        return target;    
    }

    /**
     * Do we wish to allow markup in this text (defaults to false)
     *
     * @param val true if we wish to allow markup in the text
     */
    public BLink setAllowMarkupInText(boolean val) {
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

    /**
     * if has vc, but no views: render as an &lt;a&gt; link, otherwise use
     * super.toString(ViewContext)
     * 
     * @see super#toString(ViewContext)
     */
    public String toString(ViewContext vc) {
        //csc_122205_1 - added (jrk_122505_01 moved from AbstractBComponent to here)
        //if the component HAS a view context, but DOESN'T have any views, then
        //render as a link (this shouldn't break anything, since when TemplateHelper
        //gets a component back it creates a view for it. Basically, this allows
        //us to inline BLinks even when we are not using DOMs. A _better_ way of 
        //doing this would be to modify the renderer to work w/ out a dom, but
        //that's probably easier said then done. So for now, we'll go w/ this.
        if (vc != null && !hasViews()) {
            if (this.isEnabled()) return "<a href=\""+this.getAction(vc)+"\">"+this.getText()+"</a>";
            else return this.getText();
        } else {
            return super.toString(vc);
        }
    }

}
