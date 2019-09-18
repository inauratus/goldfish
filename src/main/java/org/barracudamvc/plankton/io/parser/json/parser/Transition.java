package org.barracudamvc.plankton.io.parser.json.parser;

import org.barracudamvc.plankton.io.parser.json.Builder;
import org.barracudamvc.plankton.io.parser.json.lexer.Terminal;

class Transition {

    boolean requiresPush;
    
    ParseState nextState;
    Actor actor;

    Transition(ParseState nextState, boolean requiresPush, Actor action) {
        this.nextState = nextState;
        this.actor = action;
        this.requiresPush = requiresPush;
    }

    void transition(Terminal token, Builder builder) {
        actor.act(token, builder);
    }
}
