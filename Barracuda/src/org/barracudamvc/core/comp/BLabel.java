/*
 * BLabel.java
 *
 * Created on 16 novembre 2004, 17:26
 * Copyright (c) 2004 mecadu.org
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
 */

package org.barracudamvc.core.comp;

import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BInput;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLLabelRenderer;
import org.barracudamvc.core.comp.renderer.xml.XMLLabelRenderer;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;


/**
 * BLabel is used for rendering a LABEL tag. It will use the label
 * node it is bound to, or will override the node with its default
 * label node when returned from a model.<br>
 * If a BLabel has a BInput child, it will then set the BInput's id
 * attribute to BLabel's forId value.<br>
 * <strong>Beware, 'id's should be unique in a HTML document !</strong>
 */
public class BLabel extends BComponent {//implements BCompoundComponent{

    //public vars
    protected static final Logger log = Logger.getLogger(BLabel.class.getName());
    
    //private vars
    protected String text = null;
    protected String forId = null;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BLabel() {}
    
    /**
     * Public constructor which creates the component, add
     * a BInput as child and sets forId to BInput's name.
     * It will also set the Binput id attr to thesame value.
     *
     * @param label the label string that backs this component
     * @param bi the BInput this label refers to
     */
    public BLabel(String label, BInput bi) {
        this(label, bi.getName());
        this.addChild(bi);
        bi.setAttr("id", this.forId);
    }

    /**
     * Public constructor which creates the component, and
     * sets label and forId.
     * It can be used if BLabel is returned to refer to a static
     * input field.
     * A BInput can also be added later, and it will have its id
     * set to forId.
     *
     * @param label the label string that backs this component
     * @param id the forId attribute of BLabel. Should refer to the id of an input
     */
     public BLabel(String text, String id) {
        if (text!=null) this.setText(text);
        if (id!=null) this.setForId(id);
     }
     
     /**
      * Overrides addChild in BContainer, to
      * check if the added child is a BInput.
      * If it is the case, the id attribute
      * of the child will be set to forId.
      */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType extends BContainer> DesiredType addChild(BContainer child) {
        super.addChild(child);
        if (child instanceof BInput) {
            ((BInput) child).setAttr("id", this.forId);
        }
        return (DesiredType) this;
    }

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BLabel.class, HTMLElement.class);
        installRendererFactory(rfHTML, BLabel.class, HTMLDocument.class);
     
        XMLRendererFactory rfXML = new XMLRendererFactory();
        installRendererFactory(rfXML, BLabel.class, Node.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLLabelRenderer();}
    }

    /**
     * XML RendererFactory
     */
    static class XMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new XMLLabelRenderer();}
    }


    //--------------- BComponent ---------------------------------
    /**
     * Set the label text for this particular component
     *
     * @param itext the text of this label
     */
    public BLabel setText(String itext) {
        text = itext;
        invalidate();
        return this;
    }
    
    /**
     * Get the text for this particular component
     *
     * @return the text for this particular label
     */
    public String getText() {
        return text;    
    }
    
    /**
     * Set the forId attribute for this label component
     *
     * @param id the forId attribute for this label component
     */
    public BLabel setForId(String id) {
        forId = id;
        invalidate();
        return this;
    }
    
    /**
     * Get the forId attribute for this label component
     *
     * @return forId the forId attribute for this label component
     */
    public String getForId() {
        return forId;    
    }
    
    /**
     * Get a String representation of the component
     */
    public String toString() {
        return "Label: "+text+" for Id: "+forId;
    }
}
