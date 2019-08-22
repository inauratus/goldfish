package org.barracudamvc.plankton.io.parser.json.lexer;

import java.io.PushbackInputStream;
import static org.barracudamvc.plankton.io.parser.json.lexer.StaticHelpers.readChar;

class StringLexer {

    PushbackInputStream stream;
    CharBuffer buffer;
    char[] unicodeCharacterbuffer = new char[]{'\\', 'u', '\0', '\0', '\0', '\0'};

    int position;
    int line;

    public StringLexer(PushbackInputStream stream) {
        this.stream = stream;
        this.buffer = new CharBuffer();
    }

    char[] lexString(int position, int line) {
        this.line = line;
        this.position = position;
        buffer.reset();

        while (true) {
            char rawCharacter = readChar(stream);
            switch (rawCharacter) {
                case '\\':
                    rawCharacter = findEscaped(readChar(stream));
                    buffer.append(rawCharacter);
                    break;
                case '"':
                    return buffer.toArray();
                default:
                    buffer.append(rawCharacter);
            }
        }
    }

    private char findEscaped(char c) {
        switch (c) {
            case '"':
                return '"';
            case '\\':
                return '\\';
            case '/':
                return '/';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                return readUnicode();
            default:
                throw new CompleteSuprise(position + buffer.length(), line, new char[]{'\\', 'c'});
        }
    }

    private char readUnicode() {
        unicodeCharacterbuffer[2] = readChar(stream);
        unicodeCharacterbuffer[3] = readChar(stream);
        unicodeCharacterbuffer[4] = readChar(stream);
        unicodeCharacterbuffer[5] = readChar(stream);

        char hex1 = (char) toHexValue(unicodeCharacterbuffer[2]);
        char hex2 = (char) toHexValue(unicodeCharacterbuffer[3]);
        char hex3 = (char) toHexValue(unicodeCharacterbuffer[4]);
        char hex4 = (char) toHexValue(unicodeCharacterbuffer[5]);

        if (checkOutOfBounds(hex1)) {
            throw new CompleteSuprise(position + 2, line, unicodeCharacterbuffer);
        }
        if (checkOutOfBounds(hex2)) {
            throw new CompleteSuprise(position + 3, line, unicodeCharacterbuffer);
        }
        if (checkOutOfBounds(hex3)) {
            throw new CompleteSuprise(position + 4, line, unicodeCharacterbuffer);
        }
        if (checkOutOfBounds(hex4)) {
            throw new CompleteSuprise(position + 5, line, unicodeCharacterbuffer);
        }

        return (char) ((hex1 << 12) + (hex2 << 8) + (hex3 << 4) + hex4);
    }

    private static boolean checkOutOfBounds(char c) {
        return (c > 16 || c < 0);
    }

    private static int toHexValue(char c) {
        if (c >= 'a') {
            return 10 + c - 'a';
        } else if (c >= 'A') {
            return 10 + c - 'A';
        } else {
            return c - '0';
        }
    }
}
