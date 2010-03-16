package org.snowflake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class WebPageTest {

    @Test
    public void testCreateWebMethodsFindsEditAndSubmitMethods() {
        WebPage webPage = new WebPage(new CrudPage());
        webPage.createWebMethods("/", null);
        Set<WebMethod> webMethods = webPage.webMethods;

        Map<WebMethodType, WebMethod> operations = new HashMap<WebMethodType, WebMethod>();
        for (WebMethod m : webMethods) {
            operations.put(m.getType(), m);
        }
        WebMethod addFormMethod = operations.get(WebMethodType.CREATE_FORM);
        assertNotNull(addFormMethod);
        assertEquals("add", addFormMethod.getName());

        WebMethod editFormMethod = operations.get(WebMethodType.UPDATE_FORM);
        assertNotNull(editFormMethod);
        assertEquals("showForm", editFormMethod.getName());
        WebMethod submitFormMethod = operations.get(WebMethodType.SUBMIT);
        assertNotNull(submitFormMethod);
        assertEquals("saveFormData", submitFormMethod.getName());

        assertSame(addFormMethod.getNext(), submitFormMethod);
        assertSame(editFormMethod.getNext(), submitFormMethod);
    }

    @Test
    public void testCreateWebMethodsHandlesCustomMethods() {
        WebPage webPage = new WebPage(new CrudPage());
        webPage.createWebMethods("/", null);
        Set<WebMethod> webMethods = webPage.webMethods;
        WebMethod customOp = null;
        for (WebMethod webMethod : webMethods) {
            if ("customOp".equals(webMethod.getName())) {
                customOp = webMethod;
                break;
            }
        }
        assertNotNull(customOp);
        assertEquals(WebMethodType.CUSTOM, customOp.getType());
        assertNull(customOp.getNext());
    }

    @Test
    public void testCreateWebMethodsDoesntIncludeNonWebMethods() {
        WebPage webPage = new WebPage(new TestPage());
        Set<WebMethod> webMethods = webPage.webMethods;
        assertEquals(7, webMethods.size());
        HashSet<String> methodNames = new HashSet<String>();
        for (WebMethod wm : webMethods) {
            methodNames.add(wm.getName());
        }
        assertTrue(methodNames.contains("index"));
        assertTrue(methodNames.contains("foo"));
        assertTrue(methodNames.contains("someMethod"));
        assertTrue(methodNames.contains("collectionMethod"));
        assertTrue(methodNames.contains("methodWithMap"));
        assertTrue(methodNames.contains("methodWithId"));
    }

    @Test
    public void testGetActionMethods() {
        WebPage webPage = new WebPage(new TestPage());
        webPage.createWebMethods("/", null);

        List<WebMethod> webMethods = webPage.rowActionWebMethods();
        assertNotNull(webMethods);
        HashSet<String> methodNames = new HashSet<String>();
        for (WebMethod wm : webMethods)
            methodNames.add(wm.getName());

        assertTrue(methodNames.contains("methodWithId"));
        assertTrue(methodNames.contains("collectionMethod"));
        assertTrue(methodNames.contains("someMethod"));
    }

    @Test
    public void testCreateWebMethodsHandlesIgnoredArgs() {
        TestPage controller = new TestPage() {
            @SuppressWarnings("unused")
            public void methodWithIgnoreArg(Answer a, Question q, int i, WebPageTest webPageTest) {
            }
        };
        Set<Class<?>> ignoreArgs = new HashSet<Class<?>>();
        ignoreArgs.add(WebPageTest.class);
        WebPage webPage = new WebPage(controller, "/", ignoreArgs);

        WebMethod methodWithIgnoreArg = webPage.getWebMethodByName("methodWithIgnoreArg");
        assertNotNull(methodWithIgnoreArg);
        assertSame(WebMethod.HttpMethod.GET, methodWithIgnoreArg.getHttpMethod());
        assertTrue(methodWithIgnoreArg.hasHttpArg);
    }

    @Test
    public void testCreateWebMethodsHandlesOverloading() {
        OverloadedController controller = new OverloadedController();
        WebPage webPage = new WebPage(controller, "/");
        assertEquals(1, webPage.getWebMethods().size());
        WebMethod indexMethod = webPage.getWebMethodByName("index");
        assertNotNull(indexMethod);
        assertEquals(1, indexMethod.getOverloadingMethods().size());
        assertEquals("index", indexMethod.getOverloadingMethods().iterator().next().getName());
    }

    class OverloadedController {

        public Collection<Object> index() {
            return null;
        }

        public Collection<Object> index(int id) {
            return null;
        }

    }

}
