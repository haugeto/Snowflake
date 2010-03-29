package org.snowflake.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SourceCodeWriter extends PrintWriter {

    public static final String INDENT = "\t";

    StringWriter result = new StringWriter();

    String prefix = "";

    public SourceCodeWriter() {
        super(new StringWriter());
    }

    public void indent() {
        prefix += INDENT;
    }

    public void unindent() {
        if (prefix.length() > 0)
            prefix = prefix.substring(0, prefix.length() - INDENT.length());
    }

    @Override
    public void println(String s) {
        println(s, true);
    }

    public void println(String s, boolean doIndent) {
        if (doIndent)
            super.println(prefix + s);
        else
            super.println(s);
    }

    @Override
    public String toString() {
        return this.out.toString();
    }

}
