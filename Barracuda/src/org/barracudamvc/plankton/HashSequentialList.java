/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: HashLinkedList.java 
 * Created: Feb 4, 2013 5:02:03 PM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.plankton;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;

/**
 * This List is backed by both an Array and a Hash Map. This 
 * 
 * 
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class HashSequentialList<T> extends AbstractSequentialList<T> implements RandomAccess {

    private List<T> backingList = new ArrayList<T>();
    private Map<T,Integer> backingMap = new HashMap<T,Integer>();

    public HashSequentialList() {
        
    }
    
    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean contains(Object o) {
        return backingMap.get(o) != null;
    }

    
    @Override
    public ListIterator<T> listIterator(int i) {
        return new IteratorImpl(backingList.listIterator(i));
    }

    private class IteratorImpl implements ListIterator<T> {

        T obj;
        ListIterator<T> it;

        private IteratorImpl(ListIterator<T> it) {
            this.it = it;
        }

        @Override
        public T next() {
            return obj = it.next();
        }

        @Override
        public T previous() {
            return obj = it.previous();
        }

        @Override
        public void remove() {
            it.remove();
            Integer count = backingMap.get(obj);
            if(count != null) {
                count = count - 1;
                backingMap.put(obj, count == 0 ? null : count);
            } 
        }

        @Override
        public void set(T t) {
            it.set(t);

            Integer count = backingMap.get(obj);
            if(count != null) {
                count = count - 1;
                backingMap.put(obj, count == 0 ? null : count);
            } 

            
            count = backingMap.get(t);
             backingMap.put(t, count == null ? 1 : count++);
        }

        @Override
        public void add(T t) {
            it.add(t);
            Integer count = backingMap.get(t);
             backingMap.put(t, count == null ? 1 : count++);
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }
    } 
}