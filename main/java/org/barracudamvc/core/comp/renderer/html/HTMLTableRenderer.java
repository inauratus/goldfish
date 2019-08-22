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
 * $Id: HTMLTableRenderer.java 186 2007-06-04 13:08:55Z alci $
 */
package org.barracudamvc.core.comp.renderer.html;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BList;
import org.barracudamvc.core.comp.BTable;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.DefaultListModel;
import org.barracudamvc.core.comp.DefaultTableView;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.InvalidNodeException;
import org.barracudamvc.core.comp.InvalidViewException;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.TableModel;
import org.barracudamvc.core.comp.TableView;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLTableElement;

/**
 * This class handles the default rendering of a table in an HTML view.
 */
public class HTMLTableRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLTableRenderer.class.getName());
    
    
    /**
     * The purpose of this method is for a renderer to provide 
     * a default node (if none exists). This component currently
     * does not provide a default, so it throws an UnsupportedFormatException 
     * instead.
     *
     * @param doc the master Document which can be used to create elements
     *        from scratch
     * @param comp the component that we're dealing with for the current request
     * @param vc the view context for the current request
     * @return a default node (created from scratch)
     * @throws UnsupportedFormatException if the renderer has no default node
     */
    
    // fro_020407_begin - separate createDefaultNode and add defaultView
//    public Node createDefaultNode(Document doc, BComponent comp, ViewContext vc) throws UnsupportedFormatException {  //csc_110501.1
//        Node defaultNode = super.createDefaultNode(doc, comp, vc);
//        comp.setView(new DefaultTableView(defaultNode));
//        return defaultNode;
//    }
    /**
     * BTable needs a specific view implementation.
     * Return it.
     */
    public void addDefaultView(BComponent comp, Node node) {
    	comp.setView(new DefaultTableView(node));
    }
    // fro_020407_end
    
    /**
     *
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a text component
        if (!(comp instanceof BTable)) throw new NoSuitableRendererException("This renderer can only render BTable components; comp is of type:"+comp.getClass().getName());

        //make sure the View implements TableView
        if (!(view instanceof TableView)) throw new InvalidViewException ("Component is bound to an unsupported View:"+view);
        TableView tview = (TableView) view;

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);


        BTable btable = (BTable) comp;
        TableModel headerModel = btable.getHeaderModel();
        TableModel model = btable.getModel();
        TableModel footerModel = btable.getFooterModel();
        BText captionComponent = btable.getCaption();
        Node node = view.getNode();
        
        //HTMLElement Interface
        //---------------------
        //Supported Elements:
        //..HTMLTableElement
        //
        //Unsupported Elements:
        //..everything else
        if (node instanceof HTMLElement) {

            if (node instanceof HTMLTableElement) {
                if (logger.isInfoEnabled()) logger.info("Rendering table component...");

                //get the nodes for the header, body, and footer sections
                Element bodyNode = tview.getBodyElement();
                Element headerNode = tview.getHeaderElement();
                Element footerNode = tview.getFooterElement();

                Element captionNode = tview.getCaptionElement();
                //now we will create the caption, and add it to the comp
                if (captionNode!=null && captionComponent!=null){
                    if (logger.isDebugEnabled()) logger.debug("Creating caption for table...");
                    captionComponent.setView(new DefaultView(captionNode));
                    // fro_060407_2 Renderers should only add addStepChild, that are cleaned on destroyCycle
                    //              addChild should be reserved to developpers or nested components
                    //btable.addChild(captionComponent);
                    btable.addStepChild(captionComponent);
                    // fro_060407_2 end
                }

                //now create a list component and bind it to the data section node.
                //(be sure to add the comp as a temporary child--by doing this, we 
                //ensure that the child will get rendered as well. After rendering, 
                //we'll remove the child from the hierarchy, so as to return to our 
                //original state)
                if (logger.isDebugEnabled()) logger.debug("Creating table component for body...");
                DefaultListModel rowBodyModel = new DefaultListModel();
                BList rowBodyList = new BList(rowBodyModel);
                rowBodyList.setView(new DefaultView(bodyNode));
                btable.addStepChild(rowBodyList);

                //create a model, view, and list for the header (default it
                //to be the same objects as for the body)
                DefaultListModel rowHeaderModel = rowBodyModel;
                BList rowHeaderList = rowBodyList;
                if (headerNode!=null && headerModel!=null) {
                    if (logger.isDebugEnabled()) logger.debug("Creating table component for header...");
                    rowHeaderModel = new DefaultListModel();
                    rowHeaderList = new BList(rowHeaderModel);
                    rowHeaderList.setView(new DefaultView(headerNode));
                    btable.addStepChild(rowHeaderList);
                }

                //do the same thing for the footer model
                DefaultListModel rowFooterModel = rowBodyModel;
                BList rowFooterList = rowBodyList;
                if (footerNode!=null && footerModel!=null) {
                    if (logger.isDebugEnabled()) logger.debug("Creating table component for footer...");
                    rowFooterModel = new DefaultListModel();
                    rowFooterList = new BList(rowFooterModel);
                    rowFooterList.setView(new DefaultView(footerNode));
                    btable.addStepChild(rowFooterList);
                }

                //now populate the rowModel
                if (headerModel!=null) {
                    if (logger.isDebugEnabled()) logger.debug("Rendering header...");
                    populateTableFromModel(node, vc, headerModel, rowHeaderModel, rowHeaderList);
                }
                if (model!=null) {
                    if (logger.isDebugEnabled()) logger.debug("Rendering body...");
                    populateTableFromModel(node, vc, model, rowBodyModel, rowBodyList);
                }
                if (footerModel!=null) {
                    if (logger.isDebugEnabled()) logger.debug("Rendering footer...");
                    populateTableFromModel(node, vc, footerModel, rowFooterModel, rowFooterList);
                }
            } else {
                if (logger.isDebugEnabled()) logger.debug("Interface "+node.getClass().getName()+" not a supported View for a BTable component...");
                throw new InvalidNodeException ("View is bound to an unsupported Node:"+node);
            }
                
        } else {
            String errmsg = "Node does not implement HTMLElement and cannot be rendered: "+node;
            logger.warn(errmsg);
            throw new NoSuitableRendererException(errmsg);
        }
    }
    
    private static void populateTableFromModel(Node node, ViewContext vc, TableModel curModel, DefaultListModel rowModel, BList rowList) throws RenderException {

        //give the model a chance to initialize
        curModel.setViewContext(vc);
        curModel.resetModel();
        Node origTemplateNode = vc.getTemplateNode();
        vc.putState(ViewContext.TEMPLATE_NODE, node);
                
        //now process items in the model based on model type
        try {
            for (int i=0,max=curModel.getRowCount(); i<max; i++) {

                //create a generic row element and add it to the rowModel
                Element rowNode = node.getOwnerDocument().createElement("TR");
                rowModel.add(rowNode);

                //create a list component for the cols in the row and bind it
                //to the rowNode
                DefaultListModel colModel = new DefaultListModel();
                DefaultView colView = new DefaultView(rowNode); 
                BList colList = new BList(colModel);
                colList.setView(colView);
                rowList.addChild(colList);
            
                //now populate the colModel
                for (int j=0,jmax=curModel.getColumnCount(); j<jmax; j++) {
                    Object item = curModel.getItemAt(i, j);
                    if (item!=null) colModel.add(item);
                }
            }
        } finally {        
            vc.putState(ViewContext.TEMPLATE_NODE, origTemplateNode);
            curModel.setViewContext(null);
        }
    }
}
