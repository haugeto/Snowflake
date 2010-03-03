package org.snowflake;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.snowflake.devserver.DevServer;

public class SmokeTest {

    private DevServer devServer;

    @Before
    public void setUp() throws Exception {
        devServer = new DevServer(DevServer.DEFAULT_PORT + 1);
        devServer.run();
    }

    @Test
    public void testHttpGet() throws HttpException, IOException {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://localhost:" + devServer.getPort() + "/static/no/iterate/snowflake/views/snowflake.css");
        client.executeMethod(method);

        BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
        String content = new String();
        String line;
        while ((line = reader.readLine()) != null) {
            content += line + "\n";
        }
        reader.close();
        assertNotNull(content);
        assertTrue(content.contains("#doc {"));
    }

    @After
    public void tearDown() {
        devServer.shutdown();
    }

}
