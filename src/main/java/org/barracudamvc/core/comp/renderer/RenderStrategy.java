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
 * $Id: RenderStrategy.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.comp.renderer;


/**
 * This class defines several basic render strategies.
 * <strong>NEVER_SCRIPT</strong> indicates that we want to avoid scripting at
 * all costs, even if the client supports it. <strong>CUSTOM_SCRIPT</strong> is
 * exactly like NEVER_SCIPT, except that indicates to Barracuda
 * (component renderers, mostly) not to throw exceptions in cases where the
 * rendered markup wouldn't be able to work properly without scripting. One
 * should use CUSTOM_SCRIPT in cases where developer-created custom client-side
 * scripts are already in use and Barracuda shouldn't be interfering with them.
 * <strong>SCRIPT_AS_NEEDED</strong> indicates the renderer should feel free to
 * use scripting as desired (assuming the client supports it of course).  The
 * default render strategy, defined by <strong>DEFAULT_RENDER_STRATEGY</strong,
 * is SCRIPT_AS_NEEDED (unless modified at runtime).
 */
public class RenderStrategy {

    //concrete instances of strategies
    public static final RenderStrategy SCRIPT_AS_NEEDED = new RenderStrategy();
    public static final RenderStrategy NEVER_SCRIPT = new RenderStrategy();
    public static final RenderStrategy CUSTOM_SCRIPT = new RenderStrategy();
    public static RenderStrategy DEFAULT_RENDER_STRATEGY = SCRIPT_AS_NEEDED;

    /**
     * Protected constructor to prevent external instantiation
     */
    protected RenderStrategy() {}
    
}
