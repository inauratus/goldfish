package org.barracudamvc.plankton.io.parser.json.lexer;

import java.io.IOException;
import java.io.PushbackInputStream;
import static org.barracudamvc.plankton.io.parser.json.lexer.StaticHelpers.read;

class NumberLexer {

    PushbackInputStream stream;
    CharBuffer buffer;

    int position;
    int line;

    NumberLexer(PushbackInputStream stream) {
        this.stream = stream;
        buffer = new CharBuffer();
    }

    Terminal lexNumber(int position, int line) {
        buffer.reset();
        Type type;
        State current = State.START;
        State nextState;
        while (current.isNot(State.DONE)) {
            int c = read(stream);
            type = toType(c);

            nextState = transitions[current.ordinal()][type.ordinal()];
            if (nextState == null) {
                throw new IllegalStateException();
            }
            if (nextState.equals(State.DONE)) {
                unread(c);
            } else {
                position++;
                buffer.append((char) c);
            }
            current = nextState;
        }
        Terminal token = new Terminal();
        token.line = line;
        token.type = TerminalType.VALUE_LITERAL;
        token.value = buffer.toString();
        token.position = position;
        return token;

    }

    private Type toType(int c) {
        Type type;
        if (c >= 49 && c <= 57) {
            type = Type.DIGIT_1_9;
        } else if (c == 48) {
            type = Type.DIGIT_0;
        } else if (c == 46) {
            type = Type.DOT;
        } else if (c == 45 || c == 43) {
            type = Type.SIGN;
        } else if (c == 69 || c == 101) {
            type = Type.E;
        } else {
            type = Type.OTHER;
        }
        return type;
    }

    private static State[][] transitions = new State[State.values().length][Type.values().length];

    static {
        tran(State.START, Type.DIGIT_0, State.ZERO);
        tran(State.START, Type.DIGIT_1_9, State.NATURAL_NUMBER);
        tran(State.START, Type.SIGN, State.SIGNED_START);

        tran(State.SIGNED_START, Type.DIGIT_0, State.ZERO);
        tran(State.SIGNED_START, Type.DIGIT_1_9, State.NATURAL_NUMBER);

        tran(State.ZERO, Type.OTHER, State.DONE);
        tran(State.ZERO, Type.DOT, State.RATIONAL_START);

        tran(State.NATURAL_NUMBER, Type.OTHER, State.DONE);
        tran(State.NATURAL_NUMBER, Type.DIGIT_1_9, State.NATURAL_NUMBER);
        tran(State.NATURAL_NUMBER, Type.DIGIT_0, State.NATURAL_NUMBER);
        tran(State.NATURAL_NUMBER, Type.DOT, State.RATIONAL_START);

        tran(State.RATIONAL_START, Type.DIGIT_0, State.RATIONAL_NUMBER);
        tran(State.RATIONAL_START, Type.DIGIT_1_9, State.RATIONAL_NUMBER);

        tran(State.RATIONAL_NUMBER, Type.DIGIT_1_9, State.RATIONAL_NUMBER);
        tran(State.RATIONAL_NUMBER, Type.DIGIT_0, State.RATIONAL_NUMBER);
        tran(State.RATIONAL_NUMBER, Type.E, State.PRECISION_START);
        tran(State.RATIONAL_NUMBER, Type.OTHER, State.DONE);

        tran(State.PRECISION_START, Type.DIGIT_1_9, State.PRECISION);
        tran(State.PRECISION_START, Type.SIGN, State.SIGNED_PRECISION);

        tran(State.SIGNED_PRECISION, Type.DIGIT_1_9, State.PRECISION);

        tran(State.PRECISION, Type.OTHER, State.DONE);
        tran(State.PRECISION, Type.DIGIT_0, State.PRECISION);
        tran(State.PRECISION, Type.DIGIT_1_9, State.PRECISION);
    }

    private static void tran(State state, Type type, State next) {
        transitions[state.ordinal()][type.ordinal()] = next;
    }

    private void unread(int c) {
        try {
            stream.unread(c);
        } catch (IOException io) {
            throw new IllegalStateException();
        }
    }

    private enum Type {

        DIGIT_1_9,
        DIGIT_0,
        DOT,
        E,
        OTHER,
        SIGN
    }

    private enum State {

        SIGNED_START,
        START,
        ZERO,
        NATURAL_NUMBER,
        RATIONAL_START,
        SIGNED_PRECISION,
        RATIONAL_NUMBER,
        PRECISION_START,
        PRECISION,
        ERROR,
        DONE;

        public boolean isNot(State other) {
            return !this.equals(other);
        }
    }
}
