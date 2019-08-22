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
 * $Id: SimpleServiceFinder.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.srv;

import java.awt.Container;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.barracudamvc.core.event.EventGateway;
import org.barracudamvc.plankton.data.PData;
import org.barracudamvc.plankton.srv.SimpleServiceProvider;

/**
 * <p>Similar to org.barracudamvc.plankton.srv.SimpleServiceProvider,
 * except that it will also search EventGateway heirarchies.
 */
public class SimpleServiceFinder extends org.barracudamvc.plankton.srv.SimpleServiceFinder {

    protected static final Logger logger = Logger.getLogger(SimpleServiceFinder.class.getName());

    //public constants

    /**
     * Find an instance of a Class in an EventGateway heirarchy. Search
     * direction defaults to UPSTREAM
     *
     * @param    c the class we're looking for
     * @param    gateway the entry point to the EventGateway heirarchy
     * @return    the first instance of the specified class
     */
    public static Object findInstance (Class c, EventGateway gateway) {
        return findInstance_upstream(c, gateway);    
    }
    
    /**
     * Find an instance of a Class in an EventGateway heirarchy
     *
     * @param    c the class we're looking for
     * @param    gateway the entry point to the EventGateway heirarchy
     * @param    searchDirection the search direction
     * @return    the first instance of the specified class
     */
    public static Object findInstance (Class c, EventGateway gateway, int searchDirection) {
        if (searchDirection==DOWNSTREAM) return findInstance_downstream(c, gateway);
        else return findInstance_upstream(c, gateway);    
    }

    /**
     * the private method to actually find the desired object
     */
    private static Object findInstance_upstream (Class c, Object parent) {
        //eliminate the obvious
        if (c==null || parent==null) return null;
        if (logger.isDebugEnabled()) logger.debug("Looking for instance of "+c+" in "+parent);

        //see if the parent object matches
        if (c.isInstance(parent)) return parent;

        //now see if we can get an iterator from this object
        Iterator it = null;
        if (parent instanceof Map) it = ((Map) parent).values().iterator();
        else if (parent instanceof List) it = ((List) parent).iterator();
        else if (parent instanceof SimpleServiceProvider) it = ((SimpleServiceProvider) parent).getSupportedServices().iterator();
        if (it!=null) while (it.hasNext()) {
            Object o = it.next();
            if (logger.isDebugEnabled()) logger.debug("Evaluating iterator item: "+o);
            if (c.isInstance(o)) return o;
        }
        
        //next see if parent is a Container    
        if (parent instanceof Container) {
            Object o[] = ((Container) parent).getComponents();
            if (o!=null) for (int i=0, max=o.length; i<max; i++) {
                if (logger.isDebugEnabled()) logger.debug("Evaluating container item: "+o[i]);
                if (c.isInstance(o[i])) return o[i];
            }
        }
        
        //finally inspect the Parents     
        Object gramps = null;
        if (parent instanceof PData) gramps = ((PData) parent).getParent();
        else if (parent instanceof EventGateway) gramps = ((EventGateway) parent).getParent();
        else if (parent instanceof Container) gramps = ((Container) parent).getParent();
        if (logger.isDebugEnabled()) logger.debug("Evaluating Gramps:"+gramps);
        if (gramps==null) return null;
        else return findInstance_upstream(c, gramps);
    }

    /**
     * the private method to actually find the desired downstream object
     */
    private static Object findInstance_downstream (Class c, Object child) {
        //eliminate the obvious
        if (c==null || child==null) return null;
        if (logger.isDebugEnabled()) logger.debug("Looking for instance of "+c+" in "+child);

        //see if the parent object matches
        if (c.isInstance(child)) return child;

        //search downstream
        //...in SimpleServiceProviders
        if (child instanceof SimpleServiceProvider) {
            Iterator it = ((SimpleServiceProvider) child).getSupportedServices().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (logger.isDebugEnabled()) logger.debug("Evaluating iterator item: "+o);
                
                //evaluate the object itself
                if (c.isInstance(o)) return o;
                
                //search down the object branch
                Object inst = findInstance_downstream(c, o);
                if (inst!=null) return inst;
            }
        }
        //...in EventGateways
        if (child instanceof EventGateway) {
            Iterator it = ((EventGateway) child).getChildren().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (logger.isDebugEnabled()) logger.debug("Evaluating iterator item: "+o);
                
                //evaluate the object itself
                if (c.isInstance(o)) return o;
                
                //search down the object branch
                Object inst = findInstance_downstream(c, o);
                if (inst!=null) return inst;
            }
        }
        //...in Lists
        if (child instanceof List) {
            Iterator it = ((List) child).iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (logger.isDebugEnabled()) logger.debug("Evaluating iterator item: "+o);
                
                //evaluate the object itself
                if (c.isInstance(o)) return o;
                
                //search down the object branch
                Object inst = findInstance_downstream(c, o);
                if (inst!=null) return inst;
            }
        }
        //...in Maps
        if (child instanceof Map) {
            Iterator it = ((Map) child).values().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (logger.isDebugEnabled()) logger.debug("Evaluating iterator item: "+o);
                
                //evaluate the object itself
                if (c.isInstance(o)) return o;
                
                //search down the object branch
                Object inst = findInstance_downstream(c, o);
                if (inst!=null) return inst;
            }
        }
        //...in Containers
        if (child instanceof Container) {
            Object o[] = ((Container) child).getComponents();
            if (o!=null) for (int i=0, max=o.length; i<max; i++) {
                if (logger.isDebugEnabled()) logger.debug("Evaluating container item: "+o[i]);

                //evaluate the object itself
                if (c.isInstance(o[i])) return o[i];

                //search down the object branch
                Object inst = findInstance_downstream(c, o[i]);
                if (inst!=null) return inst;
            }
        }
        
        //if we didn't find anything return null;
        return null;
    }

}
