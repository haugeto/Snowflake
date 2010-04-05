package org.snowflake;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

public class WebAppTest {

    @Test
    public void testResolveTargetMethod() throws Exception {
        WebApp webApp = new WebApp("test", "/shoppingassistant");
        WebPage testPage = webApp.registerController("test", new CrudPage());
        WebPage defaultPage = webApp.registerController("", new CrudPage());
        
        webApp.initializeDynamicContentContexts();
        WebMethod defaultMethod = defaultPage.getDefaultWebMethod();
        WebMethod showFormMethod = testPage.getWebMethodByName("showForm");

        Set<WebPage> staticPages = webApp.initializeStaticContentContexts();
        WebMethod staticMethod = staticPages.iterator().next().getDefaultWebMethod();
        
        assertEquals(staticMethod, webApp.resolveTargetMethod("/shoppingassistant/static/org/snowflake/snowflake.css"));
        assertEquals(defaultMethod, webApp.resolveTargetMethod("/shoppingassistant/"));
        assertEquals(showFormMethod, webApp.resolveTargetMethod("/shoppingassistant/test/showForm"));
    }

}
