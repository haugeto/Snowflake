package org.snowflake;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;


import org.junit.Test;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;

public class RequestHandlerTest {

    @Test
    public void testDeduceTemplateFileName() throws Exception {
        WebPage webPage = new WebPage(new TestPage(), "/");
        webPage.createWebMethods(new HashSet<Class<?>>());
        WebMethod webMethod = webPage.getWebMethodByName("foo");
        assertEquals("org/snowflake/testpage.foo.vm", webMethod.getTemplateFileName());
    }

}
