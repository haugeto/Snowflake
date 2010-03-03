package org.snowflake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.WebRequest;

public class WebRequestTest {
    @Test
    public void testBuildArguments() throws Exception {
        Question question = new Question();
        Map<String, String> argValues = new HashMap<String, String>();
        argValues.put("id", "42");
        question.setParameters(argValues);
        Answer answer = new Answer();

        WebPage webPage = new WebPage(new TestPage(), "/");
        WebMethod someMethod = webPage.getWebMethodByName("someMethod");
        WebRequest webRequest = new WebRequest(null, webPage, someMethod, question, answer);
        someMethod.initializeArgs();

        Object[] someMethodArgs = webRequest.buildArguments(question, answer);
        assertNotNull(someMethodArgs);
        assertEquals(1, someMethodArgs.length);
        assertEquals(42, someMethodArgs[0]);

        WebMethod methodWithMap = new WebMethod(TestPage.class.getMethod("methodWithMap", Answer.class, Map.class));
        methodWithMap.initializeArgs();
        WebRequest request2 = new WebRequest(null, webPage, methodWithMap, question, answer);

        Object[] methodWithMapArgs = request2.buildArguments(question, answer);
        assertNotNull(methodWithMapArgs);
        assertEquals(2, methodWithMapArgs.length);
        assertTrue(methodWithMapArgs[0] instanceof Answer);
        assertTrue(methodWithMapArgs[1] instanceof Map<?, ?>);

    }
}
