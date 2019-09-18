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
 * $Id: HTMLListRenderer.java 244 2011-11-03 09:26:32Z alci $
 */
package org.barracudamvc.core.comp.renderer.html;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.Attrs;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BCompoundComponent;
import org.barracudamvc.core.comp.BList;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.InvalidNodeException;
import org.barracudamvc.core.comp.ListModel;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.view.FormatType;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDListElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLLIElement;
import org.w3c.dom.html.HTMLOListElement;
import org.w3c.dom.html.HTMLOptGroupElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableColElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLUListElement;

/**
 * This class handles the default rendering of a list in an HTML view.
 */
public class HTMLListRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLListRenderer.class.getName());

    // will create an UL if the templateNode is not any of DL, OL, OptGroup, Select, TableCol, TableRow, UL
    @Override
	public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc)
            throws UnsupportedFormatException {

    	Node defaultNode = null;
    	
    	Node templateNode = vc.getTemplateNode();
    	if (templateNode instanceof HTMLDListElement || templateNode instanceof HTMLOListElement || templateNode instanceof HTMLOptGroupElement
    		|| templateNode instanceof HTMLSelectElement || templateNode instanceof HTMLTableColElement || templateNode instanceof HTMLTableRowElement
    		|| templateNode instanceof HTMLUListElement) {
    		
            defaultNode = super.createDefaultNode(doc, comp, vc);
            
        } else {
        	
        	defaultNode = doc.createElement("UL");
        	
        }
        return defaultNode;
    }
    
    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a list component
        if (!(comp instanceof BList)) throw new NoSuitableRendererException("This renderer can only render BList components; comp is of type:"+comp.getClass().getName());

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        //HTMLElement Interface
        //---------------------
        //Supported Elements:

        //defs: fontstyle = (TT|I|B|BIG|SMALL)
        //defs: phrase = (EM|STRONG|DFN|CODE|SAMP|KBD|VAR|CITE|ABBR|ACRONYM)
        //defs: special = (A|IMG|OBJECT|BR|SCRIPT|MAP|Q|SUB|SUP|SPAN|BDO)
        //defs: formctrl = (INPUT|SELECT|TEXTAREA|LABEL|BUTTON)
        //defs: block = (P|heading|list|preformatted|DL|DIV|NOSCRIPT|BLOCKQUOTE|
        //               FORM|HR|TABLE|FIELDSET|ADDRESS)
        //defs: inline = (pcdata|fontstyle|phrase|special|formctrl)
        //defs: flow = (block|inline)

        //list structures
        //..HTMLDListElement <DL>                 (DT|DD)
        //..HTMLFieldSetElement <FIELDSET>         (pcdata|LEGEND|flow)
        //..HTMLFrameSetElement <FRAMESET>         ((FRAMESET|FRAME)+ & NOFRAMES?) 
        //..HTMLMapElement <MAP>                 (block|AREA)
        //..HTMLOList <OL>                         (LI)
        //..HTMLUList <UL>                         (LI)
        //..HTMLOptGroupElement <OPTGROUP>         (OPTION)
        //..HTMLSelectElement <SELECT>             (OPTGROUP|OPTION)
        //..HTMLTableColElement <COLGROUP>         (COL). 
        //..HTMLTableElement <TABLE>             (CAPTION?, (COL*|COLGROUP*), THEAD?, 
        //                                       TFOOT?, TBODY+)
        //..HTMLTableSectionElement <THEAD>        (TR) 
        //                            <TFOOT>        (TR) 
        //                            <TBODY>        (TR)
        //..HTMLTableRowElement <TR>            (TH|TD)

        //text containers        
        //..HTMLHeadingElement <H1>..<H6>         (inline)
        //..HTMLLabelElement <LABEL>             (inline)
        //..HTMLLegendElement <LEGEND>             (inline)
        //..HTMLLIElement <LI>                     (flow)
        //..HTMLModElement <INS>,<DEL>             (flow)
        //..HTMLObjectElement <OBJECT>             (PARAM|flow)
        //..HTMLOptionElement <OPTION>             (pcdata)
        //..HTMLParagraphElement <P>             (inline)
        //..HTMLPreElement <PRE>                 (inline, excl.IMG|OBJECT|BIG|SMALL|SUB|
        //                                                 SUP)
        //..HTMLQuoteElement <Q>                 (inline)
        //..HTMLTableCaptionElement <CAPTION>     (inline)
        //..HTMLTableCellElement <TH>,<TD>         (flow)
        //..HTMLTextAreaElement <TEXTAREA>         (pcdata)
        
        //
        //Unsupported Elements:
        //..HTMLAnchorElement, HTMLAppletElement (deprec in HTML 4.0), HTMLAreaElement,
        //....HTMLBaseElement, HTMLBaseFontElement, HTMLBodyElement, HTMLBRElement, 
        //....HTMLButtonElement, HTMLDirectoryElement (deprec in HTML 4.0),
        //....HTMLDivElement, HTMLFontElement, HTMLFormElement, HTMLFrameElement, 
        //....HTMLHeadElement, HTMLIFrameElement, HTMLImageElement, 
        //....HTMLIsIndexElement (deprec in HTML 4.0), HTMLInputElement, HTMLHRElement, 
        //....HTMLHtmlElement, HTMLLinkElement, HTMLMenuElement (deprec in HTML 4.0), 
        //....HTMLMetaElement, HTMLParamElement, HTMLScriptElement, HTMLTitleElement, 
        //....HTMLStyleElement,
        BList blist = (BList) comp;
        ListModel model = blist.getModel();
        Node node = view.getNode();
        Node origTemplateNode = vc.getTemplateNode();
        
        if (node instanceof HTMLElement) {
            try {
                if (logger.isInfoEnabled()) logger.info("Rendering list component...");

                //start by removing all children from the node
                while (node.hasChildNodes()) {
//System.out.println("removing all child nodes...");
                    node.removeChild(node.getFirstChild());
                }

                //give the model a chance to initialize
                model.setViewContext(vc);
                model.resetModel();
                vc.putState(ViewContext.TEMPLATE_NODE, node);

                //now get all the items from the model and add them as children
//csc_012605_1 - this is not used
//csc_012605_1                ElementFactory ef = view.getElementFactory();
//System.out.println("model size:"+model.getSize());
                for (int i=0, max=model.getSize(); i<max; i++) {                                
//System.out.println("...on model item:"+i);
                    //get the next item
                    Object item = model.getItemAt(i);

                    //if the item is actually a List
                    if (item instanceof List) {
                        Iterator it = ((List) item).iterator();
                        while (it.hasNext()) {
                            addItemToList(blist, node, vc, it.next());
                        }
                        
                    //if the item is actually an array of objects
                    } else if (item instanceof Object[]) {
                        Object itemArr[] = (Object[]) item;
                        for (int j=0,jmax=itemArr.length; j<jmax; j++) {
                            addItemToList(blist, node, vc, itemArr[j]);
                        }
                                            
                    //if its just a plain old item
                    } else {
                        addItemToList(blist, node, vc, item);
                    }
                }
            } catch (DOMException e) {
                if (logger.isInfoEnabled()) logger.info("Node ["+node+"] does not support children...rendering list as text", e);
            
                //convert the model to a string 
                String s = model.toString();
                
                //now try binding the string to the node via a BText component
                BText textComp = new BText(s);
                textComp.setView(new DefaultView(node));

                //now add the comp as a step child
                blist.addStepChild(textComp);
            } finally {
                vc.putState(ViewContext.TEMPLATE_NODE, origTemplateNode);
                model.setViewContext(null);
            }
                
        } else {
            String errmsg = "Node does not implement HTMLElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }

    private void addItemToList(BList blist, Node node, ViewContext vc, Object item) throws RenderException {
        if (item == null) {
            logger.warn("Ignoring attempt to add null item to the list");
            return;
        }

        //show what the item implements                    
        showNodeInterfaces(item, logger);
        
        //get it back into the DOM
        //..BComponent
        if (item instanceof BComponent) {
            //add in the Node for each view
            if (logger.isDebugEnabled()) logger.debug("Getting next BComponent item: "+item+"...");
            BComponent wcomp = (BComponent) item;
//System.out.println("node: "+node+", got bcomp item:"+wcomp.hasViews());

            //if the component needs a default view create one for it...
            if (!wcomp.hasViews()) {

                //get the format type and the doc from the view context
                FormatType ft = vc.getViewCapabilities().getFormatType();
                Document doc = vc.getDocument();

                try {
                    //get the appropriate renderer by looking up the
                    //DOM class associated with the given format type
                    Renderer r = wcomp.getRenderer(ft.getDOMClass());
                    
                    //ask the renderer to create the default Node
                    Node n = r.createDefaultNode(doc, wcomp, vc);
					// fro_020407_begin - handle null defaultNode (BScript)
                    if (n == null) {
                    	Node tplNode = vc.getTemplateNode();
                    	if (tplNode != null) {
                    		n = vc.getTemplateNode().cloneNode(true);
                    	}
                    	else {
                    		throw new UnsupportedFormatException("Cannot create default node");
                    	}
                    }
                    // fro_020407_end
                    // fro_061909_begin
                    // check if the default node can fit into the parent, else wrap it in the appropriate element
                    else if ( ! (n instanceof HTMLLIElement || n instanceof HTMLTableColElement || n instanceof HTMLTableCellElement
                    		     || n instanceof HTMLOptionElement || n instanceof HTMLDListElement)){
	                    String targetEl = null;
	                    if (node instanceof HTMLDListElement) targetEl = "DT";
	                    else if (node instanceof HTMLOListElement) targetEl = "LI";
	                    else if (node instanceof HTMLOptGroupElement) targetEl = "OPTION";
	                    else if (node instanceof HTMLSelectElement) targetEl = "OPTION";
	                    else if (node instanceof HTMLTableColElement) targetEl = "COL";
	                    else if (node instanceof HTMLTableRowElement) targetEl = "TD";
	                    else if (node instanceof HTMLUListElement) targetEl = "LI";
	                    else throw new DOMException((short) 0, "Unsupported List node");
	                    
	                    Node innerNode = n;
	                    n = node.getOwnerDocument().createElement(targetEl);
	                    addChildToParent(n, innerNode);
	                    wcomp.addTempView(new DefaultView(innerNode));
                    }
                    // fro_061909_end
                    
                    addAttrsToNode(item, n);
                    addChildToParent(node, n);
                    
                    //if the component still needs a default view create one for it...
                    if (!wcomp.hasViews()) {
//System.out.println("h1: adding temp view");
                        wcomp.addTempView(new DefaultView(n));
                    }
                    wcomp.invalidate();
                } catch (RenderException e) {
                    logger.warn("Unable to create default view:", e);
                } catch (DOMException e) {
                    logger.warn("Unable to create default view:", e);
                }
                
            //if the component being returned is already bound to views,
            //just add in the nodes that back the views
            } else {
                if (!(wcomp instanceof BCompoundComponent)) {
//System.out.println("h2: here we go...");
                    Iterator it = wcomp.getViews().iterator();
                    while (it.hasNext()) {
                        View view = (View) it.next();
                        addAttrsToNode(item, view.getNode());
                        addChildToParent(node, view.getNode());
//System.out.println("h2: node:"+node+" view node:"+view.getNode());
                    }
                }
            }
            
            //now add the comp as a step child
            blist.addStepChild(wcomp);

        //..Nodes
        } else if (item instanceof Node) {
            if (logger.isDebugEnabled()) logger.debug("Getting next Node item: "+item+"...");
            addChildToParent(node, (Node) item);
        
        //..Strings
        } else {
            if (logger.isDebugEnabled()) logger.debug("Getting next String item: "+item+"...");                
            String s = item.toString();
            
//csc_012605_1_start
            //get the template for this particular node. Now that we no longer use element factory, 
            //we need to get the default element manually - look for the first child element that 
            //implements Element
//            Node templateNode = vc.getElementFactory().getDefaultElement();
            Node templateNode = null;
            if (vc.getTemplateNode().hasChildNodes()) {
                NodeList nl = vc.getTemplateNode().getChildNodes();
                for (int i=0,max=nl.getLength(); i<max; i++) {
                    Node child = nl.item(i);
                    if (child instanceof Element) {
                        templateNode = child;
                        break;
                    }
                }
            }
//csc_012605_1_end
            Node newNode = null;
            if (templateNode!=null) {
                //create the new node by cloning
                newNode = templateNode.cloneNode(true);
            } else {
                //create the new node from scratch
                String targetEl = null;
                if (node instanceof HTMLDListElement) targetEl = "DT";
                else if (node instanceof HTMLOListElement) targetEl = "LI";
                else if (node instanceof HTMLOptGroupElement) targetEl = "OPTION";
                else if (node instanceof HTMLSelectElement) targetEl = "OPTION";
                else if (node instanceof HTMLTableColElement) targetEl = "COL";
                else if (node instanceof HTMLTableRowElement) targetEl = "TD";
                else if (node instanceof HTMLUListElement) targetEl = "LI";
                else throw new DOMException((short) 0, "Unsupported List node");
                newNode = node.getOwnerDocument().createElement(targetEl);
            }
            
            //now populate the new node and add it back in
            if (newNode!=null) {
                BText textComp = new BText(s);
                textComp.setView(new DefaultView(newNode));
                blist.addStepChild(textComp);
                addAttrsToNode(item, newNode);
                addChildToParent(node, newNode);
                
            } else {
                throw new InvalidNodeException("Unable to create a new Text node");
            }
        }
    }
    
    /**
     * add support for items that implement Attrs (this allows ItemMap objects to set their own attributes)
     * 
     * @param item
     * @param node
     * @since 1.2.7
     */
    private static void addAttrsToNode(Object item, Node node) {
        if ((item instanceof Attrs) &&  (node instanceof Element)) {
            Map attrs = ((Attrs) item).getAttrMap();
            if (attrs!=null) {
                Iterator it = attrs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    String key = (""+me.getKey()).trim();
                    Object val = me.getValue();
                    if (val==null) {
                        ((Element) node).removeAttribute(key);
                    } else {
                    	// fro_20111102_begin - don't create duplicate ids in HTML
                    	if ( ! (item instanceof BComponent && key.equals("id"))) {
                    		((Element) node).setAttribute(key, ""+val);           
                    	}
                    	// fro_20111102_end
                    }
                }
            }
        }
    }

}