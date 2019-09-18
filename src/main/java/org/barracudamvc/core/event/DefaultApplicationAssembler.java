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
 * $Id: DefaultApplicationAssembler.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.barracudamvc.core.util.dom.DOMLoader;
import org.barracudamvc.plankton.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class assembles a Barracuda system based on one or more XML
 * descriptor files. For a sample file look at
 * <code>/WEB-INF/classes/sample.event-gateway.xml</code>.  Event gateway files
 * are specified in the 'AssemblyDescriptor' init param of the
 * ApplicationGateway servlet in web.xml. See Barracuda's sample.web.xml for
 * example usage.
 * 
 * <p>One may specify a single file or use a pattern to load multiple files.
 * When loading multiple files, the pattern may consist of a plain directory
 * such as <code>/WEB-INF/assemblyfiles/</code> (making sure to add the
 * trailing '/'), a directory plus a partial file name such as
 * <code>/WEB-INF/event</code>, or even a directory plus a filename with an
 * asterisk as a wildcard such as <code>/WEB-INF/event*way.xml. The latter case
 * allows for specifying an alternate suffix to look for in the pattern
 * matching (for instance if the assembly files don't end in '.xml'). The
 * former two cases default to using '.xml' as the pattern matching suffix.</p>
 * 
 * <p>Supported elements of assembly descriptor files are (all attributes required unless otherwise specified):
 * <dl>
 *   <dt>&lt;event-gateway&gt;</dt>
 *     <dd>- nested in the root &lt;assemble&gt; element and itself</dd>
 *     <dd>- supports nesting of all elements including itself</dd>
 *     <dd>- attributes supported:
 *       <ul>
 *         <li>class</li>
 *       </ul>
 *     </dd>
 *     <dd>- Example usage... &lt;event-gateway class=&quot;o.e.b.core.event.DefaultEventGateway&quot;&gt; ... &lt;/event-gateway&gt;</dd>
 *   <dt>&lt;event-interest&gt;</dt>
 *     <dd>- nested in &lt;event-gateway&gt; elements</dd>
 *     <dd>- supports nesting of &lt;set-parameter&gt; and &lt;constant&gt; elements</dd>
 *     <dd>- attributes supported:
 *       <ul>
 *         <li>factory</li>
 *         <li>event</li>
 *       </ul>
 *     </dd>
 *     <dd>- Example usage... &lt;event-interest factory=&quot;o.e.b.examples.ex4.SampleControlHandler&quot; event=&quot;o.e.b.examples.ex4.events.Test1&quot;&gt; ... &lt;/event-interest&gt;</dd>
 *   <dt>&lt;event-alias&gt;</dt>
 *     <dd>- nested in &lt;event-gateway&gt; elements</dd>
 *     <dd>- no nested elements supported</dd>
 *     <dd>- attributes supported:
 *       <ul>
 *         <li>event</li>
 *       </ul>
 *     </dd>
 *     <dd>- Example usage... &lt;event-alias event=&quot;o.e.b.examples.ex4.events.Test4&quot; /&gt;</dd>
 *   <dt>&lt;constant&gt;</dt>
 *     <dd>- nested in all elements but &lt;event-alias&gt; elements</dd>
 *     <dd>- attributes supported:
 *       <ul>
 *         <li>class</li>
 *         <li>name</li>
 *         <li>delegateRuntimeValue - optional - if set, any provided value is ignored. Setting a &quot;delegate runtime value&quot; means that the value is
 *           not to be taken literally from configuration.  Rather, this responsibility is delegated to the application assembler to set the current runtime value.
 *           This is a limited feature and the only delegate runtime value currently supported is the ServletContext object.
 *         </li>
 *       </ul>
 *     </dd>
 *     <dd>- Example usage...
 *       <pre>
 *       &lt;constant class=&quot;o.e.b.core.event.ApplicationGateway&quot; name=&quot;showDebug&quot;&gt;1&lt;/constant&gt;
 *       or
 *       &lt;constant class=&quot;org.some.package.SomeClass&quot; name=&quot;servletContext&quot; delegateRuntimeValue=&quot;true&quot;/&gt;
 *       </pre>
 *     </dd>
 *   <dt>&lt;set-property&gt;</dt>
 *     <dd>- nested in all elements but &lt;event-alias&gt; elements</dd>
 *     <dd>- attributes supported:
 *       <ul>
 *         <li>name</li>
 *         <li>delegateRuntimeValue - optional - if set, any provided value is ignored. Setting a &quot;delegate runtime value&quot; means that the value is
 *           not to be taken literally from configuration.  Rather, this responsibility is delegated to the application assembler to set the current runtime value.
 *           This is a limited feature and the only delegate runtime value currently supported is the ServletContext object.
 *         </li>
 *       </ul>
 *     </dd>
 *     <dd>- Example usage...
 *       <pre>
 *       &lt;set-property name=&quot;hello&quot;>Hello World!&lt;/set-property&gt;
 *       or
 *       &lt;set-property name=&quot;servletContext&quot; delegateRuntimeValue=&quot;true&quot;/&gt;
 *       </pre>
 *     </dd>
 * </dl>
 */
public class DefaultApplicationAssembler extends DefaultHandler implements ApplicationAssembler {

    //public constants
    protected static final Logger logger = Logger.getLogger(DefaultApplicationAssembler.class.getName());
    //recognized tags
    private static final String CONSTANT = "constant";
    private static final String EVENT_ALIAS = "event-alias";
    private static final String EVENT_GATEWAY = "event-gateway";
    private static final String EVENT_INTEREST = "event-interest";
    private static final String SET_PROPERTY = "set-property";
    //recognized attributes
    private static final String CLASS = "class";
    private static final String DELEGATE = "delegateRuntimeValue";
    private static final String EVENT = "event";
    private static final String FACTORY = "factory";
    private static final String NAME = "name";

    /**
     * Assemble the system, given the root EventGateway and the
     * XML assembly decriptor name. The default parser will be
     * used.
     *
     * @param irootGateway the root EventGateway (req)
     * @param iservletConfig the ServletConfig object (may be null, if invoking
     *      from other than a servlet environment)
     * @param iassemblySourceFile the XML assembly descriptor (req)
     */
    public void assemble(EventGateway irootGateway, ServletConfig iservletConfig, String iassemblySourceFile) {
        assemble(irootGateway, iservletConfig, iassemblySourceFile, null);
    }

    /**
     * Assemble the system, given the root EventGateway, an
     * XML assembly decriptor name, and a specific SAX parser
     * class.
     *
     * @param irootGateway the root EventGateway (req)
     * @param iservletConfig the ServletConfig object (may be null, if invoking
     *      from other than a servlet environment)
     * @param iassemblySourceFile the XML assembly descriptor (req)
     * @param iparserClass the SAX parser class (if null, defaults to the parser provided by XMLReaderFactory.createXMLReader())
     */
    public void assemble(EventGateway irootGateway, ServletConfig iservletConfig, String iassemblySourceFile, String iparserClass) {
        //Setup the assemblerXmlReader
        AssemblerXMLReader assemblerXmlReader = new AssemblerXMLReader(iparserClass, irootGateway);
        assemblerXmlReader.setup();

        if (logger.isInfoEnabled())
            logger.info("Assembling system from input source");
        if (logger.isDebugEnabled())
            logger.debug("assembly path is: " + iassemblySourceFile);
        try {
            if (iservletConfig != null) {
                //running as a webapp

                ServletContext context = iservletConfig.getServletContext();
                assemblerXmlReader.setServletContext(context); //give config access to the ServletContext
                if (!hasMethod("getResourcePaths", context.getClass(), new Class[]{String.class})) {
                    //servlet 2.2...only support loading single assembly descriptor
                    InputStream is = getAssemblyResourceAsStream(context, iassemblySourceFile);
                    if (is != null) {
                        assemblerXmlReader.processXmlFile(is);
                    }
                } else {
                    //servlet 2.3+...support multiple assembly descriptors
                    //make sure we prepend "/" so string parsing is easier below
                    String normalizedResourcePath = "/" + getNormalizedPath(iassemblySourceFile);

                    //variables used for comparson against paths returned by context.getResourcePaths()
                    String dirPath = normalizedResourcePath.substring(0, normalizedResourcePath.lastIndexOf("/") + 1);
                    String fileMatch = normalizedResourcePath.substring(normalizedResourcePath.lastIndexOf("/") + 1);
                    String beginMatch = fileMatch; //default value
                    String endMatch = ".xml";    //default value
                    if (fileMatch.indexOf(".") != -1)
                        endMatch = fileMatch; //covers single file with suffix other than ".xml"

                    //take into account a wildcard (*) pattern match such as /WEB-INF/event*way.xml
                    if (fileMatch.indexOf("*") != -1) {
                        beginMatch = fileMatch.substring(0, fileMatch.indexOf("*"));
                        endMatch = fileMatch.substring(fileMatch.indexOf("*") + 1);
                        //take care of cases such as /WEB-INF/* or /WEB-INF/event*
                        if (!(endMatch.length() > 0))
                            endMatch = ".xml"; //fall back to default value
                    }

                    if (logger.isDebugEnabled())
                        logger.debug("dirPath is  : " + dirPath);
                    if (logger.isDebugEnabled())
                        logger.debug("fileMatch is: " + fileMatch);
                    if (logger.isDebugEnabled())
                        logger.debug("beginMatch is: " + beginMatch);
                    if (logger.isDebugEnabled())
                        logger.debug("endMatch is: " + endMatch);

                    Set paths = context.getResourcePaths(dirPath);
                    if (logger.isDebugEnabled())
                        logger.debug("resource paths returned: " + paths.size());
                    Iterator iter = paths.iterator();
                    while (iter.hasNext()) {
                        String currentPath = (String) iter.next();
                        if (logger.isDebugEnabled())
                            logger.debug("current path is: " + currentPath);
                        if (currentPath.indexOf(dirPath + beginMatch) != -1 && currentPath.endsWith(endMatch)) {
                            InputStream is = getAssemblyResourceAsStream(context, currentPath);
                            if (is != null) {
                                assemblerXmlReader.processXmlFile(is);
                            }
                        }
                    }
                }
            } else {
                //not running as a webapp
                //assumes that iassemblySourceFile points to a single xml file
                //and that it is a relative path available within the class loader
                //or it is an absolute file path to be loaded via File IO
                InputStream is = getAssemblyResourceAsStream(null, iassemblySourceFile);
                if (is != null) {
                    assemblerXmlReader.processXmlFile(is);
                }
            }
            if (logger.isInfoEnabled())
                logger.info("Assembly complete!");
        } catch (Exception e) {
            logger.warn("Error assembling system!", e);
            e.printStackTrace(System.err);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean hasMethod(String methodName, Class clazz, Class[] parameterTypes) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
        }
        if (method != null)
            return true;
        return false;
    }

    private static InputStream getAssemblyResourceAsStream(ServletContext context, String resource) {
        InputStream is = null;
        String normalizedResourcePath = getNormalizedPath(resource);
        //Attempt to load from the WEB-INF Dir
        if (context != null) {
            //first assume that the file was given the full path specified relative to the root of the webapp.
            //eg... "/WEB-INF/myresource.xml" or "WEB-INF/myresource.xml"
            if (logger.isDebugEnabled())
                logger.debug("Attempting to load assembly file via servlet context at: /" + normalizedResourcePath);
            is = context.getResourceAsStream("/" + normalizedResourcePath);

            //'is' should be non-null at this point.  Probably could get rid of
            //immediately below, but leaving for posterity
            if (is == null) {
                //maybe only the filename is provided with no extra path. Alternatively, maybe the path
                //given was a path relative to /WEB-INF/ rather than the root of the webapp. Try loading from /WEB-INF/.
                if (logger.isDebugEnabled())
                    logger.debug("Attempting to load assembly file via servlet context at: /WEB-INF/" + normalizedResourcePath);
                is = context.getResourceAsStream("/WEB-INF/" + normalizedResourcePath);
                if (is == null) {
                    //maybe only the filename is provided with no extra path.  Alternatively, maybe the path
                    //given was a package path, in which case the path would be relative to /WEB-INF/classes/
                    //rather than the root of the webapp. Try loading from /WEB-INF/classes/.
                    if (logger.isDebugEnabled())
                        logger.debug("Attempting to load assembly file via servlet context at: /WEB-INF/classes/" + normalizedResourcePath);
                    is = context.getResourceAsStream("/WEB-INF/classes/" + normalizedResourcePath);
                }
            }
        }

        //resource does not exist inside the webapp at the path specified.  Try loading via the classloader.
        if (is == null) {
            if (logger.isDebugEnabled())
                logger.debug("Attempting to load assembly file inside the local classloader: " + normalizedResourcePath);
            is = DefaultApplicationAssembler.class.getClassLoader().getResourceAsStream(normalizedResourcePath);
            if (is == null) {
                //this should only get triggered in the case that this class exists in a parent
                //classloader and the resource exists in a child classloader.  eg... in the WebappClassloader.
                if (logger.isDebugEnabled())
                    logger.debug("Attempting to load assembly file inside all available classloaders: " + normalizedResourcePath);
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(normalizedResourcePath);
            }
        }

        //Not a webapp or path specified is outside of the webapp. Attempt to load using the full file path
        if (is == null) {
            if (logger.isDebugEnabled())
                logger.debug("Attempting to load assembly file via file IO at: " + resource);
            File f = new File(resource);
            try {
                is = new FileInputStream(f);
            } catch (FileNotFoundException fnfe) {
            }
        }

        if (is == null) {
            logger.warn("Unable to load assembly file" + resource);
        }
        return is;
    }

    //Take a path a normalize it... eg. make WEB-INF/somefile.xml and /WEB-INF/somefile.xml equivalent
    private static String getNormalizedPath(String resourcePath) {
        String path = resourcePath;
        if (path != null) {
            if (path.startsWith("/"))
                path = (path.length() > 1) ? path.substring(1) : "";
        }
        return path;
    }

    /**
     * This class basically assembles a Barracuda system based on
     * an XML descriptor file.
     */
    class AssemblerXMLReader extends DefaultHandler {

        ServletContext servletContext;
        String parserClass = null;          //The Dom Parser
        EventGateway rootGateway = null;
        EventGateway curGateway = null;
        Stack<Object> objStack = null;
        DOMLoader domLoader = null;
        boolean needPropVal = false;
        String className = null;
        String propName = null;
        String propVal = null;
        String propDelegate = null;
        int depth = 0;
        XMLReader parser = null;            //Since the 'ProcessXml' method can be called multiple times,
        // a single instance of the XmlReader is needed.

        //default constructor
        public AssemblerXMLReader() {
        }

        public AssemblerXMLReader(String parserClass, EventGateway rootGateway) {
            setParserClass(parserClass);
            setRootGateway(rootGateway);
        }

        /**
         * Sets the SAX Parser
         * @param parserClass the SAX parser class (if null, defaults to the parser provided by XMLReaderFactory.createXMLReader())
         */
        public void setParserClass(String parserClass) {
            this.parserClass = parserClass;
        }

        /**
         * Sets the EventGateway that should be used
         * @param rootGateway the root EventGateway (req)
         */
        public void setRootGateway(EventGateway rootGateway) {
            this.rootGateway = rootGateway;
        }

        /**
         * optionally set ServletContext to make it available for
         * setting as a runtime delegate value. Eg...
         * &lt;set-property name=&amp;servletContext&amp; delegateRuntimeValue=&amp;true&amp;/&gt;
         */
        public void setServletContext(ServletContext iservletContext) {
            this.servletContext = iservletContext;
        }

        /**
         * Initialization stuff... (Need a better description here!)
         */
        public void setup() {
            try {
                if (parserClass != null) {
                    logger.info("Instantiating parser (" + parserClass + ")");
                    parser = XMLReaderFactory.createXMLReader(parserClass);
                } else {
                    parser = XMLReaderFactory.createXMLReader();
                }
            } catch (SAXException se) {
                logger.warn("Error assembling system!", se);
                se.printStackTrace(System.err);
            }
        }

        //This is the core method...
        public void processXmlFile(InputStream is) {
            try {
                //get the source file
                InputSource source = new InputSource(is);

                //parse the file
                logger.info("Parsing the source file...");
                parser.setContentHandler(this);
                parser.setErrorHandler(this);
                parser.parse(source);
            } catch (org.xml.sax.SAXParseException spe) {
                logger.warn("Error assembling system!", spe);
                spe.printStackTrace(System.err);
            } catch (org.xml.sax.SAXException se) {
                if (se.getException() != null) {
                    logger.warn("Error assembling system!", se.getException());
                    se.getException().printStackTrace(System.err);
                } else {
                    logger.warn("Error assembling system!", se);
                    se.printStackTrace(System.err);
                }
            } catch (Exception e) {
                logger.warn("Error assembling system!", e);
                e.printStackTrace(System.err);
            }
        }

        public void startDocument() {
            curGateway = rootGateway;
            objStack = new Stack<Object>();
            objStack.push(curGateway);
        }

        @SuppressWarnings("unchecked")
        public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException {
            String curTag = local;
            logger.debug("uri:" + uri + " local:" + local + " raw:" + raw + " attrs:" + attrs);

            //event-gateway
            if (curTag.equals(EVENT_GATEWAY)) {

                //figure out what the new gateway is
                depth++;
                EventGateway eg = null;
                String eventGatewayClassName = attrs.getValue(CLASS);
                logger.debug("Creating event gateway: " + eventGatewayClassName);
                try {
                    eg = (EventGateway) Class.forName(eventGatewayClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
                    objStack.push(eg);
                } catch (Exception e) {
                    String msg = "Error instantiating event gateway";
                    logger.warn(msg + ": ", e);
                    throw new SAXException(msg, e);
                }

                //add the event gateway to the current gateway and
                //then make the new gateway the current gateway
                logger.debug("Adding event gateway: " + eg.getClass().getName());
                if (curGateway != null)
                    curGateway.add(eg);
                curGateway = eg;

                //event-interest
            } else if (curTag.equals(EVENT_INTEREST)) {
                //figure out what factory we want to add
                String listenerFactoryClassName = attrs.getValue(FACTORY);

                //now see if the factory name is a property within the current Gateway
                ListenerFactory lf = null;
                try {
                    Field field = curGateway.getClass().getField(listenerFactoryClassName);
                    if (field != null) {
                        logger.debug("Getting listener factory from gateway: " + field);
                        lf = (ListenerFactory) field.get(curGateway);
                    }
                } catch (Exception e) {
                    logger.debug("Failed! " + e + " (this err is not fatal)");
                }

                //if not, we assume it's a class name and try to instantiate it
                if (lf == null) {
                    logger.debug("Creating listener factory: " + listenerFactoryClassName);
                    try {
                        lf = (ListenerFactory) Class.forName(listenerFactoryClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
//csc_122202.1                        objStack.push(lf);
                    } catch (Exception e) {
                        String msg = "Error instantiating listener factory";
                        logger.warn(msg + ": ", e);
                        throw new SAXException(msg, e);
                    }
                }

                //now push the listener factory onto the stack
                if (lf != null)
                    objStack.push(lf);    //csc_122202.1 - thanks to Srinivas Yermal [syermal@encover.com] for this patch


                //see if there is a specific event to register for
                String eventClassName = attrs.getValue(EVENT);
                Class<? extends BaseEvent> ev = null;

                if (eventClassName != null) {
                    logger.debug("Creating event class: " + eventClassName);
                    try {

                        ev = (Class<? extends BaseEvent>) Class.forName(eventClassName, true, Thread.currentThread().getContextClassLoader());
                    } catch (Exception e) {
                        String msg = "Error creating event class";
                        logger.warn(msg + ": ", e);
                        throw new SAXException(msg, e);
                    }
                }

                //now actually specify interests
                if (ev != null)
                    curGateway.specifyLocalEventInterests(lf, ev);
                else
                    curGateway.specifyLocalEventInterests(lf);

                //event-alias
            } else if (curTag.equals(EVENT_ALIAS)) {
                //see if there is a specific event to register for
                String eventClassName = attrs.getValue(EVENT);
                Class ev = null;
                logger.debug("Creating event class: " + eventClassName);
                try {
                    ev = Class.forName(eventClassName, true, Thread.currentThread().getContextClassLoader());
                } catch (Exception e) {
                    String msg = "Error creating event class";
                    logger.warn(msg + ": ", e);
                    throw new SAXException(msg, e);
                }

                //now actually specify alias'
                curGateway.specifyLocalEventAliases(ev);

                //set-property
            } else if (curTag.equals(SET_PROPERTY)) {
                //get the property name
                propName = attrs.getValue(NAME);
                propDelegate = attrs.getValue(DELEGATE);
                propVal = null;
                needPropVal = (propDelegate == null) ? true : false;

                //constant
            } else if (curTag.equals(CONSTANT)) {
                className = attrs.getValue(CLASS);
                propName = attrs.getValue(NAME);
                propDelegate = attrs.getValue(DELEGATE);
                propVal = null;
                needPropVal = (propDelegate == null) ? true : false;
            }
        }

        public void endElement(String uri, String local, String raw) throws SAXException {
            String curTag = local;

            //event-gateway
            if (curTag.equals(EVENT_GATEWAY)) {
                logger.debug("Finished w/ gateway: " + curGateway.getClass().getName());
                curGateway = curGateway.getParent();
                objStack.pop();
                depth--;

                //event-interest
            } else if (curTag.equals(EVENT_INTEREST)) {
                objStack.pop();

                //set-property
            } else if (curTag.equals(SET_PROPERTY)) {
                needPropVal = false;                                    //csc_111204_1
                Object parent = objStack.peek();
                try {
                    propVal = (propVal == null ? null : propVal.trim());  //csc_111204_1 - thanks to Nitin Vira <nvita@encover.com> for this patch.
                    String methodName = propName;
                    logger.debug("methodName:" + methodName);
                    Class clazz = parent.getClass();
                    Method methods[] = clazz.getMethods();
                    boolean success = false;
                    boolean delegate = Boolean.valueOf(propDelegate).booleanValue();
                    if (logger.isDebugEnabled())
                        logger.debug("delegate a runtime value? " + delegate);

                    for (int i = 0; i < methods.length; i++) {
                        Method m = methods[i];
                        if (!m.getName().equalsIgnoreCase("set" + propName))
                            continue;
                        methodName = m.getName();
                        Class paramTypes[] = m.getParameterTypes();
                        if (paramTypes.length > 1)
                            continue;
                        Class paramType = paramTypes[0];
                        if (logger.isDebugEnabled())
                            logger.debug("method:" + methodName + " paramType:" + paramType);
                        success = setMethod(parent, m, paramType, propVal, delegate);
                        if (!success)
                            continue;
                        if (logger.isDebugEnabled()) {
                            if (delegate)
                                logger.debug("delegate runtime setting of " + parent + "." + methodName + "(\"" + paramType + "\") successful");
                            else
                                logger.debug(parent + "." + methodName + "(\"" + propVal + "\") successful");
                        }
                        break;
                    }

                    if (!success) {
                        Field field = clazz.getField(propName);
                        Class paramType = field.getType();
                        success = setField(parent, field, paramType, propVal, delegate);
                        if (success) {
                            if (logger.isDebugEnabled()) {
                                if (delegate)
                                    logger.debug("delegate runtime setting of " + parent + "." + propName + " of type " + paramType + " successful");
                                else
                                    logger.debug(parent + "." + propName + "=\"" + propVal + "\" successful");
                            }
                        } else {
                            throw new SAXException(""); //message is already provided below...
                        }
                    }
                } catch (Exception e) {
                    String msg = "Error setting " + propName + " in parent:" + parent;
                    logger.warn(msg + ": ", e);
                    throw new SAXException(msg, e);
                }
            } else if (curTag.equals(CONSTANT)) {
                needPropVal = false;                                    //csc_111204_1
                Class clazz = null;
                boolean delegate = Boolean.valueOf(propDelegate).booleanValue();
                try {
                    propVal = (propVal == null ? null : propVal.trim());  //csc_111204_1 - thanks to Nitin Vira <nvita@encover.com> for this patch.
                    clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                    if (logger.isDebugEnabled())
                        logger.debug("class:" + className);
                    Field field = clazz.getField(propName);
                    Class paramType = field.getType();
                    if (logger.isDebugEnabled())
                        logger.debug("field:" + propName);
                    boolean success = setField(clazz, field, paramType, propVal, delegate);
                    if (success) {
                        if (logger.isDebugEnabled()) {
                            if (delegate)
                                logger.debug("delegate runtime setting of " + clazz + "." + propName + " of type " + paramType + " successful");
                            else
                                logger.debug(clazz + "." + propName + "=\"" + propVal + "\" successful");
                        }
                    } else {
                        throw new SAXException(""); //message is already provided below...
                    }
                } catch (Exception e) {
                    String msg = "Error setting " + propName + " in target:" + clazz;
                    logger.warn(msg + ": ", e);
                    throw new SAXException(msg, e);
                }
            }
        }

        public void characters(char ch[], int start, int length) {
            if (needPropVal) {
//csc_111204_1_start - thanks to Nitin Vira <nvita@encover.com> for this patch.
//                propVal = XMLUtil.fromXMLUnicodeString(new String(ch, start, length));
//                needPropVal = false;
                if (propVal == null)
                    propVal = "";
                propVal += XMLUtil.fromXMLUnicodeString(new String(ch, start, length));
//csc_111204_1_end
            } else {
                logger.debug("[characters] " + new String(ch, start, length));
            }
        }

        public void ignorableWhitespace(char ch[], int start, int length) {
            logger.debug("[whitespace] " + new String(ch, start, length));
        }

        public void warning(SAXParseException ex) {
            System.err.println("[Warning] " + getLocationString(ex) + ": " + ex.getMessage());
        }

        public void error(SAXParseException ex) {
            System.err.println("[Error] " + getLocationString(ex) + ": " + ex.getMessage());
        }

        public void fatalError(SAXParseException ex) throws SAXException {
            System.err.println("[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage());
        }

        private String getLocationString(SAXParseException ex) {
            StringBuffer str = new StringBuffer();
            String systemId = ex.getSystemId();
            if (systemId != null) {
                int index = systemId.lastIndexOf(47);
                if (index != -1)
                    systemId = systemId.substring(index + 1);
                str.append(systemId);
            }
            str.append(':');
            str.append(ex.getLineNumber());
            str.append(':');
            str.append(ex.getColumnNumber());
            return str.toString();
        }

        public boolean setMethod(Object target, Method m, Class paramType, String propVal, boolean delegate) throws InvocationTargetException, IllegalAccessException {
            if (paramType.equals(String.class)) {
                m.invoke(target, new Object[]{propVal});
                return true;
            } else if (paramType.equals(boolean.class) || paramType.equals(Boolean.class)) {
                m.invoke(target, new Object[]{new Boolean(propVal)});
                return true;
            } else if (paramType.equals(byte.class) || paramType.equals(Byte.class)) {
                m.invoke(target, new Object[]{new Byte(propVal)});
                return true;
            } else if (paramType.equals(char.class) || paramType.equals(Character.class)) {
                byte b[] = propVal.getBytes();
                m.invoke(target, new Object[]{new Character((char) b[0])});
                return true;
            } else if (paramType.equals(double.class) || paramType.equals(Double.class)) {
                m.invoke(target, new Object[]{new Double(propVal)});
                return true;
            } else if (paramType.equals(float.class) || paramType.equals(Float.class)) {
                m.invoke(target, new Object[]{new Float(propVal)});
                return true;
            } else if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
                m.invoke(target, new Object[]{new Integer(propVal)});
                return true;
            } else if (paramType.equals(long.class) || paramType.equals(Long.class)) {
                m.invoke(target, new Object[]{new Long(propVal)});
                return true;
            } else if (paramType.equals(short.class) || paramType.equals(Short.class)) {
                m.invoke(target, new Object[]{new Short(propVal)});
                return true;

                //begin delegate runtime value setting
            } else if (delegate && paramType.equals(ServletContext.class)) {
                m.invoke(target, new Object[]{servletContext});
                return true;
            } else {
                return false;
            }
        }

        public boolean setField(Object target, Field field, Class paramType, String propVal, boolean delegate) throws IllegalAccessException {
            if (paramType.equals(String.class)) {
                field.set(target, propVal);
                return true;
            } else if (paramType.equals(boolean.class) || paramType.equals(Boolean.class)) {
                field.set(target, new Boolean(propVal));
                return true;
            } else if (paramType.equals(byte.class) || paramType.equals(Byte.class)) {
                field.set(target, new Byte(propVal));
                return true;
            } else if (paramType.equals(char.class) || paramType.equals(Character.class)) {
                byte b[] = propVal.getBytes();
                field.set(target, new Character((char) b[0]));
                return true;
            } else if (paramType.equals(double.class) || paramType.equals(Double.class)) {
                field.set(target, new Double(propVal));
                return true;
            } else if (paramType.equals(float.class) || paramType.equals(Float.class)) {
                field.set(target, new Float(propVal));
                return true;
            } else if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
                field.set(target, new Integer(propVal));
                return true;
            } else if (paramType.equals(long.class) || paramType.equals(Long.class)) {
                field.set(target, new Long(propVal));
                return true;
            } else if (paramType.equals(short.class) || paramType.equals(Short.class)) {
                field.set(target, new Short(propVal));
                return true;

                //begin delegate runtime value setting
            } else if (delegate && paramType.equals(ServletContext.class)) {
                field.set(target, servletContext);
                return true;
            } else {
                return false;
            }
        }
    }
//kpd_101202.1_end

    public static void main(String[] args) {
        try {
            //manually configure the log4j stuff
            DOMConfigurator.configure("../../WEB-INF/log4j.xml");

            //manually run the assembler
            new DefaultApplicationAssembler().assemble(null, null, "../../WEB-INF/event-gateway.xml");
        } catch (Exception e) {
            System.out.println("Unexpected Exception: " + e);
            e.printStackTrace();
        }
    }
}
