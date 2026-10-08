/*
 * Copyright (C) 2003  Shawn Wilson [shawnw@atmreports.com]
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
 * $Id: CommaSeparatedDOMWriter.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.util.dom;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.StringUtil;
import org.w3c.dom.*;
import org.w3c.dom.CharacterData;
import org.w3c.dom.html.HTMLDocument;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class CommaSeparatedDOMWriter implements DOMWriter {

    private static final Class CLASS = CommaSeparatedDOMWriter.class;
    private static final Logger logger = Logger.getLogger(CLASS);

    public static final String DOCUMENT_TYPE = "table";
    public static final String ELEMENT_ROW = "tr";
    public static final String ELEMENT_HEADER = "th";
    public static final String ELEMENT_COLUMN = "td";
    public static final String ELEMENT_SPAN = "span";
    public static final String ELEMENT_DIV = "div";

    protected String contentType = "text/plain";
    protected String contentDisposition = null;
    protected boolean preventCaching = false;
    protected boolean leaveWriterOpen = false;
    protected int maxAge = 0;
    private static final Pattern SLASH_PATTERN = Pattern.compile("\"");
    private String lineEnding = System.getProperty("line.separator");

    /**
     * Create a new CommaSeparatedDOMWriter using the default values.
     * The default content type is "text/plain".
     * The default content disposition is to use none;
     */
    public CommaSeparatedDOMWriter() {
        this(null, null);
    }

    /**
     * Create a new CommaSeparatedDOMWriter with the supplied values.
     *
     * @param icontentType        The content type to use
     * @param icontentDisposition The content disposition to use
     */
    public CommaSeparatedDOMWriter(String icontentType, String icontentDisposition) {
        setContentType(icontentType);
        setContentDisposition(icontentDisposition);
    }

    /**
     * Prepare the response object
     *
     * @param node the DOM node to be written out
     * @param resp the HttpServletResponse object
     */
    @Override
    public void prepareResponse(Node node, HttpServletResponse resp) throws IOException {
        if (getContentType() == null) {
            setContentType((node instanceof HTMLDocument) ? "text/html" : "text/xml");
        }

        //set the content type and disposition
        if (contentType != null) {
            resp.setContentType(contentType);
        }
        if (contentDisposition != null) {
            resp.setHeader("Content-Disposition", contentDisposition);
        }

        // if we need to prevent caching
        if (preventCaching) {
            //add the appropriate headers to the response
            resp.setHeader("Pragma", "no-cache");
            resp.setHeader(
                    "Cache-Control",
                    "no-cache, no-store, must-revalidate, max-age=0"
            );
            resp.setDateHeader("Expires", 0);

            // otherwise explicitly give it a max-age (this will generally
            // allow browsers like IE to page back in history without reloading
            // , but if the user actually revisits the URL, then it will still
            // be reloaded)
        } else {
            resp.setHeader("Cache-Control", "max-age=" + maxAge);
            resp.setDateHeader("Last-Modified", System.currentTimeMillis());
        }
    }

    /**
     * Write a DOM to a ServletResponse object.
     * This method will automatically set the content type for you.
     *
     * @param node The DOM node to be written out
     * @param resp The HttpServletResponse object to write to
     */
    @Override
    public void write(Node node, HttpServletResponse resp) throws IOException {
        prepareResponse(node, resp);
        write(node, new OutputStreamWriter(resp.getOutputStream()));
    }

    /**
     * Write a DOM to an OutputStream.
     *
     * @param node The DOM node to be written out
     * @param out  The OutputStream to be written to
     */
    @Override
    public void write(Node node, OutputStream out) throws IOException {
        write(node, new OutputStreamWriter(out));
    }

    /**
     * Write a DOM to a Writer.
     *
     * @param node   The DOM node to be written out
     * @param writer The writer to be written to
     */
    @Override
    public void write(Node node, Writer writer) throws IOException {
        try {
            write(writer, node);
        } catch (DOMException e) {
            logger.error("Error while writing node", e);
        }
        if (!leaveWriterOpen) {
            writer.close();
        }
    }

    protected void write(Writer writer, Node node) throws IOException, DOMException {
        if (node == null) {
            return;
        }

        if (node instanceof Element) {
            write(writer, (Element) node);

        } else if (node instanceof CharacterData) {
            write(writer, (CharacterData) node);

        } else if (node instanceof Document) {
            write(writer, (Document) node);

        } else if (node instanceof DocumentType) {
            write(writer, (DocumentType) node);

        } else {
            logger.debug("Unrecognized node type: " + node.getClass());
            writeAll(node.getChildNodes(), writer);
        }
    }

    protected void write(Writer writer, Element element) throws IOException {
        String name = element.getNodeName();
        if (name.equals(ELEMENT_ROW)) {
            writeRow(element, element.getChildNodes(), writer);
        } else if (name.equals(ELEMENT_HEADER)) {
            writeHeader(element, element.getChildNodes(), writer);
        } else if (name.equals(ELEMENT_COLUMN)) {
            writeColumn(element, element.getChildNodes(), writer);
        } else {
            writeAll(element.getChildNodes(), writer);
        }
    }

    protected void write(Writer writer, CharacterData characterData) throws IOException {
        if (characterData instanceof Comment) {
            return;
        }
        writer.write(characterData.getNodeValue());
    }

    protected void write(Writer writer, Document document) throws IOException {
        writeAll(document.getChildNodes(), writer);
    }

    protected void write(Writer writer, DocumentType documentType) throws IOException {
        String name = documentType.getNodeName();
        if (!name.equals(DOCUMENT_TYPE)) {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Document type not supported; required \"" + DOCUMENT_TYPE + "\", found \"" + name + "\"");
        }
    }

    protected void writeAll(NodeList nodes, Writer writer) throws IOException, DOMException {
        int len = nodes.getLength();
        for (int i = 0; i < len; i++) {
            if (Thread.interrupted()) {
                throw new IOException("Thread interrupted while generating output");
            }
            write(writer, nodes.item(i));
        }
    }

    protected void writeRow(Node parent, NodeList nodes, Writer writer) throws IOException, DOMException {
        StringWriter sw = new StringWriter(1000);
        writeAll(nodes, sw);
        sw.close();
        writer.write(sw.getBuffer().toString());
    }

    protected void writeHeader(Node parent, NodeList nodes, Writer writer) throws IOException, DOMException {
        writeColumn(parent, nodes, writer);
    }

    protected void writeColumn(
            Node parent,
            NodeList nodes,
            Writer writer
    ) throws IOException, DOMException {

        StringWriter sw = new StringWriter(100);
        this.writeAll(nodes, sw);
        sw.close();

        String data = trim(sw.toString());
        if (!isValidNegativeNumber(data)) {
            data = escapeFormulaSymbols(data);
        }
        if (parent.getPreviousSibling() != null) {
            writer.write(",");
        }

        writer.write('"');
        writer.write(data.replace("\"", "\"\""));

        writer.write('"');
        if (parent.getNextSibling() == null) {
            writer.write(this.getLineEnding());
        }
    }

    /**
     * Set the content type (defaults to "text/html" or "text/xml" depending
     * on the document type
     */
    public final void setContentType(String icontentType) {
        contentType = icontentType;
    }

    /**
     * Get the content type
     */
    public final String getContentType() {
        return contentType;
    }

    /**
     * Set the content disposition (ie. "inline; filename=foo.txt",
     * defaults to null)
     */
    public final void setContentDisposition(String icontentDisposition) {
        contentDisposition = icontentDisposition;
    }

    /**
     * Get the content disposition
     */
    public final String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * Set whether or not to leave the writer open after writing
     */
    @Override
    public void setLeaveWriterOpen(boolean val) {
        leaveWriterOpen = val;
    }

    /**
     * Return true if the writer is configured to leave the output stream open
     */
    @Override
    public boolean getLeaveWriterOpen() {
        return leaveWriterOpen;
    }

    public final void setMaxAge(int maxAge) {
        assert maxAge > 0;
        this.maxAge = maxAge;
    }

    /**
     * Returns the max amount of time any cache can hold onto this document.
     *
     * @return the max age that intermediates may cache this document
     */
    public final int getMaxAge() {
        return maxAge;
    }

    /**
     * Set the headers to prevent caching. This will send the appropriate
     * headers to disallow intermediates from caching the data sent to
     * them from the request.
     *
     * @param prevent
     */
    public final void preventCaching(boolean prevent) {
        preventCaching = prevent;
    }


    public String getLineEnding() {
        return lineEnding;
    }

    /**
     * Sets the lineEnding to use for each row printed by the writer.
     *
     * @param lineEnding to use
     * @return the instance of the writer
     */
    public CommaSeparatedDOMWriter setLineEnding(String lineEnding) {
        this.lineEnding = lineEnding;
        return this;
    }

    public static boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidNegativeNumber(String str) {

        if (str == null || !str.startsWith("-")) {
            return false;
        }

        String number = str.substring(1);
        if (number.isEmpty()) {
            return false;
        }
        String removedSymbols = removePercentSign(removeCurrencySymbol(number));

        if (removedSymbols.length() >= 2 && !Character.isDigit(removedSymbols.charAt(0))) {
            return false;
        }
        return isNumeric(removedSymbols);
    }

    public String removeComma(String str) {
        return str.replace(",", "");
    }

    public String trim(String str) {
        return StringUtil.trim(str) == null ? "" : StringUtil.trim(str);
    }

    public String removeCurrencySymbol(String str) {
        if (!str.isEmpty() && isCurrencySymbol(str.codePointAt(0))) {
            return str.substring(Character.charCount(str.codePointAt(0)));
        } else {
            return str;
        }
    }

    public boolean isCurrencySymbol(int character) {
        return Character.getType(character) == Character.CURRENCY_SYMBOL;
    }

    public String removePercentSign(String str) {
        if (str.endsWith("%")) {
            return str.substring(0, str.length() - 1);
        } else  {
            return str;
        }
    }

    public String escapeFormulaSymbols(String field) {

        String trimmed = trim(field) == null ? "" : trim(field);

        if (!trimmed.isEmpty()) {
            char first = trimmed.charAt(0);

            if (first == '=' ||
                    first == '+' ||
                    first == '-' ||
                    first == '@') {

                return "'" + trimmed;
            }
        }

        return field;
    }
}
