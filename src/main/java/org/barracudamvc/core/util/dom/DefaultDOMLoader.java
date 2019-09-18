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
 * $Id: DefaultDOMLoader.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.util.dom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;


/**
 * Default implementation of a DOMLoader
 *
 * @see DOMLoader
 */
public class DefaultDOMLoader implements DOMLoader {

    protected static final Logger logger = Logger.getLogger(DefaultDOMLoader.class.getName());

    /**
     * @see #getGlobalInstance()
     */
    protected static DefaultDOMLoader globalLoader = null;

    /**
     * arbitrary object used to synchronize upon
     */
    protected static final Object sync = new Object();

    /**
     * used for flagging whether the globalLoader has been initialized and
     * avoiding unnecessary instantiation and synchronization.
     */
    protected static boolean initialized = false;

    /**
     * @see #setDefaultDOMFactory(DOMFactory)
     */
    protected DOMFactory defaultDOMFactory = null;
    
    /**
     * @see #registerDOMFactory(DOMFactory, String)
     * @see #deregisterDOMFactory(String)
     */
    protected Map<String, DOMFactory> factories = null;
    
    /**
     * @see #lookupClass(String)
     */
    protected Map<String, Class> classmap = null;

    /**
     * Make sure no one can directly instantiate this class using the
     * default constructor.  To get an instance of this class, one must use
     * {@link #getGlobalInstance()}
     */
    private DefaultDOMLoader() {
        defaultDOMFactory = new XMLCDeferredParsingDOMFactory();
        factories = new HashMap<String, DOMFactory>();
        classmap = new HashMap<String, Class>();
    }

    /**
     * @see DOMLoader#getDOM(Class)
     */
    @Override
    public Document getDOM(Class clazz) throws IOException {
        return _getDOM(clazz, null);
    }

    /**
     * @see DOMLoader#getDOM(Class, Locale)
     */
    @Override
    public Document getDOM(Class clazz, Locale locale) throws IOException {
        return _getDOM(clazz, locale);
    }

    /**
     * Note: the default locale is currently ignored in this implementation
     *
     * @see DOMLoader#getDOM(String)
     */
    public Document getDOM(String docPath) throws IOException {
        return _getDOMFromFile(docPath, null);
    }

    /**
     * Note: the specified locale is currently ignored in this implementation
     *
     * @see DOMLoader#getDOM(String, Locale)
     */
    public Document getDOM(String docPath, Locale locale) throws IOException {
        return _getDOMFromFile(docPath, locale);
    }

    /**
     * All getDOM(Class) methods call this private method which takes care of
     * parameter validation and then calls _getDOM(String, Locale) which does
     * all the work
     */
    private Document _getDOM(Class clazz, Locale locale) throws IOException {
        //eliminate the obvious
        if (clazz==null) throw new IOException("Invalid class: class is null");
        return _getDOM(clazz.getName(), doGetLocaleCheck(locale));
    }

    /**
     * This private method does the work of loading a class based on
     * a particular locale in a way similar to how resource bundles work and
     * then invokes a dom factory to load the document associated with the class
     */
    private Document _getDOM(String className, Locale locale) throws IOException {
        //first we need to figure out the real class name (taking into account
        //the locale). To do this, we must understand the naming convention. As
        //with resource bundles, we'll look for class name + locale. For example,
        //if we request HelloWorldHTML.class, but the locale is Spanish (es),
        //then we should get back an instance of the DOM for HelloWorldHTML_es.class,
        //unless of course that class does not exist, in which case it should return
        //the DOM for HelloWorld.class. In this way, the DOM classes themselves act
        //like resource bundles.

        String baseName = className;
        Class targetClass = null;
        String targetName = null;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        targetName = baseName+"_"+language+"_"+country+"_"+variant;
        targetClass = lookupClass(targetName);
        if (targetClass==null) {
            targetName = baseName+"_"+language+"_"+country;
            targetClass = lookupClass(targetName);
        }
        if (targetClass==null) {
            targetName = baseName+"_"+language;
            targetClass = lookupClass(targetName);
        }
        if (targetClass==null) {
            targetName = baseName;
            targetClass = lookupClass(targetName);
        }
        if (logger.isDebugEnabled()) logger.debug("Target class: "+targetClass);
        if (targetClass==null) throw new IOException("Unexpected Invalid class: class is null"); //essentially impossible for this to happen

        //at this point we should have a valid class; now we
        //need to create a DOM for it
        synchronized (factories) {
            if (logger.isDebugEnabled()) logger.debug("Loading DOM");
            DOMFactory df = (DOMFactory) factories.get(targetName);
            if (df!=null) {
                return df.getInstance(targetClass);
            }
            return defaultDOMFactory.getInstance(targetClass);
        }
    }

    /**
     * All getDOM(String) methods call this private method which takes care of
     * parameter validation and then invokes a dom factory to load the document
     * located at the specified document path
     */
    private Document _getDOMFromFile(String docPath, Locale locale) throws IOException {
        //eliminate the obvious
        if (docPath==null) throw new IOException("Invalid document path:"+docPath);
        
        //need to create a DOM for it
        synchronized (factories) {
            if (logger.isDebugEnabled()) logger.debug("Loading DOM");
            DOMFactory df = (DOMFactory) factories.get(docPath);
            if (df!=null) {
                return df.getInstance(docPath);
            }
            return defaultDOMFactory.getInstance(docPath);
        }
    }

    /**
     * Checks the provided locale and returns a default locale if the one
     * provided is null
     */
    private static Locale doGetLocaleCheck(Locale locale) throws IOException {
        //if the locale is still null, just use the default
        if (locale==null) {
            locale = Locale.getDefault();
            if (logger.isDebugEnabled()) logger.debug("Using default locale: "+locale);
        }
        return locale;
    }

    /**
     * Find a stored reference to a class. If not found, instantiate it
     * and store it for later reference and faster retrieval.
     *
     * @param className the fully qualified name of a class
     * @return the instantiated class
     */
    protected Class lookupClass(String className) {
        synchronized (classmap) {
            //first see if we have a reference to that class in our classmap
            Class clazz = (Class) classmap.get(className);

            //if not, try to instantiate it
            if (clazz==null) {
                try {
                    clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                    classmap.put(className, clazz);
                } catch (Exception e) {}
            }
            return clazz;
        }
    }

    /**
     * Specify the default DOM factory
     *
     * @param df the DOMFactory to be used by default
     */
    public void setDefaultDOMFactory(DOMFactory df) {
        if (logger.isDebugEnabled()) logger.debug("Setting default DOM factory:"+df);
        synchronized (factories) {
            defaultDOMFactory = df;
        }
    }

    /**
     * Register a DOMFactory keyed against a fully qualified class name or a
     * document path
     *
     * @param df the DOMFactory
     * @param key the string which key's a particular DOMFactory to be used
     */
    public void registerDOMFactory(DOMFactory df, String key) {
        synchronized (factories) {
            factories.put(key, df);
        }
    }

    /**
     * Deregister a DOMFactory keyed against a fully qualified class name or a
     * document path
     *
     * @param key the string which key's a particular DOMFactory to be removed
     */
    public void deregisterDOMFactory(String key) {
        if (logger.isDebugEnabled()) logger.debug("Deregistering DOM factory for key:"+key);
        synchronized (factories) {
            factories.remove(key);
        }
    }

    /**
     * Get the global instance of the DefaultDOMLoader
     *
     * @return the global instance of the DefaultDOMLoader
     */
    public static DefaultDOMLoader getGlobalInstance() {
        if (initialized == false) {
            synchronized (sync) {
                logger.info("initializing global instance of DefaultDOMLoader");
                globalLoader = new DefaultDOMLoader();
                initialized = true;
            }
        }
        return globalLoader;
    }

}
