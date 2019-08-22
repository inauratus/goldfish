/*
 * Copyright (C) 2001  Christian Cryder [christianc@granitepeaks.com]
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
 * $Id: DOMUtil.java 194 2007-11-22 20:33:08Z alci $
 */
package org.barracudamvc.core.util.dom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.barracudamvc.plankton.Classes;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * DOM related utility functions.
 */
public class DOMUtil {

    private static byte[] sep = System.getProperty("line.separator").getBytes();

    /**
     * Find the first text descendent node of an element.
     * This recursively looks more than one level to search
     * for text in font nodes, etc.
     *
     * @param node The starting node for the search.
     * @return The text node or null if not found.
     */
    public static Text findFirstText(Node node) {
        if (node instanceof Text) return (Text) node;
        for (Node child = node.getFirstChild(); child!=null; child = child.getNextSibling()) {
            Text text = findFirstText(child);
            if (text!=null) return text;
        }
        return null;
    }

    /**
     * Gets the first text descendent node of an element.
     * This recursively looks more than one level to search
     * for text in font nodes, etc. Throws a DOMException
     * if the Text object is not found.
     *
     * @param node The starting node for the search.
     * @return The text node or null if not found.
     * @throws DOMException if the Text object is not found
     */
    public static Text getFirstText(Node node) {
        Text text = findFirstText(node);
        if (text==null) {
            String msg = "No child text mode found for element";
            String id = getID(node);
            throw new DOMException((short) -1, msg+(id!=null ? "; id=\""+id+"\"" : ""));
        }
        return text;
    }

    /**
     * Automatically set text in a Node. Basically looks for 
     * the first child Text node; if it finds one, it sets the
     * text, if not, it creates a new one with the appropriate
     * text. Throws a DOMException if it's illegal to add a Text
     * child to the particular node.
     *
     * @param node the starting node for the search.
     * @param node the text to be added
     * @return the updated node
     * @throws DOMException if the Text object is not found
     */
/*
    public static Node setTextInNode(Node node, String text) {
        Text textComp = DOMUtil.findFirstText((Element) node);
        if (textComp==null) {
            textComp = node.getOwnerDocument().createTextNode(text);
            node.appendChild(textComp);
        } else {
            textComp.setData(""+text);
        }
        return node;
    }
*/


    /**
     * Automatically set text in a Node. Basically we find the first
     * Text node beneath the current node and replace it with a
     * CDATASection for the incoming text. All other Text nodes are
     * removed. Throws a DOMException if it's illegal to add a Text
     * child to the particular node.
     *
     * @param node the starting node for the search.
     * @param text the text to be set
     * @param allowMarkupInText whether to allow markup in text to pass through unparsed
     * @return the updated node
     * @throws DOMException if the Text object is not found
     */
    public static Node setTextInNode(Node node, String text, boolean allowMarkupInText) {

    	// fro_112207_begin
    	return setTextInNode(node, text, allowMarkupInText, false); 
    	//start by setting the value in the first text node we find with a comment
//        Comment comment = node.getOwnerDocument().createComment("");
//        Node newNode = null;
//        
//        //csc_092701.1 - support both encoded/unencoded text
//        if (allowMarkupInText) newNode = node.getOwnerDocument().createCDATASection(text);
//        else newNode = node.getOwnerDocument().createTextNode(text);
////System.out.println ("newNode: "+newNode);
//        
//        Text textComp = DOMUtil.findFirstText((Element) node);
////System.out.println ("textComp:"+textComp);        
//        if (textComp==null) {
//            node.appendChild(comment);
//        } else {
//            Node parent = textComp.getParentNode();
//            parent.replaceChild(comment, textComp);
//        }
//        
//        //now remove all the rest of the text nodes
//        removeAllTextNodes(node);        
//
//        //now replace the comment with the newNode
//        Node parent = comment.getParentNode();
//        parent.replaceChild(newNode, comment);
////System.out.println ("parent:  "+parent);        
////System.out.println ("result:  "+DOMUtil.findFirstText((Element) parent));        
////DOMUtil.printStackTrace(parent.getOwnerDocument().getDocumentElement());
//        return node;
     // fro_112207_end
    }
    
    public static Node setTextInNode(Node node, String text, boolean allowMarkupInText, boolean insertBefore) {
        //start by setting the value in the first text node we find with a comment
        Comment comment = node.getOwnerDocument().createComment("");
        Node newNode = null;
        
        //csc_092701.1 - support both encoded/unencoded text
        if (allowMarkupInText) newNode = node.getOwnerDocument().createCDATASection(text);
        else newNode = node.getOwnerDocument().createTextNode(text);
//System.out.println ("newNode: "+newNode);
        
        Text textComp = DOMUtil.findFirstText((Element) node);
//System.out.println ("textComp:"+textComp);        
        if (textComp==null) {
        	// fro_112207_begin
        	if (insertBefore) {
        		node.insertBefore(comment, node.getFirstChild());
        	}
        	
        	else { // fro_112207_end
        		node.appendChild(comment);
        	}
        } else {
            Node parent = textComp.getParentNode();
            parent.replaceChild(comment, textComp);
        }
        
        //now remove all the rest of the text nodes
        removeAllTextNodes(node);        

        //now replace the comment with the newNode
        Node parent = comment.getParentNode();
        parent.replaceChild(newNode, comment);
//System.out.println ("parent:  "+parent);        
//System.out.println ("result:  "+DOMUtil.findFirstText((Element) parent));        
//DOMUtil.printStackTrace(parent.getOwnerDocument().getDocumentElement());
        return node;
    }

    /**
     * Remove all text nodes below this node
     *
     * @param node The starting node for the search.
     */
    public static void removeAllTextNodes(Node node) {
        if (node==null) return;
        if (!node.hasChildNodes()) return;
        NodeList nl = node.getChildNodes();
        for (int i=nl.getLength()-1; i>=0; i--) {
            Node n = (Node) nl.item(i);        
            if (n instanceof Text) node.removeChild(n);
            else removeAllTextNodes(n);
        }
    }
    
    /**
     * Given a Node name, return the "id" attribute if it exists.
     * If it does not exist, return null instead. This is basically
     * just a convenience method to cast the node to element and 
     * return the id from that.
     *
     * @param node the node name in question
     * @return the id value for the given node, if it exists. null if 
     *        doesn't
     */
    public static String getID(Node node) {
        return getID(node, null);
    }

    /**
     * Given a Node, return the "id" attribute if it exists.
     * If it does not exist, return nullResponse instead. This is basically
     * just a convenience method to cast the node to element and 
     * return the id from that.
     *
     * @param node the node in question
     * @param nullResponse the response to be returned if the id attribute
     *        does not exist
     * @return the id value for the given node, if it exists. null if 
     *        doesn't
     */
    public static String getID(Node node, String nullResponse) {
        String nodeName = nullResponse;
        if (node instanceof Element) {
            nodeName = ((Element) node).getAttribute("id");
        }
        return nodeName;
    }

    /**
     * <p>utility method to recursively print the stack trace for a DOM Node</p>
     */
    public static void printStackTrace(Node node) {
        printStackTrace(node, System.out, 0);
    }

    /**
     * <p>utility method to recursively print the stack trace for a DOM Node</p>
     *
     * <p>Bounds: <br>
     * If depth < 0, the method returns immediately</p>
     *
     * @param node the node in question
     * @param out OutputStream to print to
     * @param depth inset depth at which to start printing
     */
    public static void printStackTrace(Node node, OutputStream out, int depth) {
        if (depth<0) depth = 0;
        if (depth>25) depth = 25;
        String spaces = "                                                                              ";
        String inset = spaces.substring(0,depth*3);
        print (out, inset+node.getClass().getName()+"@"+Integer.toHexString(node.hashCode()));            
        StringBuffer sb = new StringBuffer(200);
        String sep = "";
        Iterator it = Classes.getAllInterfaces(node).iterator();
        while (it.hasNext()) {
            sb.append(sep+Classes.getShortClassName((Class) it.next()));
            sep = ", ";
        }
        print (out, inset+"   implements: {"+sb.toString()+"}");
        print (out, inset+"   name:"+node.getNodeName());
        print (out, inset+"   attr:"+(node.hasAttributes() ? "" : " (n/a)"));
        if (node.hasAttributes()) {
            NamedNodeMap nnm = node.getAttributes();
            for (int i=0,max=nnm.getLength(); i<max; i++) {
                Attr attr = (Attr) nnm.item(i);
                print (out, inset+"      "+attr.getName()+":"+attr.getValue());
            }        
            print (out, inset+"   /end attr");
        }
        print (out, inset+"   children:"+(node.hasChildNodes() ? "" : " (n/a)"));
        if (node.hasChildNodes()) {
            NodeList nl = node.getChildNodes();
            for (int i=0,max=nl.getLength(); i<max; i++) {
                printStackTrace(nl.item(i), out, depth+2);
            }        
            print (out, inset+"   /end children");
        }
        print (out, inset+"/end @" + Integer.toHexString(node.hashCode()));
        if (out!=null) try {out.flush();} catch (IOException ioe) {}
    }
    

    protected static void print(OutputStream out, String s) {
        if (out!=null) try {
            out.write(s.getBytes());
            out.write(sep);
        } catch (IOException ioe) {}
    }

    public static void printMarkup(Node node) {
        printMarkup(node, new PrintWriter(System.out, true), true, true, 0);
    }

    public static void printMarkup(Node node, PrintWriter out, boolean isHtml, boolean skipComments, int depth) {
        if (depth<0) depth = 0;
        if (depth>35) depth = 35;
        String spaces = "                                                                              ";
        String inset = spaces.substring(0,depth*2);

        //element
        if (node instanceof Element) {
            Element el = (Element) node;
            out.print(inset+"<"+el.getTagName());
            String sep = " ";
            NamedNodeMap nnm = el.getAttributes();
            for (int i=0, max=nnm.getLength(); i<max; i++) {
                Attr attr = (Attr) nnm.item(i);
                out.print(sep+attr.getName()+"=\""+attr.getValue()+"\"");
            }
            out.println(">"+" {@"+Integer.toHexString(node.hashCode())+"}");
        
            //print the children
            printChildMarkup(node, out, isHtml, skipComments, depth);
        
            //print the closing tag (if it's not forbidden, as the given html tags are)
            String tag = el.getTagName().toLowerCase();
            if (!isHtml || 
               (!tag.equals("area") && !tag.equals("base") && !tag.equals("basefont") &&
                !tag.equals("br") && !tag.equals("col") && !tag.equals("frame") &&
                !tag.equals("hr") && !tag.equals("image") && !tag.equals("input") &&
                !tag.equals("isindex") && !tag.equals("link") && !tag.equals("meta") &&
                !tag.equals("param"))) {
                out.println(inset+"</"+el.getTagName()+">");
            }
        
        //character data
        } else if (node instanceof CharacterData) {
            if (node instanceof Comment) {
                if (!skipComments) {
                    out.println(inset+"<!-- "+((CharacterData) node).getData()+" -->");
                }
            } else {
                out.println(inset+((CharacterData) node).getData());
            }            

        //node with child nodes        
        } else if (node.hasChildNodes()) {
            printChildMarkup(node, out, isHtml, skipComments, depth);

        //anything else
        } else {        
            System.out.println("Unhandled element:"+node.getClass().getName());
            org.barracudamvc.plankton.data.CollectionsUtil.printStackTrace(Classes.getAllInterfaces(node));
        }
    }    
    
    protected static void printChildMarkup(Node node, PrintWriter out, boolean isHtml, boolean skipComments, int depth) {
        Node child = node.getFirstChild();
        if (child==null) return;
        do {
            printMarkup(child, out, isHtml, skipComments, depth+1);
            child = child.getNextSibling();
        } while (child!=null);
    }


}
