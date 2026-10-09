package org.barracudamvc.plankton.data;

import java.util.Map;
import java.util.Set;

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
