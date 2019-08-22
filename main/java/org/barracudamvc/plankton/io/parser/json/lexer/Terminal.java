package org.barracudamvc.plankton.io.parser.json.lexer;

public class Terminal {

    int position;
    int line;
    TerminalType type;
    String value;

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    public TerminalType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return line + ":" + position + " " + type + " " + (value == null ? type.getCommonValue() : value.toString());
    }

}
