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
 * $Id: AbstractBlockIterator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import java.util.Iterator;
import java.util.List;

import org.barracudamvc.plankton.data.ObjectRepository;
import org.w3c.dom.Node;

/**
 * This class provides the abstract implementation of a BlockIterator. Basically,
 * it will
 */
public abstract class AbstractBlockIterator implements BlockIterator {

    protected Object templateModels = null;
    protected BTemplate templateComp = null;
    protected String name = null;
  
    public AbstractBlockIterator() {
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        lor.putState(BlockIterator.BLOCK_ITERATOR_CONTEXT, this);
    }
  
    /** {@inheritDoc} */
    public void setName(String iname) {
        name = iname;
    }
    
    /** {@inheritDoc} */
    public String getName() {
        return name;
    }
    
    /** {@inheritDoc} */
    public void preIterate() {
        //nop
    }

    /** {@inheritDoc} */
    public abstract boolean hasNext();

    /** {@inheritDoc} */
    public abstract boolean loadNext();

    /** {@inheritDoc} */
    public Node next(ViewContext vc, Node templateNode) throws RenderException {
        try {
            //create our root component
            BComponent broot = new BComponent();
            broot.setName("Root");

            //create a template component and bind it to the views
            templateComp = new BTemplate();
            
//            Object o = getTemplateModels();
//            if (o instanceof TemplateModel) templateComp.addModel((TemplateModel) o);
//            else if (o instanceof List) templateComp.addModels((List) o);
            if (templateModels==null) templateModels = getTemplateModels();
            if (templateModels instanceof TemplateModel) templateComp.addModel((TemplateModel) templateModels);
            else if (templateModels instanceof List) templateComp.addModels((List) templateModels);
            else throw new RuntimeException("Fatal err: Model must return either TemplateModel or List");
            templateComp.setView(new DefaultTemplateView(templateNode));
            broot.addChild(templateComp);
        
            //now init the component
            broot.initCycle();

            //now render the component
            broot.render(vc);

            //make sure we save a fresh handle to template models (for post iterate cleanup) - the
            //models could have changed during render, so its important to reset the pointer here
            templateModels = templateComp.getModels();  //csc_051404_1

            //now destroy the component
            broot.destroyCycle();
            templateComp = null;
        
            //now return the rendered node
            return templateNode;
        } finally {
        
        }
    }

    /** {@inheritDoc} */
    public abstract Object getTemplateModels();

    /** {@inheritDoc} */
    public void postIterate() { 
//csc_051004_1_start - we want to make sure that we invoke postIterate on any iterative models
        if (templateModels!=null) {
            if (templateModels instanceof IterativeModel) {
                ((IterativeModel) templateModels).postIterate();
            } else if (templateModels instanceof List) {
                List models = (List) templateModels;
                Iterator it = (models!=null ? models.iterator() : null);
                while (it!=null && it.hasNext()) {                
                    Object o = it.next();
                    if (o instanceof IterativeModel) ((IterativeModel) o).postIterate();
                }
            }
            templateModels = null;
        }
//csc_051004_1_end

        //clean up the obj repos
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        lor.removeState(BlockIterator.BLOCK_ITERATOR_CONTEXT);
    }

    //csc_051404_1 - added
    /**
     * Clear the current template models (which will cause the component to be repopulated
     * using getTemplateModels()
     */
    public void invalidateTemplateModels() {
        updateTemplateModels(getTemplateModels());
    }

    //csc_051404_1 - added
    /**
     * Provide a method to immeditately force an update on the model(s) that back this iterator. 
     * The object passed may either be a single TemplateModel or a List of such models.
     */
    public void updateTemplateModels(Object itemplateModels) {
        templateModels = itemplateModels;
        if (templateComp!=null) {
            if (templateModels instanceof TemplateModel) templateComp.addModel((TemplateModel) templateModels);
            else if (templateModels instanceof List) templateComp.addModels((List) templateModels);
        }    
    }

    //csc_051404_1 - removed: I can't find anyplace that is actually using this, and updateTemplateModels() should actually 
    //accomplish the same thing. If someone has a need to update JUST one model at a time, please let me know and I
    //can put it back in
    /** {@inheritDoc} */
/*
    public void updateModelInTemplate(TemplateModel model) {
        if (templateComp!=null) templateComp.addModel(model);
    }
*/    
}
