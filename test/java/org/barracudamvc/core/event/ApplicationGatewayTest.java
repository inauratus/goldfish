/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: TestApplicationGateway.java 
 * Created: Nov 1, 2013 10:52:01 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.event;

import javax.servlet.http.HttpServletResponse;
import org.barracudamvc.core.event.events.AControlEvent;
import org.barracudamvc.core.event.events.ARenderEvent;
import org.barracudamvc.testbed.servlet.MockHttpServletResponse;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class ApplicationGatewayTest extends AbstractGatewayHelper {

    @Test
    public void testRespondWith404True_PageNotFound_404Returned() throws Exception {
        ApplicationGateway.RESPOND_WITH_404 = true;
        MockHttpServletResponse response = get("/InvalidEventThatShouldNotBeFound.event");

        assertStatusEquals(response, HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testRespondWith404False_PageNotFound_200Returned() throws Exception {
        ApplicationGateway.RESPOND_WITH_404 = false;
        MockHttpServletResponse response = get("/InvalidEventThatShouldNotBeFound.event");

        assertStatusEquals(response, HttpServletResponse.SC_OK);
    }

    @Test
    public void testValidEventWithoutListeners_ExpectErrorMessage() throws Exception {
        ApplicationGateway.RESPOND_WITH_404 = true;
        MockHttpServletResponse response = get("/" + AControlEvent.class.getName() + ".event");

        assertStatusEquals(response, HttpServletResponse.SC_OK);
        assertResponseContains(response, "Error instantiating parent");
    }

    @Test
    public void testDispatchToControl_ReceiveResponseFromRenderer() throws Exception {
        final String message = "You have reached your destination, YAY!";
        route(AControlEvent.class).to(new ARenderEvent());
        addRenderMessage(ARenderEvent.class, message);

        MockHttpServletResponse response = get("/" + AControlEvent.class.getName() + ".event");

        assertStatusEquals(response, HttpServletResponse.SC_OK);
        assertResponseContains(response, message);
    }

    private ApplicationGateway gateway;

    @Before
    public void setUp() {
        gateway = new ApplicationGateway();
    }

    @Override
    protected ApplicationGateway getGateway() {
        return gateway;
    }
}
