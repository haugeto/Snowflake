package org.snowflake;

/**
 * A request "filter" that is given the chance to pre and post process a request to the application.
 * 
 * @see WebApp#addRequestInterceptor(RequestInterceptor)
 * @author haugeto
 */
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
