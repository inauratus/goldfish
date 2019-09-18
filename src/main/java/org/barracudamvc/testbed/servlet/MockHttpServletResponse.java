/*
 * Copyright (C) 2013 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 * 
 * Name: MockHttpServletResponse.java 
 * Created: Nov 1, 2013 8:32:31 AM
 * Author: Chuck Lowery <chuck.lowery @ gopai.com>
 */
package org.barracudamvc.testbed.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class MockHttpServletResponse implements HttpServletResponse {

    private Locale locale = Locale.getDefault();
    private MockServletOutputStream outputStream;
    private String contentType;
    private int status = HttpServletResponse.SC_OK;
    private String errorMessage;

    public MockHttpServletResponse() {

        this.outputStream = new MockServletOutputStream();
    }

    @Override
    public void addCookie(Cookie cookie) {
    }

    @Override
    public boolean containsHeader(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeURL(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectURL(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeUrl(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectUrl(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError(int i, String string) throws IOException {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    @Override
    public void sendError(int status) throws IOException {
        this.status = status;
    }

    @Override
    public void sendRedirect(String string) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDateHeader(String string, long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDateHeader(String string, long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHeader(String string, String string1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addHeader(String string, String string1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIntHeader(String string, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntHeader(String string, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(int i, String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaders(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new ResponsePrintWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    public void setCharacterEncoding(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentLength(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setBufferSize(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new IllegalStateException();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public byte[] getContentsAsBtyeArray() {
        ByteArrayOutputStream stream = outputStream.getStream();
        try {
            stream.flush();
        } catch (Exception e) {

        }
        return outputStream.getStream().toByteArray();
    }

    private class ResponsePrintWriter extends PrintWriter {

        public ResponsePrintWriter(Writer out) {
            super(out, true);
        }

        public void write(String string) {
            super.write(string);;
            super.flush();
        }
        
        public void write(char buf[], int off, int len) {
            super.write(buf, off, len);
            super.flush();

        }

        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();

        }

        public void write(int c) {
            super.write(c);
            super.flush();
        }

        public void flush() {
            super.flush();
        }
    }

}
