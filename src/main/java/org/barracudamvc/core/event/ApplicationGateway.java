/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: ApplicationGateway.java 268 2014-05-05 16:58:42Z charleslowery $
 */
package org.barracudamvc.core.event;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.barracudamvc.core.event.events.ActionEvent;
import org.barracudamvc.core.helper.servlet.*;
import org.barracudamvc.core.view.ViewCapabilities;
import org.barracudamvc.plankton.Classes;
import org.barracudamvc.plankton.data.ObjectRepository;
import org.barracudamvc.plankton.exceptions.ExceptionUtil;
import org.barracudamvc.plankton.exceptions.NestableException;
import org.barracudamvc.plankton.http.URLRewriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>The application gateway is responsible for a number of things.
 *
 * <ol>
 *   <li>It acts as a gateway servlet for all event handlers in this
 *         application.</li>
 *
 *   <li>It defines and instantiates a number of entities needed to
 *       dispatch events (EventBroker, EventPool, DispatcherFactory, etc)</li>
 *
 *      <li>It defines which event extension we are using</li>
 *
 *      <li>It registers all EventGateways, and any local Event interests</li>
 *
 *      <li>It performs the initial mapping of HTTPRequests to Events for this
 *       particular domain </li>
 * </ol>
 *
 * <p>Consequently, if you want to use the Barracuda event model, this
 * is the class that really kicks it all off. You must either extend this
 * class OR use the DefaultApplicationAssembler in order to specify that
 * this servlet handles all event requests for an application. This will
 * allow the system to convert requests to events and dispatch them through
 * the EventBroker to any listeners within EventGateways that have registered
 * interest with the broker.
 *
 * <p>This class should be the first servlet loaded in your web.xml file.
 *
 * <p>For an example of how to do this, look at
 * <strong>org.barracudamvc.examples.ex1.SampleApplicationGateway</strong>
 *
 * <p>You might also want to look at the <a href="sm_barracuda_event.gif">UML Class
 * diagram of the Event model classes.</a>
 *
 * <Note on MDC support. If you turn on logging for org.barracudamvc.core.event.ApplicationGateway,
 * the following MDC values will be available to your logging - INFO: event DEBUG: params
 *
 * @author  Christian Cryder <christianc@granitepeaks.com>
 * @author  Diez Roggisch <diez.roggisch@artnology.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0
 */
public class ApplicationGateway extends HttpServlet implements EventGateway {

    private static final Class CLASS = ApplicationGateway.class;

    public static boolean USE_EVENT_POOLING = true;
    public static boolean RESPOND_WITH_404 = false;
    public static RequestWrapper REQUEST_WRAPPER = null;
    public static ResponseWrapper RESPONSE_WRAPPER = null;
    //...configuration constants (set through servlet init params)
    private static final String APPLICATION_ASSEMBLER = "ApplicationAssembler";
    private static final String ASSEMBLY_DESCRIPTOR = "AssemblyDescriptor";
    private static final String SAX_PARSER = "SAXParser";
    //...LocalObjectRepository constants (available for apps to access)
    public static final String HTTP_SERVLET_REQUEST = CLASS + ".HttpServletRequest";                  //(HttpServletReques)
    public static final String HTTP_SERVLET_RESPONSE = CLASS + ".HttpServletResponse";                //(HttpServletResponse)
    public static final String THREAD_POOL = CLASS + ".ThreadPool";                                   //(ThreadPool)
    private static final String METHOD_GET = "GET";
    private static final String METHOD_HEAD = "HEAD";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";

    //...EventContext constants (available for apps to access)
    //private vars
    private EventBroker masterEventBroker = null;
    private EventPool masterEventPool = null;
    protected EventGateway eventGateway = (EventGateway) Classes.newInstance(A_Classes.DEFAULT_EVENT_GATEWAY);
    protected List<EventGateway> gateways = null;
    Set<EventBroker> eventBrokers = new HashSet<>();

    /**
     * <p>Handle the default HttpRequest. It will probably be rare for
     * developers to override this.
     *
     * <p>Basically, this method receives a request and attempts to map it
     * to a valid HttpRequestEvent. If the event is invalid, or if the
     * event is NOT an instance of HttpRequestEvent, then we simply create
     * a new instance of HttpRequestEvent and dispatch that instead. This
     * is a very important feature, because it allows us to define events
     * which are not accessible to the outside world -- the gateway will
     * only dispatch HttpRequest events, so you define your publically
     * accessible API by creating an event hierarchy that extends from
     * the HttpRequestEvent object.
     *
     * <p>Once we have a valid event, we dispatch it. In this case (because this
     * is an Http gateway) we must have a response, so if we catch an
     * UnhandledEventException, we will generate a default error message and
     * return.
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleDefault(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ObjectRepository.setupSessionRepository(req);
            String target;
            if (req.getServletPath() == null || req.getServletPath().isEmpty()) {
                target = req.getRequestURI();
            } else {
                target = req.getServletPath();
            }
            handle(req, resp, target);
        } finally {
            ObjectRepository.removeLocalRepository();
            ObjectRepository.removeSessionRepository();
        }
    }

    protected void handle(HttpServletRequest req, HttpServletResponse resp, String target) throws RuntimeException, ServletException, IOException {
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        lor.putState(HTTP_SERVLET_REQUEST, req);
        lor.putState(HTTP_SERVLET_RESPONSE, resp);
        
        EventBroker eventBroker = getEventBroker();
        BaseEvent event = eventBroker.locateSourceEvent(eventBroker.findEventName(target));

        if (!(event instanceof HttpRequestEvent)) {
            if (RESPOND_WITH_404) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            } else {
                event = new HttpRequestEvent();
            }
        }

        event.setSource(this);
        event.setHandled(false);

        ViewCapabilities vc = new ViewCapabilities(req, resp);
        DefaultEventContext context = new DefaultEventContext(createDispatchQueue(event), vc, this.getServletConfig(), req, resp, new HttpResponseEvent());
        try {
            eventBroker.dispatchEvent(context);
        } catch (ClientSideRedirectException re) {
            context.persistContext(re);
            String url = URLRewriter.encodeRedirectURL(context.getRequest(), context.getResponse(), re.getRedirectURL());
            url = ScriptDetector.prepareRedirectURL(url, vc);
            resp.sendRedirect(url);
        } catch (EventException e) {
            handleEventException(e, context.getRequest(), context.getResponse());
        }
    }

    @Override
    public void init() throws ServletException {
        try {
            //perform any automated assembly if necessary
            String assemblerName = this.getServletConfig().getInitParameter(APPLICATION_ASSEMBLER);
            String descriptor = this.getServletConfig().getInitParameter(ASSEMBLY_DESCRIPTOR);
            if (assemblerName != null && descriptor != null) {
                try {
                    String parser = this.getServletConfig().getInitParameter(SAX_PARSER);
                    ApplicationAssembler assembler = (ApplicationAssembler) Classes.newInstance(assemblerName);  //csc_060903_1
                    if (parser != null) {
                        assembler.assemble(this, this.getServletConfig(), descriptor, parser);
                    } else {
                        assembler.assemble(this, this.getServletConfig(), descriptor);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error invoking assembler:" + e);
                }
            }

            //start by getting a reference to the EventBroker. Also request the
            //EventPool, just to make sure they're both instantiated. We do this here
            //primarily so that if there is a problem instantiating these, we find
            //out about it immediately on startup, rather than waiting until a request
            //actually comes through.
            EventBroker eb = this.getEventBroker();
            getEventPool();

            //allow for local initialization
            initializeLocal();

            //handle any gateways which were specified
            if (gateways != null) {
                for (EventGateway gateway : gateways) {
                    this.add(gateway);
                }
            }

            //now register the event gateway (note that we have to set the value to
            //false when registering to ensure that all possible combinations get
            //registered. If we don't do this we run into problems later)
            boolean aliasVal = DefaultBaseEvent.USE_ID_ALIASES;
            DefaultBaseEvent.USE_ID_ALIASES = false;
            register(eb);
            DefaultBaseEvent.USE_ID_ALIASES = aliasVal;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * <p>Perform any local initialization (this is where you should
     * add any other known EventGateways)
     *
     * @param iconfig the ServletConfig object used to configure this servlet
     * @deprecated If your code is still attempting to extend this old method
     *      signature, you need to change it to use initializeLocal() instead.
     *      If you still need to get a reference to the servlet config, just
     *      call 'this.getServletConfig()'. This method is now final so that
     *      code which might still be using it will no longer compile.
     */
    public final void initializeLocal(ServletConfig iconfig) throws ServletException {
    }

    /**
     * <p>Perform any local cleanup (this is where you should
     * remove any known EventGateways)
     */
    public void destroyLocal() {
    }

    /**
     * <p>Provide an instance of the specific EventBroker we want to use.
     * Override this method if you'd like to use something other than
     * the DefaultEventBroker.
     *
     * @return a new instance of the EventBroker
     */
    public EventBroker getNewEventBrokerInstance() {
        return new DefaultEventBroker(getDispatcherFactory(), getEventExtension());
    }

    /**
     * <p>Provide an instance of the specific DispatchQueue we want to use.
     * Override this method if you'd like to use something other than
     * the DefaultDispatchQueue.
     *
     * @return a new instance of the DispatchQueue.
     */
    protected DispatchQueue createDispatchQueue(BaseEvent event) {
        DispatchQueue queue = new DefaultDispatchQueue(true);
        queue.addEvent(event);
        return queue;
    }

    /**
     * <p>Provide an instance of the specific EventDispatcher we want to use.
     * Override this method if you'd like to use something other than
     * DefaultEventBroker.
     *
     * @return a new instance of the DispatcherFactory
     */
    public DispatcherFactory getDispatcherFactory() {
        return new DefaultDispatcherFactory();
    }

    /**
     * <p>Indicate which event extension we are handling. By default the gateway
     * handles extensions of <strong>.event</strong>. If you wish to handle
     * a different function you should override this method to return the
     * value defined in the web.xml file.
     *
     * @return a string defining the event extension handled by this servlet
     */
    public String getEventExtension() {
        return ".event";
    }

    /**
     * <p>Handle an EventException. Basically, this is where we handle the
     * really bad, unexpected type of event exceptions. Generally, as you code,
     * if you want to interrupt the dispatch and fire a new event, you should
     * throw an InterruptDispatchException. Only throw EventExceptions in
     * truly exceptional circumstances.
     *
     * @param e the EventException to handle
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void handleEventException(EventException e, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //first see what the base exception is...if it's a servlet exception
        //or an IOException, rethrow it...
        Exception rootException = NestableException.getRootException(e);
        if (rootException instanceof ServletException) {
            throw (ServletException) rootException;
        }
        if (rootException instanceof IOException) {
            throw (IOException) rootException;
        }
        try {
            resp.setContentType("text/html");
            ExceptionUtil.logExceptionAsHTML(resp.getWriter(), e, req);
        } catch (Exception ex) {
        }
    }

    //csc_031005_1 - added
    /**
     * This method is used to log param details associated with the current request. It is called
     * automatically when Barracuda hits a serious exception; you may wish to call it manually in
     * your own code that extends BaseEventListener
     */
    public static void logRequestDetails(Logger l, Level level) {
        //get the request info if present
        StringBuilder sb = new StringBuilder(1000);
        String newline = System.getProperty("line.separator");
        ObjectRepository lor = ObjectRepository.getLocalRepository();
        HttpServletRequest req = (HttpServletRequest) lor.getState(ApplicationGateway.HTTP_SERVLET_REQUEST);
        if (req != null) {
            sb.append(newline).append("...(http uri)     ").append(req.getRequestURI());
            sb.append(newline).append("...(http params)  ");
            Map<Object, Object> pmap = new TreeMap<Object, Object>();
            Enumeration enum3 = req.getParameterNames();
            while (enum3.hasMoreElements()) {
                String key = (String) enum3.nextElement();
                String vals[] = req.getParameterValues(key);
                pmap.put(key, vals);
            }
            for (Map.Entry me : pmap.entrySet()) {
                String key = (String) me.getKey();
                String vals[] = (String[]) me.getValue();
                for (int i = 0, max = vals.length; i < max; i++) {
                    String s = vals[i];
                    if (key.equalsIgnoreCase("password") || key.equalsIgnoreCase("pwd")) {
                        s = "********";
                    }
                    sb.append(newline).append("      key:").append(key).append(" value:").append(s);
                }
            }
        } else {
            sb.append(newline).append("...(http uri)     n/a");
            sb.append(newline).append("...(http params)  n/a");
        }
        l.log(level, "Logging additional request details:" + sb.toString());
    }

    //-------------------- ApplicationGateway --------------------
    /**
     * <p>Specify event gateways. You can call this method with
     * as many different gateways as you like. When the Application
     * Gateway initializes, it will load them all. When the gateway
     * is destroyed, they will be deregistered.
     *
     * @param gateway an event gateway we'd like to have registered
     */
    public final void specifyEventGateways(EventGateway gateway) {
        if (gateways == null) {
            gateways = new ArrayList<>();
        }
        gateways.add(gateway);
    }

    /**
     * <p>Provide a reference to the event broker.
     *
     * @return a reference to the master event broker
     */
    public final EventBroker getEventBroker() {
        if (masterEventBroker == null) {
            masterEventBroker = getNewEventBrokerInstance();

            //this ensures that the App-gateway can successfully route
            //generic ActionEvents (if we ever add other "well-known"
            //generic events we should pre-register them here as well
            //so that they can be used without any special registration
            //process)
            try {
                masterEventBroker.addEventAlias(ActionEvent.class);
            } catch (InvalidClassException e) {
            }
        }
        return masterEventBroker;
    }

    /**
     * <p>Provide a reference to the event pool. May return
     * null if we're not using event pooling.
     *
     * @return a reference to the master event pool
     */
    public final EventPool getEventPool() {
        if (USE_EVENT_POOLING && masterEventPool == null) {
            masterEventPool = (EventPool) Classes.newInstance(A_Classes.DEFAULT_EVENT_POOL);
        }
        if (!USE_EVENT_POOLING) {
            masterEventPool = null;
        }
        return masterEventPool;
    }

    protected HttpServletRequest wrapRequest(HttpServletRequest req) {
        return new ServletWrapperFactory().create(req);
    }

    protected HttpServletResponse wrapResponse(HttpServletResponse resp) {
        if (RESPONSE_WRAPPER != null) {
            return RESPONSE_WRAPPER.wrap(resp);
        } else {
            return new DefaultServletResponseWrapper(resp);
        }
    }

    //-------------------- EventGateway --------------------------
    /**
     * <p>Set the parent gateway. Null indicates its the root.
     * By definition, an ApplicationGateway is always the root.
     *
     * @param eg the parent event gateway for this gateway
     */
    @Override
    public final void setParent(EventGateway eg) {
        eventGateway.setParent(null);
    }

    /**
     * <p>Get the parent gateway. Returns null if it's the root.
     *
     * @return the parent event gateway
     */
    @Override
    public final EventGateway getParent() {
        return eventGateway.getParent();
    }

    /**
     * <p>Add an event gateway to this one
     *
     * @param eg the event gateway to be added
     */
    @Override
    public final void add(EventGateway eg) {
        eventGateway.add(eg);
        eg.setParent(this);    //do this so the parent points at the ApplicationGateway, not the EventGateway we are using to actually store the values
    }

    /**
     * <p>Remove an event gateway from this one
     *
     * @param eg the event gateway to be removed
     */
    @Override
    public final void remove(EventGateway eg) {
        eventGateway.remove(eg);        
    }

    /**
     * Get a list of child gateways. The list returned is a copy of the
     * underlying child gateway list.
     *
     * @return a list of child gateways
     */
    @Override
    public List getChildren() {
        return eventGateway.getChildren();
    }

    /**
     * <p>Ask all interested parties to register with
     * the EventBroker
     *
     * @param eb the event broker to register with
     */
    @Override
    public final void register(EventBroker eb) {
        eventGateway.register(eb);
    }

    /**
     * <p>Ask all interested parties to de-register with
     * the EventBroker
     *
     * @param eb the event broker to de-register with
     */
    @Override
    public final void deregister(EventBroker eb) {
        eventGateway.deregister(eb);
    }

    /**
     * Register any local interests in the EventBroker
     *
     * @param eb the event broker this gateway should use to
     *        register for local events
     */
    @Override
    public final void registerLocalEventInterests(EventBroker eb) {
        eventGateway.registerLocalEventInterests(eb);
    }

    /**
     * Deregister any local interests in the EventBroker
     *
     * @param eb the event broker this gateway should use to
     *        de-register for local events
     */
    @Override
    public final void deregisterLocalEventInterests(EventBroker eb) {
        eventGateway.deregisterLocalEventInterests(eb);
    }

    /**
     * Register any local event aliases in the EventBroker
     *
     * @param eb the event broker this gateway should use to
     *        register aliases for local events
     */
    @Override
    public final void registerLocalEventAliases(EventBroker eb) {
        eventGateway.registerLocalEventAliases(eb);
    }

    /**
     * Rather than overriding the registerLocalEventInterests
     * method, you can just invoke this method instead for each
     * interest you'd like to register. The gateway will keep track
     * of all factories specified, and register/deregister when
     * appropriate (so you don't have to worry about it). Notice that
     * this method registers just the listener id (not for a specific
     * class of event).
     *
     * The only real reason for using the registerLocalEventInterests
     * method would be if you actually need access to the EventBroker.
     *
     * Note that if the event class is not an instance of BaseEvent, the
     * request is just silently ignored (unlike the event broker, which
     * throws an exception).
     *
     * @param factory the factory we wish to register with the event broker
     */
    @Override
    public final void specifyLocalEventInterests(ListenerFactory factory) {
        eventGateway.specifyLocalEventInterests(factory);
    }

    /**
     * Rather than overriding the registerLocalEventInterests
     * method, you can just invoke this method instead for each
     * interest you'd like to register. The gateway will keep track
     * of all factories specified, and register/deregister when
     * appropriate (so you don't have to worry about it).
     *
     * The only real reason for using the registerLocalEventInterests
     * method would be if you actually need access to the EventBroker.
     *
     * Note that if the event class is not an instance of BaseEvent, the
     * request is just silently ignored (unlike the event broker, which
     * throws an exception).
     *
     * @param factory the factory we wish to register with the event broker
     * @param event the class of events we are interested in
     */
    @Override
    public final void specifyLocalEventInterests(ListenerFactory factory, Class<? extends BaseEvent> event) {
        eventGateway.specifyLocalEventInterests(factory, event);
    }

    /**
     * Rather than overriding the registerLocalEventAliases
     * method, you can just invoke this method instead for type
     * of event you want to manually alias.
     *
     * The only real reason for using the registerLocalEventAliases
     * method would be if you actually need access to the EventBroker.
     *
     * Note that if the event class is not an instance of BaseEvent, the
     * request is just silently ignored (unlike the event broker, which
     * throws an exception).
     *
     * @param event the class of events we are interested in registering
     *        aliases for
     */
    @Override
    public final void specifyLocalEventAliases(Class event) {
        eventGateway.specifyLocalEventAliases(event);
    }

    @Override
    public void cleanUp() {
        if (gateways != null) {
            for (EventGateway gateway : gateways) {
                gateway.cleanUp();
                this.remove(gateway);
            }
        }
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req = wrapRequest(req);
        resp = wrapResponse(resp);

        String method = req.getMethod();
        switch (method) {
            case METHOD_GET: {
                long lastModified = getLastModified(req);
                if (lastModified == -1) {
                    // servlet doesn't support if-modified-since, no reason
                    // to go through further expensive logic
                    doGet(req, resp);
                } else {
                    long ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
                    if (ifModifiedSince < (lastModified / 1000 * 1000)) {
                        maybeSetLastModified(resp, lastModified);
                        doGet(req, resp);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    }
                }
                break;
            }
            case METHOD_HEAD: {
                maybeSetLastModified(resp, getLastModified(req));
                doGet(req, resp);
                break;
            }
            default:
                doAny(req, resp);
                break;
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
        if (resp.containsHeader(HEADER_LASTMOD))
            return;
        if (lastModified >= 0)
            resp.setDateHeader(HEADER_LASTMOD, lastModified);
    }

    /**
     * <p>By default the GET request is mapped to the handleDefault method
     *
     * @param req the servlet request
     * @param resp the servlet response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (ScriptDetector.checkClientReq(req, resp)) {
            return;
        }
        handleDefault(req, resp);
    }

    protected void doAny(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleDefault(req, resp);
    }

    public void initializeLocal() {
    }

    @Override
    public void destroy() {
        //start by getting a reference to the EventBroker
        EventBroker eb = this.getEventBroker();

        //now deregister the event gateway
        boolean aliasVal = DefaultBaseEvent.USE_ID_ALIASES;
        DefaultBaseEvent.USE_ID_ALIASES = false;
        deregister(eb);
        DefaultBaseEvent.USE_ID_ALIASES = aliasVal;

        //finally allow for any local destruction
        destroyLocal();

        
        if (masterEventPool != null) {
            masterEventPool.shutdown();
            masterEventPool = null;
        }
        
        
        masterEventBroker = null;
        
        ObjectRepository.destroy();
        this.cleanUp();
        
        System.gc();
        System.runFinalization();
    }
}
