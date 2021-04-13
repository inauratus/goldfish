package org.barracudamvc.plankton.io.parser.json;

public interface Builder<Type> {
    void emptyStream();

    public void builderMap();

    public void buildMapKey(String key);

    public void buildMapValue(String value);

    public void finishMap();

    public void buildArray();

    public void buildArrayValue(String value);

    public void finishArray();
    
    public Type getResult();
}
