/*
 * ---------------------------------------------------------------------
 * This class is taken verbatim from a free implementation which is described
 * and documented here: http://archive.devx.com/java/free/articles/Kabutz01/Kabutz01-1.asp.
 * You may also wish to visit the author's website here: http://www.javaspecialists.co.za
 *
 * The only modification made has been to change the package for easy inclusion
 * in Barracuda's Plankton library, and to include the EPL licensing verbiage
 * ---------------------------------------------------------------------
 *
 * Copyright (C) 2003  Dr. Heinz M. Kabutz [h.kabutz@computer.org]
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
 * $Id: SoftHashMap.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.plankton.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SoftHashMap<KeyType, ValueType> extends AbstractMap<KeyType, ValueType> implements Serializable {

    static final long serialVersionUID = 1;
    /** Reference queue for cleared SoftReference objects. */
    private transient ReferenceQueue<ValueType> queue;
    /** The internal HashMap that will hold the SoftReference. */
    private transient Map<KeyType, SoftValue<ValueType>> hash;
    /** The FIFO list of hard references, order of last access. */
    private transient List<Object> hardCache;

    /** The number of "hard" references to hold internally. */
    private final int hardSize;

    public SoftHashMap() {
        this(200);
    }

    public SoftHashMap(int hardSize) {
        queue = new ReferenceQueue<>();
        hash = new HashMap<>();
        hardCache = Collections.synchronizedList(new LinkedList<>());
        this.hardSize = hardSize;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ValueType get(Object key) {
        Object result = null;
        // We get the SoftReference represented by that key
        SoftReference soft_ref = (SoftReference) hash.get(key);
        if (soft_ref != null) {
            // From the SoftReference we get the value, which can be
            // null if it was not in the map, or it was removed in
            // the processQueue() method defined below
            result = soft_ref.get();
            if (result == null) {
                // If the value has been garbage collected, remove the
                // entry from the HashMap.
                hash.remove(key);
            } else {
                // We now add this object to the beginning of the hard
                // reference queue.  One reference can occur more than
                // once, because lookups of the FIFO queue are slow, so
                // we don't want to search through it each time to remove
                // duplicates.
                hardCache.add(0, result);
                if (hardCache.size() > hardSize) {
                    // Remove the last entry if list longer than HARD_SIZE
                    synchronized (hardCache) {
                        hardCache.remove(hardCache.size() - 1);
                    }
                }
            }
        }
        return (ValueType) result;
    }

    /** 
     * We define our own subclass of SoftReference which contains
     * not only the value but also the key to make it easier to find
     * the entry in the HashMap after it's been garbage collected. 
     */
    private static class SoftValue<ValueType> extends SoftReference<ValueType> {

        private final Object key; // always make data member final

        private SoftValue(ValueType value, Object key, ReferenceQueue<ValueType> q) {
            super(value, q);
            this.key = key;
        }
    }

    /** Here we go through the ReferenceQueue and remove garbage
     collected SoftValue objects from the HashMap by looking them
     up using the SoftValue.key data member. */
    @SuppressWarnings("unchecked")
    private void processQueue() {
        SoftValue<ValueType> sv;
        while ((sv = (SoftValue<ValueType>) queue.poll()) != null) {
            hash.remove(sv.key);
        }
    }

    /** Here we put the key, value pair into the HashMap using
     a SoftValue object. */
    @Override
    public ValueType put(KeyType key, ValueType value) {
        processQueue(); // throw out garbage collected values first
        SoftValue<ValueType> oldValue = hash.put(key, new SoftValue<>(value, key, queue));
        return oldValue == null ? null : (ValueType) oldValue.get();
    }

    @Override
    public ValueType remove(Object key) {
        processQueue(); // throw out garbage collected values first
        SoftValue<ValueType> result = hash.remove(key);
        return result == null ? null : result.get();
    }

    @Override
    public void clear() {
        hardCache.clear();
        processQueue(); // throw out garbage collected values
        hash.clear();
    }

    @Override
    public int size() {
        processQueue(); // throw out garbage collected values first
        return hash.size();
    }

    @Override
    public Set<Entry<KeyType, ValueType>> entrySet() {
        processQueue(); // throw out garbage collected values first
        Set<Entry<KeyType, ValueType>> set = new HashSet<>();
        for (final Entry<KeyType, SoftValue<ValueType>> entry : hash.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            set.add(new Entry<KeyType, ValueType>() {
                @Override
                public KeyType getKey() {
                    return entry.getKey();
                }

                @Override
                public ValueType getValue() {
                    if (entry.getValue() == null) {
                        return null;
                    } else {
                        return (ValueType) entry.getValue().get();
                    }
                }

                @Override
                public ValueType setValue(ValueType value) {
                    throw new UnsupportedOperationException();
                }
            });

        }
        return set;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        queue = new ReferenceQueue<>();
        hash = new HashMap<>();
        hardCache = Collections.synchronizedList(new LinkedList<>());
    }
}
