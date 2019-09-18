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
 * $Id: HTMLScriptRenderer.java 267 2014-04-09 06:12:44Z alci $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BScript;
import org.barracudamvc.core.comp.BScript.RenderMode;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.DOMComponentRenderer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLScriptElement;

/**
 * This class handles the default rendering script references into an HTML view.
 * 
 * 	note that we always want to render this script...this way if the client
 *   		has scripting disabled now, but re-enables it later, we will know about it
 *  		the next time around. If they don't have scripting enabled this can't hurt.
 */
public class HTMLScriptRenderer extends DOMComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLScriptRenderer.class.getName());

    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {
        return null; // returning null will bind the BScript to the parent view
    }

    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //unlike other components, this one really doesn't care what we're bound
        //to...we do need to make sure the component is a script component
        if (!(comp instanceof BScript))
            throw new NoSuitableRendererException("This renderer can only render BScript components");

        BScript bsComp = (BScript) comp;

        // if a BTemplate is ongoing and we want to render at the end of the body, differ rendering until after
        // BTemplate has finished manipulating the Document
        if (vc.getOngoingTemplate() != null && bsComp.getRenderMode().equals(RenderMode.BODY)) {
            vc.getOngoingTemplate().differComponent(comp);
            return;
        }

        //show what we're doing
        showNodeInterfaces(view, logger);

        //now allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        Node node = view.getNode();
        Node child = null;
        Document doc = node.getOwnerDocument();
        Element elRoot = doc.getDocumentElement();
        boolean exists = false;
        String newScr = null;

        //sanity checks, shortcircuit rendering if no src to set
        String jscmd = bsComp.getCmd();
        if (jscmd == null && bsComp.getResources() == null || !(node instanceof Element)) {
            logger.debug("Null jscmd, or bound node not an Element, skipping rendering.");
            return;
        }

        switch (bsComp.getRenderMode()) {
            case INLINE:
                //sanity checks, shortcircuit rendering if no jsattr
                String jsattr = bsComp.getJSAttr();
                if (jsattr == null || !(node instanceof Element)) {
                    logger.debug("Null jsattr, jscmd, or bound node not an Element, skipping rendering.");
                    return;
                }

                int mode = bsComp.getMode();
                Element el = (Element) node;

            //note that we always want to render this script...this way if the client
                //has scripting disabled now, but re-enables it later, we will know about it
                //the next time around. If they don't have scripting enabled this can't hurt.
                //render the component
                if (!jscmd.endsWith(";"))
                    jscmd = jscmd + "; ";
                String cur_attr = el.getAttribute(jsattr);
                if (cur_attr == null)
                    mode = BScript.REPLACE;
                //...append
                if (mode == BScript.APPEND) {
                    if (!cur_attr.endsWith(";"))
                        cur_attr = cur_attr + "; ";
                    el.setAttribute(jsattr, cur_attr + jscmd);
                    //...prepend
                } else if (mode == BScript.PREPEND) {
                    el.setAttribute(jsattr, jscmd + cur_attr);
                    //...replace
                } else {
                    el.setAttribute(jsattr, jscmd);
                }
                break;

            case HEADER:
                Element elHead = findHead(elRoot);

                //else, create one and add it in the document
                if (elHead == null) {
                    elHead = doc.createElement("HEAD");
                    elRoot = doc.getDocumentElement();
                    elRoot.insertBefore(elHead, elRoot.getFirstChild());
                }

    		//now see if we have script elements that match already in place
                //(if so, we don't need to do anything)
                child = elHead.getFirstChild();
                exists = false;
                newScr = "/**/\n " + jscmd + " \n//";
                while (child != null) {
                    if (child instanceof HTMLScriptElement) {
                        HTMLScriptElement scel = (HTMLScriptElement) child;
                        // see if the src already exists
                        if (newScr.equals(scel.getText())) {
                            exists = true;
                        }
                    }
                    child = child.getNextSibling();
                }

                if (!exists) {
                    HTMLScriptElement elScript = (HTMLScriptElement) doc.createElement("SCRIPT");
                    elHead.appendChild(elScript);

                    elScript.setType("text/javascript");
                    elScript.setText("/*");
                    CDATASection cdata = (CDATASection) doc.createCDATASection("*/\n " + jscmd + " \n//");
                    elScript.appendChild(cdata);
                }
                break;

            case BODY:
                Element elBody = findBody(elRoot);

                //else, create one and add it in the document
                if (elBody == null) {
                    elBody = doc.createElement("BODY");
                    elRoot = doc.getDocumentElement();
                    elRoot.insertBefore(elBody, elRoot.getFirstChild());
                }

    		//now see if we have script elements that match already in place
                //(if so, we don't need to do anything)
                child = elBody.getFirstChild();
                exists = false;
                newScr = "/**/\n " + jscmd + " \n//";
                while (child != null) {
                    if (child instanceof HTMLScriptElement) {
                        HTMLScriptElement scel = (HTMLScriptElement) child;
                        // see if the src already exists
                        if (newScr.equals(scel.getText())) {
                            exists = true;
                        }
                    }
                    child = child.getNextSibling();
                }

                if (!exists) {
                    HTMLScriptElement elScript = (HTMLScriptElement) doc.createElement("SCRIPT");
                    elBody.appendChild(elScript);

                    elScript.setType("text/javascript");
                    elScript.setText("/*");
                    CDATASection cdata = (CDATASection) doc.createCDATASection("*/\n " + jscmd + " \n//");
                    elScript.appendChild(cdata);
                }
                break;
        }
    }

    // Search for a HTMLHeadElement
    private Element findHead(Element elRoot) {
        Element elHead = null;
        Node child = elRoot.getFirstChild();
        while (child != null) {
            if (child instanceof HTMLHeadElement) {
                elHead = (HTMLHeadElement) child;
                break;
            }
            child = child.getNextSibling();
        }
        return elHead;
    }

    // Search for a HTMLBodyElement
    private Element findBody(Element elRoot) {
        Element elBody = null;
        Node child = elRoot.getFirstChild();
        while (child != null) {
            if (child instanceof HTMLBodyElement) {
                elBody = (HTMLBodyElement) child;
                break;
            }
            child = child.getNextSibling();
        }
        return elBody;
    }
}
