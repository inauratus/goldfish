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
 * $Id: BetterPipedReader.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.io;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.StringUtil;

/**
 * This class is based on Sun's PipedReader. It attempts to address 2 deficiencies 
 * in the Sun implementation: the PIPE_SIZE is configurable, and if we get a 
 * dewadlock on the writer (because the pipe size has been reached) then the writer
 * will timeout after a certain amount of time. This is important in a servlet environment,
 * where one request may start a writer pumping data into the sink, but you have no guarantee
 * that anyone is going to actually read the data from the sink. Using the Sun implementation,
 * the writer deadlocks and that thread is completely out of action for the duration...not 
 * very robust behavior for a servlet environment. This implementation will gracefully timeout,
 * allowing the thread to complete and be returned to the servlet container for further use.
 * It should also be noted that this implementation supports the notion of a Pausable controller,
 * which if present will block the writer (up to but not exceeding the timeout period) while
 * the parent controller isPaused().
 *
 * Note that we completely reimplement these classes because the Sun classes weren't really
 * designed to be extended (grrr).
 *
 * @since csc_031204_1
 * @author Christian Cryder [christianc@granitepeaks.com]
 */
public class BetterPipedReader extends Reader {

    protected static final Logger logger = Logger.getLogger(BetterPipedReader.class.getName());

    protected boolean closedByWriter = false;
    protected boolean closedByReader = false;
    protected boolean connected = false;
    protected boolean notifiedFirst = false;
    protected Pausable pausable = null;

    /* REMIND: identification of the read and write sides needs to be
       more sophisticated.  Either using thread groups (but what about
       pipes within a thread?) or using finalization (but it may be a
       long time until the next GC). */
    protected Thread readSide;
    protected Thread writeSide;

    /**
     * The default (global) size of the pipe's circular input buffer.
     */
    public static int DEFAULT_PIPE_SIZE = 1024;

    /**
     * The pipe size for this particular circular input buffer (defaults to
     * DEFAULT_PIPE_SIZE)
     */
    protected int pipeSize = DEFAULT_PIPE_SIZE;

    /**
     * The default (global) timeout (in millis)
     */
    public static int DEFAULT_TIMEOUT = 60000;  //60 secs

    /**
     * The timeout for this particular pipe reader (defaults to DEFAULT_TIMEOUT)
     */
    protected int timeout = DEFAULT_TIMEOUT;

    /**
     * The circular buffer into which incoming data is placed. This
     * buffer is not actually initialized until you connect, to take
     * advantage of any configuration changes to the pipe size
     */
//    protected char buffer[] = null;
    protected StringBuffer sbuffer = null;

    /**
     * The index of the position in the circular buffer at which the 
     * next character of data will be stored when received from the connected 
     * piped writer. <code>in&lt;0</code> implies the buffer is empty, 
     * <code>in==out</code> implies the buffer is full
     */
    protected int in = -1;

    /**
     * The index of the position in the circular buffer at which the next 
     * character of data will be read by this piped reader.
     */
    protected int out = 0;

    /**
     * Creates a <code>BetterPipedReader</code> that is not yet connected. It must be
     * manually connected to a <code>BetterPipedWriter</code> before being used.
     *
     * @see org.barracudamvc.plankton.io.BetterPipedReader#connect(org.barracudamvc.plankton.io.BetterPipedWriter)
     * @see org.barracudamvc.plankton.io.BetterPipedWriter#connect(org.barracudamvc.plankton.io.BetterPipedReader)
     */
    public BetterPipedReader() {
    }

    /**
     * Creates a <code>BetterPipedReader</code> that is not yet connected, specifying
     * a custom pipe size. It must be manually connected to a <code>BetterPipedWriter</code> 
     * before being used.
     *
     * @param pipeSize the specific pipe size of the circular buffer
     * @see org.barracudamvc.plankton.io.BetterPipedReader#connect(org.barracudamvc.plankton.io.BetterPipedWriter)
     * @see org.barracudamvc.plankton.io.BetterPipedWriter#connect(org.barracudamvc.plankton.io.BetterPipedReader)
     */
    public BetterPipedReader(Pausable ipausable, int ipipeSize) {
        setPausable(ipausable);
        setPipeSize(ipipeSize);
    }

    /**
     * Creates a <code>BetterPipedReader</code> and automatically connect it to the piped writer
     * <code>src</code>. Data written to <code>src</code> will then be  available as input 
     * from this stream. Note that if you use this method, calls to <code>setPipeSize</code> will
     * have no effect (because the circular buffer has already been initialized when the connection
     * was made).
     *
     * @param src the stream to connect to.
     * @exception IOException if an I/O error occurs.
     */
    public BetterPipedReader(BetterPipedWriter src) throws IOException {
        connect(src);
    }

    /**
     * set the pipe size. You want to do this before calling connect
     * (otherwise the default pipe size will be used).
     */
    public void setPipeSize(int ipipeSize) {
        pipeSize = ipipeSize;
    }

    /**
     * get the pipe size
     */
    public int getPipeSize() {
        return pipeSize;    
    }

    /**
     * set the timeout (you may call this at any time). A value less &lt; 0 indicates
     * no timeout.
     */
    public void setPipeTimeout(int itimeout) {
        timeout = itimeout;
    }

    /**
     * get the timeout setting. A value less &lt; 0 indicates
     * no timeout.
     */
    public int getPipeTimeout() {
        return timeout;    
    }

    /**
     * set the Pausable controller (null indicates the pipe can't be paused).
     * Pausing a pipe effectively blocks the write process for the pipe timeout
     * duration - thus preventing the pipe from filling up before you want it
     * to
     */
    public void setPausable(Pausable ipausable) {
        pausable = ipausable;
    }

    /**
     * get the Pausable controller
     */
    public Pausable getPausable() {
        return pausable;    
    }

    /**
     * Causes this piped reader to be connected to the piped  writer <code>src</code>.
     * If this object is already connected to some other piped writer, an <code>IOException</code>
     * is thrown.
     * 
     * <p>If <code>src</code> is an unconnected piped writer and <code>snk</code>
     * is an unconnected piped reader, they may be connected by either the call:
     * 
     * <p><pre><code>snk.connect(src)</code> </pre> 
     * 
     * <p>or the call:
     *
     * <p><pre><code>src.connect(snk)</code> </pre> 
     * 
     * <p>The two calls have the same effect.
     *
     * @param src The piped writer to connect to.
     * @exception IOException if an I/O error occurs.
     */
    public void connect(BetterPipedWriter src) throws IOException {
        src.connect(this);
    }
    
    /**
     * Receives a char of data. This method will block if no input is
     * available, and then timeout if the PIPE_TIMEOUT is exceeded.
     */
    protected synchronized void receive(int c) throws IOException {
        if (!connected) {
            throw new IOException("Pipe not connected (in receive)");
        } else if (closedByWriter || closedByReader) {
            throw new IOException("Pipe closed (in receive)");
        } else if (readSide != null && !readSide.isAlive()) {
            throw new IOException("Read end dead (in receive)");
        }

        //the purpose of this is to check and see if we're paused; Pausing blocks
        //writing (thus prevent us from filling up the pipe), without impinging
        //on the read side of things
        long smillis = -1;
        if (pausable!=null) {
            while (pausable.isPaused()) {
                if (logger.isInfoEnabled()) logger.info("I/O blocked bcause controller is paused (ok)");
                if (smillis<0) smillis = System.currentTimeMillis();
                if ((timeout>0) && (System.currentTimeMillis()-smillis>timeout)) {
                if (logger.isInfoEnabled()) logger.info("timing out read pipe because we were paused for too long");
                    close();    //very important that we do this, since if they've wrapped it in a PrintWriter the exception might get consumed - we want this sucker to die NOW         
                    throw new IOException("Pipe timed out after "+StringUtil.getElapsedStr(timeout));
                }
                try {
                    Thread.sleep(100);
                    Thread.yield();
                } catch (InterruptedException ex) {
                    throw new java.io.InterruptedIOException();
                }
             }
             smillis = -1;
        }

        //actually write to the pipe
        writeSide = Thread.currentThread();
        waitLoop: while (in==out) {
            if (smillis<0) smillis = System.currentTimeMillis();
            logger.warn("I/O blocked bcause pipe is full... in:"+in+" out:"+out+" (not so good...if you are seeing this message a lot in your logs, consider upping PIPE_SIZE)");
            if ((readSide!=null) && !readSide.isAlive()) {
                throw new IOException("Pipe broken (in receive)");
            }
            if ((timeout>0) && (System.currentTimeMillis()-smillis>timeout)) {
            if (logger.isInfoEnabled()) logger.info("timing out read pipe because the pipe was full for too long");
                close();    //very important that we do this, since if they've wrapped it in a PrintWriter the exception might get consumed - we want this sucker to die NOW         
                throw new IOException("Pipe timed out after "+StringUtil.getElapsedStr(timeout));
            }
            /* full: kick any waiting readers */
            notifyAll();    
            try {
                wait(1000);
                Thread.yield();
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
        }
        if (in < 0) {
            in = 0;
            out = 0;
        }
/*        
        buffer[in++] = (char) c;
        if (in >= buffer.length) {
            in = 0;
        }
*/
        //write to next position (in)
        if (in>=sbuffer.length()) sbuffer.append((char) c);
        else sbuffer.setCharAt(in, (char) c);
        if (!notifiedFirst) receivedFirst();
        
        //incr the next pos (in)                
        in++;

        //if we've reached buffer capacity and we _haven't_ started reading yet, 
        //then expand the buffer by 20% rather than wrapping (note that while we 
        //only ask it to increase by this percentage, StringBuffer may in fact 
        //expand it more significantly - it just guarantees this percentage)
        if (out==0 && in>=sbuffer.capacity()) { 
            int len = sbuffer.length(); 
            sbuffer.ensureCapacity((int) (len + (len*.20)));
        }        

        //wrap the next pos if necessary
        if (in >= sbuffer.capacity()) {
            in = 0;
        }

    }

    /**
     * Receives data into an array of characters.  This method will
     * block until some input is available. 
     */
    protected synchronized void receive(char c[], int off, int len)  throws IOException {
        while (--len >= 0) {
            receive(c[off++]);
        }
    }

    /**
     * Notifies all waiting threads that we have started to receive data
     */
    protected synchronized void receivedFirst() {
        notifiedFirst = true;
        notifyAll();
    }

    /**
     * Notifies all waiting threads that the last character of data has been
     * received.
     */
    protected synchronized void receivedLast() {
        closedByWriter = true;
        notifyAll();
    }

    /**
     * Reads the next character of data from this piped stream.
     * If no character is available because the end of the stream 
     * has been reached, the value <code>-1</code> is returned. 
     * This method blocks until input data is available, the end of
     * the stream is detected, or an exception is thrown. 
     *
     * If a thread was providing data characters
     * to the connected piped writer, but
     * the  thread is no longer alive, then an
     * <code>IOException</code> is thrown.
     *
     * @return     the next character of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if the pipe is broken.
     */
    public synchronized int read()  throws IOException {
        if (!connected) {
            throw new IOException("Pipe not connected (in read)");
        } else if (closedByReader) {
            throw new IOException("Pipe closed (in read)");
        } else if (writeSide != null && !writeSide.isAlive() && !closedByWriter && (in < 0)) {
            throw new IOException("Write end dead (in read)");
        }

        readSide = Thread.currentThread();
        int trials = 2;
        while (in < 0) {
            if (closedByWriter) { 
                /* closed by writer, return EOF */
                return -1;
            }
            if ((writeSide != null) && (!writeSide.isAlive()) && (--trials < 0)) {
                throw new IOException("Pipe broken (in read)");
            }
            /* might be a writer waiting */
            notifyAll();
            try {
                wait(1000);
                Thread.yield();
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
         }
/*
        int ret = buffer[out++];
        if (out >= buffer.length) {
            out = 0;
        }
        if (in == out) {
            //now empty
            in = -1;        
        }
*/        
        int ret = sbuffer.charAt(out++);
        if (in == out) in = -1;                 //now empty     
        if (out >= sbuffer.length()) out = 0;
        if (in == out) in = -1;                 //now empty     
        return ret;
    }

    /**
     * Reads up to <code>len</code> characters of data from this piped
     * stream into an array of characters. Less than <code>len</code> characters
     * will be read if the end of the data stream is reached. This method 
     * blocks until at least one character of input is available. 
     * If a thread was providing data characters to the connected piped output, 
     * but the thread is no longer alive, then an <code>IOException</code> 
     * is thrown.
     *
     * @param      cbuf     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of characters read.
     * @return     the total number of characters read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized int read(char cbuf[], int off, int len)  throws IOException {
        if (!connected) {
            throw new IOException("Pipe not connected (in read2)");
        } else if (closedByReader) {
            throw new IOException("Pipe closed (in read2)");
        } else if (writeSide!=null && !writeSide.isAlive() && !closedByWriter && (in<0)) {
            throw new IOException("Write end dead (in read2)");
        }

        if ((off<0) || (off>cbuf.length) || (len<0) || ((off+len)>cbuf.length) || ((off+len)<0)) {
            throw new IndexOutOfBoundsException();
        } else if (len==0) {
            return 0;
        }

        /* possibly wait on the first character */
        int c = read();
        if (c < 0) {
            return -1;
        }
        cbuf[off] = (char) c;
        int rlen = 1;
/*        
        while ((in >= 0) && (--len > 0)) {
            cbuf[off + rlen] = buffer[out++];
            rlen++;
            if (out >= buffer.length) {
                out = 0;
            }
            if (in == out) {
                //now empty
                in = -1;    
            }
        }
        return rlen;
*/            
        while ((in >= 0) && (--len > 0)) {
            cbuf[off + rlen] = sbuffer.charAt(out++);
            rlen++;
            if (in == out) in = -1;                     //now empty     
            if (out >= sbuffer.length()) out = 0;
            if (in == out) in = -1;                     //now empty     
        }
        return rlen;
    }

    /**
     * Tell whether this stream is ready to be read.  A piped character
     * stream is ready if the circular buffer is not empty.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public synchronized boolean ready() throws IOException {
        if (!connected) {
            throw new IOException("Pipe not connected (in ready)");
        } else if (closedByReader) {
            throw new IOException("Pipe closed (in ready)");
        } else if (writeSide != null && !writeSide.isAlive() && !closedByWriter && (in < 0)) {
            throw new IOException("Write end dead (in ready)");
        }
        if (in < 0) {
            return false;
        } else {
            return true;
        }
    }
 
    /**
     * Closes this piped stream and releases any system resources 
     * associated with the stream. 
     *
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException {
        in = -1;
        closedByReader = true;
    }
}
