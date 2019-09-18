/*
 */
package org.barracudamvc.core.comp.renderer.html;


import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.BTree;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.model.TreeModel;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.view.FormatType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLUListElement;

/**
 * This class handles the default rendering of a tree in an HTML view.
 */
public class HTMLTreeRenderer extends HTMLComponentRenderer {

	protected static final Logger logger = Logger.getLogger(HTMLTreeRenderer.class.getName());
	
    /* (non-Javadoc)
     * @see org.barracudamvc.core.comp.renderer.DOMComponentRenderer#createDefaultNode(org.w3c.dom.Document, org.barracudamvc.core.comp.BComponent, org.barracudamvc.core.comp.ViewContext)
     */
    @Override
	public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc)
    	throws UnsupportedFormatException {

    	Node defaultNode = null;
    	Node templateNode = vc.getTemplateNode();
        
    	if (templateNode instanceof HTMLUListElement) {
    		defaultNode = super.createDefaultNode(doc, comp, vc); // will clone the templateNode
    	}
    	else {
    		defaultNode = doc.createElement("UL");
    	}
    	return defaultNode;
    }
    
    /**
     *
     */
    @Override
	public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a tree component
        if (!(comp instanceof BTree)) throw new NoSuitableRendererException("This renderer can only render BTree components; comp is of type:"+comp.getClass().getName());

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        // then recursively render the tree
        BTree btree = (BTree) comp;
        TreeModel model = btree.getModel();
    	Node node = view.getNode();
    	Document doc = vc.getDocument();
    	
    	if ( ! (node instanceof HTMLElement)) {
    		throw new RenderException("Node does not implement HTMLElement");
    	}
    	if ( ! (node instanceof HTMLUListElement)) {
    		Element newNode = doc.createElement("UL");
    		node.getParentNode().replaceChild(newNode, node);
    		node = newNode;
    	}
    	// else remove children
    	else {
    		while (node.hasChildNodes()) {
                node.removeChild(node.getFirstChild());
            }
    	}
    	
    	// then go for the recursive rendering of the tree
    	handleNode(model.getRoot(), node, vc, btree);
    }

    /**
     * Here is what we want :
     * UL
     *   LI value
     *     UL
     *   
     * @param treeNode
     * @param current
     * @param vc
     */
    private void handleNode(TreeModel treeNode, Node current, ViewContext vc, BTree btree) throws RenderException {
    	
    	Document doc = vc.getDocument();
    	// First, add an LI
    	Element currentItem = doc.createElement("LI");
    	currentItem.setAttribute("id", treeNode.getId());
    	currentItem.setAttribute("class", "axg_selectableTreeNode");
    	
    	current.appendChild(currentItem);
    	//logger.debug("Inserted LI...");
    	// ... then get the value for the tree entry and add it to the view
    	//logger.debug("... calling getItem() on " + treeNode);
    	Object entry = treeNode.getItem();
    	Node entryNode = null;
    	// BComponent
    	if (entry instanceof BComponent && ! (entry instanceof BText)) {
    		//logger.debug("Item is a BComponent");
    		BComponent bcomp = (BComponent) entry;
    		
    		FormatType ft = vc.getViewCapabilities().getFormatType();
    		//Node newNode = null;
            try {
        		//get the appropriate renderer by looking up the
                //DOM class associated with the given format type
                Renderer r = bcomp.getRenderer(ft.getDOMClass());
                //ask the renderer to create the default Node
                entryNode = r.createDefaultNode(doc, bcomp, vc);
	                                    
                // handle null defaultNode (BScript)
                if (entryNode == null) {
                	Node tplNode = vc.getTemplateNode();
                	if (tplNode != null) {
                		entryNode = vc.getTemplateNode().cloneNode(true);
                	}
                	else {
                		throw new UnsupportedFormatException("Cannot create default node");
                	}
                }
            }
            catch (NoSuitableRendererException e) {
            	// TODO what to do here ???
            }
            addChildToParent(currentItem, entryNode);
    		bcomp.addTempView(new DefaultView(entryNode));
    		btree.addStepChild(bcomp);
    		//finally, invalidate the component to ensure redraw
    		bcomp.invalidate();
    	}
    	else if (entry instanceof BText) {
	        BText bcomp = (BText) entry; 
	        bcomp.setInsertBefore(true);
	        
	        bcomp.addTempView(new DefaultView(currentItem));
			btree.addStepChild(bcomp);
			//finally, invalidate the component to ensure redraw
			bcomp.invalidate();
		}
    	// Node
    	else if (entry instanceof Node) {
    		logger.debug("Item is a Node");
    		entryNode = (Node) entry;
            // if the returned node does not belong to this document, automatically import it
            if (doc!= entryNode.getOwnerDocument()) {
                if (logger.isDebugEnabled()) logger.debug("auto-importing returned node...");
                entryNode = doc.importNode(entryNode, true);
            }
            //currentItem.appendChild(entryNode);
            addChildToParent(currentItem, entryNode);
    	}
    	// String
    	else {
    		String str = null;
    		if (entry instanceof String) {
    			str = (String) entry;
    			logger.debug("Item is a String: "+str);
//    		}
//    		else if(entry instanceof AbstractTreeStructure){
//    			AbstractTreeStructure dde = (AbstractTreeStructure)entry;
//    			str = dde.getString(dde.getValueFieldMeta()).concat(" - "+dde.getLic());
//    			currentItem.setAttribute("id",  dde.getString(dde.getValueFieldMeta()));
    		} else {
    			logger.warn("Returned item is not a BComponent, nor a node or a String");
    			str = entry.toString();
    		}
			
    		BText bcomp = new BText(str);
    		bcomp.setInsertBefore(true);
    		bcomp.addTempView(new DefaultView(currentItem));
    		btree.addStepChild(bcomp);
    		//finally, invalidate the component to ensure redraw
    		bcomp.invalidate();
    		
    	}
    	
    	// then, if the TreeNode has children, add a sublist...
    	if ( ! treeNode.isLeaf()) {
    		Element newCurrent = doc.createElement("UL");
        	//currentItem.setAttribute("class", currentItem.getAttribute("class")+" axg_ajaxIt");
    		currentItem.appendChild(newCurrent);
    		
    		if (treeNode.getChildren() == null || treeNode.getChildren().size()==0){
    			
    			Element liElement = doc.createElement("LI");
    			newCurrent.appendChild(liElement);

    			// hasChildren
    			currentItem.setAttribute("class", currentItem.getAttribute("class") + " axg_ajaxIt hasChildren");
    		}
    		current = newCurrent;
    		logger.debug("TreeNode has children, adding an UL");
    	}
    	
    	// then, id the TreeNode is selected, mark it
    	if ( treeNode.isSelected())
    		currentItem.setAttribute("class", currentItem.getAttribute("class") + " ui-tree-node-selected");
    	
    	// and recursively handle children
		for (TreeModel child : treeNode.getChildren()) {
			logger.debug("calling handleNode for: "+child.getItem().toString());
			handleNode(child, current, vc, btree);
		}
	}    
}