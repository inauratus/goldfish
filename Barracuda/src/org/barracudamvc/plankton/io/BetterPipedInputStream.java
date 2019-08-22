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
 * $Id: BetterPipedInputStream.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.plankton.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.barracudamvc.plankton.StringUtil;

/**
 * An InputStream that functions just like BetterPipedReader.
 * See {@link BetterPipedReader} for more information and javadocs.
 *
 * @author  shawnw@atmreports.com
 * @since   saw_032304_1
 */
public class BetterPipedInputStream extends InputStream {

    protected static final Logger logger = Logger.getLogger(BetterPipedInputStream.class.getName());

    public static int DEFAULT_PIPE_SIZE = 1024;
    public static int DEFAULT_TIMEOUT = 60000;  //60 secs

    protected boolean closedByWriter = false;
    protected boolean closedByReader = false;
    protected boolean connected = false;
    protected boolean notifiedFirst = false;
    protected Pausable pausable = null;

    protected Thread readSide;
    protected Thread writeSide;

    protected int pipeSize = DEFAULT_PIPE_SIZE;
    protected int timeout = DEFAULT_TIMEOUT;
    protected byte[] buffer = null;
    protected int bufsize = 0;
    protected int in = -1;
    protected int out = 0;

    public BetterPipedInputStream() {
    }
    
    public BetterPipedInputStream(Pausable ipausable, int ipipeSize) {
        setPausable(ipausable);
        setPipeSize(ipipeSize);
    }

    public BetterPipedInputStream(BetterPipedOutputStream src) throws IOException {
        connect(src);
    }

    public void setPipeSize(int ipipeSize) {
        pipeSize = ipipeSize;
    }

    public int getPipeSize() {
        return pipeSize;    
    }

    public void setPipeTimeout(int itimeout) {
        timeout = itimeout;
    }

    public int getPipeTimeout() {
        return timeout;    
    }

    public void setPausable(Pausable ipausable) {
        pausable = ipausable;
    }

    public Pausable getPausable() {
        return pausable;    
    }

    public void connect(BetterPipedOutputStream src) throws IOException {
        src.connect(this);
    }
    
    protected synchronized void receive(byte b) throws IOException {
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

        //write to next position (in)
        buffer[in++] = b;
        if (!notifiedFirst) receivedFirst();

        //if we've reached buffer capacity and we _haven't_ started reading yet, 
        //then expand the buffer by 20% rather than wrapping
        if (out==0 && in>=buffer.length) { 
            int len = buffer.length;
            byte[] newbuf = new byte[(int) (len + (len*.20))];
            System.arraycopy(buffer, 0, newbuf, 0, len);
            buffer = newbuf;
        }

        //wrap the next pos if necessary
        if (in >= buffer.length) {
            in = 0;
        }

    }

    protected synchronized void receive(byte b[], int off, int len)  throws IOException {
        while (--len >= 0) {
            receive(b[off++]);
        }
    }

    protected synchronized void receivedFirst() {
        notifiedFirst = true;
        notifyAll();
    }

    protected synchronized void receivedLast() {
        closedByWriter = true;
        notifyAll();
    }

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

        int ret = buffer[out++];
        if (in == out) in = -1;                 //now empty
        if (out >= buffer.length) out = 0;
        if (in == out) in = -1;                 //now empty
        return ret;
    }

    public synchronized int read(byte bbuf[], int off, int len)  throws IOException {
        if (!connected) {
            throw new IOException("Pipe not connected (in read2)");
        } else if (closedByReader) {
            throw new IOException("Pipe closed (in read2)");
        } else if (writeSide!=null && !writeSide.isAlive() && !closedByWriter && (in<0)) {
            throw new IOException("Write end dead (in read2)");
        }

        if ((off<0) || (off>bbuf.length) || (len<0) || ((off+len)>bbuf.length) || ((off+len)<0)) {
            throw new IndexOutOfBoundsException();
        } else if (len==0) {
            return 0;
        }

        /* possibly wait on the first character */
        int c = read();
        if (c < 0) {
            return -1;
        }
        bbuf[off] = (byte) c;
        int rlen = 1;
        
        while ((in >= 0) && (--len > 0)) {
            bbuf[off + rlen] = buffer[out++];
            rlen++;
            if (in == out) in = -1;                     //now empty     
            if (out >= buffer.length) out = 0;
            if (in == out) in = -1;                     //now empty     
        }
        return rlen;
    }

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
 
    public void close() throws IOException {
        if (logger.isInfoEnabled()) logger.info("Closed reader!");
        in = -1;
        closedByReader = true;
    }
}
