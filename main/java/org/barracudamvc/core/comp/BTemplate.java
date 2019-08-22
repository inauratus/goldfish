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
 * $Id: BTemplate.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.model.Model;
import org.barracudamvc.core.comp.model.ModelListener;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.comp.renderer.html.HTMLTemplateRenderer;
import org.barracudamvc.core.comp.renderer.xml.XMLTemplateRenderer;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

/**
 * A BTemplate is used to process part of a DOM as a template--the
 * component will look for directives and then query the models to
 * return the data associated with a given key. BTemplate essentially
 * brings "pull-mvc" to XMLC.
 *
 * <p>In the case of BTemplate, you will ALMOST ALWAYS need to manually
 * bind it to a View, unless you happen to be returning it from a model
 * (in which case this will be done for you automatically)
 */
public class BTemplate extends BComponent implements BCompoundComponent {

    protected static final Logger logger = Logger.getLogger(BTemplate.class.getName());
    protected HashMap<String, TemplateModel> templateModels = new HashMap<String, TemplateModel>();
    private LocalModelListener callback = new LocalModelListener();
    private List<BComponent> diferredComponent = new ArrayList<BComponent>();

    //--------------- Constructors -------------------------------
    /**
     * Public noargs constructor
     */
    public BTemplate() {
        this(null, null);
    }

    /**
     * Public constructor which creates the component and
     * binds it to a specific model. You will need to manually
     * set the View if you use this constructor.
     *
     * @param imodel the specific model to back this component
     */
    public BTemplate(TemplateModel imodel) {
        this(imodel, null);
    }

    /**
     * Public constructor which creates the component and
     * binds it to a specific model. The component is also
     * bound to the specified view. 
     * 
     * <p>Null values may be passed in for any parameters, 
     * but if you do so you will need manually provide these
     * values (via the accessor methods) prior to actually 
     * rendering the component
     *
     * @param model the specific model to back this component
     * @param view the View the component should be bound to
     */
    BTemplate(TemplateModel imodel, TemplateView iview) {
        if (imodel != null) {
            addModel(imodel);
        }
        if (iview != null) {
            this.addView(iview);
        }
    }

    //--------------- Renderer -----------------------------------
    /**
     * Default component renderer factory registrations
     */
    static {
        HTMLRendererFactory rfHTML = new HTMLRendererFactory();
        installRendererFactory(rfHTML, BTemplate.class, HTMLElement.class);
        installRendererFactory(rfHTML, BTemplate.class, HTMLDocument.class);

        XMLRendererFactory rfXML = new XMLRendererFactory();
        installRendererFactory(rfXML, BTemplate.class, Node.class);
    }

    /**
     * HTML RendererFactory
     */
    static class HTMLRendererFactory implements RendererFactory {

        @Override
        public Renderer getInstance() {
            return new HTMLTemplateRenderer();
        }
    }

    /**
     * XML RendererFactory
     */
    static class XMLRendererFactory implements RendererFactory {

        @Override
        public Renderer getInstance() {
            return new XMLTemplateRenderer();
        }
    }

    //--------------- BTemplate ---------------------------
    /**
     * Add a model to the component. Unlike other components, the template
     * component can have ant number of models...
     *
     * @param imodel a model that backs the template
     */
    public void addModel(TemplateModel imodel) {
        //eliminate the obvious
        if (imodel == null) {
            return;
        }

        //saw_121102.1 - deregister first if possible; this ensures that if there
        //is already a model in place with the same name, that one will get properly
        //removed and have it listeners deregistered
        imodel.removeModelListener(callback);

        //add the model    and set the default if necess.
        templateModels.put(imodel.getName(), imodel);
        invalidate();

        //reregister if possible
        imodel.addModelListener(callback);
    }

    /**
     * Add a component to the list of components with deferred rendering (ie after the BTemplate has finished
     * re-adding the nodes into the document
     */
    public void differComponent(BComponent comp) {
        this.diferredComponent.add(comp);
    }

    public List<BComponent> getDifferedComponents() {
        return this.diferredComponent;
    }

    /**
     * Add a whole list of models to the component. 
     *
     * @param ilist a list of TemplateModels to back the component
     */
    public void addModels(List ilist) {
        //eliminate the obvious
        if (ilist == null) {
            return;
        }

        //iterate through the list, adding any instances of TemplateModel
        Iterator it = ilist.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof TemplateModel) {
                addModel((TemplateModel) o);
            }
        }
    }

    /**
     * Remove a model from the component. 
     *
     * @param imodel a model that backs the template
     */
    public void removeModel(TemplateModel imodel) {
        //eliminate the obvious
        if (imodel == null) {
            return;
        }

        //add the model    and set the default if necess.
        templateModels.remove(imodel.getName());
        invalidate();

        //deregister if possible
        imodel.removeModelListener(callback);
    }

    /**
     * Remove a model from the component by model name
     *
     * @param modelName the name of the model to be removed
     */
    public void removeModel(String modelName) {
        removeModel(getModel(modelName));
    }

    /**
     * Remove all models from the component
     */
    public void removeAllModels() {
        Iterator it = templateModels.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            TemplateModel model = (TemplateModel) entry.getValue();

            it.remove();
            model.removeModelListener(callback);
        }
        invalidate();
    }

    /**
     * Get the model that backs the table
     *
     * @param modelName the name of the model we're interested in
     */
    public TemplateModel getModel(String modelName) {
        return templateModels.get(modelName);
    }

    /**
     * Get a list of models associated with this view
     *
     * @return a list of models associated with this view
     */
    public List<TemplateModel> getModels() {
        return new ArrayList<TemplateModel>(templateModels.values());
    }

    //--------------- Lifecycle ----------------------------------
    /**
     * Destroy cycle. The component should use this cycle to
     * perform any special cleanup.
     */
    @Override
    public void destroyCycle() {
        //default destroy
        super.destroyCycle();

        //we set the model to null so that the component can be 
        //garbage collected. If we don't do this, the model retains
        //a reference back to the component and so the component 
        //will never be freed up...
        removeAllModels();  //saw_121002.1 - we should remove all models first so everything gets deregistered properly, allowing it to be gc'd
        templateModels = null;
        callback = null;    //csc_041202.1 - seems like this should be getting cleared too...
        diferredComponent = null;
    }

    @Override
    protected void postRender(ViewContext vc, int depth) {
        // give a chance to differed components (eg BScripts) to actually render
        if (this.equals(vc.getOngoingTemplate())) {
            vc.setOngoingBTemplate(null);

            try {
                for (View view : getViews()) {
                    if (view == null) {
                        continue;
                    }
                    for (BComponent comp : getDifferedComponents()) {
                        comp.renderView(view, vc, depth);
                    }
                }
            } catch (RenderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class LocalModelListener implements ModelListener {
        
        @Override
        public void modelChanged(Model m) {
            invalidate();
        }
    }
}
