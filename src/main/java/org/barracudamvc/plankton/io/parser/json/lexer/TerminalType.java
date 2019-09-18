package org.barracudamvc.plankton.io.parser.json.lexer;

public enum TerminalType {

    EOS(null),
    OPEN_BRACE('{'),
    CLOSE_BRACE('}'),
    OPEN_BRACKET('['),
    CLOSE_BRACKET(']'),
    SEMICOLON(':'),
    COMMA(','),
    VALUE_LITERAL(null),
    STRING_LITERAL(null);

    Object cachedValue;

    TerminalType(char token) {
        this(new char[]{token});
    }

    TerminalType(Object cachedValue) {
        this.cachedValue = cachedValue;
    }

    public Object getCommonValue() {
        return cachedValue;
    }

}
