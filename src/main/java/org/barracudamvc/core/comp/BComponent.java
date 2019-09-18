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
 * $Id: BComponent.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.RenderStrategy;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLComponentRenderer;
import org.barracudamvc.core.comp.renderer.xml.XMLComponentRenderer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * Defines the base component class from which all other Barracuda
 * components are derived. Its Swing counterpart would be JComponent.
 */
public class BComponent extends AbstractBComponent {
    //public constants

    protected static final Logger logger = Logger.getLogger(BComponent.class.getName());
    //constants
    public static final String VISIBILITY_MARKER = "visdom";
//csc_041905_1    public static String DEFAULT_ENCODING = "UTF-8";    //csc_041805_1
    public static String DEFAULT_ENCODING = System.getProperty("file.encoding");    //csc_041905_1
    //private vars
//NOTE: the name must remain null unless specifically set; setting the name
//will cause the name attribute to be set in the markup to which the component
//is bound (meaning, if the markup specifies a form name that will get overwritten
//if you specify a name)
//    protected String name = this.getClass().getName();
    protected String name = null;
    protected boolean visible = true;
    protected boolean enabled = true;
//072304_1 - moved to AbstractBComponent
//072304_1    protected Map attrs = null;
    protected RenderStrategy rs = null; //csc_110201.1
    protected String enc = null;    //csc_041805_1

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BComponent.class, HTMLElement.class);
        installRendererFactory(rfHTML, BComponent.class, HTMLDocument.class);

        XMLRendererFactory rfXML = new XMLRendererFactory();
        installRendererFactory(rfXML, BComponent.class, Node.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {

        public Renderer getInstance() {
            return new HTMLComponentRenderer();
        }
    }

    /**
     * XML RendererFactory
     */
    static class XMLRendererFactory implements RendererFactory {

        public Renderer getInstance() {
            return new XMLComponentRenderer();
        }
    }

    //--------------- BComponent ---------------------------------
    /**
     * Set the name for this component. 
     *
     * <p>Note that for several types of views (HTMLAnchorElement, HTMLAppletElement, 
     * HTMLButtonElement, HTMLFormElement, HTMLFrameElement, HTMLIFrameElement, 
     * HTMLInputElement, HTMLMapElement, HTMLMetaElement, HTMLObjectElement, 
     * HTMLParamElement, HTMLSelectElement, and HTMLTextAreaElement) this property 
     * will be used in rendering if it is actually set. This means that if you set 
     * the component name, and it is bound to a view that is backed by one of these
     * nodes, then the name attribute in that node will be overridden. So be careful!!!
     *
     * @param iname the name for this component
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BContainer> T setName(String iname) {
        name = iname;
        return (T) this;
    }

    /**
     * Get the name for this component
     *
     * @return the name for this component
     */
    public String getName() {
        return name;
    }

    /**
     * Set the component visibility
     *
     * @param val true if the component should be visible
     */
    public BComponent setVisible(boolean val) {
        return setVisible(val, false);
    }

    /**
     * Set the component visibility recursively
     *
     * @param val true if the component should be visible
     * @param recurse true if we want to set this value recursively
     */
    @Override
    public BComponent setVisible(boolean val, boolean recurse) {
        visible = val;
        if (recurse) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                BContainer child = (BContainer) it.next();
                if (child != null && child instanceof BComponent) {
                    BComponent wcomp = (BComponent) child;
                    wcomp.setVisible(val, recurse);
                }
            }
        }
        invalidate();
        return this;
    }

    /**
     * Get the component visibility
     *
     * @return true if the component is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Enable/disable the component 
     *
     * @param val true if the component should be enabled
     */
    public BComponent setEnabled(boolean val) {
        return setEnabled(val, false);
    }

    /**
     * Enable/disable the component recursively
     *
     * @param val true if the component should be enabled
     * @param recurse true if we want to set this value recursively
     */
    public BComponent setEnabled(boolean val, boolean recurse) {
        enabled = val;
        if (recurse) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                BContainer child = (BContainer) it.next();
                if (child != null && child instanceof BComponent) {
                    BComponent wcomp = (BComponent) child;
                    wcomp.setEnabled(val, recurse);
                }
            }
        }
        invalidate();
        return this;
    }

    /**
     * See if the component is enabled
     *
     * @return true if the component is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    //csc_041805_1 - added
    /**
     * Set the encoding for this component (and all children, unless they specifically
     * specify a different encoding). Defaults to DEFAULT_ENCODING
     *
     * @param enc the encoding for this component (null = look to parent)
     */
    public void setEncoding(String ienc) {
        enc = ienc;
    }

    //csc_041805_1 - added
    /**
     * Get the encoding for this component. If null, look to parent. If parent is null, default
     * to DEFAULT_ENCODING
     *
     * @return the encoding for this component
     */
    public String getEncoding() {
        if (enc != null)
            return enc;
        else if (parent != null && parent instanceof BComponent)
            return ((BComponent) parent).getEncoding();
        else
            return DEFAULT_ENCODING;
    }

    //csc_041805_1 - added
    /**
     * Actually encode a string based on the current encoding - note that we 
     * eat any acceptions that get thrown; if they occur we simply re-encode using
     * UTF-8
     */
    protected String encodeStr(String s) {
        String s2 = s;
        try {
            s2 = URLEncoder.encode(s, getEncoding());
        } catch (UnsupportedEncodingException e) {
            try {
                s2 = URLEncoder.encode(s, DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e2) {
                try {
                    s2 = URLEncoder.encode(s, "UTF-8");
                } catch (UnsupportedEncodingException e3) {
                    //if THAT doesn't work we're just going to return the string that came in
                }
            }
        }
        return s2;
    }

    //csc_041805_1 - added
    /**
     * Actually decode a string based on the current encoding - note that we 
     * eat any acceptions that get thrown; if they occur we simply re-encode using
     * UTF-8
     */
    protected String decodeStr(String s) {
        String s2 = s;
        try {
            s2 = URLDecoder.decode(s, getEncoding());
        } catch (UnsupportedEncodingException e) {
            try {
                s2 = URLDecoder.decode(s, DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e2) {
                try {
                    s2 = URLDecoder.decode(s, "UTF-8");
                } catch (UnsupportedEncodingException e3) {
                    //if THAT doesn't work we're just going to return the string that came in
                }
            }
        }
        return s2;
    }

    /**
     * Set the components primary view. This method effectively
     * removes any other views and binds the component to the 
     * newly specified view.
     *
     * @param view the view to which this component is bound
     */
    public BComponent setView(View view) {
        removeAllViews();
        return addView(view);
    }

    /**
     * Bind a component to a view
     *
     * @param view the view to which this component is bound
     */
    @Override
    public BComponent addView(View view) {
        if (view == null || (views != null && views.contains(view)))
            return this;
        if (views == null) {
            views = new ArrayList<View>();
        }
        views.add(view);
        invalidate();
        return this;
    }

    /**
     * Remove a view from this component
     *
     * @return true if we were able to remove the view from the component
     */
    public boolean removeView(View view) {
        if (view == null)
            return false;
        invalidate();
        // dbr_20020415.2
        return (views != null && views.remove(view));
    }

    /**
     * Remove all views from this component
     */
    public BComponent removeAllViews() {
        if (views != null)
            views.clear();
        invalidate();
        return this;
    }

    /**
     * Get a list of all the views for this component. This returns
     * a copy of the underlying view list.
     *
     * @return a List of all the views for this component
     */
    @Override
    public List<View> getViews() {
//jrk_20020414.1_start
        //make sure we don't send a null views object to the
        //ArrayList constructor or we'll get a NullPointerException
        //just return null if views is null
        //Note: views only seems to be null when the Barracuda libraries are in
        //the servlet container's common webapp lib directory (eg.. $TOMCAT_HOME/lib)
        //and not when they exist in the local webapp's WEB-INF/lib directory.  
        //Why is this????  Probably should find the root cause.  Likely has to do with
        //more classloader problems...
        //see if we have any views
        if (views == null)
            return null;
        else
            return new ArrayList<View>(views);
    }

    protected View getFirstView() {
        //see if we have any views
        if (views != null && views.size() > 0) {
            return (View) views.get(0);
        }

        //if not, see if the children have any views
        View firstView = null;
        if (children != null) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof BComponent) {
                    firstView = ((BComponent) o).getFirstView();
                    if (firstView != null)
                        break;
                }
            }
        }
        return firstView;
    }

    /**
     * set an attribute for this particular component. When the component
     * is rendered, component attributes will be shown as element attributes
     * in the elements that back each of the views associated with this component.
     * This means that if you set an attribute for the component, it will 
     * affect all views associated with the component.If you wish to set an 
     * attribute for a specific view alone, then you should get the view, find
     * the node that backs it, and then set the attribute manually that way.
     *
     * @param attr the attribute name
     * @param val the attribute value
     */
//csc_072604_1 - moved to AbstractBComponent
/*
     public void setAttr(Object attr, Object val) {
     if (attrs==null) attrs = new TreeMap();
     attrs.put(attr,val);
     invalidate();
     }
     */
    /**
     * get an attribute associated with this particular component. Note that
     * the attribute map that backs this method only keeps tracks of specific
     * attributes you have added to the component. It does not look at attributes
     * that are physically associated with the underlying elements that back each
     * of the views associated with this component. What this means is that if
     * the template that backs a view has some attribute "foo" and you try to
     * see the value of that attribute using this method, you will not be able 
     * to find it unless you have actually associated an attribute named "foo" 
     * with the specific component.
     *
     * @param attr the attribute name
     * @return the value for the given attribute (may be null)
     */
//csc_072604_1 - moved to AbstractBComponent
/*
     public Object getAttr(Object attr) {
     if (attrs==null) return null;
     return attrs.get(attr);
     }
     */
    /**
     * get a copy of the underlying component attribute Map
     *
     * @return a copy of the underlying component attribute Map
     */
//csc_072604_1 - moved to AbstractBComponent
/*
     public Map getAttrMap() {
     if (attrs==null) return null;
     return new TreeMap(attrs);    
     }
     */
    //csc_110201.1 - added
    /**
     * Set the render strategy for this component and all its children. 
     * Valid values include:
     * <ul>
     *   <li>RenderStrategy.SCRIPT_AS_NEEDED</li>
     *   <li>RenderStrategy.NEVER_SCRIPT</li>
     *   <li>RenderStrategy.CUSTOM_SCRIPT</li>
     *   <li>null</li>
     * <ul>
     *
     * <p>If the value is null, it will inherit setting from parent. If the 
     * setting for the root parent is null, it will default to 
     * RenderStrategy.DEFAULT_RENDER_STRATEGY
     *
     * @param irs the RenderStrategy
     */
    public BComponent setRenderStrategy(RenderStrategy irs) {
        rs = irs;
        return this;
    }

    //csc_110201.1 - added
    /**
     * Get the render strategy for this component
     *
     * @return the render strategy for this component
     */
    public RenderStrategy getRenderStrategy() {
        if (rs != null)
            return rs;
        else if (parent == null)
            return RenderStrategy.DEFAULT_RENDER_STRATEGY;
        else if (parent instanceof BComponent)
            return ((BComponent) parent).getRenderStrategy();
        else
            return RenderStrategy.DEFAULT_RENDER_STRATEGY;
    }

    /**
     * Render the component for a view with the specified 
     * ViewContext. You shouldn't override this method unless
     * you really know what you're doing...
     *
     * @param vc ViewContext for the client view
     * @throws RenderException if the particular View cannot be rendered
     */
    public BComponent render(ViewContext vc) throws RenderException {
        render(vc, 0);
        return this;
    }

    protected void render(ViewContext vc, int depth) throws RenderException {

        //if the component is not already validated, re-render it
        Iterator it = null;
        if (!validated) {
            try {
                //prepare for rendering 
                if (logger.isInfoEnabled())
                    logger.info("preparing to render comp " + this.toRef());
//                preRender(vc, 0);
                preRender(vc, depth);

                //add in any temp views
                if (logger.isInfoEnabled())
                    logger.info("adding in temp views " + this.toRef());
                if (tempViews != null) {
                    //jrk_20021018.1 - added null check for views
                    if (views == null)
                        views = new ArrayList<View>();
                    views.addAll(tempViews);
                }

                //render our own views
                if (logger.isDebugEnabled())
                    logger.debug("rendering our own view in comp " + this.toRef());
                int vcntr = -1;
                View view = null;
                it = views.iterator();
                while (it.hasNext()) {
                    view = (View) it.next();
                    if (view == null)
                        continue;
                    vcntr++;
                    try {
                        if (logger.isDebugEnabled())
                            logger.debug("view[" + vcntr + "] is bound to node: " + view.getNode());
                        if (view.getNode() instanceof Element) {
                            Element el = (Element) view.getNode();

                            //set visibility
                            if (isVisible()) {
                                el.removeAttribute(VISIBILITY_MARKER);
                            } else {
                                el.setAttribute(VISIBILITY_MARKER, "false");
                                return; //if the component is not visible, there is no need to go any further!!!
                            }
                        }

                        renderView(view, vc, depth + 1);
                    } catch (RenderException e) {
                        //for now just consume the exceptions
                        logger.warn("RenderException:" + e + " for View:" + view, e);
                    }
                }
                BContainer child = null;
                it = children.iterator();
                while (it.hasNext()) {
                    child = (BContainer) it.next();
                    if (child == null)
                        continue;
                    try {
                        if (child instanceof BComponent) {
                            BComponent wcomp = (BComponent) child;
                            if (wcomp.supports(vc)) {
                                wcomp.render(vc, depth + 1);
                            }
                        }
                    } catch (RenderException e) {
                        //for now just consume the exceptions
                        logger.warn("RenderException:" + e + " for Child:" + child, e);
                    }
                }
            } finally {
                //allow for any cleanup after render
                if (logger.isDebugEnabled())
                    logger.debug("cleaning up after render in comp " + this.toRef() + (this.hasChildren() ? "" : "(n/a)"));
//               postRender(vc, 0);
                postRender(vc, depth);

                //cleanup any step children
                removeAllStepChildren();

                //cleanup any temp views
                if (tempViews != null)
                    if (tempViews != null) {
                        it = tempViews.iterator();
                        while (it.hasNext()) {
                            View tempView = (View) it.next();
                            this.removeView(tempView);
                        }
                        tempViews = null;
                    }

                //now consider ourselves validated
                validated = true;
            }
        }

        if (logger.isInfoEnabled())
            logger.info("rendering complete in comp " + this.toRef());
    }

    /**
     * Prep phase before rendering. This is typically where you would put
     * any pre-rendering specific logic.
     */
//    protected final void preRender(ViewContext vc) {
//        preRender(vc, 0);
//    }
    protected void preRender(ViewContext vc, int depth) {
        //this is actually the method you should override when implementing
        //pre-render logic
        //(--n/a--)
    }

    /**
     * Render a specific view for the component. 
     *
     * @param view View to be rendered
     * @param vc ViewContext for the client view
     * @throws RenderException if the particular View cannot be rendered
     */
    protected final void renderView(View view, ViewContext vc) throws RenderException {
        renderView(view, vc, 0);
    }

    protected void renderView(View view, ViewContext vc, int depth) throws RenderException {
        //021102.3_csc_start - this method didn't used to do anything, which meant that
        //if you tried to use a plain-jane BComponent to control visibility it would never
        //actually get rendered. Dumb. THis should make it work now...
        if (logger.isInfoEnabled())
            logger.info("rendering view: " + view);

        //actually render the view according to known interfaces
        try {
            Renderer r = getRenderer(view);
            r.renderComponent(this, view, vc);

        } catch (DOMException e) {
            logger.warn("DOM Error:", e);
            throw new DOMAccessException("Error rendering component in view:" + e, e);
        }
        //021102.3_csc_end
    }

    /**
     * Cleanup after rendering. This method is guaranteed to be invoked, even 
     * if there is an error during rendering. This is typically where you would
     * put any custom post-rendering cleanup.
     */
//    protected final void postRender(ViewContext vc) {
//        postRender(vc, 0);
//  }
    protected void postRender(ViewContext vc, int depth) {
        //this is actually the method you should override when implementing
        //post-render logic
        //(--n/a--)
    }

    /**
     * Determine if a specific ViewContext is supported
     * by this component
     *     
     * @param vc ViewContext for the client view
     * @return true if the specified ViewCapabilites are supported
     */
    public boolean supports(ViewContext vc) {
        return true;
    }
}
