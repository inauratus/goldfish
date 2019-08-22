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
 * $Id: ListSelectionModel.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

import org.barracudamvc.core.comp.model.Model;


/**
 * This interface defines the methods needed to implement a 
 * ListSelectionModel. Note that this comes almost directly 
 * from javax.swing.ListSelectionModel. The only thing that 
 * is different is that we've dropped the isAdjusting stuff.
 */
public interface ListSelectionModel extends Model {

    /**
     * A value for the selectionMode property: select one list index
     * at a time.
     * 
     * @see #setSelectionMode
     */
    public static final int SINGLE_SELECTION = 0;

    /**
     * A value for the selectionMode property: select one contiguous
     * range of indices at a time.
     * 
     * @see #setSelectionMode
     */
    public static final int SINGLE_INTERVAL_SELECTION = 1;

    /**
     * A value for the selectionMode property: select one or more 
     * contiguous ranges of indices at a time.
     * 
     * @see #setSelectionMode
     */
    public static final int MULTIPLE_INTERVAL_SELECTION = 2;

    public void setSelectionMode(int selectionMode);
    public int getSelectionMode();
    public void setSelectionInterval(int index0, int index1);
    public void addSelectionInterval(int index0, int index1);
    public void removeSelectionInterval(int index0, int index1);
    public int getMinSelectionIndex();
    public int getMaxSelectionIndex();
    public boolean isSelectedIndex(int index);
    public boolean isSelectionEmpty();
    public void clearSelection();

}
