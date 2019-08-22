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
 * $Id: BetterPipedWriter.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.io;

import java.io.IOException;
import java.io.Writer;


/**
 * This class is based on Sun's PipedWriter. It attempts to address 2 deficiencies 
 * in the Sun implementation: the PIPE_SIZE is configurable, and if we get a 
 * dewadlock on the writer (because the pipe size has been reached) then the writer
 * will timeout after a certain amount of time. This is important in a servlet environment,
 * where one request may start a writer pumping data into the sink, but you have no guarantee
 * that anyone is going to actually read the data from the sink. Using the Sun implementation,
 * the writer deadlocks and that thread is completely out of action for the duration...not 
 * very robust behavior for a servlet environment. This implementation will gracefully timeout,
 * allowing the thread to complete and be returned to the servlet container for further use.
 *
 * Note that we completely reimplement these classes because the Sun classes weren't really
 * designed to be extended (grrr).
 *
 * @since csc_031204_1
 * @author Christian Cryder [christianc@granitepeaks.com]
 */
public class BetterPipedWriter extends Writer {

    /* REMIND: identification of the read and write sides needs to be
       more sophisticated.  Either using thread groups (but what about
       pipes within a thread?) or using finalization (but it may be a
       long time until the next GC). */
    protected BetterPipedReader sink;

    /* This flag records the open status of this particular writer. It
     * is independent of the status flags defined in PipedReader. It is
     * used to do a sanity check on connect.
     */
    protected boolean closed = false;

    /**
     * Creates a piped writer connected to the specified piped 
     * reader. Data characters written to this stream will then be 
     * available as input from <code>snk</code>.
     *
     * @param      snk   The piped reader to connect to.
     * @exception  IOException  if an I/O error occurs.
     */
    public BetterPipedWriter(BetterPipedReader snk)  throws IOException {
        connect(snk);
    }
    
    /**
     * Creates a piped writer that is not yet connected to a 
     * piped reader. It must be connected to a piped reader, 
     * either by the receiver or the sender, before being used. 
     *
     * @see     java.io.PipedReader#connect(java.io.PipedWriter)
     * @see     java.io.PipedWriter#connect(java.io.PipedReader)
     */
    public BetterPipedWriter() {
    }
    
    /**
     * Connects this piped writer to a receiver. If this object
     * is already connected to some other piped reader, an 
     * <code>IOException</code> is thrown.
     * <p>
     * If <code>snk</code> is an unconnected piped reader and 
     * <code>src</code> is an unconnected piped writer, they may 
     * be connected by either the call:
     * <blockquote><pre>
     * src.connect(snk)</pre></blockquote>
     * or the call:
     * <blockquote><pre>
     * snk.connect(src)</pre></blockquote>
     * The two calls have the same effect.
     *
     * @param      snk   the piped reader to connect to.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void connect(BetterPipedReader snk) throws IOException {
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
//        snk.buffer = new char[snk.getPipeSize()];
        snk.sbuffer = new StringBuffer(snk.getPipeSize());
        snk.connected = true;
        snk.notifiedFirst = false;
     }

    /**
     * Writes the specified <code>char</code> to the piped output stream. 
     * If a thread was reading data characters from the connected piped input 
     * stream, but the thread is no longer alive, then an 
     * <code>IOException</code> is thrown.
     * <p>
     * Implements the <code>write</code> method of <code>Writer</code>.
     *
     * @param      c   the <code>char</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(int c)  throws IOException {
        if (sink == null) {
            throw new IOException("Pipe not connected");
        }
        sink.receive(c);
    }

    /**
     * Writes <code>len</code> characters from the specified character array 
     * starting at offset <code>off</code> to this piped output stream. 
     * If a thread was reading data characters from the connected piped input 
     * stream, but the thread is no longer alive, then an 
     * <code>IOException</code> is thrown.
     *
     * @param      cbuf  the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of characters to write.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        if (sink == null) {
            throw new IOException("Pipe not connected");
        } else if ((off | len | (off + len) | (cbuf.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        sink.receive(cbuf, off, len);
    }

    /**
     * Flushes this output stream and forces any buffered output characters 
     * to be written out. 
     * This will notify any readers that characters are waiting in the pipe.
     *
     * @exception IOException if an I/O error occurs.
     */
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

    /**
     * Closes this piped output stream and releases any system resources 
     * associated with this stream. This stream may no longer be used for 
     * writing characters.
     *
     * @exception  IOException  if an I/O error occurs.
     */
    public void close()  throws IOException {
        closed = true;
        if (sink != null) {
            sink.receivedLast();
        }
    }
}
