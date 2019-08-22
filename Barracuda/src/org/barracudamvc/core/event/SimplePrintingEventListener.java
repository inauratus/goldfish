/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.event;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;

public class SimplePrintingEventListener extends DefaultBaseEventListener {

    private final String message;

    public SimplePrintingEventListener(String message) {
        this.message = message;
    }

    @Override
    public void handleViewEvent(ViewEventContext context) throws EventException, ServletException, IOException {
        PrintWriter pw = context.getResponse().getWriter();
        pw.print(message);
    }
}
