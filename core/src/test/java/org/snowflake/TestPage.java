package org.snowflake;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.snowflake.Answer;
import org.snowflake.Question;

@Ignore
public class TestPage {

    Set<TestDataObject> testDataObjects = new HashSet<TestDataObject>();

    public void foo(Answer answer) {

    }

    public Collection<TestDataObject> index(Question question, Answer answer) {
        return testDataObjects;
    }

    public TestDataObject someMethod(long id) {
        return new TestDataObject();
    }

    public Collection<?> collectionMethod(long id) {
        Set<TestDataObject> result = new LinkedHashSet<TestDataObject>();
        result.add(new TestDataObject());
        return result;
    }

    public void methodWithMap(Answer answer, Map<String, String> httpArgs) {
    }

    public void methodWithId(Answer answer, long id) {
    }

    public void methodWithTestDataObjectArg(TestDataObject testDataObject) {
    }
}
