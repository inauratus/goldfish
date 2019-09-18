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
 * $Id: BCompoundComponent.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp;

/**
 * This is a marker interface meant to identify compund components, or those
 * components that represent compound structures such as lists and tables or
 * more complex templating structures combining arbitrary components.
 * It is helpful to distinguish compound components from other simple
 * components for certain rendering situations.  If a component implements this
 * interface, BarracudaMVC code will know to perform/avoid certain actions that
 * are to be taken upon a component at render time.
 * 
 * @author Jacob Kjome
 */
public interface BCompoundComponent {

}
