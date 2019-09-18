/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: StateMapContainer.java 
 * Created: Feb 18, 2013 11:26:49 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.plankton.data;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class StateMapContainer implements StateMap {

    StateMap stateMap;

    protected void setStateMap(StateMap stateMap) {
        this.stateMap = stateMap;
    }

    protected StateMap getStateMap() {
        return this.stateMap;
    }

    @Override
    public void putState(Object key, Object val) {
        stateMap.putState(key, val);
    }

    @Override
    public <DesiredType> DesiredType getState(Object key) {
        return stateMap.getState(key);
    }

    @Override
    public Object removeState(Object key) {
        return stateMap.removeState(key);
    }

    @Override
    public Set getStateKeys() {
        return stateMap.getStateKeys();
    }

    @Override
    public Map getStateStore() {
        return stateMap.getStateStore();
    }

    @Override
    public void clearState() {
        stateMap.clearState();
    }
    
    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return getState(key);
    }
}
