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
 * $Id: ObjectRepositoryAssembler.java 260 2013-10-24 14:41:40Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.barracudamvc.plankton.Classes;
import org.barracudamvc.plankton.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class basically assembles objects into the default ObjectRepository based on
 * an XML descriptor file. For a sample file look at /WEB-INF/object-repository.xml. 
 * This class demonstrates all the basic functionality, so please be sure to look there 
 * first if you have questions. You may want to run TestObjectRepository.xml to see
 * it in action.
 *
 * <p>In a nutshell, the ObjectRepositoryAssembler is a lightweight scripting mechanism 
 * that makes it possible to instantiate objects, set properties, invoke methods, and 
 * then register objects in the global object repository if you so desire (hence the 
 * class name). This class is extremely useful for configuration, so it will be well 
 * worth your time to learn how it works.
 *
 * <p>There are only a few basic tags:
 * <ul>
 *   <li>
 *      <strong>object</strong> - this tag asks the assembler to instantiate an
 *          object, using the name, class, and arg parameters specified. The object
 *          will be instantiated directly after the start tag is processed (ie. before
 *          any method tags, since the object must exist in order for the methods
 *          to be invoked). If the class referenced has no public constructors, a reference
 *          to the class will be saved, allowing you to invoke public static methods.
 *
 *      <p>Attributes:<ul>
 *        <li>
 *           <strong>name</strong> - If you specify an object name (ie. '$obj1'),
 *              a reference to the object will be saved for the duration of the
 *              assembly process, allowing you to refer to the object elsewhere
 *              (ie. as a parameter of another method). Note that $this is a reserved
 *              name (the only one actually), which will give you a handle to the 
 *              ObjectRepositoryAssembler object, which also happens to be a servlet, thus
 *              making it possible to grab a reference to the ServletConfig object (if you
 *              are assembling in Standalone Application mode, the ServletConfig object
 *              will obviously not be avialable)
 *        </li><li>
 *           <strong>class</strong> - The name of the object to be instantiated. Note that
 *              if no class name is specified, the assembler looks in the object map
 *              for an existing object that matches of this name. This means that if you
 *              specify the class attribute, you will necessarily be forcing instantiation.
 *              So if you want to invoke methods on an existing object, DON'T provide the 
 *              class name) 
 *        </li><li>
 *           <strong>arg, arg0...arg9</strong> - You can pass args into the object
 *              constructor as well if need be. Frequently, the args might refer to other
 *              named objects.
 *        </li>
 *      </ul>
 *   </li>
 *   <li>
 *      <strong>method</strong> - this tag invokes a method on the object currently
 *          in scope. The method is not actually invoked until the end tag is hit
 *          (allowing us to get any param tags that might accompany the method).
 *
 *      <p>Attributes:<ul>
 *        <li>
 *           <strong>name</strong> - name of the method to be invoked
 *        </li><li>
 *           <strong>return</strong> - if the method returns an object you can give it 
 *              a return name which will cause the object to be available for later reference 
 *              by the return name
 *        </li><li>
 *           <strong>arg, arg0...arg9</strong> - You can pass args into the object
 *              constructor as well if need. Frequently, the args might refer to other
 *              named objects.
 *        </li>
 *      </ul>
 *
 *      <p>In addition to using arg attributes, you can use the value of the method
 *          tag (ie. if you only have one param), or you can use multiple param
 *          tags.
 *   </li>
 *   <li>
 *      <strong>prop</strong> - this tag allows you to set a property in an object (static
 *          or otherwise). The property is not actually set until the end tag is hit
 *          (allowing us to get any param tags that might accompany the prop).
 *
 *      <p>Attributes:<ul>
 *        <li>
 *           <strong>name</strong> - name of the property to be set
 *        </li>
 *      </ul>
 *
 *      <p>Unlike the method attribute, prop doesn't take arguments. Instead, you use the
 *          value of the prop to specify what the value should be set to. Right now, Strings,
 *          Integers, Shorts, Longs, Doubles, and Booleans are supported. If you need to set 
 *          something of a different type, email the list (its easy to add support for additional
 *          types).
 *   </li>
 *   <li>
 *      <strong>param</strong> - this tag specifies a parameter for a method. Note that
 *          the param type checking is not very sophisticated; if the param is an instance
 *          of a String (as opposed to a reference to a named object), the assembler
 *          will try it first as an int (if possible), else as a String.
 *
 *      <p>Attributes: n/a
 *   </li>
 *   <li>
 *      <strong>register</strong> - this tag asks the assembler to place the specified
 *          object into the default object repository (usually the global obj repos).
 *
 *      <p>Attributes:<ul>
 *        <li>
 *           <strong>key</strong> - the key value which may be used to retrieve the object
 *              from the default object repository
 *        </li><li>
 *           <strong>val</strong> - a reference to the item to be placed in the default
 *              object repository (ie. $obj1)
 *        </li>
 *      </ul>
 *   </li>
 * </ul>
 */
public class ObjectRepositoryAssembler extends HttpServlet {

    //public constants
    protected static final Logger logger = Logger.getLogger(ObjectRepositoryAssembler.class.getName());
    //private constants
    public static String ASSEMBLY_DESCRIPTOR = "AssemblyDescriptor";
    public static String SAX_PARSER = "SAXParser";
    public static String DEFAULT_DESCRIPTOR = "object-repository.xml";
    public static String DEFAULT_PARSER = "org.apache.xerces.parsers.SAXParser";    //not used any more
    public static String LOG_HEARTBEAT_STR = "LogHeartbeat";
    public static String GLOBAL_CONTINUE_ON_ERR = "ContinueAssemblyOnError";               //csc_041603.2
    //recognized tags
    private static final String OBJECT = "object";
    private static final String METHOD = "method";
    private static final String PROP = "prop";
    private static final String PARAM = "param";
    private static final String RETURN = "return";
    private static final String REGISTER = "register";
    //recognized attributes
    private static final String NAME = "name";
    private static final String CLASS = "class";
    private static final String ARG = "arg";
    private static final String KEY = "key";
    private static final String VAL = "val";
    private static final String CONTINUE_ON_ERR = "continue_on_err";        //csc_041603.2
    //reserved objMap vars
    private static final String THIS = "$this";
    //private vars
    protected boolean logHeartbeat = false;
    public static boolean globalContinueOnErr = false; //csc_041603.2

    //--------------- ObjectRepositoryAssembler ------------------
    /**
     * Assemble the system into the default ObjectRepository given the
     * XML assembly decriptor name. The default parser will be
     * used.
     *
     * @param iassemblySourceFile the XML assembly descriptor (if null defaults
     *      to DEFAULT_DESCRIPTOR)
     */
    public void assemble(String iassemblySourceFile) {
        assemble(null, null, iassemblySourceFile);
    }

    /**
     * Assemble the system, given the root EventGateway, an
     * XML assembly decriptor name, and a specific SAX parser
     * class.
     *
     * @param ior the repository we wish to assemble into (if null
     *      defaults to default ObjectRepository)
     * @param iservletConfig the ServletConfig object (may be null if you are
     *      calling this from other than a servlet environment)
     * @param iassemblySourceFile the XML assembly descriptor (if null defaults
     *      to DEFAULT_DESCRIPTOR)
     */
    public void assemble(ObjectRepository ior, ServletConfig iservletConfig, String iassemblySourceFile) {
        if (iassemblySourceFile == null)
            iassemblySourceFile = DEFAULT_DESCRIPTOR;

        try {
            InputStream is = findInputStream(iservletConfig, iassemblySourceFile);
            assemble(ior, is);
        } catch (Exception e) {
            logger.warn("Error assembling system!", e);
        }
    }

    public void assemble(ObjectRepository ior, InputStream is) {
        if (ior == null)
            ior = ObjectRepository.getGlobalRepository();

        AssemblerXMLReader assemblerXmlReader = new AssemblerXMLReader(ior);
        assemblerXmlReader.setup();
        assemblerXmlReader.processXmlFile(is);
    }

    private InputStream findInputStream(ServletConfig iservletConfig, String iassemblySourceFile) throws FileNotFoundException {
        InputStream is = null;
        if (iservletConfig != null) {
            is = findStreamForServlet(iservletConfig, iassemblySourceFile);
        }
        String normalizedFilePath = getNormalizedPath(iassemblySourceFile);
        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream(normalizedFilePath);
        }
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(normalizedFilePath);
        }
        //Not a webapp or path specified is outside of the webapp. Attempt to load using the full file path
        if (is == null) {
            File f = new File(iassemblySourceFile);
            is = new FileInputStream(f);
        }
        return is;
    }

    private InputStream findStreamForServlet(ServletConfig iservletConfig, String rawPath) {
        String path = getNormalizedPath(rawPath);
        InputStream is = iservletConfig.getServletContext().getResourceAsStream("/" + path);
        if (is == null) {

            is = iservletConfig.getServletContext().getResourceAsStream("/WEB-INF/" + path);
        }
        if (is == null) {
            is = iservletConfig.getServletContext().getResourceAsStream("/WEB-INF/classes/" + path);
        }
        return is;
    }

    //Take a path a normalize it... eg. make WEB-INF/somefile.xml and /WEB-INF/somefile.xml equivalent
    private static String getNormalizedPath(String filePath) {
        if (filePath != null) {
            if (filePath.startsWith("/"))
                filePath = (filePath.length() > 1) ? filePath.substring(1) : "";
        }
        return filePath;
    }

    //--------------- DefaultHandler -----------------------------
    public class AssemblerXMLReader extends DefaultHandler {

        ObjectRepository or = null;
        Stack<Object> objStack = null;
        boolean needPropVal = false;
        String propName = null;
        String propVal = null;
        int depth = 0;
        Map<String, Object> objMap = new HashMap<>();
        List<Object> argList = new ArrayList<>();
        String methodName = null;
        String returnName = null;
        int paramCntr = -1;
        boolean localContinueOnErr = false; //csc_041603.2
        String skipUntilTagName = null;     //csc_041603.2
        XMLReader parser = null;            //Since the 'ProcessXml' method can be called multiple times,
        // a single instance of the XmlReader is needed.
        String reference;

        public AssemblerXMLReader() {
            objMap.put(THIS, ObjectRepositoryAssembler.this);
        }

        public AssemblerXMLReader(ObjectRepository ior) {
            objMap.put(THIS, ObjectRepositoryAssembler.this);
            setObjectRepository(ior);
        }

        /**
         * Sets the ObjectRepository that should be used
         * @param ior the object repository
         */
        public void setObjectRepository(ObjectRepository ior) {
            this.or = ior;
        }

        /**
         * Initialization stuff... (Need a better description here!)
         */
        public void setup() {
            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                logger.info("Using sax parser factory " + spf);
                spf.setNamespaceAware(true);
                parser = spf.newSAXParser().getXMLReader();
                logger.info("Using sax parser impl " + parser);
            } catch (Exception e) {
                logger.warn("Error assembling system!", e);
            }
        }

        //This is the core method...
        public void processXmlFile(InputStream is) {
            try {
                //get the source file
                InputSource source = new InputSource(is);

                //parse the file
                logger.info("Assembling source file...");
                parser.setContentHandler(this);
                parser.setErrorHandler(this);
                parser.parse(source);
                logger.info("Assembly complete!");

            } catch (org.xml.sax.SAXParseException spe) {
                logger.warn("Error assembling system!", spe);
            } catch (org.xml.sax.SAXException se) {
                if (se.getException() != null) {
                    logger.warn("Error assembling system!", se.getException());
                } else {
                    logger.warn("Error assembling system!", se);
                }
            } catch (Exception e) {
                logger.warn("Error assembling system!", e);
            }
        }

        public void startDocument() {
            objStack = new Stack<Object>();
        }

        public void startElement(String uri, String curTag, String raw, Attributes attrs) throws SAXException {
            logger.debug("Starting w/: " + curTag);

            //csc_041603.2_start
            if (skipUntilTagName != null) {
                logger.warn("Skipping start tag <" + curTag + "> because of error handling <" + skipUntilTagName + ">");
                return;
            }
            String continue_on_err = attrs.getValue(CONTINUE_ON_ERR);
            localContinueOnErr = (continue_on_err != null
                    && (continue_on_err.toLowerCase().equals("true")
                    || continue_on_err.toLowerCase().equals("yes")
                    || continue_on_err.toLowerCase().equals("1")));
            boolean continueOnErr = (globalContinueOnErr || localContinueOnErr);
            //csc_041603.2_end

            if (curTag.equals(OBJECT)) {
                //object - we need to create a new object and store it in the object map
                if (handleObject(attrs, continueOnErr))
                    return;

                //method - invoke a method
            } else if (curTag.equals(METHOD)) {
                argList = new ArrayList<>();  //csc_111203_1 - any time we start a method, make sure we clean up the arglist
                methodName = attrs.getValue(NAME);
                returnName = attrs.getValue(RETURN);
                Object arg = resolve(attrs.getValue(ARG));
                if (arg != null)
                    argList.add(arg);
                for (int i = 0; i < 10; i++) {
                    arg = resolve(attrs.getValue(ARG + i));
                    if (arg != null)
                        argList.add(arg);
                }
                propVal = null;
                needPropVal = true;
                paramCntr = -1;

                //csc_052902.1 - added
                //prop - set a property
            } else if (curTag.equals(PROP)) {
                propName = attrs.getValue(NAME);
                propVal = null;
                needPropVal = true;
                paramCntr = -1;
                reference = attrs.getValue("ref");

                //param - parameter (add to args list)
            } else if (curTag.equals(PARAM)) {
                //the first time we hit a param tag after a method tag,
                //reset the argList (because it may contain a blank parameter)
                if (++paramCntr == 0)
                    argList = new ArrayList<Object>();
                propVal = null;
                needPropVal = true;

                //register - register an object in the repository
            } else if (curTag.equals(REGISTER)) {
                String key = attrs.getValue(KEY);
                Object val = resolve(attrs.getValue(VAL));
                String qt = (val instanceof String || val instanceof StringBuffer ? "'" : "");
                logger.info("Registering " + key + " = " + qt + val + qt + " in the object repository");
                or.putState(key, val);
            }
        }

        public void endElement(String uri, String local, String raw) throws SAXException {
            String curTag = local;

            //csc_041603.2_start
            if (skipUntilTagName != null) {
                if (skipUntilTagName.equals(curTag)) {
                    logger.warn("Found end of error tag <" + skipUntilTagName + ">");
                    skipUntilTagName = null;
                } else {
                    logger.warn("Skipping end tag <" + curTag + "> because of error handling <" + skipUntilTagName + ">");
                }
                return;
            }
            boolean continueOnErr = (globalContinueOnErr || localContinueOnErr);
            //csc_041603.2_end

            if (curTag.equals(OBJECT)) {
                //pop the object back off the stack
                objStack.pop();
                argList = new ArrayList<>();
                depth--;

            } else if (curTag.equals(PARAM)) {
                needPropVal = false;            //csc_111204_1

                //method - here's where we actually invoke the method
            } else if (curTag.equals(METHOD)) {
                if (handleMethod(continueOnErr, curTag))
                    return;
            } else if (curTag.equals(PROP)) {
                needPropVal = false;                         //csc_111204_1
                if (propVal != null)
                    propVal = propVal.trim(); //csc_111204_1
                logger.debug("Setting prop--> " + propName + "=" + propVal);

                //get the target object
                Object targetObj = objStack.peek();
                Field fld = null;
                Class ocl = (targetObj instanceof Class ? (Class) targetObj : targetObj.getClass());    //csc_041105_1
                while (true) {
                    Field allFields[] = ocl.getDeclaredFields();
                    for (int i = 0; i < allFields.length; i++) {
                        Field f = allFields[i];
                        if (f.getName().equals(propName) && Modifier.isPublic(f.getModifiers())) {
                            logger.debug("found property:" + f);
                            fld = f;
                            break;
                        }
                    }
                    if (ocl == Object.class)
                        break;
                    ocl = ocl.getSuperclass();
                }
                if (fld == null) {
                    String msg = "Unable to find matching property: " + propName;
                    logger.warn(msg);
                    if (continueOnErr) {
                        logger.warn("Skipping tag <" + curTag + "> because of error...");
                        skipUntilTagName = curTag;
                        return;
                    } else {
                        throw new SAXException(msg);
                    }
                }

                //now we need to set the property
                try {

                    Object targetProp = null;
                    if(reference != null) {
                        targetProp = resolve(reference);
                    } else if (propVal != null) {
                        Object iprop = fld.get(targetObj);
                        Class targetClass = String.class;
                        if (iprop != null) {
                            if (iprop instanceof Class)
                                targetClass = Class.class;
                            else
                                targetClass = iprop.getClass();
                        }
                        //..Integer
                        if (targetClass.equals(Integer.class)) {
                            targetProp = new Integer(propVal);

                            //..Short
                        } else if (targetClass.equals(Short.class)) {
                            targetProp = new Short(propVal);

                            //..Long
                        } else if (targetClass.equals(Long.class)) {
                            targetProp = new Long(propVal);

                            //..Double
                        } else if (targetClass.equals(Double.class)) {
                            targetProp = new Double(propVal);

                            //..Float
                        } else if (targetClass.equals(Float.class)) {
                            targetProp = new Float(propVal);

                            //..Class - //csc_010404_1 - added
                        } else if (targetClass.equals(Class.class)) {
                            targetProp = Classes.getClass(propVal);

                            //..Boolean
                        } else if (targetClass.equals(Boolean.class)) {
                            String tpropVal = propVal.toLowerCase().trim();
                            targetProp = new Boolean(tpropVal.equals("true") || tpropVal.equals("yes") || tpropVal.equals("on") || tpropVal.equals("1"));

                            //..String
                        } else {
                            targetProp = new String(propVal);
                        }
                    }
                    fld.set(targetObj, targetProp);

                } catch (IllegalAccessException e) {
                    String msg = "Unexpected IllegalAccessException:" + e;
                    logger.warn(msg);
                    if (continueOnErr) {
                        logger.warn("Skipping tag <" + curTag + "> because of error...");
                        skipUntilTagName = curTag;
                        return;
                    } else {
                        throw new SAXException(msg);
                    }
                }

                //make sure we clean up the argList
                argList = new ArrayList<Object>();
            }

            logger.debug("Finished w/: " + curTag);
        }

        public void characters(char ch[], int start, int length) {
            //csc_041603.2 - added
            if (skipUntilTagName != null)
                return;

            if (needPropVal) {
//csc_111204_1_start - 
// the purpose of this code is to handle the case where the character data actually comes in
// multiple segments...according to SAX API documentation for ContentHandler.characters method, 
// "The Parser will call this method to report each chunk of character data. SAX parsers may return all 
// contiguous character data in a single chunk, or they may split it into several chunks; however, all of 
// the characters in any single event must come from the same external entity so that the Locator provides 
// useful information."
//
// What this means is that we need to accumulate the character data into a string, rather than just assuming 
// that we've got the whole value. The problem is, in some cases we are going to be accumulating multiple 
// props. SO...if propVal is initially null, that tells us that we are ready to start a new prop - so we add
// a blank item to the prop list - then we accumulate the data by getting the last item in the list and adding
// onto that. Note that needPropVal must now get cleared when the end tags are hit.
//
// Special thanks to Nitin Vira <nvita@encover.com> who located this problem in DefaultApplicationAssembler
// and provided a patch there (which then allowed us to patch this here)
/*
                 propVal = XMLUtil.fromXMLUnicodeString(new String(ch, start, length)).trim();
                 if (propVal!=null) argList.add(propVal);
                 logger.debug("got propVal:"+propVal);
                 needPropVal = false;
                 */
                if (propVal == null) {
                    argList.add("");
                }
                propVal = (String) argList.get(argList.size() - 1);
                propVal += XMLUtil.fromXMLUnicodeString(new String(ch, start, length)).trim();
                argList.set(argList.size() - 1, propVal);
                logger.debug("propVal[" + (argList.size() - 1) + "]:" + propVal);
//csc_111204_1_end

            } else {
//                logger.debug("[characters] " + new String(ch, start, length));
            }
        }

        @Override
        public void ignorableWhitespace(char ch[], int start, int length) {
        }

        @Override
        public void warning(SAXParseException ex) {
            logger.warn(getLocationString(ex) + ": " + ex.getMessage());
        }

        @Override
        public void error(SAXParseException ex) {
            logger.error(getLocationString(ex) + ": " + ex.getMessage());
        }

        @Override
        public void fatalError(SAXParseException ex) throws SAXException {
            logger.fatal(getLocationString(ex) + ": " + ex.getMessage());
        }

        private String getLocationString(SAXParseException ex) {
            StringBuilder str = new StringBuilder();
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

        protected Object resolve(Object id) {
            if (id == null)
                return null;
            if (id.equals("null"))
                return null;
            if (id.equals("true"))
                return Boolean.TRUE;
            if (id.equals("false"))
                return Boolean.FALSE;
            if (objMap.containsKey(id)) {
                return objMap.get(id);
            } else {
                return id.toString();
            }
        }

        protected boolean handleMethod(boolean continueOnErr, String curTag) throws SecurityException, SAXException {
            needPropVal = false;
            StringBuilder mthsb = new StringBuilder(methodName + "(");
            if (argList.size() > 0) {
                //String s = "    args:";
                String sep = "";
                for (Object anArgList : argList) {
                    mthsb.append(sep).append(anArgList);
                    sep = ", ";
                }
                mthsb.append(")");
            }
            logger.debug("Invoking method--> " + mthsb.toString());
            Object targetObj = objStack.peek();
            List<Method> mthList = new ArrayList<Method>();
            Class ocl = (targetObj instanceof Class ? (Class) targetObj : targetObj.getClass());
            while (true) {
                Method allMethods[] = ocl.getDeclaredMethods();
                for (int i = 0; i < allMethods.length; i++) {
                    Method m = allMethods[i];
                    if (!m.getName().equals(methodName))
                        continue;
                    int mod = m.getModifiers();
                    if (!Modifier.isPublic(mod))
                        continue;
                    Class paramCl[] = m.getParameterTypes();
                    if (paramCl.length != argList.size())
                        continue;
                    logger.debug("found possible method:" + m + " paramCl:" + paramCl + " paramCl.length:" + paramCl.length);
                    if (paramCl.length > 0 && paramCl[0] == String.class)
                        mthList.add(0, m);    //methods which take String params should be given preference (since we're already dealing with String data)
                    else
                        mthList.add(m);
                }
                if (ocl == Object.class)
                    break;
                ocl = ocl.getSuperclass();
            }
            if (mthList.size() < 1) {
                String msg = "Unable to find matching method: " + mthsb.toString();
                logger.warn(msg);
                if (continueOnErr) {
                    logger.warn("Skipping tag <" + curTag + "> because of error...");
                    skipUntilTagName = curTag;
                    return true;
                } else {
                    throw new SAXException(msg);
                }
            }
            //now we need to iterate through the methods we found and see if we
            //can invoke them
            boolean success = false;
            for (Method aMthList : mthList) {
                //start by trying to convert the params into the target types
                Method m = (Method) aMthList;
                logger.debug("trying so see if we can invoke method " + m);
                Class paramTypes[] = m.getParameterTypes();
                Object args[] = new Object[paramTypes.length];
                for (int j = 0; j < argList.size(); j++) {
                    Object arg = argList.get(j);
                    if (arg instanceof String) {
                        String sarg = (String) arg;
                        //Class targetParamCl = null;
                        try {
                            //..Integer
                            if (paramTypes[j] == int.class) {
                                //targetParamCl = Integer.class;
                                args[j] = new Integer(sarg);

                                //..Short
                            } else if (paramTypes[j] == short.class) {
                                //targetParamCl = Short.class;
                                args[j] = new Short(sarg);

                                //..Long
                            } else if (paramTypes[j] == long.class) {
                                //targetParamCl = Long.class;
                                args[j] = new Long(sarg);

                                //..Double
                            } else if (paramTypes[j] == double.class) {
                                //targetParamCl = Double.class;
                                args[j] = new Double(sarg);

                                //..Float
                            } else if (paramTypes[j] == float.class) {
                                //targetParamCl = Float.class;
                                args[j] = new Float(sarg);

                                //..Class - //csc_010404_1 - added
                            } else if (paramTypes[j] == Class.class) {
                                //targetParamCl = Class.class;
                                args[j] = Classes.getClass(sarg);

                                //..String
                            } else {
                                args[j] = arg;
                            }
                        } catch (Exception e) {
                            logger.debug("error trying to cast arg" + j + " to " + paramTypes[j] + "...trying next method");
                        }
                    } else {
                        args[j] = arg;
                    }
                }

                //now try and invoke the method
                try {
                    logger.info("...Invoking " + targetObj.getClass().getName() + "@" + Integer.toHexString(targetObj.hashCode()) + "." + mthsb.toString());
                    boolean isAccessible = m.isAccessible();
                    m.setAccessible(true);
                    Object o = m.invoke(targetObj, args);
                    m.setAccessible(isAccessible);
                    if (returnName != null) {
                        logger.debug("...Saving reference " + returnName + " to " + (o != null ? o.getClass().getName() + "@" + Integer.toHexString(o.hashCode()) : "null"));
                        objMap.put(returnName, o);
                    }
                    success = true;
                    logger.debug("successfully invoked: " + m);
                    break;
                } catch (Exception e) {
                    logger.warn("error invoking method m:" + m, e);
                    continue;
                }
            }
            if (!success) {
                String msg = "Unable to invoke method: " + mthsb.toString();
                logger.warn(msg);
                if (continueOnErr) {
                    logger.warn("Skipping tag <" + curTag + "> because of error...");
                    skipUntilTagName = curTag;
                    return true;
                } else {
                    throw new SAXException(msg);
                }
            }
            //make sure we clean up the argList
            argList = new ArrayList<Object>();

            //csc_052902.1 - added
            //prop - here's where we actually set the property
            return false;
        }

        protected boolean handleObject(Attributes attrs, boolean continueOnErr) throws SAXException {
            //start by getting the attrs and any args
            depth++;
            String objname = attrs.getValue(NAME);
            String clname = attrs.getValue(CLASS);
            List<String> argNames = new ArrayList<>();
            for (int i = 0; i < attrs.getLength(); i++) {
                String attr = attrs.getLocalName(i);
                if (attr.toLowerCase().startsWith(ARG))
                    argNames.add(attr);
            }
            Object arg = resolve(attrs.getValue(ARG));
            //if you reference this, the class and arg attributes should be ignored
            if (THIS.equals(objname)) {
                clname = null;
                arg = null;
            } else {
                if (argNames.contains(ARG))
                    argList.add(arg);
                for (int i = 0; i < 10; i++) {
                    arg = resolve(attrs.getValue(ARG + i));
                    if (argNames.contains(ARG + i))
                        argList.add(arg);
                }
            }
            //first see if we can get the object from the map (this makes it possible
            //to reference a previously instantiated object). We only do this, however, if the
            //class name is NOT specified
            Class cl = null;
            Object obj = null;
            if (objname != null && clname == null)
                obj = objMap.get(objname);
            if (obj != null) {
                cl = obj.getClass();
                logger.info("Using existing obj " + obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode()));
            }
            //now try and instantiate it
            if (obj == null)
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Trying to create new --> objname:" + objname + " clname:" + clname);
                        if (argList.size() > 0) {
                            String s = "    args:";
                            String sep = "";
                            for (int i = 0; i < argList.size(); i++) {
                                s = s + sep + argList.get(i);
                                sep = ", ";
                            }
                            logger.debug(s);
                        }
                    }

                    cl = Thread.currentThread().getContextClassLoader().loadClass(clname);

                    if (argList.size() < 1) {
                        try {
                            obj = cl.newInstance();
                            logger.info("Instantiating " + obj.getClass().getName());

                            //this err occurs when the class has no public constructor; in that case, we 
                            //want to save a reference to the class, and then only allow static methods to
                            //be invoked
                        } catch (IllegalAccessException e) {
                            obj = cl;
                            logger.info("Creating handle @pt1 to class " + cl.getName() + " (for subsequent static invocation)");
//csc_041105_1_start
                        } catch (InstantiationException e) {
                            obj = cl;
                            logger.info("Creating handle @pt2 to class " + cl.getName() + " (for subsequent static invocation)");
//csc_041105_1_end                            
                        }
                    } else {
//TODO: csc_060805 - there is actually a bug in here where if the constructor takes a parent class/interface, but a parameter
//implements a more specific implementation, this code will not find the match. To fix this properly, we'd need to catch the 
//exceptions and try all possible permutations of parent classes and interfaces for the args given. Which is probably quite a 
//bit of work to do...
                        Object args[] = new Object[argList.size()];
                        Class argcl[] = new Class[argList.size()];
                        for (int i = 0; i < argList.size(); i++) {
                            args[i] = argList.get(i);
//csc_102405_3                            argcl[i] = argList.get(i).getClass();
                            argcl[i] = (args[i] == null ? Object.class : args[i].getClass());   //csc_102405_3
                        }

                        if (logger.isDebugEnabled()) {
                            StringBuilder sb = new StringBuilder("    Looking for complex constructor " + clname + "{");
                            String sep = "";
                            for (int i = 0; i < argList.size(); i++) {
                                sb.append(sep).append(argcl[i].getName());
                                sep = ", ";
                            }
                            sb.append("}");
                            logger.debug(sb.toString());
                        }
                        Constructor constructor = null;
                        List<Constructor> cssList = new ArrayList<Constructor>();
                        Constructor[] css = cl.getConstructors();
                        for (int i = 0; i < css.length; i++) {
                            Constructor cs = css[i];
                            Class[] csargs = cs.getParameterTypes();
                            if (csargs.length == argList.size())
                                cssList.add(cs);
                        }
                        Iterator it = cssList.iterator();
                        cssloop:
                        while (it.hasNext()) {
                            Constructor cs = (Constructor) it.next();
                            Class[] csargs = cs.getParameterTypes();
                            int j = -1;

                            //only check assignable if there is more than one constructor w/ same number of params
                            if (cssList.size() > 1)
                                for (j = 0; j < csargs.length; j++) {
                                    if (!csargs[j].isAssignableFrom(argcl[j]))
                                        continue cssloop;
                                }
                            if (j == csargs.length || cssList.size() == 1) {
//csc_102405_3_end
                                if (logger.isDebugEnabled()) {
                                    StringBuilder sb = new StringBuilder("    Found a match " + clname + "{");
                                    String sep = "";
                                    for (int k = 0; k < argList.size(); k++) {
                                        sb.append(sep).append(csargs[k].getName());
                                        sep = ", ";
                                    }
                                    sb.append("}");
                                    if (cssList.size() == 1)
                                        sb.append(" (this was the only constructor that matched the same number of params, so we are assuming its the correct one)");    //csc_102405_3
                                    logger.debug(sb.toString());
                                }
                                constructor = cs;
                                break cssloop;
                            }
                        }
                        if (constructor == null)
                            logger.debug("    No matching constructor!");
//csc_060905_1_end
                        obj = constructor.newInstance(args);
                        logger.info("Instantiating " + obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode()));
                    }
                } catch (Exception e) {
                    String msg = "Error instantiating object";
                    logger.warn(msg + ": ", e);
                    if (continueOnErr) {
                        logger.warn("Skipping tag <" + OBJECT + "> because of error...");
                        skipUntilTagName = OBJECT;
                        return true;
                    } else {
                        throw new SAXException(msg, e);
                    }
                }
            //push the object onto the stack
            //System.out.println("Here 1 - pushing onto stack: "+obj);
            objStack.push(obj);
            //finally, if there was a name register the object accordingly
            if (objname != null) {
                objMap.put(objname, obj);
            }

            //method - invoke a method
            return false;
        }
    }

    //--------------- HttpServlet --------------------------------
    /**
     * This class extends HttpServlet primarily for convenience, so that you easily
     * use it to set up a servlet environment. The servlet methods that handle requests
     * do not do anything; it all happens in the config.
     */
    @Override
    public void init() throws ServletException {
        logger.info("Attempting to setup default ObjectRepository (HTTP interface)");
        String descriptor = getInitParameter(ASSEMBLY_DESCRIPTOR);
        String hbparm = getInitParameter(LOG_HEARTBEAT_STR);
        if (hbparm != null) {
            hbparm = hbparm.toLowerCase();
            logHeartbeat = (hbparm.equals("true") || hbparm.equals("yes")
                    || hbparm.equals("on") || hbparm.equals("1"));
        }
        //csc_041603.2_start
        String continue_on_err = getInitParameter(GLOBAL_CONTINUE_ON_ERR);
        globalContinueOnErr = (continue_on_err != null
                && (continue_on_err.toLowerCase().equals("true")
                || continue_on_err.toLowerCase().equals("yes")
                || continue_on_err.toLowerCase().equals("1")));
        //csc_041603.2_end
        this.assemble(null, this, descriptor);
    }
}
