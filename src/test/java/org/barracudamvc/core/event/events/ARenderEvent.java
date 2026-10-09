package org.barracudamvc.core.event.events;

import org.barracudamvc.core.event.HttpResponseEvent;

public class ARenderEvent extends HttpResponseEvent {

    @Override
    public boolean isHandled() {
        return false;
    }
}
