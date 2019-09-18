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
 * $Id: BAction.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLActionRenderer;
import org.barracudamvc.core.event.BaseEvent;
import org.barracudamvc.core.event.ControlEvent;
import org.barracudamvc.core.event.ListenerFactory;
import org.barracudamvc.core.event.events.ActionEvent;
import org.barracudamvc.core.util.http.URLRewriter;
import org.w3c.dom.html.HTMLElement;

/**
 * A BAction component is a component that you can use to catch
 * client side events. It can be used to render &lt;a&gt;, &lt;form&gt;, &lt;button&gt;,
 * &lt;input&gt;, and &lt;select&gt; elements. It allow you to specify a specific
 * action to be generated, or can simply default to what's in the
 * template markup. You can also add event listeners directly to the
 * component, and only those particular listeners will be notified when
 * the event actually occurs on the client.
 *
 * <p>In most cases you will not actually need to bind the component
 * to a view in order to use it--if you return it from a model, this
 * will be done for you automatically. If however, you intend to use
 * the component <em>standalone</em> (ie. manually attaching it to a
 * specific node in the DOM) or <em>inline</em> (ie. in a toString()),
 * then you MUST BIND IT TO A VIEW before rendering, or an error will
 * be generated.
 */
public class BAction extends BComponent {

    //public vars
    protected static final Logger logger = Logger.getLogger(BAction.class.getName());
    public static boolean DEFAULT_DISABLE_BACK_BUTTON = false;
    public static boolean DEFAULT_DISABLE_FORM_LOCKING = false;
    //private vars
    protected ControlEvent actionEvent = null;
    protected String actionUrl = null;
    protected List<ListenerFactory> listeners = null;
    protected Map<String, Object> params = null;
    protected Collection<String> scriptFunctions = null; //ideally, implementation should provide Set-like functionality
    protected boolean disableBackButton = DEFAULT_DISABLE_BACK_BUTTON;
    protected boolean disableFormLocking = DEFAULT_DISABLE_FORM_LOCKING;

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BAction() {
    }

    /**
     * Public constructor which creates the component and binds it
     * to a view. Also allows you to specify that a custom url be fired.
     *
     * @param iactionUrl the action url to be fired
     */
    public BAction(String iactionUrl) {
        if (iactionUrl != null)
            this.setAction(iactionUrl);
    }

    /**
     * Public constructor which creates the component and binds it
     * to a view. Also allows you to specify that a custom event be fired.
     *
     * @param iactionEvent the action event to be fired
     */
    public BAction(ControlEvent iactionEvent) {
        if (iactionEvent != null)
            this.setAction(iactionEvent);
    }

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BAction.class, HTMLElement.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {

        public Renderer getInstance() {
            return new HTMLActionRenderer();
        }
    }

    //--------------- BAction ------------------------------------
    /**
     * Set the action to be fired by this component.
     *
     * @param iactionUrl the URL representing the action to be fired
     */
    public BAction setAction(String iactionUrl) {
        this.actionUrl = iactionUrl;
        this.invalidate();
        return this;
    }

    /**
     * Set the action to be fired by this component.
     *
     * @param iactionEvent the event to be fired by this component
     */
    public BAction setAction(ControlEvent iactionEvent) {
        this.actionEvent = iactionEvent;
        this.invalidate();
        return this;
    }

    //csc_041403.1 - added
    /**
     * Check for an existing action
     *
     * @return true if either the action (either URL or Event) has been set
     */
    public boolean hasAction() {
        return (this.actionUrl != null || this.actionEvent != null || (this.listeners != null && this.listeners.size() > 0));
    }

    /**
     * Get the action to be fired by this component. The action
     * will either be the URL or the Event that backs this component
     *
     * @param vc the ViewContext that determines the context of the action
     * @return a string representing the action to be fired by this component
     */
    public String getAction(ViewContext vc) {
        return this.getAction(vc, false);
    }

    //csc_101701.1 - this method (with additional parameter) added
    /**
     * Get the action to be fired by this component. The action
     * will either be the URL or the Event that backs this component
     *
     * @param vc the ViewContext that determines the context of the action
     * @param preventRewriting this should be set to true only if you want to
     *      prevent URLRewriting from taking place. As a developer, you
     *      will probably never need to do this, unless to need to further
     *      modify the action prior to finally encoding it (HTMLActionRenderer
     *      does this)
     * @return a string representing the action to be fired by this component
     */
    public String getAction(ViewContext vc, boolean preventRewriting) {
        StringBuilder sb = new StringBuilder(200);
        String sep = "?";

        //build the base action
        if (this.actionUrl != null) {
            //from the url
            if (preventRewriting)
                sb.append(this.actionUrl);
            else
                sb.append(URLRewriter.encodeURL(vc, this.actionUrl));    //take into account the need for URL rewriting
        } else {
            //from the event
            if (this.actionEvent == null)
                this.setAction(new ActionEvent());

            String url = this.actionEvent.getEventURL();
            if (preventRewriting)
                sb.append(url);
            else
                sb.append(URLRewriter.encodeURL(vc, url));    //take into account the need for URL rewriting
            sep = ((url.indexOf("?") > -1) ? "&" : "?");
            if (this.listeners != null) {
                for (ListenerFactory lf : listeners) {
                    sb.append(sep).append(BaseEvent.EVENT_ID).append("=").append(lf.getListenerID());
                    sep = "&";
                }
            }
        }

        //finally add any custom params
        if (this.params != null) {
            for (Map.Entry entry : params.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();

                if (key != null && val != null) {
                    if (val instanceof String[]) {
                        String[] vals = (String[]) val;
                        for (int i = 0; i < vals.length; i++) {
                            sb.append(sep);
                            sb.append(encodeStr(key.toString()));
                            sb.append("=");
                            sb.append(encodeStr(vals[i].toString()));
                            sep = "&";
                        }
                    } else {
                        sb.append(sep);
                        sb.append(encodeStr(key.toString()));
                        sb.append("=");
                        sb.append(encodeStr(val.toString()));
                    }
                }
                sep = "&";
            }
        }
        return sb.toString();
    }

    /**
     * Set any associated params
     */
    public BAction setParam(String key, String val) {
        if (this.params == null)
            this.params = new TreeMap<String, Object>();
        this.params.put(key, val);
        return this;
    }

    //dbr_012202.2 - added
    /**
     * Set any associated list of params
     */
    public BAction setParam(String key, String[] val) {
        if (this.params == null)
            this.params = new TreeMap<String, Object>();
        this.params.put(key, val);
        return this;
    }

    /**
     * Get any associated params
     */
    public Map getParams() {
        return this.params;
    }

    /**
     * stores a Set of client-side script functions for use by the
     * {@link org.barracudamvc.core.comp.renderer.html.HTMLActionRenderer}
     * .  These script functions are javascript function names.  The
     * constraints for these functions is that they take a form element as an
     * argument (or no arguments) and that it returns a boolean.  These
     * functions are called in the order given and can be used for custom
     * client-side form validation.
     *
     * @since 1.2
     * @param functionName the name of the javascript function with no
     *        parenthasis
     */
    public BAction addScriptFunction(String functionName) {
        if (this.scriptFunctions == null)
            this.scriptFunctions = new ArrayList<String>(5); //jrk_20030501 - I'd rather use j2sdk1.4+ LinkedHashSet here to preserve original added order!!!
        if (!this.scriptFunctions.contains(functionName)) { //unnecessary if using a LinkedHashSet
            this.scriptFunctions.add(functionName);
        }
        return this;
    }

    /**
     * provides access any custom script functions added to a BAction component
     *
     * @since 1.2
     * @see #addScriptFunction(String)
     * @return a Set of script functions or null if none added
     */
    public Collection getScriptFunctions() {
        return this.scriptFunctions;
    }

    /**
     * Set disable back button (only works if your client supports
     * Javascript)
     *
     * @param idisableBackButton true if we want the back button disabled
     * @deprecated csc_033005_1 - this functionality does not work in Firefox 
     *      or Mozilla, due to the fact that those browsers ALWAYS add an 
     *      item to the browser history when a form is submitted, even if that
     *      post occurs in an IFRAME. This still does work in IE.
     */
    public BAction setDisableBackButton(boolean idisableBackButton) {
        this.disableBackButton = idisableBackButton;
        return this;
    }

    /**
     * Get disable back button
     *
     * @return true if we want the back button disabled
     * @deprecated csc_033005_1 - this functionality does not work in Firefox 
     *      or Mozilla, due to the fact that those browsers ALWAYS add an 
     *      item to the browser history when a form is submitted, even if that
     *      post occurs in an IFRAME. This still does work in IE.
     */
    public boolean getDisableBackButton() {
        return this.disableBackButton;
    }

    /**
     * Set disable form locking
     *
     * @param   idisableFormLocking     <tt>true</tt> to disable locking the form elements during submit.
     * @since   saw_082603_2
     */
    public BAction setDisableFormLocking(boolean idisableFormLocking) {
        this.disableFormLocking = idisableFormLocking;
        return this;
    }

    /**
     * Get disable form locking
     *
     * @return <tt>true</tt> if we want to disable locking the form elements during submit.
     * @since saw_082603_2
     */
    public boolean getDisableFormLocking() {
        return this.disableFormLocking;
    }

    /**
     * Add an event listener to this component.
     *
     * @param lf the event listener to be added
     */
    public BAction addEventListener(ListenerFactory lf) {
        if (lf == null)
            return this;
        if (this.listeners == null)
            this.listeners = new ArrayList<ListenerFactory>(5);
        this.listeners.add(lf);
        this.invalidate();
        return this;
    }

    /**
     * Remove an event listener from this component
     *
     * @param lf the event listener to be removed
     */
    public BAction removeEventListener(ListenerFactory lf) {
        if (lf == null)
            return this;
        if (this.listeners == null)
            return this;
        this.listeners.remove(lf);
        this.invalidate();
        return this;
    }
}
