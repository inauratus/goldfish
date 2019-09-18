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
 * $Id: BToggleButton.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLToggleRenderer;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * BToggleButton is used to render Radio or Checkbox buttons in 
 * a DOM template.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a 
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()), 
 * then you MUST BIND IT TO A VIEW before rendering, or an error will 
 * be generated.
 */
public class BToggleButton extends BInput {

    //public vars
    protected static final Logger logger = Logger.getLogger(BToggleButton.class.getName());

    //private vars
    protected boolean selected = false;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BToggleButton() {}
    
    /**
     * Public constructor which creates the component and
     * binds it to a specific view
     *
     * @param view the View the component should be bound to
     */
//    public BToggleButton(View iview) {
//        this(null, null, null, false, iview, null);
//    }
    
    /**
     * Public constructor which creates the component and
     * sets the initial data. 
     *
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param itype BInput.RADIO, BInput.CHECKBOX, or null (indicating 
     *         don't render this attribute)
     * @param iname the name of the button, or null (indicating 
     *         don't render this attribute)
     * @param ivalue the value of the button, or null (indicating 
     *         don't render this attribute)
     * @param iselected true if the button should be selected
     */
    public BToggleButton(String itype, String iname, String ivalue, boolean iselected) {
        this (itype, iname, ivalue, iselected, null, null);
    }

    /**
     * Public constructor which creates the component and
     * sets the initial data. The component is also
     * bound to the specified view.
     *
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param type BInput.RADIO, BInput.CHECKBOX, or null (indicating 
     *         don't render this attribute)
     * @param name the name of the button, or null (indicating 
     *         don't render this attribute)
     * @param value the value of the button, or null (indicating 
     *         don't render this attribute)
     * @param selected true if the button should be selected
     * @param view the View the component should be bound to
     * @param dvc the default ViewContext (opt--its presence allows the 
     *         component to be rendered as markup in toString())
     */
    BToggleButton(String itype, String iname, String ivalue, boolean iselected, View iview, ViewContext idvc) {
        if (idvc!=null) setDefaultViewContext(idvc);
        if (itype!=null) setType(itype);
        if (iname!=null) setName(iname);
        if (ivalue!=null) setValue(ivalue);
        setSelected(iselected);
        if (iview!=null) setView(iview);
    }
    



    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BToggleButton.class, HTMLElement.class);
        installRendererFactory(rfHTML, BToggleButton.class, HTMLDocument.class);

    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLToggleRenderer();}
    }




    //--------------- BToggleButton ------------------------------
    /**
     * Set the input type (RADIO, or CHECKBOX). If this value remains null, 
     * the type will default to whatever is specified in the underlying 
     * markup. If you set this value, then the type will be overridden 
     * in all the views associated with this component.
     *
     * @param itype an string value representing the size.
     */
    public BInput setType(String itype) {   //note that we can't return a type of BToggleButton, since the method is defined originally in BInput :-(
        itype = itype.toLowerCase();
        if (itype!=null && !itype.equals(RADIO) && !itype.equals(CHECKBOX)) {
            itype = CHECKBOX;
        }
        super.setType(itype);
        return this;
    }
    
    /**
     * Specify whether or not the button is selected.
     *
     * @param iselected true if the button is selected
     */
    public BToggleButton setSelected(boolean iselected) {
        selected = iselected;
        invalidate();
        return this;
    }
    
    /**
     * Return true if the button is selected.
     *
     * @return true if the button is selected.
     */
    public boolean isSelected() {
        return selected;
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
}