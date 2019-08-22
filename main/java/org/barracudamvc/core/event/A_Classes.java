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
 * $Id: A_Classes.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;

/**
 * The purpose of this class is to define the default classes which are used
 * within the Barracuda event package. If you would like to override them, you 
 * can easily do so using the OBjectRepositoryAssembler.
 */
public class A_Classes {
    public static String DEFAULT_EVENT_GATEWAY = DefaultEventGateway.class.getName();
    public static String DEFAULT_EVENT_POOL = DefaultEventPool.class.getName();
    
}
