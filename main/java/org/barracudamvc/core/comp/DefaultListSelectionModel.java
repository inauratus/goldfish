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
 * $Id: DefaultListSelectionModel.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.barracudamvc.core.comp.model.ModelListener;

/**
 * Default data model implementation for list selections. 
 */
public class DefaultListSelectionModel implements ListSelectionModel, Cloneable, Serializable {

    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    private int selectionMode = SINGLE_SELECTION;
    private int minIndex = MAX;
    private int maxIndex = MIN;
    private int firstAdjustedIndex = MAX;
    private int lastAdjustedIndex = MIN;
    private BitSet value = new BitSet(32);
    protected List<ModelListener> listeners = new ArrayList<ModelListener>();

    //--------------- ListSelectionModel -------------------------
    /**
     * Add a listener to the template that's notified each time a change
     * to the data model occurs.
     *
     * @param ml the TemplateModelListener
     */
    @Override
    public void addModelListener(ModelListener ml) {
        listeners.add(ml);
    }

    /**
     * Remove a listener
     *
     * @param ml the TemplateModelListener
     */
    @Override
    public void removeModelListener(ModelListener ml) {
        listeners.remove(ml);
    }

    /**
     * Forwards the given notification event to all
     * <code>TemplateModelListeners</code> that registered
     * themselves as listeners for this template model.
     */
    public void fireModelChanged() {
        for (ModelListener ml : listeners) {
            ml.modelChanged(this);
        }
    }

    /**
     * Sets the selection mode.  The default is
     * SINGLE_SELECTION.
     * @param selectionMode  one of three values:
     * <ul>
     * <li>SINGLE_SELECTION</li>
     * <li>SINGLE_INTERVAL_SELECTION</li>
     * <li>MULTIPLE_INTERVAL_SELECTION</li>
     * </ul>
     * @exception IllegalArgumentException  if <code>selectionMode</code>
     *        is not one of the legal values shown above
     * @see #setSelectionMode
     */
    @Override
    public void setSelectionMode(int selectionMode) {
        switch (selectionMode) {
            case SINGLE_SELECTION:
            case SINGLE_INTERVAL_SELECTION:
            case MULTIPLE_INTERVAL_SELECTION:
                this.selectionMode = selectionMode;
                break;
            default:
                throw new IllegalArgumentException("invalid selectionMode");
        }
    }

    /**
     * Returns the selection mode.
     * @return  one of the these values:
     * <ul>
     * <li>SINGLE_SELECTION</li>
     * <li>SINGLE_INTERVAL_SELECTION</li>
     * <li>MULTIPLE_INTERVAL_SELECTION</li>
     * </ul>
     * @see #getSelectionMode
     */
    @Override
    public int getSelectionMode() {
        return selectionMode;
    }

    /** 
     * Change the selection to be between index0 and index1 inclusive.
     * If this represents a change to the current selection, then
     * notify each ListSelectionListener. Note that index0 doesn't have
     * to be less than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     */
    public void setSelectionInterval(int index0, int index1) {
        if (index0 == -1 || index1 == -1)
            return;

        if (getSelectionMode() == SINGLE_SELECTION)
            index0 = index1;

        int clearMin = minIndex;
        int clearMax = maxIndex;
        int setMin = Math.min(index0, index1);
        int setMax = Math.max(index0, index1);
        changeSelection(clearMin, clearMax, setMin, setMax);
    }

    /** 
     * Change the selection to be the set union of the current selection
     * and the indices between index0 and index1 inclusive.  If this represents 
     * a change to the current selection, then notify each 
     * ListSelectionListener. Note that index0 doesn't have to be less
     * than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     */
    public void addSelectionInterval(int index0, int index1) {
        if (index0 == -1 || index1 == -1)
            return;

        if (getSelectionMode() != MULTIPLE_INTERVAL_SELECTION) {
            setSelectionInterval(index0, index1);
            return;
        }

        int clearMin = MAX;
        int clearMax = MIN;
        int setMin = Math.min(index0, index1);
        int setMax = Math.max(index0, index1);
        changeSelection(clearMin, clearMax, setMin, setMax);
    }

    /** 
     * Change the selection to be the set difference of the current selection
     * and the indices between index0 and index1 inclusive.  If this represents 
     * a change to the current selection, then notify each 
     * ListSelectionListener.  Note that index0 doesn't have to be less
     * than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     */
    public void removeSelectionInterval(int index0, int index1) {
        if (index0 == -1 || index1 == -1)
            return;

        int clearMin = Math.min(index0, index1);
        int clearMax = Math.max(index0, index1);
        int setMin = MAX;
        int setMax = MIN;

        // If the removal would produce to two disjoint selections in a mode 
        // that only allows one, extend the removal to the end of the selection. 
        if (getSelectionMode() != MULTIPLE_INTERVAL_SELECTION
                && clearMin > minIndex && clearMax < maxIndex) {
            clearMax = maxIndex;
        }

        changeSelection(clearMin, clearMax, setMin, setMax);
    }

    /**
     * Returns the first selected index or -1 if the selection is empty.
     */
    public int getMinSelectionIndex() {
        return isSelectionEmpty() ? -1 : minIndex;
    }

    /**
     * Returns the last selected index or -1 if the selection is empty.
     */
    public int getMaxSelectionIndex() {
        return maxIndex;
    }

    /** 
     * Returns true if the specified index is selected.
     */
    public boolean isSelectedIndex(int index) {
        return ((index < minIndex) || (index > maxIndex)) ? false : value.get(index);
    }

    /**
     * Returns true if no indices are selected.
     */
    public boolean isSelectionEmpty() {
        return (minIndex > maxIndex);
    }

    /**
     * Change the selection to the empty set.  If this represents
     * a change to the current selection then notify each ListSelectionListener.
     */
    public void clearSelection() {
        removeSelectionInterval(minIndex, maxIndex);
    }

    // Updates first and last change indices
    private void markAsDirty(int r) {
        firstAdjustedIndex = Math.min(firstAdjustedIndex, r);
        lastAdjustedIndex = Math.max(lastAdjustedIndex, r);
    }

    // Sets the state at this index and update all relevant state.
    private void set(int r) {
        if (value.get(r))
            return;
        value.set(r);
        markAsDirty(r);

        // Update minimum and maximum indices
        minIndex = Math.min(minIndex, r);
        maxIndex = Math.max(maxIndex, r);
    }

    // Clears the state at this index and update all relevant state.
    private void clear(int r) {
        if (!value.get(r))
            return;
        value.clear(r);
        markAsDirty(r);

        // Update minimum and maximum indices
        //
        // If (r > minIndex) the minimum has not changed.
        // The case (r < minIndex) is not possible because r'th value was set.
        // We only need to check for the case when lowest entry has been cleared,
        // and in this case we need to search for the first value set above it.
        if (r == minIndex) {
            for (minIndex = minIndex + 1; minIndex <= maxIndex; minIndex++) {
                if (value.get(minIndex)) {
                    break;
                }
            }
        }

        //If (r < maxIndex) the maximum has not changed.
        //The case (r > maxIndex) is not possible because r'th value was set.
        //We only need to check for the case when highest entry has been cleared,
        //and in this case we need to search for the first value set below it.
        if (r == maxIndex) {
            for (maxIndex = maxIndex - 1; minIndex <= maxIndex; maxIndex--) {
                if (value.get(maxIndex)) {
                    break;
                }
            }
        }

        //Performance note: This method is called from inside a loop in
        //changeSelection() but we will only iterate in the loops
        //above on the basis of one iteration per deselected cell - in total.
        //Ie. the next time this method is called the work of the previous
        //deselection will not be repeated.
        //
        //We also don't need to worry about the case when the min and max
        //values are in their unassigned states. This cannot happen because
        //this method's initial check ensures that the selection was not empty
        //and therefore that the minIndex and maxIndex had 'real' values.
        //
        //If we have cleared the whole selection, set the minIndex and maxIndex
        //to their cannonical values so that the next set command always works
        //just by using Math.min and Math.max.
        if (isSelectionEmpty()) {
            minIndex = MAX;
            maxIndex = MIN;
        }
    }

    private boolean contains(int a, int b, int i) {
        return (i >= a) && (i <= b);
    }

    private void changeSelection(int clearMin, int clearMax, int setMin, int setMax, boolean clearFirst) {
        for (int i = Math.min(setMin, clearMin); i <= Math.max(setMax, clearMax); i++) {
            boolean shouldClear = contains(clearMin, clearMax, i);
            boolean shouldSet = contains(setMin, setMax, i);

            if (shouldSet && shouldClear) {
                if (clearFirst)
                    shouldClear = false;
                else
                    shouldSet = false;
            }

            if (shouldSet)
                set(i);
            if (shouldClear)
                clear(i);
        }
        fireModelChanged();
    }

    /**   
     * Change the selection with the effect of first clearing the values
     * in the inclusive range [clearMin, clearMax] then setting the values
     * in the inclusive range [setMin, setMax]. Do this in one pass so
     * that no values are cleared if they would later be set.
     */
    private void changeSelection(int clearMin, int clearMax, int setMin, int setMax) {
        changeSelection(clearMin, clearMax, setMin, setMax, true);
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a <code>String</code> representation of this object
     */
    @Override
    public String toString() {
        String s = "=" + value.toString();
        return getClass().getName() + " " + Integer.toString(hashCode()) + " " + s;
    }

    /**
     * Returns a clone of this selection model with the same selection.
     * <code>listenerLists</code> are not duplicated.
     *
     * @exception CloneNotSupportedException if the selection model does not
     *    both (a) implement the Cloneable interface and (b) define a
     *    <code>clone</code> method.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultListSelectionModel clone = (DefaultListSelectionModel) super.clone();
        clone.value = (BitSet) value.clone();
        clone.listeners = new ArrayList<ModelListener>();
        return clone;
    }
}
