package org.barracudamvc.plankton.io.parser.json.parser;

enum ParseState {

    ENTRY,
    MAP,
    ARRAY,
    ARRAY_EXPECTING_VALUE,
    ARRAY_HAS_VALUE

}
