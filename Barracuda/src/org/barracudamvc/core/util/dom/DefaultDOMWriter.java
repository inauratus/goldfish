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
 * $Id: DefaultDOMWriter.java 254 2013-03-01 16:03:20Z charleslowery $
 */
package org.barracudamvc.core.util.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.enhydra.xml.io.DOMFormatter;
import org.enhydra.xml.io.OutputOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;

/**
 * This class provides the default implementation for writing a DOM.
 * XMLC is used for formatting the dom.  Besides output options features
 * supplied by XMLC, supported features include pretty printing and preventing
 * page caching (which prevents any redisplay of the page even via the back
 * button) or setting a max-age (which generally allows for redisplay of a
 * page via the back button, but not when revisiting the URL).  So, the prevent
 * caching and max age features are mutually exclusive. Default behavior is for
 * default output options (obtained automatically from the current document),
 * no pretty printing, and setting a max-age header of 0 (zero).  Defaults may
 * be modified via class static variables or overridden explicitly via
 * constructors and/or mutators.
 */
public class DefaultDOMWriter implements DOMWriter {

    protected static final Logger logger = Logger.getLogger(DefaultDOMWriter.class.getName());
    /**
     * Indicates whether the document should be pretty printed in the output
     * <tt>true></tt> if it should be.
     */
    public static boolean DEFAULT_PRETTY_PRINT_DOCUMENT = true;
    /**
     * Indicates whether the document should be output without spaces.
     */
    public static boolean PRESERVE_SPACES = false;
    /**
     * default OutputOptions public id (ie. 
     * <p>"-//W3C//DTD HTML 4.01 Transitional//EN"); 
     * <p>if set, the default output options will use this value
     */
    public static String DEFAULT_OO_PUBLIC_ID = null;
    /**
     * default OutputOptions system id (ie. 
     * <p>"http://www.w3.org/TR/html401/loose.dtd"); 
     * <p>if set, the default output options will use this value
     */
    public static String DEFAULT_OO_SYSTEM_ID = null;
    /**
     * default value for pretty printing, false unless modified at runtime
     */
    public static boolean DEFAULT_PRINT_PRETTY = false;
    /**
     * default value for preventing caching, false unless modified at runtime
     */
    public static boolean DEFAULT_PREVENT_CACHING = false;
    /**
     * default value for max age of rendered page, 0 (zero) unless modified at runtime
     */
    public static int DEFAULT_MAX_AGE = 0;
    protected DOMFormatter dfm = null;
    protected OutputOptions oo = null;
    protected String contentType = null;
    protected String contentDisposition = null;
    protected boolean printPretty = false;
    protected boolean preventCaching = false;
    protected boolean leaveWriterOpen = false;
    protected int maxAge = 0;

    /**
     * Default constructor
     */
    public DefaultDOMWriter() {
        this(null, DEFAULT_PRINT_PRETTY, DEFAULT_PREVENT_CACHING, DEFAULT_MAX_AGE);
    }

    /**
     * Public constructor. Allows you to specify OutputOptions.
     *
     * @param oo OutputOptions to specify how the DOM should be formatted
     */
    public DefaultDOMWriter(OutputOptions oo) {
        this(oo, DEFAULT_PRINT_PRETTY, DEFAULT_PREVENT_CACHING, DEFAULT_MAX_AGE);
    }

    /**
     * Public constructor. Allows you to specify pretty printing.
     *
     * @param printPretty true if pretty print
     */
    public DefaultDOMWriter(boolean printPretty) {
        this(null, printPretty, DEFAULT_PREVENT_CACHING, DEFAULT_MAX_AGE);
    }

    /**
     * Public constructor. Allows you to specify output options,
     * pretty printing and whether or not the page should be cached or the
     * max-age header for the page (how many seconds until it expires).
     * Note that the latter two options are mutually exclusive.
     *
     * @param ioo OutputOptions to specify how the DOM should be formatted, may be null
     * @param iprintPretty true if pretty print
     * @param ipreventCaching true if we want to prevent client side caching of the page
     * @param imaxAge how many seconds until the page should expire
     */
    public DefaultDOMWriter(OutputOptions ioo, boolean iprintPretty, boolean ipreventCaching, int imaxAge) {
        logger.info("Instantiating DOMWriter: " + this);
        setOutputOptions(ioo);
        setPrettyPrint(iprintPretty);
        setPreventCaching(ipreventCaching);
        setMaxAge(imaxAge);
    }

//csc_012804_1_start
    /**
     * Set the content type (defaults to "text/html" or "text/xml" depending on the 
     * document type
     */
    public void setContentType(String icontentType) {
        contentType = icontentType;
    }

    /**
     * Get the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the content disposition (ie. "inline; filename=foo.txt", defaults to null)
     */
    public void setContentDisposition(String icontentDisposition) {
        contentDisposition = icontentDisposition;
    }

    /**
     * Get the content disposition
     */
    public String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * Set true if we want to leave the writer open (ie. for multiple writes)
     */
    @Override
    public void setLeaveWriterOpen(boolean val) {
        leaveWriterOpen = val;
    }

    /**
     * return true if the writer is configured to leave the output stream open
     */
    @Override
    public boolean getLeaveWriterOpen() {
        return leaveWriterOpen;
    }

    /**
     * Prepare the response object
     *
     * @param node the DOM node to be written out
     * @param resp the HttpServletResponse object
     */
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

        //if we need to prevent caching
        if (preventCaching) {
            //add the appropriate headers to the response
            resp.setHeader("Pragma", "no-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expires", System.currentTimeMillis());

            //otherwise explicitly give it a max-age (this will generally allow browsers like
            //IE to page back in history without reloading, but if the user actually revisits the
            //URL, then it will still be reloaded)
        } else {
            resp.setHeader("Cache-Control", "max-age=" + maxAge);
            resp.setDateHeader("Last-Modified", System.currentTimeMillis());
        }
    }
//csc_012804_1_end

    /**
     * Write a DOM to a ServletResponse object. This method will
     * automatically set the content type for you.
     *
     * @param node the DOM node to be written out
     * @param resp the HttpServletResponse object
     */
    public void write(Node node, HttpServletResponse resp) throws IOException {
        prepareResponse(node, resp);
        write(node, new OutputStreamWriter(resp.getOutputStream()));
    }

    /**
     * Write a DOM to an OutputStream.
     *
     * @param node the DOM node to be written out
     * @param out the OutputStream to be written to
     */
    public void write(Node node, OutputStream out) throws IOException {
        //saw_031604_1 begin
        // use OutputStreamWriter instead of PrintWriter... there is not need to introduce
        // buffering here. In fact, using PrintWriter as it was used here could cause some
        // output not to get printed because the gc may reclaim the PrintWriter before output is
        // flushed.

//        write(node, new PrintWriter(out));
        write(node, new OutputStreamWriter(out));
        //saw_031604_1 end
    }

    /**
     * Write a DOM to a Writer.
     *
     * @param node the DOM node to be written out
     * @param writer the writer to be written to
     */
    @Override
    public void write(Node node, Writer writer) throws IOException {
        //build the default DOMFormatter if necessary
        if (dfm == null) {
            dfm = new DOMFormatter();
        }

        OutputOptions localoo = null;
        //set the default output options if necessary
        if (oo != null) {
            localoo = oo;
        }
        if (localoo == null) {
            localoo = getDefaultOutputOptions(node.getOwnerDocument());
        }

        if (printPretty) {
            localoo.setPrettyPrinting(DEFAULT_PRETTY_PRINT_DOCUMENT);
            localoo.setPreserveSpace(PRESERVE_SPACES);
            localoo.setIndentSize(2);
        }

        //configure the formatter
        dfm.setOutputOptions(localoo);

        //now print...
        //write the doc directly to the writer
        dfm.write(node, writer);
        if (!leaveWriterOpen) {
            writer.close();
        }
    }

    public void setPrettyPrint(boolean val) {
        this.printPretty = val;
    }

    public void setPreventCaching(boolean val) {
        this.preventCaching = val;
    }

    //csc_061202.1 - added
    public void setMaxAge(int imax) {
        this.maxAge = imax;
    }

    //jrk_20030317.1
    public void setOutputOptions(OutputOptions ioo) {
        this.oo = ioo;
    }

    /**
     * @since   saw_020204_1
     */
    public OutputOptions getOutputOptions() {
        return oo;
    }

    public static OutputOptions getDefaultOutputOptions(Document doc) {
        OutputOptions doo = DOMFormatter.getDefaultOutputOptions(doc);
        if (DEFAULT_OO_PUBLIC_ID != null || DEFAULT_OO_SYSTEM_ID != null) {
            doo.setOmitDocType(false);
        }
        if (DEFAULT_OO_PUBLIC_ID != null) {
            doo.setPublicId(DEFAULT_OO_PUBLIC_ID);
        }
        if (DEFAULT_OO_SYSTEM_ID != null) {
            doo.setSystemId(DEFAULT_OO_SYSTEM_ID);
        }
        return doo;
    }
}
