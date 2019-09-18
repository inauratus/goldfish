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
import org.barracudamvc.core.comp.renderer.html.HTMLTextAreaRenderer;
import org.barracudamvc.core.comp.renderer.html.HTMLTextRenderer;
import org.barracudamvc.core.comp.renderer.xml.XMLTextRenderer;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;


/**
 * BTextArea is used to manipulate the &lt;textarea&gt; element in a DOM
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
public class BTextArea extends BComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BTextArea.class.getName());
    
    //private vars
    protected String value = null;
    protected int rows = -1;
    protected int cols = -1;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BTextArea() {}
    
    /**
     * Public constructor which creates the component and sets 
     * the value. 
     *
     * <p>You should generally only use this constructor when returning
     * BText from a Model, as the model components will automatically
     * bind the component to a view for you. If you use this constructor
     * in some other situation, you should manually bind the component
     * to the proper view.
     *
     * @param text the text string that backs this component
     */
    public BTextArea(String name, String value) {
        this(name, value, -1, -1, null);
    }
    
    public BTextArea(String name, String value, int rows, int cols) {
        this(name, value, rows, cols, null);
    }
    
    /**
     * Public constructor which creates the component and
     * binds it to a view, and sets the value, rows and cols.
     *
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param text the text string that backs this component
     * @param view the View the component should be bound to
     */
    BTextArea(String name, String value, int rows, int cols, View view) {
        if (name!=null) this.setName(name);
        if (value!=null) this.setValue(value);
        this.rows=rows;
        this.cols=cols;
        if (view!=null) this.addView(view);
    }


    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BTextArea.class, HTMLElement.class);
        installRendererFactory(rfHTML, BTextArea.class, HTMLDocument.class);
        /*
        XMLRendererFactory rfXML = new XMLRendererFactory();
        installRendererFactory(rfXML, BTextArea.class, Node.class);
        */
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLTextAreaRenderer();}
    }

    /**
     * XML RendererFactory
     */
/*    static class XMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new XMLTextRenderer();}
    }
*/

    //--------------- BComponent ---------------------------------
    /**
     * Set the text for this particular component
     *
     * @param itext the text representation of this component
     */
    public BTextArea setValue(String ivalue) {
    	value = ivalue;
        invalidate();
        return this;
    }
    /**
     * Set the text for this particular component
     *
     * @param itext the text representation of this component
     */
    public BTextArea setRows(int irows) {
        rows = irows;
        invalidate();
        return this;
    }
    /**
     * Set the text for this particular component
     *
     * @param itext the text representation of this component
     */
    public BTextArea setCols(int icols) {
        cols = icols;
        invalidate();
        return this;
    }

    /**
     * Get the text for this particular component
     *
     * @return the text for this particular component
     */
    public String getValue() {
        return value;    
    }
    
    /**
     * Get the rows number
     * @return the rows number
     */
	public int getRows() {
		return rows;
	}
	
	/**
     * Get the cols number
	 * @return the cols number
	 */
	public int getCols() {
		return cols;
	}
}