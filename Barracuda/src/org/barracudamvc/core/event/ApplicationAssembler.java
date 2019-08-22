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
 * $Id: ApplicationAssembler.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.event;

import javax.servlet.ServletConfig;

/**
 * <p>This interface defines an ApplicationAssembler. This whole concept
 * is still pretty experimental. Basically, the idea is that whatever 
 * implements this interface gets passed a reference to the EventGateway,
 * an XML File name, and a SAX parser class...given this information, the 
 * implementor is free to assemble an event hierarchy, register listeners, 
 * etc.
 *
 * <p>The only implementation right now is found in org.barracudamvc.
 * experimental.assembler.DefaultApplicationAssembler (and this should truly 
 * be viewed as experimental)
 *
 * <p>Look at the source in ApplicationGateway to see how the assembler
 * is invoked when the servlet is intialized.
 */
public interface ApplicationAssembler {
    
    /**
     * Assemble the system, given the root EventGateway and the
     * XML assembly decriptor name. The default parser will be 
     * used.
     *
     * @param irootGateway the root EventGateway
     * @param iservletConfig the ServletConfig object
     * @param iassemblySourceFile the XML assembly descriptor
     */
    public void assemble(EventGateway irootGateway, ServletConfig iservletConfig, String iassemblySourceFile);
    
    /**
     * Assemble the system, given the root EventGateway, an
     * XML assembly decriptor name, and a specific SAX parser
     * class.
     *
     * @param irootGateway the root EventGateway
     * @param iservletConfig the ServletConfig object
     * @param iassemblySourceFile the XML assembly descriptor
     * @param iparserClass the SAX parser class
     */
    public void assemble(EventGateway irootGateway, ServletConfig iservletConfig, String iassemblySourceFile, String iparserClass);
}
