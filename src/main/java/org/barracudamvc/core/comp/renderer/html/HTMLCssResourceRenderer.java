package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.DOMComponentRenderer;
import org.barracudamvc.core.comp.BCssResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLLinkElement;


/**
 * This class handles the default rendering css references into an HTML view.
 */
public class HTMLCssResourceRenderer extends DOMComponentRenderer {

	protected static final Logger logger = Logger.getLogger(HTMLCssResourceRenderer.class.getName());

	/**
	 *
	 */
	public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
		//unlike other components, this one really doesn't care what we're bound
		//to...we do need to make sure the component is a CSS component
		if (!(comp instanceof BCssResource)) throw new NoSuitableRendererException("This renderer can only render BCssResource components");        

		//show what we're doing
		showNodeInterfaces(view, logger);

		//now allow the parent class to do anything it needs to
		super.renderComponent(comp, view, vc);

		BCssResource bsr = (BCssResource) comp;
		String hRef = bsr.getHRef();

		//Shortcircuit rendering if no href to set
		if (hRef == null) {
			logger.warn("Null href, skipping rendering.");
			return;
		}

		Node node = view.getNode();
		Node child = null;

		//find the root of the document and search for head
		Document doc = node.getOwnerDocument();
		Element elRoot = doc.getDocumentElement();
		Element elHead = findHead(elRoot);

		//else, create one and add it in the document
		if (elHead==null) {
			elHead = doc.createElement("HEAD");
			elRoot = doc.getDocumentElement();
			elRoot.insertBefore(elHead, elRoot.getFirstChild());
		}

		//now see if we have CSS elements that match already in place
		//(if so, we don't need to do anything)
		HTMLLinkElement elCss = null;
		child = elHead.getFirstChild();
		HTMLLinkElement lastLink = null;
		while (child!=null) {
			if (child instanceof HTMLLinkElement) {
				HTMLLinkElement scel = (HTMLLinkElement) child;
				lastLink = scel;
				if (hRef.equals(scel.getHref())) {
					elCss = scel;
					break;
				}
			}
			child = child.getNextSibling();
		}

		//if we don't, create one
		if (elCss==null) {
			elCss = (HTMLLinkElement) doc.createElement("LINK");
			if (lastLink != null) {
				elHead.insertBefore(elCss, lastLink.getNextSibling());
			}
			else {
				Node chld = elHead.getFirstChild();
				if (chld != null) {
					elHead.insertBefore(elCss, chld);
				}
				else {
					elHead.appendChild(elCss);
				}
			}
			elCss.setType("text/css");
			elCss.setRel("stylesheet");
			elCss.setHref(hRef);
		}
	}
	
	// Search for a HTMLHeadElement
	private Element findHead(Element elRoot) {
		Element elHead = null;
		Node child = elRoot.getFirstChild();
		while (child!=null) {
			if (child instanceof HTMLHeadElement) {
				elHead = (HTMLHeadElement) child;
				break;
			}
			child = child.getNextSibling();
		}
		return elHead;
	}
}
