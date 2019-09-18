/*
 * HTMLImageRenderer.java
 *
 * Created on 9 dec 2006
 * Copyright (c) 2006 mecadu.org
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
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLImageRenderer;
import org.apache.log4j.Logger;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

public class BImage extends BComponent {

    //public vars
    protected static final Logger log = Logger.getLogger(BImage.class.getName());
    
    //private vars
    protected String alt = "no alt text";
    protected String title = null;
    protected String src = null;
    protected String width = null;
    protected String height = null;
    

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BImage() {}
    
    /**
     * Public constructor which creates the component and
     * sets it's alt text and image file source (src).
     * Note that the title will also be set to ialt, to
     * allow strictly spec compliant engines like Gecko
     * display the alt text on mouse over (I assume
     * this is what most people expect...)
     *
     * @param ialt the alt text string of this image
     * @param isrc the src string of this image
     */
    public BImage(String ialt, String isrc) {
        
    	setAlt(ialt);
        setSrc(isrc);
        
        // Let strict spec compliant engines (ie Gecko)
        // display alt text on mouse over
        setTitle(ialt);
    }
    
    /**
     * Public constructor which creates the component and
     * sets it's alt text, the title and image file source (src).
     * Note that the title will be displayed on mouse over
     * by strictly spec compliant engines like Gecko,
     * while the alt text will be displayed by IE.
     *
     * @param ialt the alt text string of this image
     * @param ititle the title text string of this image
     * @param isrc the src string of this image
     */
    public BImage(String ialt, String ititle, String isrc) {
        setAlt(ialt);
        setTitle(ititle);
        setSrc(isrc);
    }
    
    /**
     * Public constructor which creates the component and
     * sets it's alt text and image file source (src), along with
     * the desired width and height of the image.
     * Note that the title will also be set to ialt, to
     * allow strictly spec compliant engines like Gecko
     * display the alt text on mouse over (I assume
     * this is what most people expect...)
     *
     * @param ialt the alt text string of this image
     * @param isrc the src string of this image
     * @param iheight the height string of the image (might contain %)
     * @param iwidth the width string of the image (might contain %)
     */
    public BImage(String ialt, String isrc, String iheight, String iwidth) {
        setAlt(ialt);
        setSrc(isrc);
        setHeight(iheight);
        setWidth(iwidth);
        
        // Let strict spec compliant engines (ie Gecko)
        // display alt text on mouse over
        setTitle(ialt);
    }
    
    /**
     * Public constructor which creates the component and
     * sets it's alt text, title and image file source (src), along with
     * the desired width and height of the image.
     * Note that the title will be displayed on mouse over
     * by strictly spec compliant engines like Gecko,
     * while the alt text will be displayed by IE.
     *
     * @param ialt the alt text string of this image
     * @param ititle the title text string of this image
     * @param isrc the src string of this image
     * @param iheight the height string of the image (might contain %)
     * @param iwidth the width string of the image (might contain %)
     */
    public BImage(String ialt, String ititle, String iurl, String iheight, String iwidth) {
        setAlt(ialt);
        setTitle(ititle);
        setSrc(iurl);
        setHeight(iheight);
        setWidth(iwidth);
    }
    


    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BImage.class, HTMLElement.class);
        installRendererFactory(rfHTML, BImage.class, HTMLDocument.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {
        public Renderer getInstance() {return new HTMLImageRenderer();}
    }



    //--------------- BImage --------------------------------------
    /**
     * Set the alt text for this particular component
     *
     * @param ialt the alt text of this image
     */
    public BImage setAlt(String ialt) {
        alt = ialt;
        invalidate();
        return this;
    }
    
    /**
     * Get the alt text for this particular component
     *
     * @return the alt text for this particular component
     */
    public String getAlt() {
        return alt;    
    }
    
    /**
     * Set the title for this particular component
     *
     * @param ititle the title of this component
     */
    public BImage setTitle(String ititle) {
        title = ititle;
        invalidate();
        return this;
    }
    
    /**
     * Get the title for this particular component
     *
     * @return the title for this particular component
     */
    public String getTitle() {
        return title;    
    }
    
    /**
     * Set the source for this particular component
     *
     * @param isrc the src file string
     */
    public BImage setSrc(String isrc) {
        src = isrc;
        invalidate();
        return this;
    }
    
    /**
     * Get the source for this particular component
     *
     * @return the src attribute for this particular component
     */
    public String getSrc() {
        return src;    
    }
    
    /**
     * Set the height for this particular component
     *
     * @param iheight the height og the image
     */
    public BImage setHeight(String iheight) {
        height = iheight;
        invalidate();
        return this;
    }
    
    /**
     * Get the height for this particular component
     *
     * @return the height for this image
     */
    public String getHeight() {
        return height;    
    }
    
    /**
     * Set the width for this particular component
     *
     * @param iwidth the width of the image
     */
    public BImage setWidth(String iwidth) {
        width = iwidth;
        invalidate();
        return this;
    }
    
    /**
     * Get the width for this particular component
     *
     * @return the width for this image
     */
    public String getWidth() {
        return width;    
    }

    /**
     * if has vc, but no views: render as an &lt;a&gt; link, otherwise use
     * super.toString(ViewContext)
     * 
     * @see super#toString(ViewContext)
     */
    public String toString(ViewContext vc) {
        if (vc != null && !hasViews()) {
           return this.getAlt();
        } else {
            return super.toString(vc);
        }
    }

}