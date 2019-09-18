package org.barracudamvc.plankton.io.parser.URLEncoded;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class StateContext {

    State currentState;
    char token;
    Event event;
    CharBuffer currentBuffer;

    HashMap<String, List<String>> pairs = new HashMap<>();

    CharBuffer keyBuffer;
    CharBuffer valueBuilder;

    int position = 0;

    public StateContext() {
        currentState = State.KEY;
        keyBuffer = new CharBuffer();
        valueBuilder = new CharBuffer();
        currentBuffer = keyBuffer;
    }

    void addToken(char value) {
        currentBuffer.append(value);
    }

    void takeKey() {
        currentBuffer = valueBuilder;
    }

    void takePair() {
        if (keyBuffer.length() == 0 && valueBuilder.length() == 0) {
            return;
        }
        getValues(toValue(keyBuffer)).add(toValue(valueBuilder));
        valueBuilder.reset();
        keyBuffer.reset();
        currentBuffer = keyBuffer;
    }

    private String toValue(CharBuffer builder) {
        if (builder.length() == 0) {
            return null;
        } else {
            return builder.toString();
        }
    }

    List<String> getValues(String key) {
        List<String> values = pairs.get(key);
        if (values == null) {
            values = new ArrayList<>();
            pairs.put(key, values);
        }
        return values;
    }
}
