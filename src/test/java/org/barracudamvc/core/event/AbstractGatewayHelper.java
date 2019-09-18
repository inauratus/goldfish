/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: AbstractGatewayHelper.java 
 * Created: Nov 5, 2013 11:19:53 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.core.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.servlet.ServletException;
import org.barracudamvc.testbed.servlet.MockHttpServletRequest;
import org.barracudamvc.testbed.servlet.MockHttpServletResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public abstract class AbstractGatewayHelper {

    public static final String BASE_URL = "http://www.example.com/Path";

    protected MockHttpServletResponse response() {
        MockHttpServletResponse responce = new MockHttpServletResponse();
        return responce;
    }

    protected MockHttpServletRequest request(String requestString) throws MalformedURLException {
        MockHttpServletRequest request = new MockHttpServletRequest(new URL(BASE_URL + requestString));
        return request;
    }

    protected MockHttpServletResponse get(String url) throws MalformedURLException, ServletException, IOException {
        MockHttpServletRequest request = request(url);
        MockHttpServletResponse response = response();
        getGateway().doGet(request, response);
        return response;
    }

    protected abstract ApplicationGateway getGateway();

    protected void assertStatusEquals(MockHttpServletResponse response, int statusCode) {
        assertEquals(statusCode, response.getStatus());
    }

    public void assertResponseContains(MockHttpServletResponse response, String toFind) throws UnsupportedEncodingException {
        String rString = responseToString(response);

        assertTrue("[[[" + rString + "]]] did not contain " + toFind, rString.contains(toFind));
    }

    protected String responseToString(MockHttpServletResponse response) throws UnsupportedEncodingException {
        return new String(response.getContentsAsBtyeArray(), Charset.defaultCharset().name());
    }

    Route route(final Class initEvent) {
        return new Route(getGateway().getEventBroker(), initEvent);
    }

    public static class Route {

        EventBroker broker;
        final Class initEvent;

        public Route(EventBroker broker, final Class initEvent) {
            this.broker = broker;
            this.initEvent = initEvent;
        }

        public void to(BaseEvent renderEvent) throws InvalidClassException {
            addEventDestination(broker, initEvent, renderEvent);
        }

        public void addEventDestination(EventBroker broker, final Class initEvent, final BaseEvent renderEvent) throws InvalidClassException {
            broker.addEventListener(new ListenerFactory() {

                @Override
                public BaseEventListener getInstance() {
                    return new DefaultBaseEventListener() {
                        @Override
                        public void handleControlEvent(ControlEventContext context) throws EventException, ServletException, IOException {
                            context.getQueue().addEvent(renderEvent);
                        }

                        @Override
                        public boolean isHandled() {
                            return false;
                        }
                    };
                }

                @Override
                public boolean notifyAlways() {
                    return true;
                }

                @Override
                public String getListenerID() {
                    return initEvent.getName();
                }
            }, initEvent);
        }

    }

    public void addRenderMessage(final Class event, final String message) throws InvalidClassException {
        getGateway().getEventBroker().addEventListener(new DefaultListenerFactory() {

            @Override
            public BaseEventListener getInstance() {

                return new DefaultBaseEventListener() {

                    @Override
                    public void handleViewEvent(ViewEventContext context) throws EventException, ServletException, IOException {
                        PrintWriter pw = context.getResponse().getWriter();
                        pw.print(message);
                    }
                };
            }

            @Override
            public String getListenerID() {
                return event.getName();
            }

        }, event);
    }
}
