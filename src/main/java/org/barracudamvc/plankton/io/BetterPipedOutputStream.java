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
 * $Id: BetterPipedOutputStream.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.io;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;

/**
 *
 * @author  shawnw@atmreports.com
 * @since   saw_032304_1
 */
public class BetterPipedOutputStream extends ServletOutputStream {

    protected static final Logger logger = Logger.getLogger(BetterPipedOutputStream.class.getName());

    protected BetterPipedInputStream sink;
    protected boolean closed = false;

    public BetterPipedOutputStream(BetterPipedInputStream snk)  throws IOException {
        connect(snk);
    }
    
    public BetterPipedOutputStream() {
    }
    
    public synchronized void connect(BetterPipedInputStream snk) throws IOException {
        if (snk == null) {
            throw new NullPointerException();
        } else if (sink != null || snk.connected) {
            throw new IOException("Already connected");
        } else if (snk.closedByReader || closed) {
            throw new IOException("Pipe closed");
        }
        
        sink = snk;
        snk.in = -1;
        snk.out = 0;
        snk.buffer = new byte[snk.getPipeSize()];
        snk.connected = true;
        snk.notifiedFirst = false;
     }

    public void write(int b)  throws IOException {
        if (sink == null) {
            throw new IOException("Pipe not connected");
        }
        sink.receive((byte) b);
    }

    public void write(byte bbuf[], int off, int len) throws IOException {
        if (sink == null) {
            throw new IOException("Pipe not connected");
        } else if ((off | len | (off + len) | (bbuf.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        sink.receive(bbuf, off, len);
    }

    public synchronized void flush() throws IOException {
        if (sink != null) {
            if (sink.closedByReader || closed) {
                throw new IOException("Pipe closed");
            }            
            synchronized (sink) {
                sink.notifyAll();
            }
        }
    }

    public void close()  throws IOException {
        if (logger.isInfoEnabled()) logger.info("Closed output stream!");
        closed = true;
        if (sink != null) {
            sink.receivedLast();
        }
    }
}
