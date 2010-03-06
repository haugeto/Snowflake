package org.snowflake.views;

import java.io.InputStream;
import java.io.OutputStream;

import org.snowflake.Answer;
import org.snowflake.SnowflakeException;
import org.snowflake.WebMethod;
import org.snowflake.utils.StreamHelpers;


public class StaticContentView implements View {

    public StaticContentView() {
    }

    @Override
    public void renderView(WebMethod webMethod, Answer answer, OutputStream out) throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream(answer.getTemplateFile());
        if (in == null) {
            throw new SnowflakeException("Could not find resource " + answer.getTemplateFile());
        }
        StreamHelpers.pipeToStream(in, out);
        in.close();
    }

}
