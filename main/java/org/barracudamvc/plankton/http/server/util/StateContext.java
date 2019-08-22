/*
 The MIT License (MIT)

 Copyright (c) <2014> <Charles H. Lowery>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 https://github.com/chucklowery/URLEncodingParser
 */
package org.barracudamvc.plankton.http.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class StateContext {

    State currentState;
    char token;
    Event event;
    CharBuffer currentBuffer;

    HashMap<String, List<String>> pairs = new HashMap<String, List<String>>();

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
            values = new ArrayList<String>();
            pairs.put(key, values);
        }
        return values;
    }
}
