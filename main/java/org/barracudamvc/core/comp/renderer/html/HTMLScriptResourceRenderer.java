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
 * $Id: HTMLScriptResourceRenderer.java 224 2009-05-18 16:42:53Z alci $
 */
package org.barracudamvc.core.comp.renderer.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BScriptResource;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.DOMComponentRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLScriptElement;

/**
 * This class handles the default rendering script references into an HTML view.
 */
public class HTMLScriptResourceRenderer extends DOMComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLScriptResourceRenderer.class.getName());


    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //unlike other components, this one really doesn't care what we're bound
        //to...we do need to make sure the component is a script component
        if (!(comp instanceof BScriptResource)) throw new NoSuitableRendererException("This renderer can only render BScriptResource components");        

        //show what we're doing
        showNodeInterfaces(view, logger);
        
        //now allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        BScriptResource bsr = (BScriptResource) comp;
        // fro_071508_1_begin
        List<String> sources = bsr.getSources();
        
        //Shortcircuit rendering if no src to set
        if (sources.size() < 1) {
        	logger.warn("Null src, skipping rendering.");
        	return;
        }
        // fro_071508_1_end
        Node node = view.getNode();
        
        //find the root
        Document doc = node.getOwnerDocument();
        Element elRoot = doc.getDocumentElement();

        //now see if there is a head element
        Element elHead = null;
        Node child = elRoot.getFirstChild();
        while (child!=null) {
            if (child instanceof HTMLHeadElement) {
                elHead = (HTMLHeadElement) child;
                break;
            }
            child = child.getNextSibling();
        }

        //if not, create one and add it in
        if (elHead==null) {
            elHead = doc.createElement("HEAD");
            elRoot.insertBefore(elHead, elRoot.getFirstChild());
        }

        //now see if we have script elements that match already in place
        //(if so, we don't need to do anything)
        //Also retain the last script element with a source attribute. Will insert after it.
        // fro_071508_1_begin
        HTMLScriptElement elScript = null;
        child = elHead.getFirstChild();
        HTMLScriptElement lastSrc = null;
        HTMLScriptElement firstNonSrc = null; // first script element that has no src
        List<String> srcList=new ArrayList<String>(sources);
        
        while (child!=null && !srcList.isEmpty()) {
            if (child instanceof HTMLScriptElement) {
                HTMLScriptElement scel = (HTMLScriptElement) child;
                if (scel.getSrc() != null && !"".equals(scel.getSrc())) {
                	lastSrc = scel;
                }
                else {
                	if (firstNonSrc == null)
                		firstNonSrc = scel;
                }
                // see if the src already exists
                for (String source : sources){
                	if (source.equals(scel.getSrc())) {
                		elScript = scel;
                		srcList.remove(source);
                		break;
                	}
                }
            }
            child = child.getNextSibling();
        }

        //if we don't, create one for each src in sources
//        if (elScript==null) {
        	for (String src : srcList) {
	            elScript = (HTMLScriptElement) doc.createElement("SCRIPT");
	            // fro_022308_1 make sure script resources are inserted before scripts themselves
	            if (lastSrc != null && lastSrc.getNextSibling() != null)
            		elHead.insertBefore(elScript, lastSrc.getNextSibling());
	            else if (firstNonSrc != null)
	            	elHead.insertBefore(elScript, firstNonSrc);
	            else
	            	elHead.appendChild(elScript);
	            elScript.setType("text/javascript");
	            elScript.setSrc(src);
	            lastSrc = elScript;
        	}
//        }
        // fro_071508_1_end
    }

}