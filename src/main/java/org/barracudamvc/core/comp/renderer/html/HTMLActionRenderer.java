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
 * $Id: HTMLActionRenderer.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp.renderer.html;

import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.BAction;
import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BScriptResource;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.View;
import org.barracudamvc.core.comp.ViewContext;
import org.barracudamvc.core.comp.helper.FormGateway;
import org.barracudamvc.core.comp.renderer.RenderStrategy;
import org.barracudamvc.core.helper.servlet.ParamGateway;
import org.barracudamvc.core.helper.servlet.ResourceGateway;
import org.barracudamvc.core.util.http.URLRewriter;
import org.barracudamvc.core.view.ScriptingType;
import org.barracudamvc.plankton.http.LightweightURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLSelectElement;

/**
 * This class handles the default rendering of actions into an HTML view.
 * 
 * Note that its possible for a &lt;form&gt; element to come in null even if the 
 * element lives in a form. Cases where this might happen: when you are returning
 * a DOM fragment from another template, or when you are using a block iterator.
 * In both of these cases, the component may be bound to a block of DOM that has
 * not actually been added in as a child to the master template yet. In these cases,
 * the form the element belongs to will be null. A warning should show up in the logs
 * in these cases if log4j is appropriately configured.
 */
public class HTMLActionRenderer extends HTMLComponentRenderer {

    protected static final Logger logger = Logger.getLogger(HTMLActionRenderer.class.getName());

    /**
     * Render the data from the component into the view, taking into
     * consideration the specified ViewContext
     *
     * @param comp the component to be rendered
     * @param view the view the component should be rendered in
     * @param vc the view context
     * @throws RenderException if unable to render the component in the 
     *        specified view
     */
    public void renderComponent(BComponent comp, View view, ViewContext vc) throws RenderException {
        //make sure the component is a BAction component
        if (!(comp instanceof BAction)) {
            throw new NoSuitableRendererException("This renderer can only render BAction components; comp is of type:" + comp.getClass().getName());
        }

        //show what we're doing
        showNodeInterfaces(view, logger);

        //first, allow the parent class to do anything it needs to
        super.renderComponent(comp, view, vc);

        BAction actionComp = (BAction) comp;
        HTMLElement el = (HTMLElement) view.getNode();

        manipulateActionElement(el, actionComp, vc);

        //finally, make sure we reflect the components enabled/disabled status
        EnabledHelper.setEnabled(el, actionComp.isEnabled());
    }

    /**
     * Generic Element - cast elements to more specific elements in order to
     * call the appropriate overloaded method.  Throw an exception if the
     * element is not supported.
     *
     * @param el the HTML element to be manipulated
     * @param comp the BAction component containing information about the
     *        action to take on bound HTML element
     * @param vc the current ViewContext
     * @throws RenderException
     */
    protected static void manipulateActionElement(Element el, BAction comp, ViewContext vc) throws RenderException {
        if (el instanceof HTMLAnchorElement) {
            manipulateActionElement((HTMLAnchorElement) el, comp, vc);
        } else if (el instanceof HTMLFormElement) {
            manipulateActionElement((HTMLFormElement) el, comp, vc);
        } else if (el instanceof HTMLInputElement) {
            manipulateActionElement((HTMLInputElement) el, comp, vc);
        } else if (el instanceof HTMLButtonElement) {
            manipulateActionElement((HTMLButtonElement) el, comp, vc);
        } else if (el instanceof HTMLSelectElement) {
            manipulateActionElement((HTMLSelectElement) el, comp, vc);
        } else {
            String errmsg =
                    "Element does not implement one of HTMLAnchorElement, "
                    + " HTMLFormElement, HTMLInputElement, HTMLButtonElement, "
                    + " or HTMLSelectElement and cannot be rendered: " + el + " " + comp.getAction(vc);

            StringBuilder attributeBuilder = new StringBuilder();
            try {
                NamedNodeMap attributes = el.getAttributes();
                for (int idx = 0; idx < attributes.getLength(); idx++) {
                    Node node = attributes.item(idx);
                    attributeBuilder.append("\n")
                            .append(node.getNodeName())
                            .append(" = ")
                            .append(node.getNodeValue());
                }
            } catch (Exception ex) {
            }

            logger.warn(errmsg + "\n " + attributeBuilder.toString());
            throw new NoSuitableRendererException(errmsg);
        }
    }

    /**
     * HTMLAnchorElement - set the "href" attribute
     *
     * @see #manipulateActionElement(Element, BAction, ViewContext)
     */
    @SuppressWarnings("deprecation")
    protected static void manipulateActionElement(HTMLAnchorElement el, BAction comp, ViewContext vc) throws RenderException {
        //set the href
        if (comp.hasAction()) {
            el.setHref(comp.getAction(vc));
        }

        //if we need to disable the back button and the client supports
        //Javascript, we can do so by setting the onclick attribute
        if (comp.getDisableBackButton() && clientScriptingAllowed(comp, vc)) {
            el.setAttribute("onclick", "location.replace(this.href);return false;");
        }
    }

    /**
     * HTMLFormElement - set the "action" attribute
     *
     * @see #manipulateActionElement(Element, BAction, ViewContext)
     */
    protected static void manipulateActionElement(HTMLFormElement el, BAction comp, ViewContext vc) throws RenderException {
        if (logger.isInfoEnabled()) {
            logger.info("Rendering based on HTMLFormElement interface...");
        }

        //if the client supports Javascript...
        if (clientScriptingAllowed(comp, vc)) {
            scriptActionElement(el, comp, vc, "onsubmit");
        } else { //just set the form action
            el.setAction(comp.getAction(vc));
        }
    }

    /**
     * HTMLInputElement
     *
     * @see #manipulateActionElement(Element, BAction, ViewContext)
     */
    protected static void manipulateActionElement(HTMLInputElement el, BAction comp, ViewContext vc) throws RenderException {
        if (logger.isInfoEnabled()) {
            logger.info("Rendering based on HTMLInputElement interface...");
        }

        manipulateInputOrButtonActionElement(el, comp, vc, el.getForm());
    }

    /**
     * HTMLButtonElement
     *
     * @see #manipulateActionElement(Element, BAction, ViewContext)
     */
    protected static void manipulateActionElement(HTMLButtonElement el, BAction comp, ViewContext vc) throws RenderException {
        if (logger.isInfoEnabled()) {
            logger.info("Rendering based on HTMLButtonElement interface...");
        }

        manipulateInputOrButtonActionElement(el, comp, vc, el.getForm());
    }

    /**
     * HTMLInputElement and HTMLButtonElement (common logic)
     *
     * @see #manipulateActionElement(HTMLInputElement, BAction, ViewContext)
     * @see #manipulateActionElement(HTMLButtonElement, BAction, ViewContext)
     */
    private static void manipulateInputOrButtonActionElement(HTMLElement el, BAction comp, ViewContext vc, HTMLFormElement fel) throws RenderException {
        String elType = el.getAttribute("type");

        //if the client supports Javascript...
        if (clientScriptingAllowed(comp, vc)) {
            scriptActionElement(el, comp, vc, "onclick");


        } else { //else do it the manual way
            //make sure the button type is submit
            if (!"submit".equalsIgnoreCase(elType)) {
                if ("button".equalsIgnoreCase(elType)) {
                    //modify the <input> or <button> type to make this
                    //form submitable in a non-javascript environment.
                    el.setAttribute("type", "submit");
                } else {
                    if (comp.getRenderStrategy() != RenderStrategy.CUSTOM_SCRIPT) {
                        //whoops, it's an <input> element of neither type "submit",
                        //nor type "button" or a <button> of type "reset".
                        //Probably overstepping bounds in modifying the type
                        //attribute here, so throw exception instead.
                        String errmsg = "Cannot render Input|Button action listener; input|button type is not 'submit': " + el;
                        logger.warn(errmsg);
                        throw new NoSuitableRendererException(errmsg);
                    }
                }
            }
            noScriptNonFormActionElement(el, comp, vc, fel);
        }
    }

    /**
     * HTMLSelectElement
     *
     * @see #manipulateActionElement(Element, BAction, ViewContext)
     */
    protected static void manipulateActionElement(HTMLSelectElement el, BAction comp, ViewContext vc) throws RenderException {
        if (logger.isInfoEnabled()) {
            logger.info("Rendering based on HTMLSelectElement interface...");
        }

        //if the client supports Javascript...
        if (clientScriptingAllowed(comp, vc)) {
            scriptActionElement(el, comp, vc, "onchange");
        } else {
            if (comp.getRenderStrategy() != RenderStrategy.CUSTOM_SCRIPT) {
                String errmsg = "Cannot render Select action listener; client does not support Javascript: " + el;
                logger.warn(errmsg);
                throw new NoSuitableRendererException(errmsg);
            }
            noScriptNonFormActionElement(el, comp, vc, el.getForm());
        }
    }

///////////////////////// Internal Utility Functions /////////////////////////
    /**
     * Does the client support javascript, is javascript currently enabled on
     * the client, and does the current render strategy allow for javascript?
     * If so, then client scripting is allowed.
     *
     * @param comp the BAction component containing information about the
     *        action to take on bound HTML element
     * @param vc the current ViewContext
     * @return true if javascript is allowed, false if not
     */
    private static boolean clientScriptingAllowed(BAction comp, ViewContext vc) {
        return vc.getViewCapabilities().getScriptingType() instanceof ScriptingType.JavaScript1x
                && comp.getRenderStrategy() != RenderStrategy.NEVER_SCRIPT
                && comp.getRenderStrategy() != RenderStrategy.CUSTOM_SCRIPT;
    }

    /**
     * includes a script resource to the document.  Note: script resouces will
     * only be added once to the document.  Duplicates are ignored.
     *
     * @param el an HTMLElement providing a hook into the document
     * @param comp the BAction component the script resource is being assocated
     *        with
     * @param script the path to the script resource.  This is assumed to be
     *        part of the xlib resources.  An example string to pass in might
     *        be BScriptResource.JS_FORM_CONTROL
     * @see BScriptResource
     */
    private static void includeScriptLibrary(HTMLElement el, BAction comp, String script) {
        BScriptResource bsr = new BScriptResource(ResourceGateway.EXT_RESOURCE_ID + script);
        bsr.setView(new DefaultView(el));
        comp.addStepChild(bsr, true);
    }

    /**
     * adds the standard submit script for form elements which have a BAction
     * bound to them.  Also adds any script functions added to BAction.
     *
     * @param comp the BAction component being added to the document
     * @param sb the StringBuffer with which to add the script information
     * @param form the name to use for the javascript reference to the current
     *        form element.  eg... for a form element, the value would be
     *        "this".  For an element in a form, the value would be
     *        "this.form".
     * @see BAction#addScriptFunction(String)
     */
    @SuppressWarnings("deprecation")
    private static void addSubmitScript(BAction comp, StringBuffer sb, String form) {
        sb.append("return ");
        if (comp.getDisableFormLocking()) {
            sb.append("bmvc_doSubmitAndNoLock(");
        } else {
            sb.append("bmvc_doSubmitAndLock(");
        }
        sb.append(form);
        Collection scriptFunctions = comp.getScriptFunctions();
        if (scriptFunctions != null) {
            for (Iterator iter = scriptFunctions.iterator(); iter.hasNext();) {
                sb.append(",").append((String) iter.next());
            }
        }
        if (comp.getDisableBackButton()) {
            sb.append(",$bmvc_call_onsubmit,bmvc_SubmitAndReplace,$bmvc_submitted");   //csc_033105_1 - call $bmvc_call_onsubmit first; bmvc_submitted will return false, thereby keeping the form from being default-submitted
        }
        sb.append(");");
    }

    /**
     * Helper method encapsulating the scripting of HTMLFormElement,
     * HTMLInputElement, HTMLButtonelement, and HTMLSelectElement.
     * 
     * @param el the html element to which the component is bound
     * @param comp the component containing action information
     * @param vc the current ViewContext
     * @param event one of "onsubmit", "onclick", or "onchange"
     */
    @SuppressWarnings("deprecation")
    private static void scriptActionElement(HTMLElement el, BAction comp, ViewContext vc, String event) {
        boolean isFormElement = "onsubmit".equals(event) ? true : false;
        StringBuffer sb = new StringBuffer(200);
        StringBuffer sbAction = new StringBuffer((isFormElement ? 200 : 100));
        boolean disable = comp.getDisableBackButton();

        //adjust the documents action
        if (disable) {
            sbAction.append(ParamGateway.PARAM_TARGET);
        }
        LightweightURL lu = new LightweightURL(comp.getAction(vc, true));
        sbAction.append(lu.getBaseStr());
        if (disable) {
            sbAction.append(ParamGateway.PARAM_EXT);
        }
        if (isFormElement) {
            sbAction.append(lu.getParamStr());
            //redirect the form to param gateway servlet
            el.setAttribute("action", URLRewriter.encodeURL(vc, sbAction.toString()));

        } else {
            sb.append("this.form.action='").append(URLRewriter.encodeURL(vc, sbAction.toString())).append(lu.getParamStr()).append("';");
        }

        addSubmitScript(comp, sb, (isFormElement ? "this" : "this.form"));

        //adjust the appropriate event (onclick, onchange, onsubmit) to invoke submit
        el.setAttribute(event, sb.toString());

        //make sure we include the necessary script library
        includeScriptLibrary(el, comp, BScriptResource.JS_FORM_CONTROL);
        if (disable) {
            includeScriptLibrary(el, comp, BScriptResource.JS_CLIENT_SERVER_HTTP_LIB);
        }
    }

    /**
     * Helper method encapsulating the setting up of a form in a
     * non-scripting environment for non-HTMLFormElement's, eg...
     * HTMLInputElement, HTMLButtonelement, and HTMLSelectElement.
     * 
     * @param el the html element to which the component is bound
     * @param comp the component containing action information
     * @param vc the current ViewContext
     * @param fel the form the element, el, belongs to
     */
    private static void noScriptNonFormActionElement(HTMLElement el, BAction comp, ViewContext vc, HTMLFormElement fel) {
        //Various browsers handle name and value attributes of submit fields
        //differently.  To work around these differences, create a hidden
        //element with our desired named instead of renaming the submit button.

        //don't encode name attribute
        Document doc = el.getOwnerDocument();
        Element hiddenEle = doc.createElement("input");
        hiddenEle.setAttribute("type", "hidden");
        hiddenEle.setAttribute("name", FormGateway.FORM_TARGET + comp.getAction(vc, true));
        hiddenEle.setAttribute("value", "1");  // value must not be empty, otherwise some browsers won't submit it
        el.getParentNode().appendChild(hiddenEle);

        //redirect the form to event forwarding servlet (encode)
        String actionStr = URLRewriter.encodeURL(vc, NamingHelper.getName(fel) + FormGateway.FORM_EXT);
        if (fel != null) {
            fel.setAction(actionStr);
        } else {
            logger.warn("fel==null - you may need to manually set action='" + actionStr + "' in your template");
        }
    }
}
