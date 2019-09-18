/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package org.barracudamvc.plankton.io.parser.json;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class JSONPrinter {

    public CharSequence print(Map<String, Object> source) {
        StringBuilder builder = new StringBuilder();
        Stack<String> tabs = new Stack<>();
        tabs.add("");
        print(source, builder, tabs);
        return builder;
    }

    private void print(Map<String, Object> source, StringBuilder builder, Stack<String> tabs) {
        String level = tabs.peek();
        builder.append("{\n");
        tabs.push(level + "\t");
        String sep = "";
        for (Entry<String, Object> entry : source.entrySet()) {
            builder.append(sep).append(tabs.peek());
            printString(builder, entry.getKey()).append(" : ");
            print(entry.getValue(), builder, tabs);
            sep = ",\n";
        }

        tabs.pop();
        builder.append("\n").append(level).append("}");
    }

    private void print(Object value, StringBuilder builder, Stack<String> tabs) {
        if (value instanceof Map) {
            print((Map) value, builder, tabs);
        } else if (value instanceof List) {
            print((List) value, builder, tabs);
        } else if (value instanceof String) {
            printString(builder, value);
        } else {
            builder.append(value);
        }
    }

    private void print(List<Object> source, StringBuilder builder, Stack<String> tabs) {
        String level = tabs.peek();
        builder.append(" [\n");
        tabs.push(level + "\t");
        String sep = "";
        for (Object val : source) {
            builder.append(sep).append(tabs.peek());
            print(val, builder, tabs);
            sep = ",\n";
        }

        tabs.pop();
        builder.append("\n").append(level).append("]");
    }

    private StringBuilder printString(StringBuilder builder, Object o) {
        return builder.append('"').append(o).append('"');
    }
}
