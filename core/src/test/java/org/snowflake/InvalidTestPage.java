package org.snowflake;


import org.junit.Ignore;
import org.snowflake.Answer;
import org.snowflake.Question;

@Ignore
public class InvalidTestPage {

    public void index(Question question, Answer answer) {
    }

    public void invalid1(Answer answer, Question question) {

    }

    public void invalid2(Question question, Answer answer, int i, TestDataObject nonPrimitive) {

    }

    public void valid1(Question question, Answer answer, TestDataObject nonPrimitive) {

    }

    public void valid2(Question question, Answer answer, String s, int i) {

    }

    public void valid3(String s, int i) {

    }

    public TestDataObject valid4(String s, int i, int j) {
        return null;
    }

}
