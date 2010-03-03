package org.snowflake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.snowflake.WebMethod;
import org.snowflake.WebMethodType;
import org.snowflake.WebPage;

public class WebPageTest {

    @Test
    public void testCreateWebMethodsFindsEditAndSubmitMethods() {
        WebPage webPage = new WebPage(new CrudPage());
        webPage.createWebMethods("/");
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
        webPage.createWebMethods("/");
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
        assertEquals(6, webMethods.size());
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
        webPage.createWebMethods("/");

        List<WebMethod> webMethods = webPage.rowActionWebMethods();
        assertNotNull(webMethods);
        HashSet<String> methodNames = new HashSet<String>();
        for (WebMethod wm : webMethods)
            methodNames.add(wm.getName());

        assertTrue(methodNames.contains("methodWithId"));
        assertTrue(methodNames.contains("collectionMethod"));
        assertTrue(methodNames.contains("someMethod"));
    }

}
