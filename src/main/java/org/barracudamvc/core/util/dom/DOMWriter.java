/*
 * Copyright (C) 2001  Christian Cryder [christianc@granitepeaks.com]
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
 * $Id: DOMWriter.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.util.dom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Node;

/**
 * This interface defines the methods needed to implement a DOMWriter.
 */
public interface DOMWriter {

//saw_031604_1    public void prepareResponse(HttpServletResponse resp) throws IOException;       //csc_012804_1
    public void prepareResponse(Node node, HttpServletResponse resp) throws IOException; //saw_031604_1
    public void write(Node node, HttpServletResponse resp) throws IOException;
    public void write(Node node, OutputStream out) throws IOException;
    public void write(Node node, Writer writer) throws IOException;
    public void setLeaveWriterOpen(boolean val);                                    //csc_012804_1
    public boolean getLeaveWriterOpen();                                            //csc_012804_1
}
