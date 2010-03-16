package org.snowflake.devserver;

import java.io.InputStream;
import java.io.OutputStream;

import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.SnowflakeException;
import org.snowflake.WebMethod;
import org.snowflake.utils.StreamHelpers;
import org.snowflake.views.View;


public class StaticContentView implements View {

    public StaticContentView() {
    }

    @Override
    public void renderView(WebMethod webMethod, Question question, Answer answer, OutputStream out) throws Exception {
        String requestedFile = answer.getTemplateFile();
        InputStream in = getClass().getClassLoader().getResourceAsStream(requestedFile);
        if (in == null) {
            throw new SnowflakeException("Could not find resource " + requestedFile);
        }
        StreamHelpers.pipeToStream(in, out);
        in.close();
    }

}
