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

interface StateActor {

    void takeAction(StateContext context, char state);

}

class TakePairActor implements StateActor {

    public static final TakePairActor TAKE_PAIR = new TakePairActor();

    @Override
    public void takeAction(StateContext context, char c) {
        context.takePair();
    }
}

class TakeKey implements StateActor {

    public static final TakeKey TAKE_KEY = new TakeKey();

    @Override
    public void takeAction(StateContext context, char c) {
        context.takeKey();
    }
}

class AddToken implements StateActor {

    public static final AddToken ADD_CHAR = new AddToken();

    @Override
    public void takeAction(StateContext context, char c) {
        context.addToken(c);
    }
}

class AddSpace implements StateActor {

    public static final AddSpace ADD_SPACE = new AddSpace();

    @Override
    public void takeAction(StateContext context, char state) {
        context.addToken(' ');
    }
}

class StreamInvalidException extends RuntimeException {

    public StreamInvalidException(Integer index, Character c) {
        super("Unexpected token found in stream @" + index + " found:" + c);
    }
}

class HexValueOutOfRange extends StreamInvalidException {

    public HexValueOutOfRange(Integer index, Character c) {
        super(index, c);
    }
}
