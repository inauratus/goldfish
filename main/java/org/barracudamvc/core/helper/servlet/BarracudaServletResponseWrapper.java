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
 * $Id: BarracudaServletResponseWrapper.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.core.helper.servlet;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>This class acts as a thin wrapper around a ServletResponse.
 *
 * @author christianc@granitepeaks.com
 * @since //csc_010404_1
 */
public interface BarracudaServletResponseWrapper extends HttpServletResponse {

    /**
     * Set the underlying response object.
     *
     * @param ireq the underlying HttpServletRequest
     */
//    public void setResponse(ServletResponse iresp);

    /**
     * Allow the ServletOutputStream to be overridden. Resets the PrintWriter and OutputStream
     */
//    public void setOutputStream(ServletOutputStream iout);

    /**
     * Return the servlet output stream associated with this Response.
     *
     * @exception IllegalStateException if <code>getWriter</code> has
     *  already been called for this response
     * @exception IOException if an input/output error occurs
     */
//    public ServletOutputStream getOutputStream() throws IOException;

    /**
     * Allow the PrintWriter to be overridden. Resets the PrintWriter and OutputStream
     */
//    public void setWriter(PrintWriter ipw);

    /**
     * Return the print writer associated with this Response (this method may
     * be called multiple times).
     *
     * @exception IllegalStateException if <code>getOutputStream</code> has
     *  already been called for this response
     * @exception IOException if an input/output error occurs
     */
//    public PrintWriter getWriter() throws IOException;

    /**
     * Allow the Header store to be overridden. You need to do this if you override 
     * PrintWriter or OutputStream and you still want your headers to be saved.
     */
//    public void setHeaderStore(Map ihdrmap);

    /**
     * Return the headers associated with this response.
     */
//    public Map getHeaderStore();
}
