package org.barracudamvc.plankton.io.parser.json;

import java.io.InputStream;
import org.barracudamvc.plankton.io.parser.json.lexer.LexerStream;
import org.barracudamvc.plankton.io.parser.json.parser.Parser;

public class JSONParser {

    public <Type extends Object> Type parse(InputStream stream, Builder<Type> builder) {
        LexerStream lexer = new LexerStream(stream);
        Parser parser = new Parser();
        parser.parse(lexer, builder);

        return builder.getResult();
    }
}
