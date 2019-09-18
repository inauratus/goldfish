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
 * $Id: ModelListener.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.model;


/**
 * Defines the methods needed to act as a ModelListener
 */
public interface ModelListener {

    /**
     * This high level event notification mechanism to tells listeners
     * that something in the model changed. Note that this does not
     * provide near the level of detail that Swing models do (this is because
     * most of the time when the model changes we're just going to invalidate the 
     * component and completely redraw)
     *
     * @param model a reference to the model that changed
     */
    public void modelChanged(Model model);


}