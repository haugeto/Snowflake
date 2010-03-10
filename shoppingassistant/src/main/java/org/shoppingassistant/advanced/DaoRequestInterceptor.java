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
public class DaoRequestInterceptor implements RequestInterceptor<DataAccessObject> {

    @Override
    public DataAccessObject before(Question question, Answer answer) throws Exception {
        DataAccessObject dataAccessObject = new DataAccessObject((int) (Math.random() * 1000));
        dataAccessObject.open();
        return dataAccessObject;
    }

    @Override
    public void after(Question question, Answer answer, DataAccessObject dataAccessObject) throws Exception {
        dataAccessObject.close();
    }

    @Override
    public Class<DataAccessObject> getType() {
        return DataAccessObject.class;
    }

}