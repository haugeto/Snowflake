package org.snowflake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.junit.Before;
import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.WebApp;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.WebRequest;
import org.snowflake.devserver.DevServer;

/**
 * Multiple unit tests for {@link WebRequest#delegateToController()}
 */
public class WebRequestProcessTest {

    CallbackTestPage callbackTestPage;
    WebApp devServer;
    Question question;
    Answer answer;
    WebPage webPage;

    @Before
    public void setup() {
        callbackTestPage = new CallbackTestPage();
        devServer = new DevServer("");
        devServer.registerController("/", callbackTestPage);
        answer = new Answer();
        question = new Question();
        webPage = devServer.getWebPageForController(callbackTestPage);
    }

    @Test
    public void testFoo() throws Throwable {
        WebMethod webMethod = webPage.getWebMethodByName("foo");
        WebRequest request = new WebRequest(devServer, webPage, webMethod, question, answer);
        request.delegateToController();
        assertTrue(callbackTestPage.fooInvoked);
    }

    @Test
    public void testIndex() throws Throwable {
        WebMethod webMethod = webPage.getWebMethodByName("index");
        WebRequest request = new WebRequest(devServer, webPage, webMethod, question, answer);
        request.delegateToController();
        assertTrue(callbackTestPage.indexInvoked);
    }

    @Test
    public void testSomeMethod() throws Throwable {
        WebMethod webMethod = webPage.getWebMethodByName("someMethod");
        question.setId(42);
        WebRequest request = new WebRequest(devServer, webPage, webMethod, question, answer);
        request.delegateToController();
        assertTrue(callbackTestPage.someMethodInvoked);
        assertNotNull(answer.getFormData());
    }

    @Test
    public void testCollectionMethod() throws Throwable {
        WebMethod webMethod = webPage.getWebMethodByName("collectionMethod");
        question.setId(42);
        WebRequest request = new WebRequest(devServer, webPage, webMethod, question, answer);
        request.delegateToController();
        assertTrue(callbackTestPage.collectionMethodInvoked);
        assertNotNull(answer.getIndexData());
        assertEquals(1, answer.getIndexData().size());
    }

    @Test
    public void testMethodWithMap() throws Throwable {
        WebMethod webMethod = webPage.getWebMethodByName("methodWithMap");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("1", "hello1");
        parameters.put("2", "hello2");
        question.setParameters(parameters);
        WebRequest request = new WebRequest(devServer, webPage, webMethod, question, answer);
        request.delegateToController();
        assertTrue(callbackTestPage.methodWithMapInvoked);
    }

    class CallbackTestPage extends TestPage {

        boolean collectionMethodInvoked;
        boolean fooInvoked;
        boolean indexInvoked;
        boolean methodWithMapInvoked;
        boolean someMethodInvoked;

        @Override
        public Collection<?> collectionMethod(int id) {
            collectionMethodInvoked = true;
            assertEquals(42, id);
            return super.collectionMethod(id);
        }

        @Override
        public void foo(Answer answer) {
            fooInvoked = true;
            super.foo(answer);
        }

        @Override
        public void index(Question question, Answer answer) {
            indexInvoked = true;
            super.index(question, answer);
        }

        @Override
        public void methodWithMap(Answer answer, Map<String, String> httpArgs) {
            methodWithMapInvoked = true;
            assertNotNull(httpArgs);
            assertEquals(2, httpArgs.size());
            super.methodWithMap(answer, httpArgs);
        }

        @Override
        public TestDataObject someMethod(int id) {
            someMethodInvoked = true;
            assertEquals(42, id);
            return super.someMethod(id);
        }

    }

}
