/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.plankton.io.parser.json.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.barracudamvc.plankton.io.parser.json.JSONPrinter;
import org.barracudamvc.plankton.io.parser.json.lexer.LexerStream;
import static org.hamcrest.core.Is.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class ParserTest {

    public ParserTest() {
    }
    
    @Test
    public void test() {
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        
        Parser parserV2 = new Parser();
        DefaultBuilder builder = new DefaultBuilder();
        
        parserV2.parse(new LexerStream(stream), builder);
        
        Assert.assertTrue(builder.getResult() instanceof Map);
    }

    @Test
    public void testArrayFirst() {
        ByteArrayInputStream stream = new ByteArrayInputStream(" [1,2,3] ".getBytes());

        Parser parserV2 = new Parser();
        DefaultBuilder builder = new DefaultBuilder();

        parserV2.parse(new LexerStream(stream), builder);

        Assert.assertTrue(builder.getResult() instanceof List);

    }
    

    @Test
    public void testExample1() throws IOException {
        InputStream stream = getClass().getResourceAsStream("example1.json");
        byte[] bytes = readIntoByteArray(stream);
        ByteArrayInputStream source = new ByteArrayInputStream(bytes);
        
        
        Parser parserV2 = new Parser();

        JSONPrinter printer = new JSONPrinter();
        DefaultBuilder builder = new DefaultBuilder();

        parserV2.parse(new LexerStream(source), builder);
        String required = new String(bytes);
        String result = printer.print((Map<String, Object>) builder.getResult()).toString();
        assertThat(result, is( required.replace("\r\n", "\n")));
    }
    
    @Test
    public void testBuild() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{ \"a\" : 123, \"b\" : 455, \"c\" : [1,2,3, { \"a\" : 1}] }".getBytes());

        Parser parserV2 = new Parser();

        DefaultBuilder builder = new DefaultBuilder();

        parserV2.parse(new LexerStream(stream), builder);

        Map root = (Map) builder.getResult();
        List c = (List) root.get("c");
        assertThat((String)c.get(0), is("1"));
        Map item3 = (Map) c.get(3);

        assertThat((String)item3.get("a"), is("1"));
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int index;
        while ((index = in.read(buffer)) > 0) {
            out.write(buffer, 0, index);
        }
    }

    public static byte[] readIntoByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }
}
