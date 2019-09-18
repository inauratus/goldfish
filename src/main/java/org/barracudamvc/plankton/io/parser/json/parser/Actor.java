package org.barracudamvc.plankton.io.parser.json.parser;

import org.barracudamvc.plankton.io.parser.json.Builder;
import org.barracudamvc.plankton.io.parser.json.lexer.Terminal;

public interface Actor {

    public void act(Terminal token, Builder builder);
}
