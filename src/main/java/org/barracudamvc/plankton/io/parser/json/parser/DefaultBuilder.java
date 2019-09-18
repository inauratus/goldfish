/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.plankton.io.parser.json.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.barracudamvc.plankton.io.parser.json.Builder;

public class DefaultBuilder implements Builder<Object> {

    Stack<Object> structures = new Stack();
    String mapKey;

    public DefaultBuilder() {
        structures.add(new ArrayList(1));
    }

    @Override
    public void builderMap() {
        buildNode(new LinkedHashMap());
    }

    @Override
    public void buildMapKey(String key) {
        mapKey = key;
    }

    @Override
    public void buildMapValue(String value) {
        addMapValue(value);
    }

    @Override
    public void finishMap() {
        structures.pop();
    }

    @Override
    public void buildArray() {
        ArrayList list = new ArrayList();
        buildNode(list);
    }

    @Override
    public void buildArrayValue(String value) {
        addArrayValue(value);
    }

    private void addArrayValue(Object o) {
        ((List) structures.peek()).add(o);
    }

    private void addMapValue(Object o) {
        ((Map) structures.peek()).put(mapKey, o);
        mapKey = null;
    }

    @Override
    public void finishArray() {
        structures.pop();
    }

    private void buildNode(Object list) {
        if (mapKey == null) {
            addArrayValue(list);
        } else {
            addMapValue(list);
        }
        structures.push(list);
    }

    public Object getResult() {
        return ((List) structures.pop()).get(0);
    }
}
