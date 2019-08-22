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
 * $Id: BlockIterateHandler.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event.helper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;
import org.barracudamvc.core.comp.BlockIterator;
import org.barracudamvc.core.comp.DefaultViewContext;
import org.barracudamvc.core.comp.InvalidDirectiveException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.TemplateDirective;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.renderer.TemplateHelper;
import org.barracudamvc.core.event.*;
import org.barracudamvc.core.util.dom.CommaSeparatedDOMWriter;
import org.barracudamvc.core.util.dom.DOMWriter;
import org.barracudamvc.core.util.dom.DefaultDOMLoader;
import org.barracudamvc.core.util.dom.DefaultDOMWriter;
import org.enhydra.xml.io.OutputOptions;
import org.w3c.dom.*;
import org.w3c.dom.CharacterData;
import org.w3c.dom.html.*;

/**
 * 
 */
public abstract class BlockIterateHandler extends DefaultBaseEventListener {

    //public vars...eventually, these should probably be final
    private static Logger logger = Logger.getLogger(BlockIterateHandler.class.getName());
    //other vars
    protected Stack<IteratorContext> itStack = new Stack<IteratorContext>();
    protected Map<String, BlockIterator> biCache = new HashMap<String, BlockIterator>();
    protected ViewContext vc = null;
    protected BlockIterator bi = null;
    protected DOMWriter writer = null;
    protected Node nextNode = null;
    protected Node stubNode = null;

    //-------------------- BlockIterateHandler -------------------
    public void setViewContext(ViewContext ivc) {
        vc = ivc;
    }

    public ViewContext getViewContext() {
        return vc;
    }

    public DOMWriter getDOMWriter() {
        DefaultDOMWriter dw = new DefaultDOMWriter();
        dw.setLeaveWriterOpen(true);
        return dw;
    }

    /**
     * you can override this method to handle any initializion 
     * needs for the handler. Its invoked after the view context info
     * has been set up
     */
    public void initHandler() {
    }

    /**
     * you must override this method to specify what template you wish to process
     */
    public abstract Class getTemplateClass();

    /**
     * this is where you provide iterators for blocks as they are encountered in the template
     */
    public abstract BlockIterator getIterator(String key);

    /**
     * Handle the ViewEvent
     */
    @Override
    public void handleViewEvent(ViewEventContext vec) throws EventException, ServletException, IOException {
        //load the localized DOM template
        Document page = DefaultDOMLoader.getGlobalInstance().getDOM(getTemplateClass(), vec.getViewCapabilities().getClientLocale()); //csc_061702.1

        //pass the page on to the main method
        handleViewEvent(vec, page);
    }

    /**
     * Handle the ViewEvent
     */
    public void handleViewEvent(ViewEventContext vec, Document page) throws EventException, ServletException, IOException {
        long bmillis = 0;
        if (logger.isInfoEnabled()) {
            bmillis = System.currentTimeMillis();
        }
        if (logger.isInfoEnabled()) {
            logger.info("Handling ViewEvent in " + this);
        }

        try {
            //start by figuring out the ViewCapabilities
            DefaultViewContext vc = new DefaultViewContext(vec);
            vc.setDocument(page);
            setViewContext(vc);
            HttpServletResponse resp = vec.getResponse();

            //We want to use resp.getOutputStream() instead of resp.getWriter() because the PrintWriter
            //returned by getWriter() apparently has no buffer whereas the ServletOutputStream does.
            //This means that using the PrintWriter would cause the message chunks in the response to
            //be extremely small, with each call to print() causing a new chunk to be sent.
            Writer out = new OutputStreamWriter(resp.getOutputStream());

            //give the handler a chance to initialize
            initHandler();

            //create a stub node 
            stubNode = page.createElement("div");

            //get the DOMWriter
            writer = getDOMWriter();
            writer.setLeaveWriterOpen(true);

            //prepare the response (sets the appropriate headers)
            writer.prepareResponse(page, resp);

            //saw_020204_1 begin
            //This is a bit of an ugly hack, but our HTML DOM trees don't have a DOCTYPE node within
            //them which means we can't print it out via normal means (apparently DOMFormatter
            //usually does this for us when it encounters a Document node and the appropriate 
            //OutputOptions. Since we don't want the whole DOM tree to print at once we can't simply
            //pass the Document node to the DOMWriter (and therefore DOMFormatter).
            if (writer instanceof DefaultDOMWriter && page.getDoctype() == null) {
                OutputOptions oo = ((DefaultDOMWriter) writer).getOutputOptions();
                if (oo == null) {
                    oo = DefaultDOMWriter.getDefaultOutputOptions(page);
                }

                if (!oo.getOmitDocType()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("printing document type header");
                    }
                    out.write("<!DOCTYPE ");
                    out.write(page.getDocumentElement().getNodeName());

                    String publicId = oo.getPublicId();
                    if (publicId != null) {
                        out.write(" PUBLIC \"" + publicId + "\"");
                    }

                    String systemId = oo.getSystemId();
                    if (systemId != null) {
                        out.write(" \"" + systemId + "\"");
                    }

                    out.write(">");
                }
            }
            //saw_020204_1 end

            //finally, start processing the document            
            printNode(writer, page, out, 0, (page instanceof HTMLDocument));    //csc_012804_1
            out.close();

            if (logger.isInfoEnabled()) {
                logger.info("ViewEvent handled! (rendered in " + (System.currentTimeMillis() - bmillis) + " millis)");
            }

        } catch (RenderException e) {
            //if we get an EventException, handle it
            if (logger.isInfoEnabled()) {
                logger.info("Unexpected RenderException:" + e);
                ApplicationGateway.logRequestDetails(logger, Level.INFO);    //csc_031005_1
            }
            throw new EventException("Unexpected RenderException:" + e, e);

        } catch (IOException e) {
            logger.error("Unexpected IOException:" + e, e);
            ApplicationGateway.logRequestDetails(logger, Level.ERROR);    //csc_031005_1
            throw e;

        } catch (RuntimeException e) {
            logger.error("Unexpected RuntimeException:" + e, e);
            ApplicationGateway.logRequestDetails(logger, Level.ERROR);    //csc_031005_1
            throw e;

        } finally {
            //cleanup
            vc = null;
            bi = null;

            //as a final measure, ask the system to invoke gc()
            System.gc();
        }

    }

    class IteratorContext {

        String biName = null;
        BlockIterator bi = null;
        Node startNode = null;
        boolean initialized = false;
        boolean iterating = false;
        int cntr = -1;
        int subcntr = -1;

        public IteratorContext(String ibiName, BlockIterator ibi, Node istartNode) {
            biName = ibiName;
            bi = ibi;
            startNode = istartNode;
        }
    }

    public void printNode(DOMWriter writer, Node node, Writer out, int depth, boolean isHtml) throws RenderException, IOException {

        //element
        if (node instanceof Element) {
            Element el = (Element) node;
            if (logger.isDebugEnabled()) {
                logger.debug("printNode() - el:" + node.getNodeName());
            }
            String origClassAttr = null;
            String newClassAttr = null;
            TemplateDirective td = null;
            nextNode = null;


            //look for directives in the class attribute (we're only interested in block iterator
            //directives here...everything else needs to stay in there)
            origClassAttr = el.getAttribute("class");
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for directives based on class attr: " + origClassAttr);
            }
            if (origClassAttr != null && origClassAttr.indexOf("Dir::Block_Iterate") > -1) {
                StringBuffer sbNewClassAttr = new StringBuffer(origClassAttr.length());
                StringTokenizer st = new StringTokenizer(origClassAttr, " ");
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    TemplateDirective ttd = null;
                    try {
                        ttd = TemplateDirective.getInstance(s);
                        String cmd = ttd.getCommand();
                        if (!cmd.equals(TemplateDirective.BLOCK_ITERATE)
                                && !cmd.equals(TemplateDirective.BLOCK_ITERATE_START)
                                && !cmd.equals(TemplateDirective.BLOCK_ITERATE_END)) {
                            ttd = null;
                        }
                    } catch (InvalidDirectiveException e) {
                    }
                    if (ttd != null) {
                        if (td != null) {
                            logger.warn("ERR! A given node may only contain one Dir::Block_Iterate... command; ignoring " + td);
                        } else {
                            td = ttd;
                        }
                    } else {
                        sbNewClassAttr.append(s + " ");
                    }
                }
                if (td.getCommand().equals(TemplateDirective.BLOCK_ITERATE)) {
                    newClassAttr = sbNewClassAttr.toString().trim();
                    if (logger.isDebugEnabled()) {
                        logger.debug("cleaning class attr, new attr:" + newClassAttr);
                    }
                    if (!origClassAttr.equals(newClassAttr)) {
                        el.setAttribute("class", newClassAttr);
                    }
                }
            }

            //now, process block iterator directives in this node
            String cmd = (td != null ? td.getCommand() : null);
            String blockIteratorName = (td != null ? td.getModelName() : null);



            //for BLOCK_ITERATE, just handle this node directly (old way) - note
            //that you're not going to be able to use nested BLOCK_ITERATE directives; you'd need
            //to use the start and stop version for that...
            if (TemplateDirective.BLOCK_ITERATE.equals(cmd)) {
                if (logger.isInfoEnabled()) {
                    logger.info("BLOCK_ITERATE:" + td);
                }

                bi = (BlockIterator) biCache.get(blockIteratorName);    //csc_051004_1
                if (bi == null) {
                    bi = getIterator(blockIteratorName);      //csc_051004_1
                }
                biCache.put(blockIteratorName, bi);                     //csc_051004_1
                int cntr = 0;
                if (bi != null) {
                    try {
                        //set the name
                        bi.setName(blockIteratorName);

                        //pre-iterate
                        bi.preIterate();

                        //iterate
                        while (bi.hasNext()) {
                            //allow the developer to load the next record
                            if (!bi.loadNext()) {
                                continue;
                            }

                            //clone the node, then allow the block iterator to render into it
                            Node templateNode = node.cloneNode(true);
                            Node newNode = bi.next(getViewContext(), templateNode);
                            cntr++;
                            if (logger.isDebugEnabled()) {
                                logger.debug("Got block " + cntr + ", node: " + newNode);
                            }
                            if (newNode == null) {
                                continue;
                            }

                            //now render this node to the output stream
                            writer.write(newNode, out);
                            out.flush();
                            if (logger.isDebugEnabled()) {
                                logger.debug("Successfully rendered node!");
                            }
                        }
                    } finally {
                        //post-iterate
                        bi.postIterate();
                        bi = null;
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.warn("Failed to locate corresponding BlockIterator class!");
                    }
                    if (logger.isDebugEnabled()) {
                        localLogger.warn("Failed to locate corresponding BlockIterator class!");
                    }
                    out.write("\n\n<!-- Missing Iterator: " + blockIteratorName + " -->\n\n");
                }


                //for BLOCK_ITERATE_START, either push onto the stack or increment...
            } else if (TemplateDirective.BLOCK_ITERATE_START.equals(cmd)) {
                IteratorContext context = (!itStack.empty() ? itStack.peek() : null);

                //if we have no context, or we have hit a new start tag, we need to create a NEW context
                if (context == null || !context.biName.equals(blockIteratorName)) {
                    //create the context
                    if (logger.isInfoEnabled()) {
                        logger.info("BLOCK_ITERATE_START (init):" + td);
                    }
                    bi =  biCache.get(blockIteratorName);    //csc_051004_1
                    if (bi == null) {
                        bi = getIterator(blockIteratorName);      //csc_051004_1
                    }
                    if (bi == null) {
                        throw new RuntimeException("ERR: getIterator(" + blockIteratorName + ") returned null! - check the block iterator name in your template!");
                    }
                    bi.setName(blockIteratorName);
                    biCache.put(blockIteratorName, bi);                     //csc_051004_1
                    context = new IteratorContext(blockIteratorName, bi, node);
                    context.cntr = -1;
                    context.subcntr = -1;
                    itStack.push(context);

                    //pre-iterate
                    context.bi.preIterate();
                }

                //next we move the model forward until we have data
                while (context.bi.hasNext()) {
                    context.cntr++;
                    if (logger.isInfoEnabled()) {
                        logger.info("BLOCK_ITERATE_START (next:[" + (context.cntr) + "]):" + td);
                    }
                    if (context.bi.loadNext()) {
                        break;
                    }
                }

                //for BLOCK_ITERATE_END, pop off the stack
            } else if (TemplateDirective.BLOCK_ITERATE_END.equals(cmd)) {
                IteratorContext context = (IteratorContext) itStack.peek();
                if (!context.bi.hasNext()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("BLOCK_ITERATE_END (done):" + td);
                    }
                    context.bi.postIterate();
                    TemplateHelper.cleanupAfterBlockIterate();  //hacky, but necessary (since the template has no way of knowing about the larger context - whether its done iterating or not)
                    itStack.pop();
                    nextNode = node.getNextSibling();
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("BLOCK_ITERATE_END (loop):" + td);
                    }
                    nextNode = context.startNode;
                    context.subcntr = -1;
                }


                //default case...at this point, we're dealing with a node that _doesn't_ have a recognized 
                //block iterate directive in it (ie. Block_Iterate, Block_Iterate_Start, Block_Iterate_End). 
            } else {

                //however, if we've got a block iterate context active, we need to delegate
                //to a template model
                IteratorContext context = (!itStack.empty() ? (IteratorContext) itStack.peek() : null);
                if (context != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Got context for block iterator: " + context.biName);
                    }

                    //if the block iterator is all out of data, don't print anything - just return
                    context.subcntr++;
                    bi = context.bi;
//csc_040704_1 - This check is problematic, because it causes the last element in the block iterator to be skipped 
//(after we hit the end tag, we loop up to the top and move the block iterator forward via next() - once we are on 
//the very last item, hasNext() will return false. I can't remember why I put this check in here in the first place... 

                    /*                    
                     if (!context.bi.hasNext()) {
                     if (logger.isDebugEnabled()) logger.debug("...Skipping node["+context.cntr+","+context.subcntr+"] because bi.hasNext()==false");
                     return;
                     }
                     */
                    //at this point, delegate...                
                    if (logger.isDebugEnabled()) {
                        logger.debug("...Rendering node[" + context.cntr + "," + context.subcntr + "]...");
                    }
                    Node templateNode = node.cloneNode(true);
                    stubNode.appendChild(templateNode);
                    Node newNode = context.bi.next(getViewContext(), stubNode);

                    //now render this node to the output stream
                    if (newNode != null && newNode.hasChildNodes()) {
                        writer.write(newNode.getFirstChild(), out);
                        out.flush();
                    }

                    //now clean up the stub node
                    while (stubNode.hasChildNodes()) {
                        stubNode.removeChild(stubNode.getFirstChild());
                    }

                    //otherwise, the plain ol text default case...just print the tag and attributes
                } else {
                    //unfortunately, there's really no way to defer this to the 
                    //DOMWriter, since we have no idea if the child nodes contain
                    //any block iterator tags. Consequently, we have to manually 
                    //print the tag...

                    //csc_013004_1
                    //this is a VERY hacky way of doing this, but I can't think of anything else
                    //right now without expanding the DOMWriter interface, and I'm not sure we really 
                    //want to do that. Baically, if we are writing to a CommaSeparatedDOMWriter there
                    //are certain tags that should get converted to "" (ie. they don't get printed, but
                    //their children still get processed). SO...
                    //
                    //Note also the isCSVRow and isCSVCol stuff: basically, because we are printing one node 
                    //at a time (because there may be a block iterator somewhere in the children), the nodes end
                    //up getting handled differently by the csv writer (it won't pad with quotes, add on an eol, etc)
                    //so we have to handle that here. I know its hokey, but... :-(
                    String tag = el.getTagName().toLowerCase();
                    boolean printNode = true;
                    boolean isCSVRow = false;
                    boolean isCSVCol = false;
                    if (writer instanceof CommaSeparatedDOMWriter) {
                        printNode = (!tag.equals(CommaSeparatedDOMWriter.DOCUMENT_TYPE)
                                && !tag.equals(CommaSeparatedDOMWriter.ELEMENT_ROW)
                                && !tag.equals(CommaSeparatedDOMWriter.ELEMENT_HEADER)
                                && !tag.equals(CommaSeparatedDOMWriter.ELEMENT_COLUMN)
                                && !tag.equals(CommaSeparatedDOMWriter.ELEMENT_SPAN)
                                && !tag.equals(CommaSeparatedDOMWriter.ELEMENT_DIV));
                        isCSVRow = (tag.equals(CommaSeparatedDOMWriter.ELEMENT_ROW));
                        isCSVCol = (tag.equals(CommaSeparatedDOMWriter.ELEMENT_HEADER) || tag.equals(CommaSeparatedDOMWriter.ELEMENT_COLUMN));
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Processing tag:<" + tag + "> as plain text (printNode=" + printNode + ", isCSVRow=" + isCSVRow + ", isCSVCol=" + isCSVCol + ")");
                    }

                    //now print the start tag
                    if (printNode) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("...write start tag: <" + tag + ">");
                        }
                        out.write("<" + el.getTagName());
                        String sep = " ";
                        NamedNodeMap nnm = el.getAttributes();
                        for (int i = 0, max = nnm.getLength(); i < max; i++) {
                            Attr attr = (Attr) nnm.item(i);
                            out.write(sep + attr);
                        }
                        out.write(">");
                    }

                    //print the children
                    if (isCSVCol && node.getPreviousSibling() != null) {
                        out.write(',');
                    }
                    if (isCSVCol) {
                        out.write('"');
                    }
                    if (node.hasChildNodes()) {
                        //this method just prints the child nodes one node at a time
                        if (logger.isDebugEnabled()) {
                            logger.debug("...writing children...");
                        }
                        printChildNodes(writer, node, out, depth, isHtml);
                    }
                    if (isCSVCol) {
                        out.write('"');
                    }

                    //print the closing tag (if it's not forbidden, as the given html tags are)
                    if (printNode) {
                        if (!isHtml
                                || (!tag.equals("area") && !tag.equals("base") && !tag.equals("basefont")
                                && !tag.equals("br") && !tag.equals("col") && !tag.equals("frame")
                                && !tag.equals("hr") && !tag.equals("image") && !tag.equals("input")
                                && !tag.equals("isindex") && !tag.equals("link") && !tag.equals("meta")
                                && !tag.equals("param"))) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("...write close tag: </" + tag + ">");
                            }
                            out.write("</" + el.getTagName() + ">");
                        }
                    }
                    if (isCSVRow) {
                        out.write(CommaSeparatedDOMWriter.eol);
                    }
                    out.flush();
                }
            }

            //node with child nodes        
        } else if (node.hasChildNodes()) {
            if (logger.isDebugEnabled()) {
                logger.debug("printNode() - node with child nodes: " + node.getNodeName());
            }
            printChildNodes(writer, node, out, depth, isHtml);

            //csc_102504_1 - added (we don't want to encode character data beneath a Script element)
            //node is Text and parent node is a <SCRIPT> element
        } else if ((node instanceof CharacterData) && (node.getParentNode() instanceof HTMLScriptElement)) {
            if (logger.isDebugEnabled()) {
                logger.debug("printNode() - cdata node with <script> parent: " + node.getNodeName());
            }
            out.write(((CharacterData) node).getData());
            out.flush();

            //anything else - just defer back to the DOMWriter...
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("printNode() - everything else...: " + node.getNodeName() + (node instanceof CharacterData ? ((CharacterData) node).getData() : ""));
            }
            writer.write(node, out);
            out.flush();
        }
    }

    public void printChildNodes(DOMWriter dw, Node node, Writer out, int depth, boolean isHtml) throws RenderException, IOException {
        Node child = node.getFirstChild();
        if (child == null) {
            return;
        }
        do {
            printNode(dw, child, out, depth + 1, isHtml);
            //jrk_040107_1 - check that nextNode is an ELEMENT_NODE, otherwise call getNextSibling() to avoid infinite loop
            child = (nextNode != null && nextNode.getNodeType() == Node.ELEMENT_NODE ? nextNode : child.getNextSibling());
        } while (child != null);
    }
}
