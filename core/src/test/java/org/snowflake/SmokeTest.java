package org.snowflake;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.snowflake.devserver.DevServer;
import org.snowflake.utils.ReflectionHelpers;

public class SmokeTest {

    DevServer devServer;

    TestPage testPage = new TestPage();

    @Before
    public void setUp() throws Exception {
        devServer = new DevServer("", DevServer.DEFAULT_PORT + 1);
        devServer.registerController("smoketest", testPage);
        devServer.run();
    }

    @Test
    public void staticContentRetrieveable() throws Exception {
        String request = "http://localhost:" + devServer.getPort() + "/static/org/snowflake/snowflake.css";
        String content = invokeServer(request);
        assertNotNull(content);
        assertTrue(content.contains("#doc {"));
    }

    @Test
    public void indexContentRetrievable() throws Exception {
        String request = "http://localhost:" + devServer.getPort() + "/smoketest";
        TestDataObject testDataObject = new TestDataObject(1L, "strFieldValue", 42, new Date());
        testPage.testDataObjects.add(testDataObject);
        String content = invokeServer(request);
        assertNotNull(content);
        System.out.println(content);
        assertTrue(content.contains("<table"));
        Map<String, Object> fieldValues = ReflectionHelpers.fieldValues(testDataObject);
        for (String fieldName : fieldValues.keySet()) {
            assertTrue(content.contains("<th>" + StringUtils.capitalize(fieldName) + "</th>"));
            assertTrue(content.contains("<td>" + fieldValues.get(fieldName) + "</td>"));
        }
    }

    protected static String invokeServer(String request) throws IOException, HttpException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(request);
        client.executeMethod(method);

        BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
        String content = new String();
        String line;
        while ((line = reader.readLine()) != null) {
            content += line + "\n";
        }
        reader.close();
        return content;
    }

    @After
    public void tearDown() {
        devServer.shutdown();
    }

}
