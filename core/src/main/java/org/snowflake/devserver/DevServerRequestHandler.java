package org.snowflake.devserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.snowflake.Answer;
import org.snowflake.Question;
import org.snowflake.SnowflakeException;
import org.snowflake.WebMethod;
import org.snowflake.WebPage;
import org.snowflake.WebRequest;
import org.snowflake.WebRequestDispatcher;
import org.snowflake.utils.Console;
import org.snowflake.utils.HttpHelpers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handles incoming HTTP requests from the {@link DevServer}. Each instance of
 * this class is bound to a particular URL. The HttpHandler is a specific
 * implementation for the Java 6 built-in HTTP server, which should be used for
 * development only.
 * 
 * @author haugeto
 */
public class DevServerRequestHandler extends WebRequestDispatcher implements HttpHandler {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public DevServerRequestHandler(DevServer devServer, WebPage webPage, WebMethod webMethod) {
        super(devServer, webPage, webMethod);
    }

    public void handle(HttpExchange exchange) throws IOException {
        Console.justify(exchange.getRequestMethod() + " " + exchange.getRequestURI(), "=> " + webMethod.toString());
        try {
            Question question = parseRequest(exchange);
            Answer answer = webMethod.createAnswer();
            WebRequest webRequest = webApp.createWebRequest(webPage, webMethod, question, answer);
            try {
                webRequest.before(question, answer);
                processController(webRequest);
                sendSuccessfulResponseHeaders(exchange, webRequest.getAnswer());
                processViewOnSuccess(webRequest, exchange.getResponseBody());

            } catch (SnowflakeException e) {
                sendFailureResponseHeaders(exchange, webRequest.getAnswer());
                processViewOnFailure(webRequest, exchange.getResponseBody(), e);
            }
            finally {
                webRequest.after(question, answer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            exchange.close();
        }
    }

    void sendFailureResponseHeaders(HttpExchange exchange, Answer answer) throws IOException {
        exchange.sendResponseHeaders(answer.getHttpCode(), 0);
    }

    void sendSuccessfulResponseHeaders(HttpExchange exchange, Answer answer) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", answer.getContentType());
        if (answer.getContentLength() != null) {
            responseHeaders.set("Content-Length", Long.toString(answer.getContentLength()));
        }
        if (answer.getLastModified() != null) {
            responseHeaders.set("Last-Modified", answer.getLastModified().toString());
        }
        exchange.sendResponseHeaders(WebRequest.HTTP_OK, 0);
    }

    Question parseRequest(HttpExchange exchange) throws Exception {
        Question result = new Question();

        URI requestURI = exchange.getRequestURI();
        String incomingUrl = requestURI.getPath();
        result.setUrl(incomingUrl);
        String boundUrl = webMethod.getUrl();

        if (incomingUrl.length() > boundUrl.length()) {
            // assume restful lookup
            String urlFragment = StringUtils.substringBefore(incomingUrl, "?");

            String idStr = StringUtils.substringAfterLast(urlFragment, "/");
            if (idStr.length() > 0) {
                int id;
                try {
                    id = Integer.parseInt(idStr);
                    result.setId(id);
                } catch (NumberFormatException e) {
                    // happens when there is no ID embedded in the URL, which is
                    // perfectly fine.
                }
            }
        }
        // parse URL get parameters, if any
        String urlParameters = requestURI.getQuery();
        if (!StringUtils.isEmpty(urlParameters)) {
            Map<String, String> urlVariables = HttpHelpers.parseHttpParameters(urlParameters, DEFAULT_ENCODING);
            result.setParameters(urlVariables);
        }
        if (WebMethod.HttpMethod.POST.name().equalsIgnoreCase(exchange.getRequestMethod())) {
            BufferedReader r = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String requestBody = r.readLine();
            if (requestBody != null) {
                // TODO: Figure out the clients enc
                Map<String, String> httpVariables = HttpHelpers.parseHttpParameters(requestBody, DEFAULT_ENCODING);
                // FIXME: This overwrites possible URL parameters
                result.setParameters(httpVariables);
            }
        }
        return result;
    }

}