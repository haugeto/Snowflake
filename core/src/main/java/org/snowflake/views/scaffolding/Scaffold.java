package org.snowflake.views.scaffolding;

import org.snowflake.Answer;
import org.snowflake.Question;

public interface Scaffold {

    public String generate(Question question, Answer answer) throws Exception;

}