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
 * $Id: TemplateHelper.java 267 2014-04-09 06:12:44Z alci $
 */
package org.barracudamvc.core.comp.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BCompoundComponent;
import org.barracudamvc.core.comp.BTemplate;
import org.barracudamvc.core.comp.BText;
import org.barracudamvc.core.comp.BlockIterator;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.IterativeModel;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.TemplateDirective;
import org.barracudamvc.core.comp.TemplateModel;
import org.barracudamvc.core.comp.TemplateView;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.view.FormatType;
import org.barracudamvc.plankton.data.ObjectRepository;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Provide a default encapsulation of the template rendering stuff.
 */
public class TemplateHelper {

    protected static final Logger logger = Logger.getLogger(TemplateHelper.class.getName());
    protected static final String ITERATIVE_MODEL_MAP = TemplateHelper.class.getName() + ".IterativeModelMap";        //(Map) - local object repository
    protected static final String IT_STACK = TemplateHelper.class.getName() + ".ItStack";                             //(Stack) - local object repository
    final String s = "";
    protected Renderer masterRenderer = null;
    protected BTemplate btemplate = null;
    protected Stack<IteratorContext> itStack = null;
    protected boolean skipUntilBlockIterateEnd = false;
    static boolean showDebug = false;
    ObjectRepository localRepository;

    public TemplateHelper(Renderer imasterRenderer) {
        if (logger.isInfoEnabled()) {
            logger.info("Instantiating TemplateHelper " + this);
        }
        masterRenderer = imasterRenderer;
        localRepository = ObjectRepository.getLocalRepository();
        itStack = localRepository.getState(IT_STACK);
    }

    /**
     * This renders a BTemplate component into the specified TemplateView,
     * for the given ViewCOntext
     */
    public void render(BTemplate comp, TemplateView view, ViewContext vc) throws RenderException {
        //csc_091401.1_start - the purpose of this mod is to make it possible to process
        //directives in the root node of the template view. We can only do this for
        //BTemplates that are step children because in this case they will only be rendered
        //once and thus its not a big deal to go replacing the root node

        //get a reference to the template and the root node
        btemplate = (BTemplate) comp;
        Node node = view.getNode();
        Node origNode = node;
//        Node origParent = origNode.getParentNode();
        Node newNode = null;

        //if we're dealing with a step child, we know that this component/view will only be 
        //rendered once. As such, we can actually replace the node that the component is 
        //bound to with one that's been parsed for directives (allowing us to effectively
        //parse the root node for directives as well). We can't do this with permanent views
        //since the component has established a view on the node so we can't just go changing 
        //that node
        if (comp.isStepChild()) {
            newNode = getNode(node, view, vc, 0);
            if (newNode != null) {
                node = newNode;
            }
        }

        //now, if we're NOT dealing with a step child, go ahead and do this. If
        //we ARE dealing with a step child, we want to skip this step (or else it'll
        //end up processing parts of the template twice)
        if (!comp.isStepChild()) {  //csc_032202.1 - added

            //start by removing all the children from the target node
            while (node.hasChildNodes()) {
                node.removeChild(node.getFirstChild());
            }

            //now iterate through the master template and add children back in, 
            //applying template directives as we go.
            Node masterTemplate = view.getMasterTemplate();

            copyChildNodes(masterTemplate, node, view, vc, 0);
        }

        //now, if we actually are using newNode, then we need to replace the 
        //origNode with it once we're done
        if (newNode != null) {
            origNode.getParentNode().replaceChild(newNode, origNode);
        }
        //csc_091401.1_end
    }

    /**
     * Copy nodes from the template to the target
     */
    protected void copyChildNodes(Node templateNode, Node targetNode, TemplateView view, ViewContext vc, int depth) throws RenderException {

        //iterate through all the template node's children, copying them across
        Node child = templateNode.getFirstChild();
        Node newChild = null;
        while (child != null) {
            //get the new child
            newChild = getNode(child, view, vc, depth + 1);

            //if we're iterating and we need to loop, reset to the starting
            //point for the current iteration loop
            if (itStack != null) {
                IteratorContext itcontext = itStack.peek();
                if (itcontext.NEXTLOOP) {
                    child = itcontext.startNode;
                    itcontext.NEXTLOOP = false;
                    continue;
                } else if (itcontext.EOF) {
                    //Only set it equal to the endNode if the endNode is not null; 
                    //otherwise, set it equal to the next sibling.
                    if (itcontext.endNode != null) {
                        child = itcontext.endNode;
                    } else {
                        child = child.getNextSibling();
                    }
                    continue;
                }
            }

            //add the newchild in
            if (newChild != null) {
                masterRenderer.addChildToParent(targetNode, newChild);
            }

            //get the next child
            child = child.getNextSibling();
        }
    }

    /**
     * Given the specified templateNode, return the new Node to be rendered into the view
     */
    protected Node getNode(Node templateNode, TemplateView view, ViewContext vc, int depth) throws RenderException {
        //put the template node into the view context
        vc.putState(ViewContext.TEMPLATE_NODE, templateNode);


        //now start by getting the directives for the current node. 
        //
        //When we look for directives, we do so by looking first to see if the 
        //previous node was a processing instruction that contained directives. 
        //This is how you would use directives in XML, since the DTD probably 
        //wouldn't support them in the class attribute. 
        //
        //Now, once we have checked there, see if the view contains any directive 
        //lists associated witha  particular node. If you are a developer who
        //doesn't want directives embedded in *ML, or if the *ML won't allow for it,
        //this is how you provide for them--by passing in a map to the view which
        //can be used to xref ids to dir lists.
        //
        //finally, look to see if there are any directives directly associated
        //with the class attribute in the *ML. If there are, add them to the 
        //list. 
        //
        //When we're all done, we will have a list consisting of all the directives
        //associated witha  given node...
        List<TemplateDirective> dirList = null;

        //...now look in the attributes
        String origClassAttr = null;
        String newClassAttr = null;
        Map<String, String> attrMap = new HashMap<String, String>();
        boolean idMatchesDirectives = false;
        if (templateNode instanceof Element) {
            Element el = (Element) templateNode;

            //...look for directives based on id attribute
            String idName = el.getAttribute(view.getIDAttrName());
            if (idName != null && idName.trim().length() > 0 && view.getDirIDMap() != null) {
                if (dirList == null) {
                    dirList = new ArrayList<TemplateDirective>();
                }
                List<TemplateDirective> tlist = view.lookupDirsByID(idName);
                if (tlist != null) {
                    dirList.addAll(tlist);
                }
                if (dirList.size() > 0) {
                    idMatchesDirectives = true;
                }
            }

            //...look for directives in the class attribute
            origClassAttr = el.getAttribute(view.getDirAttrName());
            if (origClassAttr != null) {
                StringBuilder sbNewClassAttr = new StringBuilder(origClassAttr.length());
                StringTokenizer st = new StringTokenizer(origClassAttr, " ");
                while (st.hasMoreTokens()) {
                    String directiveName = st.nextToken();
                    TemplateDirective td = view.lookupDir(directiveName);
                    if (td != null) {
                        if (dirList == null) {
                            dirList = new ArrayList<TemplateDirective>(1);
                        }
                        dirList.add(td);
                    } else {
                        sbNewClassAttr.append(directiveName).append(" ");
                    }
                }
                newClassAttr = sbNewClassAttr.toString().trim();
            }
        }


        //take a quick prelim pass through the dir list - we are looking for block iterator directives.
        //Basically, anythign that contains a block iterator needs to get left as is for a subsequent
        //pass, at which point the block iterator stuff will be rendered
        boolean deepCopyThisNode = skipUntilBlockIterateEnd;
        if (dirList != null && dirList.size() > 0) {
            for (TemplateDirective td : dirList) {
                String cmd = td.getCommand();

                //block iterate directives get handled a bit differently
                if (cmd.equals(TemplateDirective.BLOCK_ITERATE)) {
                    deepCopyThisNode = true;
                    localRepository.putState(TemplateDirective.HAS_BLOCK_ITERATOR, Boolean.TRUE);

                } else if (cmd.equals(TemplateDirective.BLOCK_ITERATE_START)) {
                    skipUntilBlockIterateEnd = true;
                    deepCopyThisNode = true;
                    localRepository.putState(TemplateDirective.HAS_BLOCK_ITERATOR, Boolean.TRUE);

                } else if (cmd.equals(TemplateDirective.BLOCK_ITERATE_END)) {
                    skipUntilBlockIterateEnd = false;
                    deepCopyThisNode = true;
                }
            }
        }

        //next, process any directives
        Node newNode = null;
        boolean processedDir = false;
//        if (dirList!=null && dirList.size()>0) {
        if (dirList != null && dirList.size() > 0 && !deepCopyThisNode) {

            //csc_050702.1 - first and foremost, see if we are in the middle of an iteration. If so,
            //see if we need to SKIP_TO_NEXT. If so, run through the list of directives and strip out 
            //any that occur up to and including the Iterate_Start directive. Then clear the SKIP_TO_NEXT
            //flag in the iterator context. What this should effectively do is allow us to nest start
            //iterator directives within the same node
            if (itStack != null) {
                IteratorContext itcontext = itStack.peek();
                if (itcontext.SKIP_TO_NEXT) {
                    Iterator itDir = dirList.iterator();
                    while (itDir.hasNext()) {
                        TemplateDirective td = (TemplateDirective) itDir.next();
                        if (td.equals(itcontext.startTd)) {
                            //only remove it if the template directive is a start dir; if its a NEXT
                            //dir (which can happen now that the START is really unnecessary), then
                            //we want to leave it in place so that we increment the counter properly
//csc_033004_1                            itDir.remove();
                            if (td.getCommand().equals(TemplateDirective.ITERATE_START)) {
                                itDir.remove();    //csc_033004_1
                            }
                            break;
                        } else {
                            itDir.remove();
                        }
                    }
                    itcontext.SKIP_TO_NEXT = false;
                }
            }

            //iterate through all directives.
//System.out.println("Processing directive list");
            Iterator itDir = dirList.iterator();
            TemplateModel tm = null;
            while (itDir.hasNext()) {
                try {
                    TemplateDirective td = (TemplateDirective) itDir.next();
                    String cmd = td.getCommand();
                    String model = td.getModelName();
                    String data = td.getKeyData();
                    Object item = null;

                    // If tm is null, that means we couldn't find a TemplateModel by the
                    // requested name.  In that case, we'll just log a warning and skip
                    // this directive. - Submitted by Jeff French 7/17/2001
                    tm = btemplate.getModel(model);
                    if (tm == null && !cmd.equals(TemplateDirective.DISCARD)) {
                        StringBuilder errorMessageBuilder = new StringBuilder();

                        errorMessageBuilder
                                .append("Cannot find a model named ")
                                .append(model)
                                .append(". Skipping directive ")
                                .append(td);

                        if (vc != null) {
                            if (vc.getOngoingTemplate() != null) {
                                errorMessageBuilder.append("\n\tIn Template ")
                                        .append(vc.getOngoingTemplate().getName())
                                        .append(" ");
                            }
                            if (vc.getEventContext() != null && vc.getEventContext().getEvent() != null) {
                                errorMessageBuilder.append("\n\tEvent ID: ")
                                        .append(vc.getEventContext().getEvent().getEventID())
                                        .append("\n\tEvent URL: ")
                                        .append(vc.getEventContext().getEvent().getEventURL());

                            }
                        }

                        for (View viewElement : btemplate.getViews()) {
                            errorMessageBuilder.append("\n\t")
                                    .append(viewElement);
                        }

                        logger.debug(errorMessageBuilder.toString());
                        continue;
                    }

                    //if the directive is aimed at a specific model, let the model 
                    //know about it. If this directive gets vetoed, no further 
                    //directives will get processed and the entire node will get 
                    //skipped
                    if (tm != null) {
                        tm.setViewContext(vc);
                        boolean ok = tm.processDirective(td);
                        if (!ok) {
                            return null;
                        }
                    }

                    //Dir::Get_Data                
                    if (cmd.equals(TemplateDirective.GET_DATA)) {
                        //make sure that if we're in the middle of an iteration we still have data
                        //available. If not, don't return this node!
                        if (itStack != null) {
                            IteratorContext itcontext = itStack.peek();
                            if (itcontext.EOF) {
                                return null;
                            }
                        }

                        //get the item and process accordingly
                        item = tm.getItem(td);                      //csc_030603.2
                        if (item != null) {
                            //..BComponent
                            if (item instanceof BComponent) {
                                //add in the Node for the first view

                                BComponent bcomp = (BComponent) item;

                                //this is not really the right way to do this, but its a 
                                //quick fix that will work for now. See csc for details...
                                boolean defaultNodeCreatedViews = false;    //csc_110501.1

                                //if the component needs a default view create one for it...
                                View mainView = null;                                   //csc_072604_1
                                if (!bcomp.hasViews() || bcomp.getViews().size() < 1) {  //jrk_040702.2
                                    //get the format type and the doc from the view context
                                    FormatType ft = vc.getViewCapabilities().getFormatType();
                                    Document doc = vc.getDocument();    //csc_072604_2

                                    try {
                                        //get the appropriate renderer by looking up the
                                        //DOM class associated with the given format type
                                        Renderer r = bcomp.getRenderer(ft.getDOMClass());

                                        //ask the renderer to create the default Node
                                        newNode = r.createDefaultNode(doc, bcomp, vc); //csc_110501.1

                                        // fro_020407 - handle null defaultNode (BScript)
                                        if (newNode == null) {
                                            Node tplNode = vc.getTemplateNode();
                                            if (tplNode != null) {
                                                newNode = vc.getTemplateNode().cloneNode(true);
                                            } else {
                                                throw new UnsupportedFormatException("Cannot create default node");
                                            }
                                        }
                                        // fro_020407_end
                                        //if the component still needs a default view create one for it...
                                        if (bcomp.hasViews()) {
                                            defaultNodeCreatedViews = true;
//csc_072604_1_start
//                                    else bcomp.addTempView(new DefaultView(newNode));
                                        } else {
                                            mainView = new DefaultView(newNode);
                                            bcomp.addTempView(mainView);
                                        }
//csc_072604_1_end

                                        //finally, invalidate the component to ensure redraw
                                        bcomp.invalidate();

                                    } catch (RenderException e) {
                                        logger.warn("Unable to create default view:", e);
                                    } catch (DOMException e) {
                                        logger.warn("Unable to create default view:", e);
                                    }

                                    //if the component being returned is already bound to views,
                                    //just add in the nodes that back the views
                                } else {                                    //csc_110501.1
                                    //hmmm...I'm not sure if it would make sense to return components from a template model
                                    //that had more than one view. Probably not; in case someone tries it, it won't work
                                    //since we only handle the first one here...                            
                                    mainView = (View) bcomp.getViews().get(0);
                                    newNode = mainView.getNode();
                                }



                                //now add the comp as a temporary child (by doing this, we ensure that
                                //the child will get rendered as well. After rendering, we'll remove
                                //the child from the hierarchy, so as to return to our original state)
                                //
                                //(Note that we DON'T have these step children inherit the parent 
                                //settings...this is because the model will be returning distinct 
                                //components whose visibility/enabled status should NOT be the same as 
                                //that of the master template)
                                btemplate.addStepChild(bcomp);

                                //csc_091301.2_start - added
                                //if the component returned is NOT an instance of BTemplate,
                                //BTable, or BList, then check to see if it has any non-text
                                //children. If so, automatically create BTemplate components
                                //to parse them as well (thereby catching any nested directives
                                //they might also contain...)
                                if (!(bcomp instanceof BCompoundComponent)
                                        && !(defaultNodeCreatedViews) && //csc_110501.1
                                        (newNode != null && newNode.hasChildNodes())) {

//csc_112102.1_start
//ok, so the problem with this approach is that while immediately rendering the
//btChild here does solved the nested iterative directive problem (making sure they all
//get called in the current iteration, rather than later as was happening when we were
//simply adding to btemplate as a step child), it inadvertantly SKIPS directives that
//are in the DOM layer that is immediately below newNode

//The solution then, is to run through all the child elements; if any of them
//actually implement element, then create one BTemplate and bind it to newNode
//(rather than each individual child node as we were doing before), and THEN
//render immediately. Voila! All is well...whew! This was a bugger to find.
                                    Node chNode = newNode.getFirstChild();
                                    boolean hasElements = false;
                                    while (chNode != null) {
                                        if (chNode instanceof Element) {
                                            hasElements = true;
                                            break;
                                        }
                                        chNode = chNode.getNextSibling();
                                    }

                                    if (hasElements) {
                                        TemplateView tv = (TemplateView) view.clone();
                                        tv.setNode(newNode);
                                        BTemplate broot = new BTemplate();
                                        broot.setView(tv);
                                        broot.setEncoding(bcomp.getEncoding());     //csc_041805_1
                                        broot.addModels(btemplate.getModels());
                                        broot.render(vc);
                                        broot.destroyCycle();  //saw_121102.1 - we must call this so that the BTemplate can be gc'd
                                    }
//csc_112102.1_end
                                }
                                //csc_091301.2_end

                                //..Nodes
                            } else if (item instanceof Node) {
                                newNode = (Node) item;

                                //csc_072604_2 - if the returned node does not belong to this document, automatically import it
                                if (vc.getDocument() != newNode.getOwnerDocument()) {
                                    newNode = vc.getDocument().importNode(newNode, true);
                                }



                                /*
                                 NOTE - csc_022304 - there is a problem here with the immediate notion render. Consider the following chunk of 
                                 markup:

                                 <form ...>
                                 <div class="Dir::Get_Data:SomeModel.Foo1">
                                 <input class="Dir::Get_Data:SomeModel.Foo2">
                                 </div>
                                 </form>

                                 What happens here is that if Foo1 returns a BComponent (ie. perhaps just to set an attribute), the
                                 whole node is cloned and then immediately rendered (which it needs to be). The problem is that when Foo2
                                 renders, the cloned <div> is not yet part of the final dom hierarchy, and thus the renderers can't get
                                 access to anything up above (like the form!). This can be quite bad in the case of HTMLActionRenderer, 
                                 which needs that form in certain cases. I suspect BScript components are also going to have problems.

                                 What really needs to happen here is either
                                 a) we need to be able to somehow set the parent component of the cloned <div> before we actually add it 
                                 back into the DOM (that doesn't happen until we return from this node) -OR-
                                 b) we need a way to postpone this render until -AFTER- the cloned <div> has been added back in (but before
                                 any other nodes are processed - for the reason we put this immediate render notion in here in the first 
                                 place
   
                                 Not sure of the solution yet, but I wanted to at least capture the key information about the problem while
                                 it was fresh in my mind - Christian
                                 */
                                Node chNode = newNode.getFirstChild();
                                boolean hasElements = false;
                                while (chNode != null) {
                                    if (chNode instanceof Element) {
                                        hasElements = true;
                                        break;
                                    }
                                    chNode = chNode.getNextSibling();
                                }

                                if (hasElements) {
                                    TemplateView tv = (TemplateView) view.clone();
                                    tv.setNode(newNode);
                                    BTemplate broot = new BTemplate();
                                    broot.setView(tv);
                                    broot.setEncoding(btemplate.getEncoding());     //csc_041805_1
                                    broot.addModels(btemplate.getModels());
                                    broot.render(vc);
                                    broot.destroyCycle();  //saw_121102.1 - we must call this so that the BTemplate can be gc'd
                                }
//csc_112102.1_end

                                //..Strings
                            } else {
                                newNode = templateNode.cloneNode(true);
                                String s = item.toString();
                                BText textComp = new BText(s);
                                textComp.setView(new DefaultView(newNode));
                                textComp.setEncoding(btemplate.getEncoding());     //csc_041805_1
                                btemplate.addStepChild(textComp);

                                //TODO: if the String value contains any directives, create a 
                                //BTemplate component to process them as well...this is 
                                //actually not at all trivial, because by this point the dom 
                                //structure (if there even was one) has been flattened...even
                                //if we can tell there's a directive embedded in the text, its
                                //not clear to me how we'd process it, since the structure is 
                                //flat. We could conceivably try break the string up and reconstruct 
                                //DOM pieces, but I suspect this would be very difficult to do
                                //with any reliability...hmm. More thought needed before we bite 
                                //this one off to do.

                            }
                            //..skip this directive
                        } else {
                            return null;
                        }
                        processedDir = true;

                        //Dir::Set_Attr        
                    } else if (cmd.equals(TemplateDirective.SET_ATTR)) {
                        //make sure that if we're in the middle of an iteration we still have data
                        //available. If not, don't return this node!
                        if (itStack != null) {
                            IteratorContext itcontext = (IteratorContext) itStack.peek();
                            if (itcontext.EOF) {
                                return null;
                            }
                        }

                        item = tm.getItem(td);

                        String curAttr = attrMap.get(data);
                        String newAttr = (item == null ? null : item.toString());
                        if (curAttr == null) {
                            attrMap.put(data, newAttr);
                        } else if (newAttr != null) {
                            attrMap.put(data, curAttr + " " + newAttr);
                        }
                        //Dir::Discard
                    } else if (cmd.equals("Put_Attr")) {
                        if (itStack != null) {
                            IteratorContext itcontext = (IteratorContext) itStack.peek();
                            if (itcontext.EOF) {
                                return null;
                            }
                        }
                        item = tm.getItem(td);
                        String newAttr = (item == null ? null : item.toString());
                        attrMap.put(data, newAttr);

                        templateNode.getAttributes().getNamedItem(data).setNodeValue(newAttr);

                    } else if (cmd.equals(TemplateDirective.DISCARD)) {
                        //cause the node to be discarded by returning null immediately
                        return null;

                        //Dir::Iterate_Start            
                    } else if (cmd.equals(TemplateDirective.ITERATE_START)) {
                        //invoke preiteration (only has an effect the very first time)
                        if (!preiterate(templateNode, tm, td)) {
                            continue;
                        }

                        //Dir::Iterate_Next
                    } else if (cmd.equals(TemplateDirective.ITERATE_NEXT)) {

                        //csc_032604_1 - added
                        //invoke preiteration (only has an effect the very first time). Note that by putting
                        //this call here, we effectively eliminate the need for a ITERATE_START tag, because 
                        //its essentially implied in ITERATE_NEXT. Of course it won't hurt to use it (may be more
                        //readable, but recognize that its not really needed anymore)
                        preiterate(templateNode, tm, td);

                        //if itStack is null something is wrong...just continue
                        if (itStack == null) {
                            String errmsg = "ERR: Missing itStack!...(This should not have happened)";
                            logger.warn(errmsg, new Exception(errmsg));
                            continue;
                        }

                        //peek on the stack and get the iteration context. If there is no context,
                        //or if the model names don't match, something's wrong so just continue
                        IteratorContext itcontext = (IteratorContext) itStack.peek();
                        if (itcontext == null) {
                            String errmsg = "ERR: Missing itcontext!...(This should not have happened)";
                            logger.warn(errmsg, new Exception(errmsg));
                            continue;
                        }
                        if (!itcontext.startTd.getModelName().equals(td.getModelName())) {
                            String errmsg = "ERR: itcontext model:" + itcontext.startTd.getModelName() + " does not match current model:" + td.getModelName() + "!";
                            logger.warn(errmsg, new Exception(errmsg));
                            continue;
                        }

                        //move the iterator forward. If we're out of records, return immediately
                        if (itcontext.itm.hasNext()) {
                            itcontext.cntr++;
                            itcontext.itm.loadNext();
                        } else {
                            itcontext.EOF = true;
                            postiterate(tm, td);    //csc_052404_1
                            return null;
                        }

                        //Dir::Iterate_End                
                    } else if (cmd.equals(TemplateDirective.ITERATE_END)) {

                        //if we are rendering in the context of a block iterator, which has the same model name
                        //as this template directive, then we can assume the block iterator is taking responsibility
                        //for iterating over the model. Consequently, we will simply break and return null here - 
                        //we don't want to loop  back up to the start of the loop, since the block iterator
                        //will take care of that. This effectively makes it possible to leave the IterateEnd stmts in the
                        //template when you switch to block iterators and not have them adversely affect anything.
//                    if (Boolean.TRUE.equals(ObjectRepository.getLocalRepository().getState(IS_IN_BLOCK_ITERATOR))) return null;
                        BlockIterator bi = (BlockIterator) localRepository.getState(BlockIterator.BLOCK_ITERATOR_CONTEXT);
                        if (bi != null && model.equals(bi.getName())) {
                            return null;
                        }

                        //if the stack is null or empty, something is probably wrong
                        if (itStack == null || itStack.empty()) {
                            String errmsg = "ERR: Missing itStack!...(This should not have happened)";
                            logger.warn(errmsg, new Exception(errmsg));
                            continue;
                        }

                        //peek on the stack and get the iteration context. If there is no context,
                        //or if the model names don't match, something's wrong so just continue
                        IteratorContext itcontext = (IteratorContext) itStack.peek();
                        if (itcontext == null) {
                            String errmsg = "ERR: Missing itcontext!...(This should not have happened)";
                            logger.warn(errmsg, new Exception(errmsg));
                            continue;
                        }
                        if (!itcontext.startTd.getModelName().equals(td.getModelName())) {
                            String errmsg = "ERR: itcontext model:" + itcontext.startTd.getModelName() + " does not match current model:" + td.getModelName() + "!";
                            logger.warn(errmsg, new Exception(errmsg));
                            continue;
                        }

                        //see if we're at the end of the iteration
                        if (itcontext.EOF) {
                            popstack();

                            //otherwise, just nextloop    
                        } else {
                            itcontext.NEXTLOOP = true;
                            itcontext.SKIP_TO_NEXT = true;
                            itcontext.endNode = templateNode;

                            //return immediately since we don't want to add this node
                            //in, instead we want to loop back up to the top of the iterator
                            return null;
                        }
                    }
                } finally {
                    if (tm != null) {
                        tm.setViewContext(null);
                    }
                }
            }
        }

        //see if we need to deep copy this node
        if (deepCopyThisNode) {
            newNode = templateNode.cloneNode(true);

            //otherwise...
        } else {
            //if no directives were processed, shallow copy and then handle child nodes
            if (!processedDir) {
                //if there aren't any, shallow copy and continue and then 
                //copy in child nodes
                newNode = templateNode.cloneNode(false);
                copyChildNodes(templateNode, newNode, view, vc, depth + 1);
            }

            //adjust the outgoing class attributes on the node        
            if (origClassAttr != null && !(origClassAttr.equals(newClassAttr))) {
                Element elNew = (Element) newNode;
                if (newClassAttr != null && newClassAttr.length() > 0) {
                    elNew.setAttribute(view.getDirAttrName(), newClassAttr);
                } else {
                    elNew.removeAttribute(view.getDirAttrName());
                }
            }

            //adjust the outgoing id attributes on the node        
            if (idMatchesDirectives) {
                Element elNew = (Element) newNode;
                elNew.removeAttribute(view.getIDAttrName());
            }

            //set any attributes that were set through directives
            if (attrMap.size() > 0) {
                Element elNew = (Element) newNode;
                for (Map.Entry<String, String> attribute : attrMap.entrySet()) {
                    if (attribute.getValue() == null) {
                        elNew.removeAttribute(attribute.getKey());
                    } else {
                        elNew.setAttribute(attribute.getKey(), attribute.getValue());
                    }
                }
            }
        }

        return newNode;
    }

    //csc_032604_1 - added
    /**
     * Handles the ITERATE_START tag; returns false if we can just continue
     * preIterate() will only be called the first time. We break this out into
     * a subroutine so that INTERATE_NEXT logic can also invoke it, meaning that
     * you don't really have to have an ITERATE_START in your template (its now 
     * implicit in ITERATE_NEXT)
     */
    protected boolean preiterate(Node templateNode, TemplateModel tm, TemplateDirective td) {
        //make sure the model supports iteration
        if (tm == null || !(tm instanceof IterativeModel)) {
            logger.warn("ERR: Model " + tm + " does not support iteration!");
            return false;
        }
        IterativeModel itm = (IterativeModel) tm;
        //create the iterator stack if necessary
        if (itStack == null) {
            itStack = new Stack<IteratorContext>();
            localRepository.putState(IT_STACK, itStack);

            //csc_050704_1
            //if we are creating a new iterator stack, we want to make SURE that we don't
            //consider it "already iterated"...so clean it up just to be sure. This handles
            //the case where you want to iterate over the same iterative model twice (ie. 
            //different portions of the page

            Map itModelMap = (Map) localRepository.getState(ITERATIVE_MODEL_MAP);
            if (itModelMap != null) {
                itModelMap.remove(itm);
            }
        }

        //see if the IteratorContext currently on the stack matches this one. If it does, and
        //we have already called preiterate, just continue
        IteratorContext itcontext = null;
        if (!itStack.empty()) {
            itcontext = (IteratorContext) itStack.peek();
        }
        if (itcontext != null && itcontext.startTd.getModelName().equals(td.getModelName()) && itcontext.HAS_CALLED_PREITERATE) {
            return false;
        }

        //if not, this is a brand new iteration. Create an iteration 
        //context and add it to the stack
        itcontext = new IteratorContext(itm, templateNode, td);     //csc_050702.1
        itStack.push(itcontext);

        //notify the IterativeModel to prepare for iteration. What we're doing here is actually 
        //a little bit funky. Most times, when using a template component, you are going to 
        //be using a new model each time you instantiate the component. So when you iterate, you
        //need to preiterate. The situation is different, however, in the case of BlockIterator - 
        //there, we are reusing the same model with different template model components for
        //each block in the template. Consequently, we don't want to call preIterate() each time
        //the component hits a new START_ITERATE tag, because the model that's backing it may
        //actually already have been preiterated. Consequently, we store an identity reference
        //to models that have been preiterated, ensuring that for any given instance of a model,
        //it will only be preiterated one time. What this means, however, is that the postiterate
        //method could get called multiple times for a given model. To offset this, we check the lor
        //to see if we're operating in the context of a BlockIterator, and if so, we only call postiterate
        //when the blockiterator is finished

        Map<IterativeModel, IterativeModel> itModelMap = localRepository.getState(ITERATIVE_MODEL_MAP);
        if (itModelMap == null) {
            itModelMap = new IdentityHashMap<IterativeModel, IterativeModel>();
            localRepository.putState(ITERATIVE_MODEL_MAP, itModelMap);
        }
        if (!itModelMap.containsKey(itm)) {
            itm.preIterate();
            itModelMap.put(itm, itm);
        }
        itcontext.HAS_CALLED_PREITERATE = true;
        return true;
    }

    //csc_052404_1 - added    
    /**
     * Handles the post iteration stuff; returns false if positeration has already occurred. 
     * postIterate() will only be called the first time. 
     */
    protected boolean postiterate(TemplateModel tm, TemplateDirective td) {
        //make sure the model supports iteration
        if (tm == null || !(tm instanceof IterativeModel)) {
            logger.warn("ERR: Model " + tm + " does not support iteration!");
            return false;
        }
        IterativeModel itm = (IterativeModel) tm;

        //if the stack is null, just return false
        if (itStack == null) {
            return false;
        }

        //see if the IteratorContext currently on the stack matches this one. If it does, and
        //we have already called postiterate, just continue
        IteratorContext itcontext = null;
        if (!itStack.empty()) {
            itcontext = (IteratorContext) itStack.peek();
        }

        if (itcontext != null && itcontext.startTd.getModelName().equals(td.getModelName()) && itcontext.HAS_CALLED_POSTITERATE) {
            return false;
        }

        //notify the IterativeModel to cleanup after iteration. Basically, we only want to postiterate
        //once on a model, and then only if its already been preiterated (and after its been postiterated,
        //act like its never existed)
        Map itModelMap = (Map) localRepository.getState(ITERATIVE_MODEL_MAP);
        if (itModelMap == null) {
            return false;
        }
        if (itModelMap.containsKey(itm)) {
            //postiterate

            itm.postIterate();
            itModelMap.remove(itm);
            itcontext.HAS_CALLED_POSTITERATE = true;
        }

        return true;
    }

    protected void popstack() {
        itStack.pop();
        if (itStack.empty()) {
            itStack = null;
            localRepository.putState(IT_STACK, itStack);
        }
    }

    public static void cleanupAfterBlockIterate() {
        //clean up the stack        
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        Stack itStack = (Stack) lor.getState(IT_STACK);
        if (itStack != null) {
            itStack.clear();
        }
        lor.removeState(IT_STACK);
    }

    class IteratorContext {

        IterativeModel itm = null;
        Node startNode = null;
        Node endNode = null;
        TemplateDirective startTd = null;   //csc_050702.1
        boolean HAS_CALLED_PREITERATE = false;
        boolean HAS_CALLED_POSTITERATE = false;
        boolean SKIP_TO_NEXT = false;
        boolean NEXTLOOP = false;
        boolean EOF = false;
        int cntr = -1;

        public IteratorContext(IterativeModel iitm, Node istartNode, TemplateDirective istartTd) {  //csc_050702.1
            itm = iitm;
            startNode = istartNode;
            endNode = null;
            startTd = istartTd;
        }
    }
}
