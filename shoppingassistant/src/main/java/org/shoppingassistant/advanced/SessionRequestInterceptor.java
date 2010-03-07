/**
 * 
 */
package org.shoppingassistant.advanced;

import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.RequestInterceptor;

/**
 * Example of how to use a RequestInterceptor.
 * 
 * @author haugeto
 */
public class SessionRequestInterceptor implements RequestInterceptor<Session> {

    @Override
    public Session before(Question question, Answer answer) throws Exception {
        Session session = new Session((int) (Math.random() * 1000));
        session.open();
        return session;
    }

    @Override
    public void after(Question question, Answer answer, Session session) throws Exception {
        session.close();
    }

    @Override
    public Class<Session> getType() {
        return Session.class;
    }

}