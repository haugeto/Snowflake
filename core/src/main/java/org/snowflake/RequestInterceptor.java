package org.snowflake;

public interface RequestInterceptor {
    /**
     * Invoked before a request is processed
     */
    public void before(Question question, Answer answer) throws Exception;

    /**
     * Invoked after a request is processed
     */
    public void after(Question question, Answer answer) throws Exception;
}
