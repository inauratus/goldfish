package org.barracudamvc.plankton.io.parser.URLEncoded;

interface StateActor {

    void takeAction(StateContext context, char state);

}

class TakePairActor implements StateActor {

    public static final TakePairActor TAKE_PAIR = new TakePairActor();

    @Override
    public void takeAction(StateContext context, char c) {
        context.takePair();
    }
}

class TakeKey implements StateActor {

    public static final TakeKey TAKE_KEY = new TakeKey();

    @Override
    public void takeAction(StateContext context, char c) {
        context.takeKey();
    }
}

class AddToken implements StateActor {

    public static final AddToken ADD_CHAR = new AddToken();

    @Override
    public void takeAction(StateContext context, char c) {
        context.addToken(c);
    }
}

class AddSpace implements StateActor {

    public static final AddSpace ADD_SPACE = new AddSpace();

    @Override
    public void takeAction(StateContext context, char state) {
        context.addToken(' ');
    }
}

class StreamInvalidException extends RuntimeException {

    public StreamInvalidException(Integer index, Character c) {
        super("Unexpected token found in stream @" + index + " found:" + c);
    }
}

class HexValueOutOfRange extends StreamInvalidException {

    public HexValueOutOfRange(Integer index, Character c) {
        super(index, c);
    }
}
