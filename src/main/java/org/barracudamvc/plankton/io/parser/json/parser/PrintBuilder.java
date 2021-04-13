package org.barracudamvc.plankton.io.parser.json.parser;

import java.util.Stack;
import org.barracudamvc.plankton.io.parser.json.Builder;

public class PrintBuilder implements Builder<String> {

    StringBuilder builder = new StringBuilder();
    Stack<String> depth;

    public PrintBuilder() {
        depth = new Stack<>();
        depth.push("");
    }

    @Override
    public void emptyStream() {
        builder.append("");
    }

    @Override
    public void builderMap() {
        builder.append(depth.peek()).append("{\n");
        depth.push(depth.peek() + "\t");
    }

    @Override
    public void buildMapKey(String key) {
        builder.append(depth.peek()).append('"').append(key).append('"').append(" : ");
    }

    @Override
    public void buildMapValue(String value) {
        if (value instanceof String) {
            builder.append('"').append(value).append('"');
        } else {
            builder.append(value);
        }
        builder.append("\n");

    }

    @Override
    public void buildArray() {
        builder.append("[\n");
        depth.push(depth.peek() + "\t");
    }

    @Override
    public void buildArrayValue(String value) {
        builder.append(depth.peek());
        if (value instanceof String) {
            builder.append('"').append(value).append('"');
        } else {
            builder.append(value);
        }
        builder.append("\n");
    }

    @Override
    public void finishMap() {
        depth.pop();
        builder.append(depth.peek());
        builder.append("}\n");
    }

    @Override
    public void finishArray() {
        depth.pop();
        builder.append(depth.peek());
        builder.append("]\n");
    }

    public String toString() {
        return builder.toString();
    }

    @Override
    public String getResult() {
        return builder.toString();
    }
}
