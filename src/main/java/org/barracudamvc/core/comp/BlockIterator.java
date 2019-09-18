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
 * $Id: BlockIterator.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import org.w3c.dom.Node;

/**
 * This interface defines the methods necessary to implement a BlockIterator
 */
public interface BlockIterator {
    public static final String BLOCK_ITERATOR_CONTEXT = BlockIterator.class.getName()+".BlockIteratorContext";   //(BlockIterator) - local object repository

    /**
     * Set the block iterator name (from the block iterator template directive)
     */
    public void setName(String name);
    
    /**
     * Get the block iterator name (from the block iterator template directive)
     */
    public String getName();
    
    /**
     * Gets called prior to iteration. If you override this method, its always good 
     * practice to call super.preIterate() just to be safe.
     */
    public void preIterate();

    /**
     * Allows the developer to return true if there are more rows to be processed
     */
    public boolean hasNext();

    /**
     * Actually allows developer to increment forward in the data layer. Returning
     * false here just means 'skip this particular iteration'
     */
    public boolean loadNext();

    /**
     * Asks the developer to process a node (given the current data layer context)
     */
    public Node next(ViewContext vc, Node templateNode) throws RenderException;

    /** 
     * Provide the template models which back this iterator. This may be an individual
     * TemplateModel, or a List of such models. By default, this method is only called
     * ONCE for the entire iteration (ie. models reused across each item in the iteration).
     * If you want to update the models as you go, you need to either call invalidateModels()
     * or updateModel(TemplateModel) (and the best place to do this is in the loadNext() 
     * method)
     */
    public Object getTemplateModels();

    /**
     * Gets called after iteration is complete. If you override this method, you MUST  
     * call super.postIterate() to allow the parent class a chance to perform cleanup
     */
    public void postIterate();

    //csc_051404_1 - added
    /**
     * Clear the current template models (which will cause the component to be repopulated
     * using getTemplateModels()
     */
    public void invalidateTemplateModels();

    //csc_051404_1 - added
    /**
     * Provide a method to immeditately force an update on the model(s) that back this iterator. 
     * The object passed may either be a single TemplateModel or a List of such models.
     */
    public void updateTemplateModels(Object templateModels);

    //csc_051404_1 - removed: I can't find anyplace that is actually using this, and updateTemplateModels() should actually 
    //accomplish the same thing. If someone has a need to update JUST one model at a time, please let me know and I
    //can put it back in
    /**
     * Provide a hook so that developers can update the models that back the block iterator
     */
//    public void updateModelInTemplate(TemplateModel model);



}
