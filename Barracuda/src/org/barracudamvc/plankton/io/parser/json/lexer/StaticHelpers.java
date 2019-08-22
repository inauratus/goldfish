package org.barracudamvc.plankton.io.parser.json.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

class StaticHelpers {

    static char readChar(InputStream stream) {
        int raw = read(stream);
        checkNotEndOfStream(raw);
        return (char) raw;
    }

    static int read(InputStream stream) {
        try {
            return stream.read();
        } catch (IOException io) {
            throw new UnexpectedEndOfStream();
        }
    }

    static void checkNotEndOfStream(int raw) {
        if (raw == -1) {
            throw new UnexpectedEndOfStream();
        }
    }

    static void uncheckPushBack(PushbackInputStream stream, int raw) {
        try {
            stream.unread(raw);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
}
