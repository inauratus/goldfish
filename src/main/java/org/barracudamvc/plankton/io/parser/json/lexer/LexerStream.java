package org.barracudamvc.plankton.io.parser.json.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import static org.barracudamvc.plankton.io.parser.json.lexer.StaticHelpers.read;
import static org.barracudamvc.plankton.io.parser.json.lexer.StaticHelpers.readChar;


public class LexerStream {

    PushbackInputStream stream;
    int position = 0;
    int line = 1;
    char rawCharacter;
    boolean hasMore = true;

    Terminal eos;
    CharBuffer buffer;
    StringLexer stringLexer;
    NumberLexer numberLexer;

    public LexerStream(InputStream stream) {
        this.stream = new PushbackInputStream(stream, 1);
        buffer = new CharBuffer();
        stringLexer = new StringLexer(this.stream);
        numberLexer = new NumberLexer(this.stream);
    }

    public Terminal next() {
        int raw;
        while (true) {
            raw = read(stream);
            switch (raw) {
                case -1:
                    eos = asToken(TerminalType.EOS);
                    hasMore = false;
                    return eos;
                case ' ':
                case '\t':
                    position++;
                    break;
                case '\n':
                    position = 0;
                    line++;
                    break;
                case '\r':
                    position = 0;
                    line++;
                    if ((raw = read(stream)) != '\n') {
                        uncheckPushBack(raw);
                    }
                    break;
                default:
                    return parseToken(raw);
            }
        }
    }

    private void uncheckPushBack(int raw) {
        try {
            stream.unread(raw);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private Terminal parseToken(int raw) throws CompleteSuprise {
        Terminal token = new Terminal();
        token.line = line;
        token.position = position;

        rawCharacter = (char) raw;

        switch (rawCharacter) {
            case '{':
                return asToken(TerminalType.OPEN_BRACE);
            case '}':
                return asToken(TerminalType.CLOSE_BRACE);
            case '[':
                return asToken(TerminalType.OPEN_BRACKET);
            case ']':
                return asToken(TerminalType.CLOSE_BRACKET);
            case ':':
                return asToken(TerminalType.SEMICOLON);
            case ',':
                return asToken(TerminalType.COMMA);
            case '"':
                return lexStringToken();
            case 'n':
                return asLiteralToken("null", null);
            case 't':
                return asLiteralToken("true", "true");
            case 'f':
                return asLiteralToken("false", "false");
            case '-':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
                uncheckPushBack((int) rawCharacter);
                return numberLexer.lexNumber(position, line);
            default:
                throw new CompleteSuprise(position, line, new char[]{rawCharacter});
        }
    }

    private Terminal asLiteralToken(String literal, String value) {
        checkIf(literal);
        Terminal asToken = asToken(TerminalType.VALUE_LITERAL, literal.length());
        asToken.value = value;
        return asToken;
    }

    private Terminal lexStringToken() {
        char[] chars = stringLexer.lexString(position, line);
        Terminal t = asToken(TerminalType.STRING_LITERAL, chars.length);
        t.value = new String(chars);
        return t;
    }

    private Terminal asToken(TerminalType type, int length) {
        Terminal token = new Terminal();
        token.line = line;
        token.position = position + length;
        token.value = null;
        token.type = type;
        return token;
    }

    private Terminal asToken(TerminalType type) {
        return asToken(type, 1);
    }

    private void checkIf(String reserved) {
        for (int i = 1; i < reserved.length(); i++) {
            char letter = readChar(stream);
            if (letter != reserved.charAt(i)) {
                throw new CompleteSuprise(position, line, reserved.toCharArray(), " expected *" + reserved + "* ");
            }
        }
    }
}

class UnexpectedEndOfStream extends IllegalStateException {

}

class CompleteSuprise extends IllegalStateException {

    public CompleteSuprise(int position, int line, char[] tokens) {
        this(position, line, tokens, "");
    }

    public CompleteSuprise(int position, int line, char[] tokens, String message) {
        super(line + ":" + position + " This came as a complete suprise to me: " + new String(tokens) + " " + message);
    }
}
