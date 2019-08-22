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
 * $Id: EventBuilder.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.taskdefs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>This Ant taskdef reads in an xml file that conforms to
 * BarracudaEventBuilder.dtd and generates a set of event classes
 * according to the specified structure. The primary benefit of
 * this is that it makes it very easy to declaratively define event
 * hierarchies that still get compiled to real Java classes (thereby
 * retaining the benefits of strong typing which you get through the
 * manual approach)</p>
 *
 * <p>This taskdef takes two required parameters: <strong>sourceout</strong> (which
 * should point to a location to write generated .java event source files) and
 * <strong>descriptor</strong> (which should refer to the location of a valid xml file
 * describing the event hierarchy).<br>Second, there is a third optional parameter:
 * <strong>template</strong> which should refer to the localtion of the template file
 * for generating the Java classes. Specifying this parameter will overwrite the
 * template attribute inside the "build-events" tag in the xml file.</p>
 *
 * <p>Look at the Barracuda build.xml file for further usage examples.</p>
 *
 * <p>csc_010404_1 - 2 minor but important enhancements:
 *
 * a) you can now use the template attribute in all parts of the event file, making it
 * possible to specify different templates for individual events, and
 *
 * b) added the ability to replace any key values in the template, simply by including
 * them in the attributes. For instance, if you use a custom attribute which is NOT defined
 * in the dtd (ie. foo="blah"), the task will attempt to replace @foo@ in the specified template
 * with the value 'blah'.
 *
 * For examples of both of these things, see src\org\barracuda\examples\ex1\events.xml
 *
 * @author Christian Cryder [christianc@granitepeaks.com]
 * @author Thorsten Moeller - ThorstenMoeller(at)web.de
 *
 * @since   saw_101209_2 - copied from Barracuda simply so that we can locate a template in our own jar.
 */
public class EventBuilder extends Task {

    //private constants
    private static final String DEFAULT_PARSER = "org.apache.xerces.parsers.SAXParser";
    //private vars
    protected String parserClass = null;
    protected File xmlFile = null; //the xml file
    protected File templateFile = null; //the Java class template
    //csc_062204_1    protected String sourceOutDir = null; //the directory where source is generated
    protected File sourceOutDir = null; //the directory where source is generated     //csc_062204_1

    /**
     * Sets the xml event descriptor file.
     */
    public void setDescriptor(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    /**
     * Sets directory where source is generated.
     */
    public void setSourceout(String sourceOutDir) {
        this.sourceOutDir = getProject().resolveFile(sourceOutDir); //jrk_20040703_1
    }

    /**
     * Sets the Java class template.
     */
    public void setTemplate(File templateFile) {
        this.templateFile = templateFile;
    }

    /**
     * Parse the specified event.xml file, generate event classes from
     * it, and then compile the resulting classes.
     */
    @Override
    public void execute() throws BuildException {
        //instantiate the handler, entity resolver
        LocalHandler handler = new LocalHandler();
        LocalEntityResolver resolver = new LocalEntityResolver();

        //now parse the event.xml file
        try {
            //instantiate the parser
            if (parserClass == null) {
                parserClass = DEFAULT_PARSER;
            }
            //log("Instantiating parser "+parserClass, Project.MSG_DEBUG);
            //XMLReader parser = (XMLReader) Class.forName(parserClass, true, getClass().getClassLoader());
            XMLReader parser = getXMLReader(parserClass);
            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);
            parser.setEntityResolver(resolver);

            //get the source file
            log("Generating events from " + xmlFile, Project.MSG_DEBUG);
            InputSource source = new InputSource(new FileInputStream(xmlFile));

            //parse the file
            parser.parse(source);

        } catch (org.xml.sax.SAXParseException spe) {
            spe.printStackTrace(System.err);
            throw new BuildException("SAX Error parsing event.xml!", spe);
        } catch (org.xml.sax.SAXException se) {
            if (se.getException() != null) {
                se.getException().printStackTrace(System.err);
                throw new BuildException("SAX Error processing event.xml!", se.getException());
            } else {
                se.printStackTrace(System.err);
                throw new BuildException("SAX Error processing event.xml!", se);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new BuildException("Error processing event.xml!", e);
        } finally {
            //finally, make sure we clean up any accessed resources
            if (resolver != null) {
                resolver.cleanup();
            }
        }
    }

    /**
     * create SAX2 Parser (XMLReader) instance.  Attempts to load the passed-in
     * preferred parser first. If that fails, it falls back to loading the
     * parser set by the org.xml.sax.driver property. Failing that, other known
     * parsers are explicitly attempted for loading. If that fails, you are
     * trying really hard to defeat this method!
     *
     * @param preferred the preferred parser name, may be null
     * @return an XMLReader, guaranteed non-null
     * @throws SAXException when no SAX2 Parser is available
     */
    private XMLReader getXMLReader(String preferred) throws SAXException {
        XMLReader parser = null;
        try { // Preferred
            if (preferred != null) {
                parser = XMLReaderFactory.createXMLReader(preferred);
            } else {
                throw new SAXException("Preferred SAX parser unavailable!");
            }
        } catch (SAXException e1) {
            try { // default
                // obtain parser via system property...
                // loads whatever parser was set in the system property
                // org.xml.sax.driver and will also fall back to the SAX1
                // system property org.xml.sax.parser
                parser = XMLReaderFactory.createXMLReader();
            } catch (SAXException e2) {
                try { // Piccolo (speed demon: http://piccolo.sourceforge.net/bench.html -- non-validating, but can be wrapped by a validating parser)
                    parser = XMLReaderFactory.createXMLReader("com.bluecast.xml.Piccolo");
                } catch (SAXException e3) {
                    try { // Xerces (the standard)
                        parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                    } catch (SAXException e4) {
                        try { // Crimson (exists in j2sdk1.4.x)
                            parser = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
                        } catch (SAXException e5) {
                            try { // Aelfred (optionally validating)
                                parser = XMLReaderFactory.createXMLReader("gnu.xml.aelfred2.XmlReader");
                            } catch (SAXException e6) {
                                try { // older Aelfred (non-validating)
                                    parser = XMLReaderFactory.createXMLReader("net.sf.saxon.aelfred.SAXDriver");
                                } catch (SAXException e7) {
                                    // Oracle (well, why not?) ...last ditch attempt, let the exception go after this
                                    parser = XMLReaderFactory.createXMLReader("oracle.xml.parser.v2.SAXParser");
                                }
                            }
                        }
                    }
                }
            }
        }
        return parser;
    }

    /**
     * helper class to keep track of the various settings for a
     * given event node
     */
    class CurrentSettings {

        String template = null;
        String eventPackage = "";
        String eventName = null;
        String baseClass = null;
        Map<String, String> templateKeys = new HashMap<String, String>();   //csc_010404_1
        CurrentSettings parent = null;

        public CurrentSettings createChild() {
            CurrentSettings cs = new CurrentSettings();
            cs.template = template;
            cs.baseClass = baseClass;
            cs.eventName = eventName;
            cs.eventPackage = eventPackage;
            cs.parent = CurrentSettings.this;
            return cs;
        }

        public CurrentSettings getRoot() {
            if (parent == null) {
                return CurrentSettings.this;
            } else {
                return parent.getRoot();
            }
        }

        public String getParentClassName() {
            return (parent.eventName == null ? baseClass : parent.eventPackage + "." + parent.eventName);
        }

        @Override
        public String toString() {
            return "{" + eventName + " extends " + getParentClassName() + "}";
        }
    }

    /**
     * Helper class to actually parse the XML file (we use SAX)
     */
    class LocalHandler extends DefaultHandler {

        //tags
        String BUILD_EVENTS = "build-events";
        String CONTROL_EVENTS = "control-events";
        String REQ_EVENTS = "req-events";
        String RESP_EVENTS = "resp-events";
        String EVENT = "event";
        //attributes
        String PKG = "pkg";
        String TEMPLATE = "template";
        String BASE_CLASS = "base-class";
        String NAME = "name";
        //variables
        CurrentSettings cs = null;
        Map<String, byte[]> templateCache = new HashMap<String, byte[]>();
        int fileCnt = 0;

        @Override
        public void startDocument() {
            //set up the default settings
            cs = new CurrentSettings();
            cs.template = null;
            cs.baseClass = null;
            cs.eventName = null;
            cs.eventPackage = ".";
        }

        @Override
        public void startElement(String uri, String local, String raw, Attributes attrs) throws SAXException {
            String curTag = local;

            //whenever we start a new element, the first thing to do is to push
            //a copy of the current settings onto the stack
            cs = cs.createChild();

            //unload the attributes
            for (int i = 0, max = attrs.getLength(); i < max; i++) {
                String name = attrs.getLocalName(i);
                String value = attrs.getValue(i);
                if (name.equals(PKG)) {
                    cs.eventPackage = value;
                } else if (name.equals(TEMPLATE)) {
                    cs.template = value;
                } else if (name.equals(BASE_CLASS)) {
                    cs.baseClass = value;
                } else if (name.equals(NAME)) {
                    cs.eventName = value;
//csc_010404_1_start
                    //treat any other attributes as custom replacement key/vals for template
                } else {
                    log("got custom template key: " + name, Project.MSG_DEBUG);
                    cs.templateKeys.put("@" + name + "@", value);
//csc_010404_1_end
                }
            }

            //control-events
            if (curTag.equals(CONTROL_EVENTS)) {
                if (cs.baseClass == null) {
                    cs.baseClass = "org.barracudamvc.core.event.ControlEvent";
                }

                //req-events
            } else if (curTag.equals(REQ_EVENTS)) {
                if (cs.baseClass == null) {
                    cs.baseClass = "org.barracudamvc.core.event.HttpRequestEvent";
                }

                //resp-events
            } else if (curTag.equals(RESP_EVENTS)) {
                if (cs.baseClass == null) {
                    cs.baseClass = "org.barracudamvc.core.event.HttpResponseEvent";
                }

                //event
            } else if (curTag.equals(EVENT)) {
                boolean created = buildEventFile();
                if (created) {
                    fileCnt++;
                }
            }
        }

        @Override
        public void endElement(String uri, String local, String raw) throws SAXException {
            cs = cs.parent;
        }

        @Override
        public void endDocument() {
            if (fileCnt > 0) {
                log("Created " + fileCnt + " event files from: " + xmlFile + ((templateFile != null) ? ", using template: " + templateFile : ""));
            }
        }

        /*private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();
        String systemId = ex.getSystemId();
        if (systemId!=null) {
        int index = systemId.lastIndexOf(47);
        if (index!=-1) systemId = systemId.substring(index + 1);
        str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());
        return str.toString();
        }*/
        protected byte[] loadTemplate(String targetTemplate) {
            if (targetTemplate == null) {
                targetTemplate = "~dflt~";
            }
            byte[] templateBytes = (byte[]) templateCache.get(targetTemplate);
            if (templateBytes == null) {
                BufferedReader br = null;
                try {
                    if (templateFile != null && templateFile.exists()) {
                        br = new BufferedReader(new FileReader(templateFile));
                    } else if (cs.template != null) {
                        br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(cs.template)));
                    } else {
                        br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("EventBuilder.template")));
                    }
                    templateBytes = FileUtils.readFully(br).getBytes();
                    templateCache.put(targetTemplate, templateBytes);
                } catch (IOException e) {
                    throw new BuildException("Error reading template", e);
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        throw new BuildException("Error closing template", e);
                    }
                }
            }
            return templateBytes;
        }

        protected boolean buildEventFile() {
            //first of all, see if we need to create a new event file
            String targetPackage = cs.eventPackage.replace('.', '/');
//csc_062604_1            File targetPath = new File(sourceOutDir+"/"+targetPackage);
            File targetPath = new File(sourceOutDir, targetPackage);    //csc_062604_1
            File targetFile = new File(targetPath.toString(), cs.eventName + ".java");

            //if the target path does not exist create it
            if (!targetPath.exists()) {
//csc_091801 - this doesn't compile in JDK 1.2.2...
//                if (!targetPath.mkdirs()) throw new BuildException("Error creating path: "+targetPath, location);
                if (!targetPath.mkdirs()) {
                    throw new BuildException("Error creating path: " + targetPath, getLocation());
                }
            }

            //if the file exists and is newer than the events.xml timestamp,
            //skip it
            if (targetFile.exists()
                    && targetFile.lastModified() > xmlFile.lastModified()) {
                return false;
            }

            //get the template
            byte[] templateBytes = loadTemplate(cs.template);

            //create the file from the template
            try {
                targetFile.createNewFile();
                FileOutputStream out = new FileOutputStream(targetFile);
                out.write(templateBytes, 0, templateBytes.length);
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new BuildException("Error writing " + targetFile, e);
            }

            //now perform the replace
            Replace replace = new Replace();
//csc_091801 - this doesn't compile in JDK 1.2.2...
//            replace.setProject(project);
            replace.setProject(getProject());
            replace.setFile(targetFile);
            Replace.Replacefilter rf = null;
            //...package name
            rf = replace.createReplacefilter();
            rf.setToken("@event.package@");
            rf.setValue(cs.eventPackage);
            //...class name
            rf = replace.createReplacefilter();
            rf.setToken("@event.name@");
            rf.setValue(cs.eventName);
            //...parent class name
            rf = replace.createReplacefilter();
            rf.setToken("@event.parent@");
            rf.setValue(cs.getParentClassName());
            //...template keys
            for(Map.Entry<String, String> entry : cs.templateKeys.entrySet()) {
                rf = replace.createReplacefilter();
                rf.setToken((String) entry.getKey());
                rf.setValue((String) entry.getValue());
            }

            //...execute it!
            replace.execute();
            return true;
        }
    }

    /**
     * local entity resolver to convert references to remote DTDs
     * to local resources that can be loaded from the classpath
     */
    class LocalEntityResolver implements EntityResolver {

        String id = "http://barracudamvc.org/Barracuda";
        List<InputStream> resourceList = new ArrayList<InputStream>();

        public InputSource resolveEntity(String publicId, String systemId) {
            if (systemId.startsWith(id)) {
                // return a special input source
                String altId = "/xlib" + systemId.substring(id.length());
                InputStream is = this.getClass().getResourceAsStream(altId);
                BufferedInputStream in = new BufferedInputStream(is);
                resourceList.add(in);
                return new InputSource(in);
            } else {
                // use the default behaviour
                return null;
            }
        }

        public void cleanup() {
            for(InputStream inputStream : resourceList) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    System.out.println("unexpected error: " + e);
                }
            }
        }
    }
}

/*
 * $Log$
 */
