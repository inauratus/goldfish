package org.barracudamvc.core.event.events;

import org.barracudamvc.core.event.HttpRequestEvent;

public class AControlEvent extends HttpRequestEvent {

    @Override
    public boolean isHandled() {
        return true;
    }
}
