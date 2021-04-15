package org.barracudamvc.plankton.io.parser.json.parser;

import org.barracudamvc.plankton.io.parser.json.Builder;
import java.util.Stack;
import org.barracudamvc.plankton.io.parser.json.lexer.LexerStream;
import org.barracudamvc.plankton.io.parser.json.lexer.Terminal;
import org.barracudamvc.plankton.io.parser.json.lexer.TerminalType;
import static org.barracudamvc.plankton.io.parser.json.lexer.TerminalType.*;
import static org.barracudamvc.plankton.io.parser.json.parser.Parser.State.*;

public class Parser {

    private static Transition[][] transitions = new Transition[State.values().length][TerminalType.values().length];

    static {
        inital:
        {
            tran(INITIAL, OPEN_BRACE, MAP, new BuildMap(State.FINISHED));
            tran(INITIAL, OPEN_BRACKET, ARRAY, new BuildArray(FINISHED));
            tran(INITIAL, EOS, FINISHED, new BuildEmptyStream(FINISHED));
        }
        map:
        {
            tran(MAP, STRING_LITERAL, MAP_KEY, new AddMapKey());
            tran(MAP, CLOSE_BRACE, null, new FinilizeMap());
            tran(MAP_KEY, SEMICOLON, MAP_ASIGN);
            tran(MAP_ASIGN, STRING_LITERAL, MAP_VALUE, new AddMapValue());
            tran(MAP_ASIGN, VALUE_LITERAL, MAP_VALUE, new AddMapValue());
            tran(MAP_ASIGN, OPEN_BRACE, MAP, new BuildMap(State.MAP_VALUE));
            tran(MAP_ASIGN, OPEN_BRACKET, ARRAY, new BuildArray(State.MAP_VALUE));

            tran(MAP_VALUE, CLOSE_BRACE, null, new FinilizeMap());
            tran(MAP_VALUE, COMMA, MAP);
        }
        array:
        {
            tran(ARRAY, STRING_LITERAL, ARRAY_VALUE, new AddArrayValue());
            tran(ARRAY, VALUE_LITERAL, ARRAY_VALUE, new AddArrayValue());
            tran(ARRAY, OPEN_BRACKET, ARRAY, new BuildArray(State.ARRAY_VALUE));
            tran(ARRAY, OPEN_BRACE, MAP, new BuildMap(State.ARRAY_VALUE));
            tran(ARRAY, CLOSE_BRACKET, null, new FinilizeArray());
            tran(ARRAY_VALUE, COMMA, ARRAY);
            tran(ARRAY_VALUE, CLOSE_BRACKET, null, new FinilizeArray());
        }
    }

    private void handleError(State state, Terminal terminal) {
        Transition[] trans = transitions[current.ordinal()];
        String error = terminal.getLine() + "|" + terminal.getPosition() + " Unexpected token found. Transition: " + state.name() + " Found " + terminal.getType() + "|" + terminal.getValue() + " expected one of:";
        for (int i = 0; i < trans.length; i++) {
            if (trans[i] != null) {
                TerminalType type = TerminalType.values()[i];
                error += " " + type.name() + ", ";
            }
        }
        throw new IllegalStateException(error);
    }

    static enum State {

        INITIAL,
        MAP,
        MAP_KEY,
        MAP_VALUE,
        MAP_ASIGN,
        MAP_SEPERATOR,
        ARRAY,
        ARRAY_VALUE,
        FINISHED
    }

    private State current = State.INITIAL;
    private Stack<State> stack = new Stack<>();
    private Builder builder;
    private Terminal terminal;

    public void parse(LexerStream stream, Builder builder) {
        this.builder = builder;
        while (!current.equals(State.FINISHED)) {
            terminal = stream.next();
            Transition transition = transitions[current.ordinal()][terminal.getType().ordinal()];
            if (transition == null) {
                handleError(current, terminal);
            } else {
                current = transition.next;
                if (transition.operation != null) {
                    transition.operation.accept(this);
                }
            }
        }

    }

    static class Transition {

        State next;
        Operation operation;

        public Transition(State next, Operation q) {
            this.next = next;
            this.operation = q;
        }

    }

    private static void tran(State given, TerminalType type, State next, Operation operation) {
        transitions[given.ordinal()][type.ordinal()] = new Transition(next, operation);
    }

    private static void tran(State given, TerminalType type, State next) {
        transitions[given.ordinal()][type.ordinal()] = new Transition(next, null);
    }

    public static interface Operation {

        public void accept(Parser parser);
    }

    private void pop() {
        current = stack.pop();
    }

    private void push(State state) {
        stack.add(state);
    }

    private static class BuildMap implements Operation {

        private State state;

        public BuildMap(State state) {
            this.state = state;
        }

        @Override
        public void accept(Parser parser) {
            parser.builder.builderMap();
            parser.push(state);

        }
    }

    private static class BuildArray implements Operation {

        private final State state;

        public BuildArray(State state) {
            this.state = state;
        }

        @Override
        public void accept(Parser parser) {
            parser.builder.buildArray();
            parser.push(state);
        }
    }

    private static class BuildEmptyStream implements Operation {

        private final State state;

        public BuildEmptyStream(State state) {
            this.state = state;
        }

        @Override
        public void accept(Parser parser) {
            parser.builder.emptyStream();
            parser.push(state);
        }
    }

    private static class AddMapKey implements Operation {

        public void accept(Parser parser) {
            parser.builder.buildMapKey(parser.terminal.getValue());
        }
    }

    private static class AddMapValue implements Operation {

        public void accept(Parser parser) {
            parser.builder.buildMapValue(parser.terminal.getValue());
        }
    }

    private static class AddArrayValue implements Operation {

        public void accept(Parser parser) {
            parser.builder.buildArrayValue(parser.terminal.getValue());
        }
    }

    private static class FinilizeMap implements Operation {

        public void accept(Parser parser) {
            parser.pop();
            parser.builder.finishMap();
        }
    }

    private static class FinilizeArray implements Operation {

        public void accept(Parser parser) {
            parser.pop();
            parser.builder.finishArray();
        }
    }
}
