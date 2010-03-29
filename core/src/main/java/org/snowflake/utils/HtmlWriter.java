package org.snowflake.utils;

import org.apache.commons.lang.StringUtils;

public class HtmlWriter extends SourceCodeWriter {

    public HtmlWriter() {

    }

    public HtmlWriter(int initialIndent) {
        super.prefix = StringUtils.leftPad("", initialIndent, SourceCodeWriter.INDENT);
    }

    public void startTag(String s) {
        super.println(s);
        indent();
    }

    public void endTag(String s) {
        unindent();
        super.println(s);
    }

    public void tags(String... tags) {
        if (tags.length >= 1)
            startTag(tags[0]);

        for (int i = 1; i < tags.length - 1; i++)
            println(tags[i]);

        if (tags.length > 1)
            endTag(tags[tags.length - 1]);
    }

}
