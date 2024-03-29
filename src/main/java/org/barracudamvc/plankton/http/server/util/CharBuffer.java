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

import static java.util.Arrays.copyOf;

class CharBuffer {

    char[] chars;
    int chunkSize = 32;
    int mark = 0;

    public void append(char b) {
        ensureAdditionalSpace(1);
        chars[mark++] = b;
    }

    public void append(char[] bs) {
        ensureAdditionalSpace(bs.length);

        System.arraycopy(bs, 0, chars, mark, bs.length);
        mark += bs.length;
    }

    protected void ensureAdditionalSpace(int space) {
        if (chars == null) {
            chars = new char[nextSize(space)];
        } else {
            if (getBufferSize() < space + mark) {
                chars = copyOf(chars, nextSize(space));
            }
        }
    }

    private int nextSize(int space) {
        return (space < chunkSize ? chunkSize : space) + mark;
    }

    private int getBufferSize() {
        return chars == null ? 0 : chars.length;
    }

    public char[] toArray() {
        return copyOf(chars, mark);
    }

    public String toString() {
        return new String(toArray());
    }

    public void reset() {
        mark = 0;
    }

    public int length() {
        return mark;
    }

    public boolean isEmpty() {
        return mark == 0;
    }

    public boolean isNotEmpty() {
        return mark > 0;
    }
}
