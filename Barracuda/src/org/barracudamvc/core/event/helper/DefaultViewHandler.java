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
 * $Id: DefaultViewHandler.java 252 2013-02-21 18:15:44Z charleslowery $
 */
package org.barracudamvc.core.event.helper;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.*;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BlockIterator;
import org.barracudamvc.core.comp.DefaultViewContext;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.TemplateDirective;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.event.*;
import org.barracudamvc.core.helper.servlet.ScriptDetector;
import org.barracudamvc.core.util.dom.DOMWriter;
import org.barracudamvc.core.util.dom.DefaultDOMWriter;
import org.barracudamvc.plankton.data.ObjectRepository;
import org.w3c.dom.Document;

/**
 * <p>A very simple view handler that provides a default implementation
 * tailored for the use of components. In many ways, this is analagous to
 * the ComponentGateway class--a component hierarchy is created, initialized
 * and rendered automatically for you. All the developer has to do is a)
 * implement the handleViewEvent method to add any custom components to the root
 * component (ie. for rendering) and b) return the master DOM page that needs
 * to be rendered to generate the final view.
 */
public abstract class DefaultViewHandler extends DefaultBaseEventListener {

    //public vars...eventually, these should probably be final
    private static final Logger logger = Logger.getLogger(DefaultViewHandler.class.getName());
    //private vars
    //csc_061202.1_start - add support for max age; also note that these should NOT be global
    //as they will cause the pretty printing and caching stuff to apply to the entire app, 
    //and realistically, we probably wouldn't want to do that...we'd probably want to do
    //it on a page level instead
//    protected boolean printPretty = false;
//    protected boolean preventCaching = false;
//    protected int maxAge = 0;
    //csc_061202.1_end
    protected boolean recycleChildren = false;
    protected ViewContext vc = null;    //csc_030503.1

    /**
     * Public noargs constructor
     */
    public DefaultViewHandler() {
    }

    //-------------------- DefaultViewHandler --------------------
    /**
     * <p>Handle a view event. This is the method developers will 
     * typically override. The developers shaould add any components
     * to the root component and then return the underlying DOM Document 
     * (that backs their components) so it can be rendered
     *
     * @param root the root component which will get rendered as a result 
     *         of this request
     * @return the Document to be rendered
     * @throws ServletException
     * @throws IOException
     */
//csc_030603.1    public abstract Document handleViewEvent(BComponent root, ViewContext vc) throws EventException, ServletException, IOException;
    public abstract Document handleViewEvent(BComponent root) throws EventException, ServletException, IOException; //csc_030603.1

    /**
     * The purpose of this method is to allow for optional pre-component-render 
     * cycle processing (ie. to stick a value in the user's session). If you need 
     * a reference to the view context, call getViewContext()
     *
     * @param root the root component which will get rendered as a result 
     *         of this request
     */
//csc_030603.1    public void preCompRender(BComponent root, ViewContext vc) {
    public void preCompRender(BComponent root) {    //csc_030603.1
        //(--n/a--)
    }

    /**
     * The purpose of this method is to allow for optional post-component-render 
     * cycle processing (ie. to remove a value from the user's session). If you need 
     * a reference to the view context, call getViewContext()
     *
     * @param root the root component which will get rendered as a result 
     *         of this request
     */
//csc_030603.1    public void postCompRender(BComponent root, ViewContext vc) {
    public void postCompRender(BComponent root) {   //csc_030603.1
        //(--n/a--)
    }

    /**
     * The purpose of this method is to allow for optional cleanup after all rendering is complete.
     * Guaranteed to be invoked every time
     */
    public void cleanup() {
        //(--n/a--)
    }

    /**
     * <p>Get a DOMWriter. By default, we use a DefaultDOMWriter. If
     * you'd like to use something else, override this method.
     *
     * @return a DOMWriter to be used to render the DOM
     */
    public DOMWriter getDOMWriter() {
        //return DefaultDOM writer
//        DefaultDOMWriter ddw = new DefaultDOMWriter(printPretty, preventCaching);
//        ddw.setMaxAge(maxAge);  //csc_061202.1
//        return ddw;
        //since we don't override defaults here and there are no setter methods for
        //printPretty, preventCaching, and maxAge in this class, don't bother storing
        //this stuff in this class.  Let DefaultDOMWriter deal with the defaults.
        //If one wants specific behavior, override this method and set up the DOMWriter
        //however you want.
        return new DefaultDOMWriter();
    }

    //csc_030503.1_start - make it possible to get a reference to the ViewContext
    //without having to pass it everywhere. NOte that this actually renders the ViewContext
    //paramter in handleViewEvent, preCompRender, and postCompRender obsolete, but for
    //now we'll just leave them in there (so we don't break existing code)
    /**
     * Set the view context
     */
    public void setViewContext(ViewContext ivc) {
        vc = ivc;
    }

    /**
     * Get the view context
     */
    public ViewContext getViewContext() {
        return vc;
    }
    //csc_030503.1_end

    /**
     * Get a block iterator (optional)
     */
    public BlockIterator getIterator(String key) {
        return null;
    }

    //-------------------- DefaultBaseEventListener --------------
    /**
     * Handle the ViewEvent
     */
    @Override
    public void handleViewEvent(ViewEventContext vec) throws EventException, ServletException, IOException {
        long bmillis = 0;
        long smillis = 0;
        long emillis = 0;
        long elapsed1 = 0;
        long elapsed2 = 0;
        if (logger.isInfoEnabled()) {
            bmillis = System.currentTimeMillis();
        }
        if (logger.isInfoEnabled()) {
            logger.info("Handling ViewEvent in " + this);
        }

        try {
            //start by figuring out the ViewCapabilities
            if (logger.isDebugEnabled()) {
                logger.debug("Create the ViewContext");
            }
            DefaultViewContext vc = new DefaultViewContext(vec);
            setViewContext(vc);

            //create our root component
            if (logger.isDebugEnabled()) {
                logger.debug("Create component root");
            }
            BComponent broot = new BComponent();
            broot.setName("Root");

            //give the implementation a chance to add any components to the root
            if (logger.isDebugEnabled()) {
                logger.debug("Handling default");
            }
            Document doc = handleViewEvent(broot);
            vc.setDocument(doc);                                   //csc_072604_2

            //now init the component
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking initCycle on component hierarchy");
            }
            broot.initCycle();

            //allow for pre-rendering processing
            if (logger.isDebugEnabled()) {
                logger.debug("Pre-component render");
            }
            if (logger.isDebugEnabled()) {
                smillis = System.currentTimeMillis();
            }
            preCompRender(broot);
            if (logger.isDebugEnabled()) {
                elapsed1 = System.currentTimeMillis() - smillis;
            }

            //now render the component
            if (logger.isDebugEnabled()) {
                logger.debug("Rendering component hierarchy");
            }
            if (logger.isDebugEnabled()) {
                smillis = System.currentTimeMillis();
            }
            broot.render(vc);
            if (logger.isDebugEnabled()) {
                elapsed1 = System.currentTimeMillis() - smillis;
            }

            //allow for post-rendering processing
            if (logger.isDebugEnabled()) {
                logger.debug("Post-component render");
            }
            if (logger.isDebugEnabled()) {
                smillis = System.currentTimeMillis();
            }
            postCompRender(broot);
            if (logger.isDebugEnabled()) {
                elapsed1 = System.currentTimeMillis() - smillis;
            }

            //its possible the implementor may want to recycle children...if so, remove them
            //prior to calling destroy on the root
            if (recycleChildren) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Recycling child components");
                }
                List children = broot.getChildren();
                if (children != null) {
                    for (int i = children.size() - 1; i >= 0; i--) {
                        broot.removeChild(i);
                    }
                }
            }

            //now destroy the component
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking destroyCycle on component hierarchy");
            }
            broot.destroyCycle();

            //check to see if the template contained any block iterators (TemplateHelper sets this value)        
            ObjectRepository lor = ObjectRepository.getLocalRepository();
            Boolean b = (Boolean) lor.getState(TemplateDirective.HAS_BLOCK_ITERATOR);
            boolean hasbi = (b != null ? b.booleanValue() : false);

            //if so, we want to delegate rendering to BlockIterateHandler
            if (hasbi) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Delegating the DOM Rendering to LocalBlockIterateHandler");
                }
                BlockIterateHandler bih = new LocalBlockIterateHandler();
                bih.handleViewEvent(vec, doc);

                //otherwise, we're just going to render it out ourselves (normal)
            } else {
                //now adjust the outgoing page (this is critical to make sure we
                //can accurately detect client scripting)
                ScriptDetector.prepareClientResp(doc, vc);

                //now render the page
                if (logger.isDebugEnabled()) {
                    logger.debug("Rendering the DOM");
                }
                if (logger.isInfoEnabled()) {
                    smillis = System.currentTimeMillis();
                }
                this.getDOMWriter().write(doc, vec.getResponse());
                if (logger.isInfoEnabled()) {
                    elapsed2 = System.currentTimeMillis() - smillis;
                    emillis = System.currentTimeMillis();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Finished rendering the DOM");
                }
            }

        } catch (RenderException e) {
            //if we get an EventException, handle it
            if (logger.isInfoEnabled()) {
                logger.warn("Unexpected RenderException:" + e);
                ApplicationGateway.logRequestDetails(logger, Level.WARN);    //csc_031005_1
            }
            throw new EventException("Unexpected RenderException:" + e, e);

        } catch (RuntimeException re) {
            //if we get an RuntimeException, just log that it happened
            if (logger.isInfoEnabled()) {
                logger.error("Unexpected RuntimeException:" + re, re);
                ApplicationGateway.logRequestDetails(logger, Level.ERROR);    //csc_031005_1
            }
            throw re;

        } finally {
            //finally, allow for any cleanup
            cleanup();
            setViewContext(null); //csc_030503.1
        }

        if (logger.isInfoEnabled()) {
            logger.info("ViewEvent handled! (rendered in " + (elapsed1) + "/written in " + (elapsed2) + " of " + (emillis - bmillis) + " millis)");
        }
    }

    class LocalBlockIterateHandler extends BlockIterateHandler {

        //-------------------- BlockIterateHandler -------------------
        /**
         * you must override this method to specify what template you wish to process
         * (Note that this method should never get called in LocalBlockIterateHandler
         * since we are loading the template up above in DefaultViewHandler and then passing
         * that in to the block iterator. So we just return null...)
         */
        @Override
        public Class getTemplateClass() {
            return null;
        }

        /**
         * delegate the getting of the DOMWriter back to the handler
         */
        @Override
        public DOMWriter getDOMWriter() {
            return DefaultViewHandler.this.getDOMWriter();
        }

        /**
         * this is where you provide iterators for blocks as they are encountered in the template
         */
        @Override
        public BlockIterator getIterator(String key) {
            return DefaultViewHandler.this.getIterator(key);
        }

        /**
         * Handle the ViewEvent
         */
        @Override
        public void handleViewEvent(ViewEventContext vec) throws EventException, ServletException, IOException {
            throw new RuntimeException("SimpleBlockIterateHandler does not support the handleViewEvent(ViewEventContext vec) method");
        }
    }
}
