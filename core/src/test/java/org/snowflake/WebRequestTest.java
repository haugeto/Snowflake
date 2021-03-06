package org.snowflake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.snowflake.argumentinjection.ArgumentsBuilder;
import org.snowflake.devserver.DevServer;

public class WebRequestTest {
    @Test
    public void testBuildArguments() throws Exception {
        Question question = new Question();
        question.setId(42L);
        Answer answer = new Answer();

        WebPage webPage = new WebPage(new TestPage(), "/");
        webPage.createWebMethods(new HashSet<Class<?>>());
        WebMethod someMethod = webPage.getWebMethodByName("someMethod");
        WebRequest webRequest = new WebRequest(null, webPage, someMethod, question, answer);
        webPage.createWebMethods(new HashSet<Class<?>>());
        someMethod.initializeArgs(null);
        Object[] someMethodArgs = new ArgumentsBuilder(webRequest).buildArguments(webRequest.webMethod.getMethod());
        assertNotNull(someMethodArgs);
        assertEquals(1, someMethodArgs.length);
        assertEquals(42L, someMethodArgs[0]);

        WebMethod methodWithMap = new WebMethod(TestPage.class.getMethod("methodWithMap", Answer.class, Map.class));
        methodWithMap.initializeArgs(null);
        WebRequest request2 = new WebRequest(null, webPage, methodWithMap, question, answer);
        Object[] methodWithMapArgs = new ArgumentsBuilder(request2).buildArguments(request2.webMethod.getMethod());
        assertNotNull(methodWithMapArgs);
        assertEquals(2, methodWithMapArgs.length);
        assertTrue(methodWithMapArgs[0] instanceof Answer);
        assertTrue(methodWithMapArgs[1] instanceof Map<?, ?>);

        question = new Question();

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("strField", "str1");
        paramMap.put("intField", "52");
        question.setParameters(paramMap);
        WebMethod methodWithCustomArg = new WebMethod(TestPage.class.getMethod("methodWithTestDataObjectArg",
                TestDataObject.class));
        methodWithCustomArg.initializeArgs(null);
        WebApp webApp = new DevServer("");
        WebRequest request3 = new WebRequest(webApp, webPage, methodWithCustomArg, question, answer);
        Object[] methodWithCustomArgArgs = new ArgumentsBuilder(request3).buildArguments(methodWithCustomArg
                .getMethod());
        assertNotNull(methodWithMapArgs);
        assertEquals(1, methodWithCustomArgArgs.length);
        assertTrue(methodWithCustomArgArgs[0] instanceof TestDataObject);
        TestDataObject testDataObject = (TestDataObject) methodWithCustomArgArgs[0];
        assertEquals("str1", testDataObject.getStrField());
        assertEquals(new Integer(52), testDataObject.getIntField());

    }
}
