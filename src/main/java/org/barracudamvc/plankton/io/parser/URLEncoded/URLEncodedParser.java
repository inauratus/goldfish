package org.barracudamvc.plankton.io.parser.URLEncoded;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static org.barracudamvc.plankton.io.parser.URLEncoded.AddSpace.ADD_SPACE;
import static org.barracudamvc.plankton.io.parser.URLEncoded.AddToken.ADD_CHAR;
import static org.barracudamvc.plankton.io.parser.URLEncoded.Event.AMPERSAND;
import static org.barracudamvc.plankton.io.parser.URLEncoded.Event.CHAR;
import static org.barracudamvc.plankton.io.parser.URLEncoded.Event.EQUAL;
import static org.barracudamvc.plankton.io.parser.URLEncoded.Event.PERCENT;
import static org.barracudamvc.plankton.io.parser.URLEncoded.Event.PLUS;
import static org.barracudamvc.plankton.io.parser.URLEncoded.State.KEY;
import static org.barracudamvc.plankton.io.parser.URLEncoded.State.VALUE;
import static org.barracudamvc.plankton.io.parser.URLEncoded.TakeKey.TAKE_KEY;
import static org.barracudamvc.plankton.io.parser.URLEncoded.TakePairActor.TAKE_PAIR;

/**
 * Thread safe URL encoded key=value parser.
 *
 * @author Charles H. Lowery <chuck.lowery @ gmail.com>
 */
public class URLEncodedParser {

    private static final Transition[][] transitions;

    static {
        transitions = new Transition[State.values().length][Event.values().length];

        transition(KEY, CHAR, KEY, ADD_CHAR);
        transition(KEY, AMPERSAND, KEY, TAKE_PAIR);
        transition(KEY, EQUAL, VALUE, TAKE_KEY);
        transition(KEY, PERCENT, KEY, ADD_CHAR);
        transition(KEY, PLUS, KEY, ADD_SPACE);

        transition(VALUE, CHAR, VALUE, ADD_CHAR);
        transition(VALUE, EQUAL, VALUE, ADD_CHAR);
        transition(VALUE, AMPERSAND, KEY, TAKE_PAIR);
        transition(VALUE, PERCENT, VALUE, ADD_CHAR);
        transition(VALUE, PLUS, VALUE, ADD_SPACE);
    }

    public Map<String, List<String>> parse(InputStream rawStream) {
        final InputStreamReader stream = new InputStreamReader(rawStream, StandardCharsets.UTF_8);
        StateContext context = new StateContext();
        for (context.position = 0; true; context.position++) {
            if (!scanForToken(stream, context)) {
                break;
            }
            findTransition(context.currentState, context.event).transition(context);
        }
        context.takePair();
        return context.pairs;
    }

    private static boolean scanForToken(final InputStreamReader stream, StateContext context) throws StreamInvalidException {
        int raw = read(stream);
        if (raw == -1) {
            return false;
        }
        context.token = (char) raw;

        switch (context.token) {
            case '%':
                context.event = PERCENT;
                context.token = scanForHexValue(stream, context);
                context.position += 2;
                break;
            case '=':
                context.event = EQUAL;
                break;
            case '&':
                context.event = AMPERSAND;
                break;
            case '+':
                context.event = PLUS;
                break;
            default:
                context.event = CHAR;
                break;
        }
        return true;
    }

    private static char scanForHexValue(final InputStreamReader stream, StateContext context) throws StreamInvalidException, HexValueOutOfRange {
        int hex1 = read(stream);
        int hex2 = read(stream);
        if (hex1 == -1 || hex2 == -1) {
            throw new StreamInvalidException(context.position, '%');
        }
        checkOutOfBounds((char) hex1, context);
        checkOutOfBounds((char) hex2, context);
        return (char) ((toHexValue((char) hex1) << 4) + toHexValue((char) hex2));
    }

    private static Transition findTransition(State state, Event event) {
        return transitions[state.ordinal()][event.ordinal()];
    }

    private static int read(InputStreamReader stream) {
        try {
            return stream.read();
        } catch (IOException ex) {
            throw new UnexpectEndOfStream();
        }
    }

    private static void transition(State given, Event when, State then, StateActor action) {
        transitions[given.ordinal()][when.ordinal()] = new Transition(then, action);
    }

    private static void checkOutOfBounds(char c, StateContext context) throws HexValueOutOfRange {
        int value = toHexValue(c);
        if (value > 15 || value < 0) {
            throw new HexValueOutOfRange(context.position, c);
        }
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

class UnexpectEndOfStream extends RuntimeException {

    public UnexpectEndOfStream() {
        super();
    }
}
