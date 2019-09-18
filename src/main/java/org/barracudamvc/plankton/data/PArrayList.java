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
 * $Id: PArrayList.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>This class extends AbstractPData (which provides the
 * parental/statemap functionality) and delegates most of 
 * the List functionality back to an underlying ArrayList
 */
public class PArrayList extends AbstractPData implements PList {

    private List<Object> list = new ArrayList<Object>();


    //--------------- PArrayList ---------------------------------
    /**
     * Set the underlying store (you only really need to use
     * this method if you want to store the data in something 
     * other than an ArrayList, which is the default)
     *
     * @param ilist the List structure to be used as the underlying 
     *        store
     */
    public void setStore(List<Object> ilist) {
        list = ilist;
    }
    
    //--------------- PList --------------------------------------
    /**
     * Inserts the specified element at the specified position in this list 
     * (optional operation). 
     */
    public void add(int index, Object el) {
        //this check is used to ensure the parental hierarchy is automatically
        //maintained. If you add an element to this list and that element implements
        //PData and that element has inheritParents=true, then this list should
        //automatically automatically become that objects parent
        if (el!=null && el instanceof PData) {
            PData pdata = (PData) el;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(this);
            if (pdata.isInheritParents() && pdata.getParent()==null) pdata.setParent(this);    //csc_012003.1
        }
        
        //add the element to the list
        list.add(index, el);
    }

    /**
     * Appends the specified element to the end of this list (optional 
     * operation). 
     */
    public boolean add(Object el) {
        //this check is used to ensure the parental hierarchy is automatically
        //maintained. If you add an element to this list and that element implements
        //PData and that element has inheritParents=true, then this list should
        //automatically automatically become that objects parent
        if (el!=null && el instanceof PData) {
            PData pdata = (PData) el;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(this);
            if (pdata.isInheritParents() && pdata.getParent()==null) pdata.setParent(this);    //csc_012003.1
        }

        //add the element to the list        
        return list.add(el);
    }

    /**
     * Appends all of the elements in the specified collection to the end 
     * of this list, in the order that they are returned by the specified 
     * collection's iterator (optional operation). 
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection c) {
        //this check is used to ensure the parental hierarchy is automatically
        //maintained. If you add an element to this list and that element implements
        //PData and that element has inheritParents=true, then this list should
        //automatically automatically become that objects parent. Here we have to 
        //iterate through all the elements in the collection making this check...
        if (c!=null) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                Object el = it.next();
                if (el!=null && el instanceof PData) {
                    PData pdata = (PData) el;
                    if (pdata.isInheritParents() && pdata.getParent()==null) pdata.setParent(this);    //csc_012003.1
                }
            }
        }

        return list.addAll(c);
    }

    /**
     * Inserts all of the elements in the specified collection into this 
     * list at the specified position (optional operation). 
     */
    @SuppressWarnings("unchecked")
    public boolean addAll(int index, Collection c) {
        //this check is used to ensure the parental hierarchy is automatically
        //maintained. If you add an element to this list and that element implements
        //PData and that element has inheritParents=true, then this list should
        //automatically automatically become that objects parent. Here we have to 
        //iterate through all the elements in the collection making this check...
        if (c!=null) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                Object el = it.next();
                if (el!=null && el instanceof PData) {
                    PData pdata = (PData) el;
//csc_012003.1                    if (pdata.isInheritParents()) pdata.setParent(this);
                    if (pdata.isInheritParents() && pdata.getParent()==null) pdata.setParent(this);    //csc_012003.1
                }
            }
        }
    
        //add the collection to the list
        return list.addAll(index, c);
    }

    /**
     * Removes all of the elements from this list (optional operation). 
     */
    public void clear() {
        //we need to start by clearing parents for any PData items in the list
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object el = it.next();
            if (el!=null && el instanceof PData) {
                PData pdata = (PData) el;
//csc_012003.1                if (pdata.isInheritParents()) pdata.setParent(null);
                if (pdata.isInheritParents() && pdata.getParent()==this) pdata.setParent(null);    //csc_012003.1
            }
        }
    
        //now clear the list
        list.clear();
    }

    /**
     * Returns true if this list contains the specified element. 
     */
    public boolean contains(Object el) {
        return list.contains(el);
    }

    /**
     * Returns true if this list contains all of the elements of the 
     * specified collection. 
     */
    public boolean containsAll(Collection c) {
        //the containsAll implementation in AbstractCollection
        //throws a NullPointerException if our list or c's list 
        //is empty...this manual check prevents that error: if
        //the list is empty we say that it contains all items 
        //if the other list is also empty.
        if (list.size()<1) return c.size()<1;
        
        //else just delegate to the underlying list function
        return list.containsAll(c);
    }

    /**
     * Returns the element at the specified position in this list. 
     */
    public Object get(int index) {
        return list.get(index);
    }

    /**
     * Returns the index in this list of the first occurrence of the 
     * specified element, or -1 if this list does not contain this element. 
     */
    public int indexOf(Object el) {
        return list.indexOf(el);
    }

    /**
     * Returns true if this list contains no elements. 
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence. 
     */
    public Iterator iterator() {
        return list.iterator();
    }

    /**
     * Returns the index in this list of the last occurrence of the 
     * specified element, or -1 if this list does not contain this element. 
     */
    public int lastIndexOf(Object el) {
        return list.lastIndexOf(el);
    }

    /**
     * Returns a list iterator of the elements in this list (in proper sequence). 
     */
    public ListIterator listIterator() {
        return list.listIterator();
    }

    /**
     * Returns a list iterator of the elements in this list (in proper sequence), 
     * starting at the specified position in this list. 
     */
    public ListIterator listIterator(int index) {
        return listIterator(index);
    }

    /**
     * Removes the element at the specified position in this list (optional 
     * operation). 
     */
    public Object remove(int index) {
        //this check is to ensure that the parental relationship is automatically
        //cleaned up from the item currently at the specified index. The idea here
        //is that if you're removing an element (which you are effectively doing
        //via a set) then that element should no longer point to this object as its 
        //parent.
        Object curEl = list.get(index);
        if (curEl!=null && curEl instanceof PData) {
            PData pdata = (PData) curEl;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(null);
            if (pdata.isInheritParents() && pdata.getParent()==this) pdata.setParent(null);    //csc_012003.1
        }

        //remove the element at the specified index from the list
        return list.remove(index);
    }

    /**
     * Removes the first occurrence in this list of the specified element 
     * (optional operation). 
     */
    public boolean remove(Object el) {
        //this check is to ensure that the parental relationship is automatically
        //cleaned up from the item currently at the specified index. The idea here
        //is that if you're removing an element (which you are effectively doing
        //via a set) then that element should no longer point to this object as its 
        //parent.
        if (el!=null && el instanceof PData && list.contains(el)) {
            PData pdata = (PData) el;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(null);
            if (pdata.isInheritParents() && pdata.getParent()==this) pdata.setParent(null);    //csc_012003.1
        }

        //remove the element from the list
        return list.remove(el);
    }

    /**
     * Removes from this list all the elements that are contained in the 
     * specified collection (optional operation). 
     */
    public boolean removeAll(Collection c) {
        //this check is to ensure that the parental relationship is automatically
        //cleaned up from the item currently at the specified index. The idea here
        //is that if you're removing an element (which you are effectively doing
        //via a set) then that element should no longer point to this object as its 
        //parent.
        if (c!=null) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                Object el = it.next();
                if (el!=null && el instanceof PData) {
                    PData pdata = (PData) el;
//csc_012003.1                    if (pdata.isInheritParents()) pdata.setParent(null);
                    if (pdata.isInheritParents() && pdata.getParent()==this) pdata.setParent(null);    //csc_012003.1
                }
            }
        }

        //remove the collection from the list
        return list.removeAll(c);
    }

    /**
     * Retains only the elements in this list that are contained in the 
     * specified collection (optional operation). 
     */
    public boolean retainAll(Collection c) {
        //this check is to ensure that the parental relationship is automatically
        //cleaned up from the item currently at the specified index. The idea here
        //is that if you're removing an element (which you are effectively doing
        //via a set) then that element should no longer point to this object as its 
        //parent.
        if (c!=null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object el = it.next();
                if (el!=null && el instanceof PData) {
                    PData pdata = (PData) el;
//csc_012003.1                    if (pdata.isInheritParents() && !(c.contains(el))) pdata.setParent(null);
                    if (pdata.isInheritParents() && pdata.getParent()==this && !(c.contains(el))) pdata.setParent(null);    //csc_012003.1
                }
            }
        }

        //retain the items in the collection
        return list.retainAll(c);
    }

    /**
     * Replaces the element at the specified position in this list with the 
     * specified element (optional operation). 
     */
    public Object set(int index, Object el) {
        //this check is to ensure that the parental relationship is automatically
        //cleaned up from the item currently at the specified index. The idea here
        //is that if you're removing an element (which you are effectively doing
        //via a set) then that element should no longer point to this object as its 
        //parent.
        Object curEl = list.get(index);
        if (curEl!=null && curEl instanceof PData) {
            PData pdata = (PData) curEl;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(null);
            if (pdata.isInheritParents() && pdata.getParent()==this) pdata.setParent(null);    //csc_012003.1
        }

        //this check is used to ensure the parental hierarchy is automatically
        //maintained. If you add an element to this list and that element implements
        //PData and that element has inheritParents=true, then this list should
        //automatically automatically become that objects parent
        if (el!=null && el instanceof PData) {
            PData pdata = (PData) el;
//csc_012003.1            if (pdata.isInheritParents()) pdata.setParent(this);
            if (pdata.isInheritParents() && pdata.getParent()==null) pdata.setParent(this);    //csc_012003.1
        }

        //set the element in the list
        return list.set(index, el);
    }

    /**
     * Returns the number of elements in this list. 
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns a view of the portion of this list between the specified fromIndex, 
     * inclusive, and toIndex, exclusive. 
     */
    public List subList(int fromIndex, int toIndex){
        return list.subList(fromIndex, toIndex);
    }

    /**
     * Returns an array containing all of the elements in this list in proper 
     * sequence. 
     */
    public Object[] toArray() {
        return list.toArray();
    }

    /**
     * Returns an array containing all of the elements in this list in proper 
     * sequence; the runtime type of the returned array is that of the specified 
     * array. 
     */
    public Object[] toArray(Object[] a) {
        return list.toArray(a);
    }


    //--------------- Cloneable ----------------------------------
    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance. (The
     * elements themselves are not copied.)
     *
     * @return  a clone of this <tt>ArrayList</tt> instance.
     */
    public Object clone() {
        try { 
            PArrayList pal = (PArrayList) super.clone();
            pal.list = new ArrayList<Object>(list);            
            return pal;
        } catch (CloneNotSupportedException e) { 
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }


    //--------------- Object -------------------------------------
    /**
     * Check object for equality. Will return true if the incoming 
     * object is a) non-null, b) the size of the underlying list 
     * structures is the same and c) the list containsAll() the
     * same elements
     *
     * @param obj the object we're comparing against
     * @return true if the objects are equal
     */
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if (obj==this) return true;
        if (!(obj instanceof PList)) return false;
        PList pl = (PList) obj;
        if (this.size()!=pl.size()) return false;
        return (this.containsAll(pl));
    }

    /**
     * Returns the hash code value for this list. 
     */
    @Override
    public int hashCode() {
        return list.hashCode();
    }


    //--------------- Utility Methods ----------------------------
    
    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return getState(key);
    }

}
