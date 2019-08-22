package org.barracudamvc.plankton.io.parser.URLEncoded;

import static java.util.Arrays.copyOf;

public class CharBuffer {

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
