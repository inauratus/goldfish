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
 * $Id: EventConnectorFactory.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.event.helper;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.barracudamvc.core.event.BaseEventListener;
import org.barracudamvc.core.event.DefaultListenerFactory;

/**
 * <p> This class associates a specific event handler class with an event by
 * extending DefaultListenerFactory and making implementation details in the
 * gateways much simpler... Without this class, each instance of a listener
 * factory must create a separate anonymous class and provide the
 * implementation for getInstance() and getListenerID().
 *
 * <p>With this class, the association of an event to a handler is a reduced
 * to a single line:
 *
 * <p><code>specifyLocalEventInterests(new EventConnectorFactory(Handler.class), Event.class);</code>
 *
 * <p> IMPORTANT NOTE: When using this class, the passed-in event handler must
 * either be a non-inner class or an inner class which is declared public static.
 * Otherwise, when the getInstance() method attempts to instantiate the handler a
 * <code>java.lang.InstantiationException</code> will get thrown which will show up
 * as a <code>java.lang.NullPointerException</code>. This is because getInstance()
 * returns null if it fails to instantiate the handler. This won't be caught
 * at compile time, so be careful!
 *
 * @author  Stephen Peterson <stephen_peterson@agilent.com>
 * @author  Jacob Kjome <hoju@visi.com>
 * @version %I%, %G%
 * @since   1.0.1 (2002-02-15)
 */
public class EventConnectorFactory extends DefaultListenerFactory {

    protected static final Logger logger = Logger.getLogger(EventConnectorFactory.class.getName());
    protected Class eventHandlerClass = null;
    protected Object[] constructParams = null;      // fro_021908_1 parameters for EventHandler
    protected Class[] constructParamsTypes = null; // fro_021908_1 parameters for EventHandler

    /**
     * Constructor
     *
     * @param aClass the handler class to associate with an event
     */
    public EventConnectorFactory(Class aClass) {
        this(aClass, null, null);    // fro_021908_1 parameters for EventHandler

    }

    // fro_021908_1 begin parameters for EventHandler
    /**
     * Constructor with parameters for the event handler constructor
     *
     * @param aClass the handler class to associate with an event
     */
    public EventConnectorFactory(Class aClass, Object[] params, Class[] types) {
        logger.debug("Creating new EventConnectorFactory --> " + aClass);

        //sanity check & assignment
        eventHandlerClass = aClass;
        if (eventHandlerClass == null) {
            logger.fatal("Event handler class cannot be null!", new IllegalAccessException());
        }

        constructParams = params;
        constructParamsTypes = types;

        // If type are not specified, we try to guess them...
        if (constructParams != null && constructParamsTypes == null) {
            logger.warn("Construct Params Types are not specified");
            constructParams = params;
            if (constructParams != null) {
                constructParamsTypes = new Class[constructParams.length];
                int i = 0;
                for (Object obj : constructParams) {
                    constructParamsTypes[i] = obj.getClass();
                    i++;
                }
            }
        }

        if (constructParams != null && constructParamsTypes != null
                && constructParams.length != constructParamsTypes.length) {
            logger.fatal("contructor params and types array must not have a different size !", new IllegalAccessException());
        }
        // fro_021908_1 end


        //now go ahead and invoke getInstance(), just to make sure its going
        //to work later. This will effectively catch errors immediately at
        //startup, rather than waiting until someone stumbles into the problem
        //while using the application
        getInstance();
    }

    /**
     * Get an instance of the underlying BaseEventListener
     *
     * @return get an instance of the BaseEventListener
     */
    @Override
    @SuppressWarnings("unchecked")
    public BaseEventListener getInstance() {
        //create an instance of the class
        BaseEventListener bel = null;
        try {
            // fro_021908_1 begin parameters for EventHandler
            if (constructParams == null) {
                bel = (BaseEventListener) eventHandlerClass.newInstance();
            } else {
                bel = (BaseEventListener) eventHandlerClass.getConstructor(constructParamsTypes).newInstance(constructParams);
            }
            // fro_021908_1 end
        } catch (IllegalAccessException iae) {
            logger.fatal("Illegal Access Exception!", iae);
        } catch (InstantiationException ie) {
            logger.fatal("Error instantiating " + eventHandlerClass.getName() + "; if you defined it as an inner class, make sure its declared public static", ie);
        } catch (ClassCastException cce) {
            logger.fatal("Error casting " + eventHandlerClass.getName() + " to BaseEventListner. This class or its superclass must implement the BaseEventListener interface", cce);
            // fro_021908_1 begin parameters for EventHandler
        } catch (IllegalArgumentException iae) {
            logger.fatal("Illegal Argument Exception while invocating " + eventHandlerClass.getName(), iae);
        } catch (SecurityException se) {
            logger.fatal("Security Exception while instanciating " + eventHandlerClass.getName(), se);
        } catch (InvocationTargetException ite) {
            logger.fatal("Error invocating " + eventHandlerClass.getName(), ite);
        } catch (NoSuchMethodException nsme) {
            logger.fatal("No Such Method Exception while getting constructor " + eventHandlerClass.getName(), nsme);
            // fro_021908_1 end
        }

        //return the event listener
        return bel;
    }

    /**
     * Get the Listener ID associated with this class of listener. This will
     * generally either be the class name of the listener that the factory
     * creates
     *
     * @return the listener ID that describes this factory
     */
    @Override
    public String getListenerID() {
        return getID(eventHandlerClass);
    }
}
