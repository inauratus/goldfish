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
 * $Id: BContainer.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.util.List;

/**
 * This interface defines the characteristics of a container. It
 * provides the ability to create a hierarchy of containers
 * and to navigate that hierachy. Every container has a parent,
 * except for the root container, who's parent is null.
 */
public interface BContainer {

    public <T extends BContainer> T setParent(BContainer iparent);

    public BContainer getParent();

    public <T extends BContainer> T setName(String iname);

    public String getName();

    public <T extends BContainer> T addChild(BContainer child);

    public BContainer getChild(int index);

    public <T extends BContainer> T removeChild(BContainer child);

    public <T extends BContainer> T removeChild(int index);

    public <T extends BContainer> T removeAll();

    public boolean hasChildren();

    public <T extends BContainer> List<T> getChildren();

    public <T extends BContainer> T invalidate();

    public <T extends BContainer> T validate();
}