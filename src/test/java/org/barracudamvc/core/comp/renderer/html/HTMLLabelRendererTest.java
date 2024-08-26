/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.core.comp.renderer.html;

import org.barracudamvc.core.comp.BComponent;
import org.barracudamvc.core.comp.BLabel;
import org.barracudamvc.core.comp.DefaultView;
import org.barracudamvc.core.comp.DefaultViewContext;
import org.barracudamvc.core.comp.NoSuitableRendererException;
import org.barracudamvc.core.comp.RenderException;
import org.barracudamvc.core.comp.UnsupportedFormatException;
import org.enhydra.xml.xmlc.dom.XMLCDomFactory;
import org.enhydra.xml.xmlc.dom.XMLCDomFactoryCache;
import org.enhydra.xml.xmlc.dom.xerces.XercesHTMLDomFactory;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLLabelElement;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class HTMLLabelRendererTest {

    DefaultViewContext viewContext;
    DefaultView view;
    Document document;

    @Before
    public void setup() {
        XMLCDomFactory factory = XMLCDomFactoryCache.getFactory(XercesHTMLDomFactory.class);
        try {
            document = factory.createDocument(null, "HTML", null);
        } catch(Throwable e){
            System.out.println("Hello there");
            e.printStackTrace();
        }

        viewContext = new DefaultViewContext();
        view = new DefaultView();
    }

    @Test
    public void testDefaultCreate_GivenNullTemplateView() throws UnsupportedFormatException {
        viewContext.setTemplateNode(null);

        assertThat(create(null), IsInstanceOf.instanceOf(HTMLLabelElement.class));
    }

    @Test
    public void testDefaultCreate_GivenLabelTemplateView() throws UnsupportedFormatException {
        viewContext.setTemplateNode(createElement("label"));

        assertThat(create(null), IsInstanceOf.instanceOf(HTMLLabelElement.class));
    }

    @Test(expected = NoSuitableRendererException.class)
    public void testRender_GivenNoComponent_expectException() throws RenderException {
        HTMLLabelElement htmlLabel = createElement("label");
        htmlLabel.setTextContent("This is some text");

        render(null, htmlLabel);
    }

    @Test(expected = NoSuitableRendererException.class)
    public void testRender_GivenNoNode_expectException() throws RenderException {
        BComponent label = new BLabel("label-text", "label-id");

        render(label, null);
    }

    @Test
    public void testRender_ReplaceInnerHTMLWithBLabel() throws RenderException {
        BComponent label = new BLabel("label-text", "label-id");

        HTMLLabelElement htmlLabel = createElement("label");
        htmlLabel.setTextContent("This is some text");

        render(label, htmlLabel);

        assertThat(htmlLabel.getHtmlFor(), is("label-id"));
        assertThat(htmlLabel.getChildNodes().getLength(), is(1));
        assertThat(((Text) htmlLabel.getFirstChild()).getWholeText(), is("label-text"));
    }

    @Test
    public void testRender_AppendInnerHTMLWithBLabel() throws RenderException {
        BComponent label = new BLabel("label-text", "label-id");

        HTMLLabelElement htmlLabel = createElement("label");
        createElement("label");
        htmlLabel.appendChild(createElement("div"));

        render(label, htmlLabel);

        assertThat(htmlLabel.getHtmlFor(), is("label-id"));
        assertThat(htmlLabel.getChildNodes().getLength(), is(2));
        assertThat(((Text) htmlLabel.getFirstChild()).getWholeText(), is("label-text"));
    }

    private void render(BComponent label, Node node) throws RenderException {
        view.setNode(node);
        new HTMLLabelRenderer().renderComponent(label, view, viewContext);
    }

    private Node create(BComponent component) throws UnsupportedFormatException {
        return new HTMLLabelRenderer().createDefaultNode(document, component, viewContext);
    }

    private <DesiredType extends Node> DesiredType createElement(final String nodeType) throws DOMException {
        return (DesiredType) document.createElement(nodeType);
    }
}
