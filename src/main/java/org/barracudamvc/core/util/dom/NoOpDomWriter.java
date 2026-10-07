package org.barracudamvc.core.util.dom;

import org.w3c.dom.Node;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class NoOpDomWriter implements DOMWriter{
    @Override
    public void prepareResponse(Node node, HttpServletResponse resp) throws IOException {

    }

    @Override
    public void write(Node node, HttpServletResponse resp) throws IOException {

    }

    @Override
    public void write(Node node, OutputStream out) throws IOException {

    }

    @Override
    public void write(Node node, Writer writer) throws IOException {

    }

    @Override
    public void setLeaveWriterOpen(boolean val) {

    }

    @Override
    public boolean getLeaveWriterOpen() {
        return false;
    }
}
