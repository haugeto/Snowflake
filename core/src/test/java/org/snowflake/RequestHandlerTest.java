package org.snowflake;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;

public class RequestHandlerTest {

    @Test
    public void testDeduceTemplateFileName() throws Exception {
        WebPage webPage = new WebPage(new TestPage(), "/");
        WebMethod webMethod = webPage.getWebMethodByName("foo");
        assertEquals("org/snowflake/testpage.foo.vm", webMethod.getTemplateFileName());
    }

}
