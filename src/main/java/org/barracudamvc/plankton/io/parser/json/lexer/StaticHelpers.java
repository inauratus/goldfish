package org.barracudamvc.plankton.io.parser.json.lexer;

import java.io.IOException;
import java.io.PushbackReader;

class StaticHelpers {

    static char readChar(PushbackReader stream) {
        int raw = read(stream);
        checkNotEndOfStream(raw);
        return (char) raw;
    }

    static int read(PushbackReader stream) {
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

}
