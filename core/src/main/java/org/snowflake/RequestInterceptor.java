package org.snowflake;

/**
 * A request "filter" that is given the chance to pre- and post-process a
 * request to the application.
 * 
 * @see WebApp#addRequestInterceptor(RequestInterceptor)
 * @author haugeto
 */
public interface RequestInterceptor<T> {
    /**
     * Invoked before a request is processed
     * 
     * @return Value to be argument-injected to those controller methods that
     *         have the class of the returned type as one of their arguments.
     *         Returns null if not applicable.
     */
    public T before(Question question, Answer answer) throws Exception;

    /**
     * Invoked after a request is processed
     * 
     * @param returnedByBefore
     *            The value that was returned by
     *            {@link #before(Question, Answer)}
     */
    public void after(Question question, Answer answer, T returnedByBefore) throws Exception;

    /**
     * Tells the framework which type this interceptor will pass to controller
     * methods. Returns null if not applicable.
     */
    public Class<T> getType();
}
