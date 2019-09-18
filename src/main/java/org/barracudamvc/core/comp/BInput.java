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
 * $Id: BInput.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.List;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLInputRenderer;
import org.barracudamvc.core.event.ListenerFactory;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * BInput is used to manipulate the &lt;input&gt; element in a DOM
 * template.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a 
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()), 
 * then you MUST BIND IT TO A VIEW before rendering, or an error will 
 * be generated.
 */
public class BInput extends BComponent {

    public static final String TEXT = "text";
    public static final String PASSWORD = "password";
    public static final String SUBMIT = "submit";
    public static final String RESET = "reset";
    public static final String FILE = "file";
    public static final String HIDDEN = "hidden";
    public static final String IMAGE = "image";
    public static final String BUTTON = "button";
    public static final String RADIO = "radio";
    public static final String CHECKBOX = "checkbox";
    //private vars
    protected List<ListenerFactory> listeners = null;
    protected String type = null;
    protected String value = null;
    protected boolean disableBackButton = false;
    protected BAction baction = null;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BInput() {
    }

    /**
     * Public constructor which creates the component and
     * sets the initial data. 
     *
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param itype valid input type. May be null (indicating don't render 
     *        this attribute)
     * @param iname the name of the button, or null (indicating 
     *         don't render this attribute)
     * @param ivalue a String value for the input. May be null (indicating 
     *        don't render this attribute)
     */
    public BInput(String itype, String iname, String ivalue) {
        this(itype, iname, ivalue, null, null);
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
     * @param type valid input type. May be null (indicating don't render 
     *        this attribute)
     * @param name the name of the button, or null (indicating 
     *         don't render this attribute)
     * @param value a String value for the input. May be null (indicating 
     *        don't render this attribute)
     * @param view the View the component should be bound to
     * @param dvc the default ViewContext (opt--its presence allows the 
     *         component to be rendered as markup in toString())
     */
    BInput(String itype, String iname, String ivalue, View iview, ViewContext idvc) {
        if (idvc != null)
            setDefaultViewContext(idvc);
        if (itype != null)
            setType(itype);
        if (iname != null)
            setName(iname);
        if (ivalue != null)
            setValue(ivalue);
        if (iview != null)
            setView(iview);
    }

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BInput.class, HTMLElement.class);
        installRendererFactory(rfHTML, BInput.class, HTMLDocument.class);

    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {

        @Override
        public Renderer getInstance() {
            return new HTMLInputRenderer();
        }
    }

    //--------------- BInput --------------------------------------
    /**
     * Set the input type (TEXT, PASSWORD, SUBMIT, RESET, FILE, HIDDEN,
     * IMAGE, BUTTON, RADIO, or CHECKBOX). If this value remains null, 
     * the type will default to whatever is specified in the underlying 
     * markup. If you set this value, then the type will be overridden 
     * in all the views associated with this component.
     *
     * @param itype an string value representing the size.
     */
    public BInput setType(String itype) {
        itype = itype.toLowerCase();
        if (itype != null && !itype.equals(TEXT) && !itype.equals(PASSWORD)
                && !itype.equals(SUBMIT) && !itype.equals(RESET)
                && !itype.equals(FILE) && !itype.equals(HIDDEN)
                && !itype.equals(IMAGE) && !itype.equals(BUTTON)
                && !itype.equals(RADIO) && !itype.equals(CHECKBOX)) {
            itype = TEXT;
        }
        type = itype;
        invalidate();
        return this;
    }

    /**
     * Get the type of input. May return a null if the type has not been 
     * manually specified. 
     *
     * @return the component type
     */
    public String getType() {
        return type;
    }

    //csc_031003.1 - added
    /**
     * Convenience mechanism to set the value for this input using an Object.
     *
     * @param ivalue the value object
     */
    public BInput setValue(Object ivalue) {
        setValue(ivalue != null ? ivalue.toString() : (String) null);
        return this;
    }

    /**
     * Set the value for this input. In most cases, the value object will 
     * simply be rendered as the 'value' attribute. If this value remains null, 
     * the type will default to whatever is specified in the underlying 
     * markup. If you set this value, then the type will be overridden in all 
     * the views associated with this component.
     *
     * @param ivalue the value object
     */
    public BInput setValue(String ivalue) {
        value = ivalue;
        invalidate();
        return this;
    }

    /**
     * Get the value for this input. May return a null if the type has not been 
     * manually specified. 
     *
     * @return the value for this input
     */
    public String getValue() {
        return value;
    }

    //csc_041403.2 - added
    /**
     * Specify an action for this component (rather than adding an even listener)
     *
     * @param ibaction the action to be fired when the BSelect is activated on the client
     */
    public BInput setAction(BAction ibaction) {
        baction = ibaction;
        return this;
    }

    //csc_041403.2 - added
    /**
     * Returns the action associated with this component (if any)
     * 
     * @return the action associated with this component (if any)
     */
    public BAction getAction() {
        return baction;
    }

    /**
     * Add an event listener to this component. 
     *
     * @param lf the event listener to be added
     */
    public BInput addEventListener(ListenerFactory lf) {
        addEventListener(lf, false);
        return this;
    }

    /**
     * Add an event listener to this component. 
     *
     * @param lf the event listener to be added
     * @param idisableBackButton true if the back button should be 
     *      disabled when the action occurs
     */
    public BInput addEventListener(ListenerFactory lf, boolean idisableBackButton) {
        if (lf == null)
            return this;
        disableBackButton = idisableBackButton;
        if (listeners == null)
            listeners = new ArrayList<ListenerFactory>(5);
        listeners.add(lf);
        invalidate();
        return this;
    }

    /**
     * Remove an event listener from this component
     *
     * @param lf the event listener to be removed
     */
    public BInput removeEventListener(ListenerFactory lf) {
        if (lf == null)
            return this;
        if (listeners == null)
            return this;
        listeners.remove(lf);
        invalidate();
        return this;
    }

    /**
     * Here in the pre-render phase we actually add
     * BAction step children for any of the listeners
     * that might have been added to this component
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void preRender(ViewContext vc, int depth) {
        //add in our BAction as a step child
        if (baction != null)
            this.addStepChild(baction, true);

        //we want to actually add proxy components as step children
        //for any event listeners we might have to support
        if (listeners != null) {
            for (ListenerFactory lf : listeners) {
                BAction baComp = new BAction();
                baComp.setDisableBackButton(disableBackButton);
                baComp.addEventListener(lf);
                this.addStepChild(baComp, true);
            }
        }
    }
}