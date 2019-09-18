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

import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLAnchorRenderer;
import org.barracudamvc.core.event.ControlEvent;
import org.w3c.dom.html.HTMLElement;


/**
 * BAnchor is intended to replace the most controversed BLink
 * component, that try be a BText as well as a BAction, and
 * doesn't succeed in this ubiquity...
 * Instead of heritage, component should rather use composition
 * via parent / child relationship.
 * 
 * So BAnchor will happily bind to an Anchor tag, and produce
 * an anchor as its default node.
 *  
 */
public class BAnchor extends BComponent {

    //public vars
    
    public static final String BLANK = "_blank"; //--Loads the link into a new blank window.
    public static final String PARENT = "_parent"; // --Loads the link into the immediate parent of the document the link is in.
    public static final String SELF = "_self"; //--Loads the link into the same window. (default)
    public static final String TOP = "_top"; //--Loads the link into the full body of the current window. 
    
    //private vars
    protected String text = null;
    protected String href = null;
    protected String target = null;
    protected boolean allowMarkupInText = false;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BAnchor() {}
    
    /**
     * Public constructor which creates the component and sets the text, 
     * and target values. This link will fire the default action event (unless
     * you manually specify an action). 
     *
     * @param itext the text string that backs this component
     */
    public BAnchor(String itext) {
        this(itext, (String)null);
    }

    /**
     * Public constructor which creates the component and sets the text 
     * and action values. 
     *
     * @param itext the text string that backs this component
     * @param iactionUrl the action url to be fired
     */
    public BAnchor(String itext, String iactionUrl) {
    	if (itext != null) setText(itext);
        if (iactionUrl != null) setHref(iactionUrl);
    }
    
    /**
     * Constructor that will delegate the rendering of the link
     * to a BAction based of the supplied controlEvent
     * @param itext
     * @param event
     */
    public BAnchor(String itext, ControlEvent event) {
    	this(itext);
    	this.addChild(new BAction(event));
    }

    /**
     * Constructor that will delegate the rendering of the link
     * to the supplied BAction
     * @param itext
     * @param action
     */
    public BAnchor(String itext, BAction action) {
    	this(itext);
    	this.addChild(action);
    }


    // --------------- Renderer -----------------------------------
    // FIXME Pour éviter les dépendances cycliques, on devrait mettre la factory dans le Renderer, 
    // ainsi que tout les autres BComp. du package
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BAnchor.class, HTMLElement.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {
            return new HTMLAnchorRenderer();
        }
    }

    /**
     * Set the text for this particular component
     *
     * @param itext the text representation of this component
     */
    public BAnchor setText(String itext) {
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
    public BAnchor setHref(String itarget) {
        href = itarget;
        invalidate();
        return this;
    }
    
    /**
     * Get the href for this particular component
     *
     * @return the target for this particular component
     */
    public String getHref() {
        return href;    
    }
    
    /**
     * Set the target for this particular component
     * You can use one of the constants BLANK, SELF, PARENT, TOP, or any target you want
     */
    public void setTarget(String itarget) {
    	target = itarget;
    }

    /**
     * Get the target for this particular component
     */
    public String getTarget() {
    	return target;
    }
    
    

    /**
     * Do we wish to allow markup in this text (defaults to false)
     *
     * @param val true if we wish to allow markup in the text
     */
    public BAnchor setAllowMarkupInText(boolean val) {
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

}
