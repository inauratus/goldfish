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
 * $Id: SimpleServiceProvider.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.srv;

import java.util.List;

/**
 * <p>Interface which indicates the implementor is capable of
 * providing a list all services the implementor provides. 
 *
 * <p>SimpleServiceFinder sweeps up a container heirarchy looking
 * for service. Implementing this class provides a way for 
 * a class in the hierarchy to redirect the search back down
 * a particular branch (although the implementation is really 
 * up to the developer...in other words, if the List of supported
 * services includes other SimpleServiceProviders, those also can
 * be examined for services).
 */
public interface SimpleServiceProvider {

    /**
     * Provide a list of supported services.
     *
     * @return list of supported services
     */
    public List getSupportedServices();
}
