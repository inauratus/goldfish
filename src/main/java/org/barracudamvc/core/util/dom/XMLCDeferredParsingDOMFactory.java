/*
 * Copyright (C) 2001  Jacob Kjome [hoju@visi.com]
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
 * $Id: XMLCDeferredParsingDOMFactory.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.util.dom;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.enhydra.xml.xmlc.XMLObject;
import org.enhydra.xml.xmlc.deferredparsing.XMLCDeferredParsingFactory;
import org.w3c.dom.Document;


/**
 * XMLC deferred parsing implementation of a DOMFactory. This class will load a
 * DOM using XMLCDeferredParsingFactory.
 * <p>Optionally, one may provide the XMLCDeferredParsingFactory that this DOMFactory
 * backs with context init parameters in the web.xml.  In order to do that, the
 * current ServletContext must be set using {@link #setServletContext(ServletContext)}
 * before adding a DOMFactory object to a {@link org.barracudamvc.core.util.dom.DOMLoader DOMLoader}
 * object. This can be done either programatically or set via the assembly descriptor with
 * <pre>
 * &lt;dom-loader factory=&quot;org.enhydra.xml.xmlc.deferredparsing.XMLCDeferredParsingFactory&quot;&gt;
 *   &lt;set-property name=&quot;servletContext&quot; delegateRuntimeValue=&quot;true&quot;/&gt;
 * &lt;/dom-loader&gt;
 * </pre></p>
 * <p>Once above requirement is met, one can set the following &lt;context-param&gt;'s in
 * the web.xml...
 * <ul>
 *   <li><tt>xmlcReparseResourceDirs</tt> - A list of additional directories to 
 *      load the html and meta data files, separated by File.separator</li>
 *   <li><tt>xmlcReparsePackagePrefixes</tt> - A list of package prefixes to be
 *      removed while searching for html and meta data files, relative to the
 *      classpath and resource dirs, separated by File.separator</li>
 *   <li><tt>xmlcReparseDefaultMetaDataPath</tt> - A path to the default meta data
 *      file, relative to the classpath and resource dirs</li>
 * </ul></p>
 * <p>These context init parameters match up exactly with those used by org.enhydra.xml.xmlc.servlet.XMLCContext.
 * Therefore, the configuration used for this class is entirely compatible with XMLCContext.</p>
 */
public class XMLCDeferredParsingDOMFactory implements DOMFactory {

    /**
     * used for logging
     */
    protected static final Logger logger = Logger.getLogger(XMLCDeferredParsingDOMFactory.class.getName());

    /**
     * Definition of xmlcReparseResourceDirs parameter.
     */
    protected final String PARAM_XMLC_REPARSE_RESOURCE_DIRS = "xmlcReparseResourceDirs";

    /**
     * Definition of xmlcReparsePackagePrefixes parameter
     */
    protected final String PARAM_XMLC_REPARSE_PACKAGE_PREFIXES = "xmlcReparsePackagePrefixes";

    /**
     * Definition of xmlcReparseDefaultMetaDataPath parameter
     */
    protected final String PARAM_XMLC_REPARSE_DEFAULT_METADATA_PATH = "xmlcReparseDefaultMetaDataPath";

    /**
     * optional, used to access context init parameters if set, ignored if null
     * 
     * @see #setServletContext(ServletContext)
     */
    protected ServletContext servletContext;

    /**
     * optional, used to access programatically set xmlc reparse resource dirs, ignored if null
     * 
     * @see #setXMLCReparseResourceDirs(String)
     */
    protected String xmlcReparseResourceDirs;

    /**
     * optional, used to access programatically set xmlc reparse package prefixes, ignored if null
     * 
     * @see #setXMLCReparsePackagePrefixes(String)
     */
    protected String xmlcReparsePackagePrefixes;

    /**
     * optional, used to access programatically set xmlc reparse default meta data path, ignored if null
     * 
     * @see #setXMLCReparseDefaultMetaDataPath(String)
     */
    protected String xmlcReparseDefaultMetaDataPath;

    /**
     * arbitrary object used to synchronize upon
     */
    protected final Object sync = new Object();

    /**
     * used for flagging whether the xmlcFactory has been initialized and
     * avoiding unnecessary synchronization.
     */
    protected boolean initialized = false;

    /**
     * XMLCFactory instance, stored so that it isn't re-created on
     * every request.
     */
    protected XMLCDeferredParsingFactory xmlcFactory;

    /**
     * Get a new instance of the DOM that is associated with the
     * given class
     *
     * @param clazz the class to be loaded as a DOM object. In this case, this class
     *        should implement XMLObject or the underlying XMLCFactory will
     *        not be able to instantiate it.
     * @return the document that most closely corresponds with the requested
     *         class/locale combination
     */
    public Document getInstance(Class clazz) throws IOException {
        if (!((XMLObject.class).isAssignableFrom(clazz))) throw new IOException ("Class "+clazz.getName()+" can not be loaded by this DOMFactory because it does not implement XMLOBject");
        initFactory();
        return xmlcFactory.create(clazz);
    }

    /**
     * Load a document directly from file. This method allows for skipping the compilation
     * of XMLC classes. In order to be used, the DOMLoader being utilized must support
     * calling this method. Make sure to provide &quot;xmlcReparseResourceDirs&quot;,
     * and &quot;xmlcReparseDefaultMetaDataPath&quot; &lt;context-param&gt;'s because
     * the path provide is relative to the provided resource dirs and the xmlc options file
     * must be available in order to load the document file.
     *
     * @param docPath path to file relative to provided resource directories
     * @return the document that most closely corresponds with the requested
     *         doc path/locale combination
     */
    @Override
    @SuppressWarnings("deprecation")
    public Document getInstance(String docPath) throws IOException {
        initFactory();
        return xmlcFactory.createFromFile(docPath);
    }

    /**
     * optional method to set the current servlet context. Must be set
     * before getInstance() is first called in order to be used.
     *
     * @param iservletContext the current servlet context
     */
    public void setServletContext(ServletContext iservletContext) {
        this.servletContext = iservletContext;
    }

    /**
     * optional method to directly set the xmlc resource dirs path. Note
     * that, if set, this supercedes the xmlcReparseResourceDirs context
     * param value (described above) obtained from a ServletContext.
     * This must be set before getInstance() is first called in order to be used.
     *
     * @param ireparseResourceDirs a list of additional directories to load the html and meta
     *        data files separated by File.separator
     */
    public void setXMLCReparseResourceDirs(String ireparseResourceDirs) {
        this.xmlcReparseResourceDirs = ireparseResourceDirs;
    }

    /**
     * optional method to directly set the xmlc reparse package prefixes. Note
     * that, if set, this supercedes the xmlcReparsePackagePrefixes context
     * param value (described above) obtained from a ServletContext.
     * This must be set before getInstance() is first called in order to be used.
     *
     * @param ireparsePackagePrefixes a list of package prefixes to be
     *        removed while searching for html and meta data files, relative to the
     *        classpath and resource dirs, separated by File.separator
     */
    public void setXMLCReparsePackagePrefixes(String ireparsePackagePrefixes) {
        this.xmlcReparsePackagePrefixes = ireparsePackagePrefixes;
    }

    /**
     * optional method to directly set the xmlc reparse default meta data path. Note
     * that, if set, this supercedes the xmlcReparseDefaultMetaDataPath context
     * param value (described above) obtained from a ServletContext.
     * This must be set before getInstance() is first called in order to be used.
     *
     * @param ireparseDefaultMetaDataPath a path to the default meta data
     *        file, relative to the classpath and resource dirs
     */
    public void setXMLCReparseDefaultMetaDataPath(String ireparseDefaultMetaDataPath) {
        this.xmlcReparseDefaultMetaDataPath = ireparseDefaultMetaDataPath;
    }

    private void initFactory() {
        if (initialized == false) {
            synchronized (sync) {
                logger.info("initializing an XMLC deferred parsing factory for returning XMLC-generated documents");
                xmlcFactory = new XMLCDeferredParsingFactory(null, Thread.currentThread().getContextClassLoader(), null);

                String resDirs = null;
                if (xmlcReparseResourceDirs != null) {
                    resDirs = xmlcReparseResourceDirs;
                } else {
                    if (servletContext != null) {
                        resDirs = servletContext.getInitParameter(PARAM_XMLC_REPARSE_RESOURCE_DIRS);
                    }
                }
                if (resDirs != null) {
                    StringTokenizer st = new StringTokenizer (resDirs, File.pathSeparator);
                    while (st.hasMoreTokens()) {
                        xmlcFactory.addResourceDir(st.nextToken());
                    }
                }

                String pkgPrefixes = null;
                if (xmlcReparsePackagePrefixes != null) {
                    pkgPrefixes = xmlcReparsePackagePrefixes;
                } else {
                    if (servletContext != null) {
                        pkgPrefixes = servletContext.getInitParameter(PARAM_XMLC_REPARSE_PACKAGE_PREFIXES);
                    }
                }
                if (pkgPrefixes != null) {
                    StringTokenizer st = new StringTokenizer (pkgPrefixes, File.pathSeparator);
                    while (st.hasMoreTokens()) {
                        xmlcFactory.addPackagePrefix(st.nextToken());
                    }
                }

                String defaultMetaDataPath = null;
                if (xmlcReparseDefaultMetaDataPath != null) {
                    defaultMetaDataPath = xmlcReparseDefaultMetaDataPath;
                } else {
                    if (servletContext != null) {
                        defaultMetaDataPath = servletContext.getInitParameter(PARAM_XMLC_REPARSE_DEFAULT_METADATA_PATH);
                    }
                }
                if (defaultMetaDataPath != null) {
                    xmlcFactory.setDefaultMetaDataPath(defaultMetaDataPath);
                }
                initialized = true;
            }
        }
    }

}
