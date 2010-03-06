package org.snowflake;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.snowflake.Answer;
import org.snowflake.Question;

@Ignore
public class TestPage {

    public void foo(Answer answer) {

    }

    public void index(Question question, Answer answer) {

    }

    public TestDataObject someMethod(int id) {
        return new TestDataObject();
    }

    public Collection<?> collectionMethod(int id) {
        Set<TestDataObject> result = new LinkedHashSet<TestDataObject>();
        result.add(new TestDataObject());
        return result;
    }

    public void methodWithMap(Answer answer, Map<String, String> httpArgs) {
    }

    public void methodWithId(Answer answer, int id) {
    }

    public void methodWithTestDataObjectArg(TestDataObject testDataObject) {
    }
}
