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
 * $Id: DefaultListModel.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.model.ModelListener;

/**
 * This class provides a default implementation for List Model.
 * This implementation is backed by a java.util.ArrayList.
 */
public class DefaultListModel extends AbstractListModel implements List, Cloneable, Serializable {

    //public vars
    protected static final Logger logger = Logger.getLogger(DefaultListModel.class.getName());

    protected List<Object> items = new ArrayList<Object>();

    /**
     * Get an element at a specific index
     *
     * @param index the target index
     * @return the element at the specific index
     */
    @Override
    public Object getItemAt(int index) {
        return get(index);
    }

    /**
     * Get the size of the list
     *
     * @return the size of the list
     * @see List#size()
     */
    @Override
    public int getSize() {
        return size();
    }

    /**
     * Returns the number of elements in this list.  
     *
     * @return the number of elements in this list.
     * @see List#size()
     */
    @Override
    public int size() {
        return items.size();
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements.
     * @see List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * 
     * Returns <tt>true</tt> if this list contains the specified element.
     * 
     * @param o element whose presence in this list is to be tested.
     * @return <tt>true</tt> if this list contains the specified element.
     * @see List#contains(Object)
     */
    @Override
    public boolean contains(Object o) {
        return items.contains(o);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence.
     * @see List#iterator()
     */
    @Override
    public Iterator iterator() {
        return items.iterator();
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence.  Obeys the general contract of the
     * <tt>Collection.toArray</tt> method.
     *
     * @return an array containing all of the elements in this list in proper
     *           sequence.
     * @see List#toArray()
     */
    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence; the runtime type of the returned array is that of the
     * specified array.  Obeys the general contract of the
     * <tt>Collection.toArray(Object[])</tt> method.
     *
     * @param a the array into which the elements of this list are to
     *        be stored, if it is big enough; otherwise, a new array of the
     *         same runtime type is allocated for this purpose.
     * @return  an array containing the elements of this list.
     * @throws ArrayStoreException if the runtime type of the specified array
     *           is not a supertype of the runtime type of every element in
     *           this list.
     * @see List#toArray(Object[])
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray(Object a[]) {
        return items.toArray(a);
    }

    /**
     * Appends the specified element to the end of this list (optional
     * operation). <p>
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     * @throws UnsupportedOperationException if the <tt>add</tt> method is not
     *           supported by this list.
     * @throws ClassCastException if the class of the specified element
     *           prevents it from being added to this list.
     * @throws IllegalArgumentException if some aspect of this element
     *            prevents it from being added to this collection.
     * @see List#add(Object)
     */
    @Override
    public boolean add(Object o) {
        boolean result = items.add(o);
        if (result) {
            fireModelChanged();
        }
        return result;
    }

    /**
     * Removes the first occurrence in this list of the specified element 
     * (optional operation).  If this list does not contain the element, it is
     * unchanged.  
     *
     * @param o element to be removed from this list, if present.
     * @return <tt>true</tt> if this list contained the specified element.
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     *          not supported by this list.
     * @see List#remove(Object)
     */
    @Override
    public boolean remove(Object o) {
        boolean result = items.remove(o);
        if (result) fireModelChanged();
        return result;
    }

    /**
     * 
     * Returns <tt>true</tt> if this list contains all of the elements of the
     * specified collection.
     *
     * @param c collection to be checked for containment in this list.
     * @return <tt>true</tt> if this list contains all of the elements of the
     *            specified collection.
     * @see List#containsAll(Collection)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection c) {
        return items.containsAll(c);
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator (optional operation).  
     *
     * @param c collection whose elements are to be added to this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
     *         not supported by this list.
     * @throws ClassCastException if the class of an element in the specified
     *            collection prevents it from being added to this list.
     * @throws IllegalArgumentException if some aspect of an element in the
     *         specified collection prevents it from being added to this
     *         list.
     * @see List#addAll(Collection)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection c) {
        boolean result = items.addAll(c);
        if (result) fireModelChanged();
        return result;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list at the specified position (optional operation).  Shifts the
     * element currently at that position (if any) and any subsequent
     * elements to the right (increases their indices).  
     *
     * @param index index at which to insert first element from the specified
     *                collection.
     * @param c elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
     *          not supported by this list.
     * @throws ClassCastException if the class of one of elements of the
     *           specified collection prevents it from being added to this
     *           list.
     * @throws IllegalArgumentException if some aspect of one of elements of
     *          the specified collection prevents it from being added to
     *          this list.
     * @throws IndexOutOfBoundsException if the index is out of range (index
     *          &lt; 0 || index &gt; size()).
     * @see List#addAll(int,Collection)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(int index, Collection c) {
        boolean result = items.addAll(index, c);
        if (result) fireModelChanged();
        return result;
    }

    /**
     * Removes from this list all the elements that are contained in the
     * specified collection (optional operation).
     *
     * @param c collection that defines which elements will be removed from
     *          this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     *           is not supported by this list.
     * @see List#removeAll(Collection)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection c) {
        boolean result = items.removeAll(c);
        if (result) fireModelChanged();
        return result;
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this list all the elements that are not contained in the specified
     * collection.
     *
     * @param c collection that defines which elements this set will retain.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
     *           is not supported by this list.
     * 
     * @see List#retainAll(Collection)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection c) {
        boolean result = items.retainAll(c);
        if (result) fireModelChanged();
        return result;
    }

    /**
     * Removes all of the elements from this list
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> method is
     *           not supported by this list.
     * @see List#clear()
     */
    public void clear() {
        items.clear();
    }

    /**
     * Compares the specified object with this list for equality.  Returns
     * <tt>true</tt> if and only if the specified object is also a list, both
     * lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>.  
     *
     * @param o the object to be compared for equality with this list.
     * @return <tt>true</tt> if the specified object is equal to this list.
     * @see List#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        return items.equals(o);
    }

    /**
     * Returns the hash code value for this list.
     *
     * @return the hash code value for this list.
     * @see List#hashCode()
     */
    @Override
    public int hashCode() {
        return items.hashCode();
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     * @throws IndexOutOfBoundsException if the index is out of range (index
     *           &lt; 0 || index &gt;= size()).
     * @see List#get(int)
     */
    public Object get(int index) {
        return items.get(index);
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element (optional operation).
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws UnsupportedOperationException if the <tt>set</tt> method is not
     *          supported by this list.
     * @throws    ClassCastException if the class of the specified element
     *           prevents it from being added to this list.
     * @throws    IllegalArgumentException if some aspect of the specified
     *          element prevents it from being added to this list.
     * @throws    IndexOutOfBoundsException if the index is out of range
     *          (index &lt; 0 || index &gt;= size()).  
     * @see List#set(int,Object)
     */
    public Object set(int index, Object element) {
        Object result = items.set(index, element);
//        if (result!=null) fireContentsChanged(this, index, index);
        if (result!=null) fireModelChanged();
        return result;
    }

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * @throws UnsupportedOperationException if the <tt>add</tt> method is not
     *          supported by this list.
     * @throws    ClassCastException if the class of the specified element
     *           prevents it from being added to this list.
     * @throws    IllegalArgumentException if some aspect of the specified
     *          element prevents it from being added to this list.
     * @throws    IndexOutOfBoundsException if the index is out of range
     *          (index &lt; 0 || index &gt; size()).
     * @see List#add(int,Object)
     */
    public void add(int index, Object element) {
        items.add(index, element);
//        fireIntervalAdded(this, index, index);
        fireModelChanged();
    }

    /**
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
     *
     * @param index the index of the element to removed.
     * @return the element previously at the specified position.
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     *          not supported by this list.
     * @throws IndexOutOfBoundsException if the index is out of range (index
     *            &lt; 0 || index &gt;= size()).
     * @see List#remove(int)
     */
    public Object remove(int index) {
        Object result = items.remove(index);
//        if (result!=null) fireIntervalRemoved(this, index, index);
        if (result!=null) fireModelChanged();
        return result;
    }

    /**
     * Returns the index in this list of the first occurrence of the specified
     * element, or -1 if this list does not contain this element.
     *
     * @param o element to search for.
     * @return the index in this list of the first occurrence of the specified
     *            element, or -1 if this list does not contain this element.
     * @see List#indexOf(Object)
     */
    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    /**
     * Returns the index in this list of the last occurrence of the specified
     * element, or -1 if this list does not contain this element.
     *
     * @param o element to search for.
     * @return the index in this list of the last occurrence of the specified
     *            element, or -1 if this list does not contain this element.
     * @see List#lastIndexOf(Object)
     */
    public int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }

    /**
     * Returns a list iterator of the elements in this list (in proper
     * sequence).
     *
     * @return a list iterator of the elements in this list (in proper
     *            sequence).
     * @see List#listIterator()
     */
    public ListIterator listIterator() {
        return items.listIterator();
    }

    /**
     * Returns a list iterator of the elements in this list (in proper
     * sequence), starting at the specified position in this list.
     *
     * @param index index of first element to be returned from the
     *            list iterator (by a call to the <tt>next</tt> method).
     * @return a list iterator of the elements in this list (in proper
     *            sequence), starting at the specified position in this list.
     * @throws IndexOutOfBoundsException if the index is out of range (index
     *         &lt; 0 || index &gt; size()).
     * @see List#listIterator(int)
     */
    public ListIterator listIterator(int index) {
        return items.listIterator(index);
    }

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)  The returned list is backed by this list, so changes in the
     * returned list are reflected in this list, and vice-versa.  The returned
     * list supports all of the optional list operations supported by this
     * list.<p>
     *
     * @param fromIndex low endpoint (inclusive) of the subList.
     * @param toIndex high endpoint (exclusive) of the subList.
     * @return a view of the specified range within this list.
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *     (fromIndex &lt; 0 || toIndex &gt; size || fromIndex &gt; toIndex).
     * @see List#subList(int,int)
     */
    public List subList(int fromIndex, int toIndex) {
        return items.subList(fromIndex, toIndex);
    }




    //--------------- Object -------------------------------------
    /**
     * Create a string representation of the list and return it.
     * This method is invoked by the list component when rendering 
     * a list into a text based item that does not inherantly support 
     * a list structure
     *
     * @return a string representation of the list
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(500);
        String sep = "";
        Iterator it = items.iterator();
        while (it.hasNext()) {
            sb.append(sep+it.next().toString());
            sep = ", ";
        }
        String result = sb.toString().trim();
        if (result==null || result.length()<1) result = "[empty list]";
        return result;
    }

    //csc_031103.2 - added
    /**
     * Returns a clone of this list model with the same selection.
     * <code>listenerLists</code> are not duplicated.
     *
     * @exception CloneNotSupportedException if the selection model does not
     *    both (a) implement the Cloneable interface and (b) define a
     *    <code>clone</code> method.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultListModel clone = (DefaultListModel) super.clone();
        clone.items = new ArrayList<Object>(items);
        clone.listeners = new ArrayList<ModelListener>();
        return clone;
    }
}